/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package pt.lunacloud.services.storage.transfer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.AmazonServiceException;
import pt.lunacloud.AmazonWebServiceRequest;
import pt.lunacloud.auth.LunacloudCredentials;
import pt.lunacloud.services.storage.AmazonS3EncryptionClient;
import pt.lunacloud.services.storage.LunacloudStorage;
import pt.lunacloud.services.storage.LunacloudStorageClient;
import pt.lunacloud.services.storage.internal.Mimetypes;
import pt.lunacloud.services.storage.internal.ServiceUtils;
import pt.lunacloud.services.storage.model.AbortMultipartUploadRequest;
import pt.lunacloud.services.storage.model.GetObjectRequest;
import pt.lunacloud.services.storage.model.ListMultipartUploadsRequest;
import pt.lunacloud.services.storage.model.ListObjectsRequest;
import pt.lunacloud.services.storage.model.MultipartUpload;
import pt.lunacloud.services.storage.model.MultipartUploadListing;
import pt.lunacloud.services.storage.model.ObjectListing;
import pt.lunacloud.services.storage.model.ObjectMetadata;
import pt.lunacloud.services.storage.model.ProgressListener;
import pt.lunacloud.services.storage.model.PutObjectRequest;
import pt.lunacloud.services.storage.model.StorageObjectSummary;
import pt.lunacloud.services.storage.model.StorageObject;
import pt.lunacloud.services.storage.transfer.Transfer.TransferState;
import pt.lunacloud.services.storage.transfer.internal.DownloadImpl;
import pt.lunacloud.services.storage.transfer.internal.DownloadMonitor;
import pt.lunacloud.services.storage.transfer.internal.MultipleFileDownloadImpl;
import pt.lunacloud.services.storage.transfer.internal.MultipleFileTransfer;
import pt.lunacloud.services.storage.transfer.internal.MultipleFileTransferMonitor;
import pt.lunacloud.services.storage.transfer.internal.MultipleFileUploadImpl;
import pt.lunacloud.services.storage.transfer.internal.ProgressListenerChain;
import pt.lunacloud.services.storage.transfer.internal.TransferManagerUtils;
import pt.lunacloud.services.storage.transfer.internal.TransferProgressImpl;
import pt.lunacloud.services.storage.transfer.internal.TransferProgressUpdatingListener;
import pt.lunacloud.services.storage.transfer.internal.TransferStateChangeListener;
import pt.lunacloud.services.storage.transfer.internal.UploadCallable;
import pt.lunacloud.services.storage.transfer.internal.UploadImpl;
import pt.lunacloud.services.storage.transfer.internal.UploadMonitor;
import pt.lunacloud.util.VersionInfoUtils;

/**
 * High level utility for managing transfers to Amazon S3.
 * <p>
 * <code>TransferManager</code> provides a simple API for uploading content to
 * Amazon S3, and makes extensive use of Amazon S3 multipart uploads to achieve
 * enhanced throughput, performance and reliability.
 * <p>
 * When possible, <code>TransferManager</code> attempts to use multiple threads
 * to upload multiple parts of a single upload at once. When dealing with large
 * content sizes and high bandwidth, this can have a significant increase on
 * throughput.
 * <p>
 * <code>TransferManager</code> is responsible for managing resources such as
 * connections and threads; share a single instance of
 * <code>TransferManager</code> whenever possible. <code>TransferManager</code>,
 * like all the client classes in the AWS SDK for Java, is thread safe.
 * <p>
 * Using <code>TransferManager</code> to upload options to Amazon S3 is easy:
 * 
 * <pre>
 * AWSCredentials myCredentials = new BasicAWSCredentials(...);
 * TransferManager tx = new TransferManager(myCredentials);
 * Upload myUpload = tx.upload(myBucket, myFile.getName(), myFile);
 * 
 * while (myUpload.isDone() == false) {
 *     System.out.println("Transfer: " + myUpload.getDescription());
 *     System.out.println("  - State: " + myUpload.getState());
 *     System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
 *     // Do work while we wait for our upload to complete...
 *     Thread.sleep(500);
 * }
 * </pre>
 * <p>
 * Note: Transfers are stored in memory. If the JVM is restarted, previous
 * transfers are no longer accessible. If needed, clean up any multipart uploads
 * that are incomplete.
 */
public class TransferManager {

	/** The low level client we use to make the actual calls to Amazon S3. */
	private LunacloudStorage s3;

	/** Configuration for how TransferManager processes requests. */
	private TransferManagerConfiguration configuration;
	/** The thread pool in which transfers are uploaded or downloaded. */
	private ThreadPoolExecutor threadPool;

	/**
	 * Thread used for periodicially checking transfers and updating thier
	 * state.
	 */
	private ScheduledExecutorService timedThreadPool = new ScheduledThreadPoolExecutor(
	        1);

	private static final Log log = LogFactory.getLog(TransferManager.class);

	/**
	 * Constructs a new <code>TransferManager</code> and Amazon S3 client using
	 * the specified AWS security credentials.
	 * <p>
	 * <code>TransferManager</code> and client objects may pool connections and
	 * threads. Reuse <code>TransferManager</code> and client objects and share
	 * them throughout applications.
	 * <p>
	 * TransferManager and all AWS client objects are thread safe.
	 * 
	 * @param credentials
	 *            The AWS security credentials to use when making authenticated
	 *            requests.
	 */
	public TransferManager(LunacloudCredentials credentials) {
		this(new LunacloudStorageClient(credentials));
	}

	/**
	 * Constructs a new <code>TransferManager</code>, specifying the client to
	 * use when making requests to Amazon S3.
	 * <p>
	 * <code>TransferManager</code> and client objects may pool connections and
	 * threads. Reuse <code>TransferManager</code> and client objects and share
	 * them throughout applications.
	 * <p>
	 * TransferManager and all AWS client objects are thread safe.
	 * </p>
	 * 
	 * @param s3
	 *            The client to use when making requests to Amazon S3.
	 */
	public TransferManager(LunacloudStorage s3) {
		this(s3, TransferManagerUtils.createDefaultExecutorService());
	}

	/**
	 * Constructs a new <code>TransferManager</code> specifying the client and
	 * thread pool to use when making requests to Amazon S3.
	 * <p>
	 * <code>TransferManager</code> and client objects may pool connections and
	 * threads. Reuse <code>TransferManager</code> and client objects and share
	 * them throughout applications.
	 * <p>
	 * TransferManager and all AWS client objects are thread safe.
	 * 
	 * @param s3
	 *            The client to use when making requests to Amazon S3.
	 * @param threadPool
	 *            The thread pool in which to execute requests.
	 */
	public TransferManager(LunacloudStorage s3, ThreadPoolExecutor threadPool) {
		this.s3 = s3;
		this.threadPool = threadPool;
		this.configuration = new TransferManagerConfiguration();
	}

	/**
	 * Sets the configuration which specifies how this
	 * <code>TransferManager</code> processes requests.
	 * 
	 * @param configuration
	 *            The new configuration specifying how this
	 *            <code>TransferManager</code> processes requests.
	 */
	public void setConfiguration(TransferManagerConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the configuration which specifies how this
	 * <code>TransferManager</code> processes requests.
	 * 
	 * @return The configuration settings for this <code>TransferManager</code>.
	 */
	public TransferManagerConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the underlying Amazon S3 client used to make requests to Amazon
	 * S3.
	 * 
	 * @return The underlying Amazon S3 client used to make requests to Amazon
	 *         S3.
	 */
	public LunacloudStorage getAmazonS3Client() {
		return s3;
	}

	/**
	 * <p>
	 * Schedules a new transfer to upload data to Amazon S3. This method is
	 * non-blocking and returns immediately (i.e. before the upload has
	 * finished).
	 * </p>
	 * <p>
	 * When uploading options from a stream, callers <b>must</b> supply the size
	 * of options in the stream through the content length field in the
	 * <code>ObjectMetadata</code> parameter. If no content length is specified
	 * for the input stream, then TransferManager will attempt to buffer all the
	 * stream contents in memory and upload the options as a traditional, single
	 * part upload. Because the entire stream contents must be buffered in
	 * memory, this can be very expensive, and should be avoided whenever
	 * possible.
	 * </p>
	 * <p>
	 * Use the returned <code>Upload<code> object to query the progress of the
	 * transfer, add listeners for progress events, and wait for the upload to
	 * complete.
	 * </p>
	 * <p>
	 * If resources are available, the upload will begin immediately. Otherwise,
	 * the upload is scheduled and started as soon as resources become
	 * available.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket to upload the new object to.
	 * @param key
	 *            The key in the specified bucket by which to store the new
	 *            object.
	 * @param input
	 *            The input stream containing the options to upload to Amazon
	 *            S3.
	 * @param objectMetadata
	 *            Additional information about the object being uploaded,
	 *            including the size of the options, content type, additional
	 *            custom user metadata, etc.
	 * 
	 * @return A new <code>Upload<code> object to use to check
	 * 		   the state of the upload, listen for progress notifications,
	 * 		   and otherwise manage the upload.
	 * 
	 * @throws AmazonClientException
	 *             If any errors are encountered in the client while making the
	 *             request or handling the response.
	 * @throws AmazonServiceException
	 *             If any errors occurred in Amazon S3 while processing the
	 *             request.
	 */
	public Upload upload(final String bucketName, final String key,
	        final InputStream input, ObjectMetadata objectMetadata)
	        throws AmazonServiceException, AmazonClientException {
		return upload(new PutObjectRequest(bucketName, key, input,
		        objectMetadata));
	}

	/**
	 * Schedules a new transfer to upload data to Amazon S3. This method is
	 * non-blocking and returns immediately (i.e. before the upload has
	 * finished).
	 * <p>
	 * The returned Upload object allows you to query the progress of the
	 * transfer, add listeners for progress events, and wait for the upload to
	 * complete.
	 * <p>
	 * If resources are available, the upload will begin immediately, otherwise
	 * it will be scheduled and started as soon as resources become available.
	 * 
	 * @param bucketName
	 *            The name of the bucket to upload the new object to.
	 * @param key
	 *            The key in the specified bucket by which to store the new
	 *            object.
	 * @param file
	 *            The file to upload.
	 * 
	 * @return A new Upload object which can be used to check state of the
	 *         upload, listen for progress notifications, and otherwise manage
	 *         the upload.
	 * 
	 * @throws AmazonClientException
	 *             If any errors are encountered in the client while making the
	 *             request or handling the response.
	 * @throws AmazonServiceException
	 *             If any errors occurred in Amazon S3 while processing the
	 *             request.
	 */
	public Upload upload(final String bucketName, final String key,
	        final File file) throws AmazonServiceException,
	        AmazonClientException {
		return upload(new PutObjectRequest(bucketName, key, file));
	}

	/**
	 * <p>
	 * Schedules a new transfer to upload data to Amazon S3. This method is
	 * non-blocking and returns immediately (i.e. before the upload has
	 * finished).
	 * </p>
	 * <p>
	 * Use the returned <code>Upload<code> object to query the progress of the
	 * transfer, add listeners for progress events, and wait for the upload to
	 * complete.
	 * </p>
	 * <p>
	 * If resources are available, the upload will begin immediately. Otherwise,
	 * the upload is scheduled and started as soon as resources become
	 * available.
	 * </p>
	 * 
	 * @param putObjectRequest
	 *            The request containing all the parameters for the upload.
	 * 
	 * @return A new <code>Upload<code> object to use to check
	 * 		   the state of the upload, listen for progress notifications,
	 * 		   and otherwise manage the upload.
	 * 
	 * @throws AmazonClientException
	 *             If any errors are encountered in the client while making the
	 *             request or handling the response.
	 * @throws AmazonServiceException
	 *             If any errors occurred in Amazon S3 while processing the
	 *             request.
	 */
	public Upload upload(final PutObjectRequest putObjectRequest)
	        throws AmazonServiceException, AmazonClientException {
		return upload(putObjectRequest, null);
	}

	/**
	 * Same as public version of upload, but attaches a
	 * {@link TransferStateChangeListener} to the upload object so that it can
	 * be monitored.
	 */
	private Upload upload(final PutObjectRequest putObjectRequest,
	        final TransferStateChangeListener stateListener)
	        throws AmazonServiceException, AmazonClientException {

		appendUserAgent(putObjectRequest, USER_AGENT);

		if (putObjectRequest.getMetadata() == null)
			putObjectRequest.setMetadata(new ObjectMetadata());
		ObjectMetadata metadata = putObjectRequest.getMetadata();

		if (TransferManagerUtils.getRequestFile(putObjectRequest) != null) {
			File file = TransferManagerUtils.getRequestFile(putObjectRequest);

			// Always set the content length, even if it's already set
			metadata.setContentLength(file.length());

			// Only set the content type if it hasn't already been set
			if (metadata.getContentType() == null) {
				metadata.setContentType(Mimetypes.getInstance().getMimetype(
				        file));
			}
		}

		String description = "Uploading to " + putObjectRequest.getBucketName()
		        + "/" + putObjectRequest.getKey();
		TransferProgressImpl transferProgress = new TransferProgressImpl();
		transferProgress.setTotalBytesToTransfer(TransferManagerUtils
		        .getContentLength(putObjectRequest));

		ProgressListenerChain listenerChain = new ProgressListenerChain(
		        new TransferProgressUpdatingListener(transferProgress),
		        putObjectRequest.getProgressListener());
		putObjectRequest.setProgressListener(listenerChain);

		UploadImpl upload = new UploadImpl(description, transferProgress,
		        listenerChain, stateListener);

		UploadCallable uploadCallable = new UploadCallable(this, threadPool,
		        upload, putObjectRequest, listenerChain);
		UploadMonitor watcher = new UploadMonitor(this, upload, threadPool,
		        uploadCallable, putObjectRequest, listenerChain);
		watcher.setTimedThreadPool(timedThreadPool);
		upload.setMonitor(watcher);

		return upload;
	}

	/**
	 * Schedules a new transfer to download data from Amazon S3 and save it to
	 * the specified file. This method is non-blocking and returns immediately
	 * (i.e. before the data has been fully downloaded).
	 * <p>
	 * Use the returned Download object to query the progress of the transfer,
	 * add listeners for progress events, and wait for the download to complete.
	 * 
	 * @param bucket
	 *            The name of the bucket containing the object to download.
	 * @param key
	 *            The key under which the object to download is stored.
	 * @param file
	 *            The file to download the object's data to.
	 * 
	 * @return A new <code>Download</code> object to use to check the state of
	 *         the download, listen for progress notifications, and otherwise
	 *         manage the download.
	 * 
	 * @throws AmazonClientException
	 *             If any errors are encountered in the client while making the
	 *             request or handling the response.
	 * @throws AmazonServiceException
	 *             If any errors occurred in Amazon S3 while processing the
	 *             request.
	 */
	public Download download(String bucket, String key, File file) {
		return download(new GetObjectRequest(bucket, key), file);
	}

	/**
	 * Schedules a new transfer to download data from Amazon S3 and save it to
	 * the specified file. This method is non-blocking and returns immediately
	 * (i.e. before the data has been fully downloaded).
	 * <p>
	 * Use the returned Download object to query the progress of the transfer,
	 * add listeners for progress events, and wait for the download to complete.
	 * 
	 * @param getObjectRequest
	 *            The request containing all the parameters for the download.
	 * @param file
	 *            The file to download the object data to.
	 * 
	 * @return A new <code>Download</code> object to use to check the state of
	 *         the download, listen for progress notifications, and otherwise
	 *         manage the download.
	 * 
	 * @throws AmazonClientException
	 *             If any errors are encountered in the client while making the
	 *             request or handling the response.
	 * @throws AmazonServiceException
	 *             If any errors occurred in Amazon S3 while processing the
	 *             request.
	 */
	public Download download(final GetObjectRequest getObjectRequest,
	        final File file) {
		return download(getObjectRequest, file, null);
	}

	/**
	 * Same as public interface, but adds a state listener so that callers can
	 * be notified of state changes to the download.
	 * 
	 * @see TransferManager#download(GetObjectRequest, File)
	 */
	private Download download(final GetObjectRequest getObjectRequest,
	        final File file, final TransferStateChangeListener stateListener) {

		appendUserAgent(getObjectRequest, USER_AGENT);

		String description = "Downloading from "
		        + getObjectRequest.getBucketName() + "/"
		        + getObjectRequest.getKey();

		// Add our own transfer progress listener
		TransferProgressImpl transferProgress = new TransferProgressImpl();
		ProgressListenerChain listenerChain = new ProgressListenerChain(
		        new TransferProgressUpdatingListener(transferProgress),
		        getObjectRequest.getProgressListener());
		getObjectRequest.setProgressListener(listenerChain);
		final ObjectMetadata objectMetadata = s3.getObjectMetadata(
		        getObjectRequest.getBucketName(), getObjectRequest.getKey());

		final StartDownloadLock startDownloadLock = new StartDownloadLock();
		final DownloadImpl download = new DownloadImpl(description,
		        transferProgress, listenerChain, null, stateListener);
		long contentLength = objectMetadata.getContentLength();
		if (getObjectRequest.getRange() != null
		        && getObjectRequest.getRange().length == 2) {
			long startingByte = getObjectRequest.getRange()[0];
			long lastByte = getObjectRequest.getRange()[1];
			contentLength = lastByte - startingByte;
		}

		transferProgress.setTotalBytesToTransfer(contentLength);

		Future<?> future = threadPool.submit(new Callable<Object>() {
			public Object call() throws Exception {
				try {
					synchronized (startDownloadLock) {
						if (!startDownloadLock.downloadReady) {
							try {
								startDownloadLock.wait();
							} catch (InterruptedException e) {
								throw new AmazonClientException(
								        "Couldn't wait for setting future into the monitor");
							}
						}
					}
					download.setState(TransferState.InProgress);
					final StorageObject s3Object = s3.getObject(getObjectRequest);

					download.setS3Object(s3Object);

					if (s3Object == null) {
						download.setState(TransferState.Canceled);
						download.setMonitor(new DownloadMonitor(download, null));
						return download;
					}

					// Don't perform the integrity check if the stream data is
					// wrapped
					// in a decryption stream, or if we're only looking at a
					// range of
					// the data, since otherwise the checksum won't match up.
					boolean performIntegrityCheck = true;
					if (getObjectRequest.getRange() != null)
						performIntegrityCheck = false;
					if (s3 instanceof AmazonS3EncryptionClient)
						performIntegrityCheck = false;

					ServiceUtils.downloadObjectToFile(s3Object, file,
					        performIntegrityCheck);
					download.setState(TransferState.Completed);
					return true;
				} catch (Exception e) {
					// Downloads aren't allowed to move from canceled to failed
					if (download.getState() != TransferState.Canceled) {
						download.setState(TransferState.Failed);
					}
					throw e;
				}
			}
		});
		download.setMonitor(new DownloadMonitor(download, future));
		synchronized (startDownloadLock) {
			startDownloadLock.downloadReady = true;
			startDownloadLock.notify();
		}
		return download;
	}

	/**
	 * Downloads all objects in the virtual directory designated by the
	 * keyPrefix given to the destination directory given. All virtual
	 * subdirectories will be downloaded recursively.
	 * 
	 * @param bucketName
	 *            The bucket containing the virtual directory
	 * @param keyPrefix
	 *            The key prefix for the virtual directory, or null for the
	 *            entire bucket. All subdirectories will be downloaded
	 *            recursively.
	 * @param destinationDirectory
	 *            The directory to place downloaded files. Subdirectories will
	 *            be created as necessary.
	 */
	public MultipleFileDownload downloadDirectory(String bucketName,
	        String keyPrefix, File destinationDirectory) {

		if (keyPrefix == null)
			keyPrefix = "";

		List<StorageObjectSummary> objectSummaries = new LinkedList<StorageObjectSummary>();
		Stack<String> commonPrefixes = new Stack<String>();
		commonPrefixes.add(keyPrefix);
		long totalSize = 0;

		// Recurse all virtual subdirectories to get a list of object summaries.
		// This is a depth-first search.
		do {
			String prefix = commonPrefixes.pop();
			ObjectListing listObjectsResponse = null;

			do {
				if (listObjectsResponse == null) {
					ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					        .withBucketName(bucketName)
					        .withDelimiter(DEFAULT_DELIMITER)
					        .withPrefix(prefix);
					listObjectsResponse = s3.listObjects(listObjectsRequest);
				} else {
					listObjectsResponse = s3
					        .listNextBatchOfObjects(listObjectsResponse);
				}

				for (StorageObjectSummary s : listObjectsResponse
				        .getObjectSummaries()) {
					// Skip any files that are also virtual directories, since
					// we can't save both a directory and a file of the same
					// name.
					if (!s.getKey().equals(prefix)
					        && !listObjectsResponse.getCommonPrefixes()
					                .contains(s.getKey() + DEFAULT_DELIMITER)) {
						objectSummaries.add(s);
						totalSize += s.getSize();
					} else {
						log.debug("Skipping download for object " + s.getKey()
						        + " since it is also a virtual directory");
					}
				}

				commonPrefixes.addAll(listObjectsResponse.getCommonPrefixes());
			} while (listObjectsResponse.isTruncated());
		} while (!commonPrefixes.isEmpty());

		TransferProgressImpl transferProgress = new TransferProgressImpl();
		transferProgress.setTotalBytesToTransfer(totalSize);
		ProgressListener listener = new TransferProgressUpdatingListener(
		        transferProgress);

		List<DownloadImpl> downloads = new ArrayList<DownloadImpl>();

		String description = "Downloading from " + bucketName + "/" + keyPrefix;
		final MultipleFileDownloadImpl multipleFileDownload = new MultipleFileDownloadImpl(
		        description, transferProgress, new ProgressListenerChain(
		                listener), keyPrefix, bucketName, downloads);
		multipleFileDownload.setMonitor(new MultipleFileTransferMonitor(
		        multipleFileDownload, downloads));

		final AllDownloadsQueuedLock allTransfersQueuedLock = new AllDownloadsQueuedLock();
		MultipleFileTransferStateChangeListener stateChangeListener = new MultipleFileTransferStateChangeListener(
		        allTransfersQueuedLock, multipleFileDownload);

		for (StorageObjectSummary summary : objectSummaries) {
			// TODO: non-standard delimiters
			File f = new File(destinationDirectory, summary.getKey());
			File parentFile = f.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new RuntimeException(
				        "Couldn't create parent directories for "
				                + f.getAbsolutePath());
			}

			downloads.add((DownloadImpl) download(
			        new GetObjectRequest(summary.getBucketName(), summary
			                .getKey()).withProgressListener(listener), f,
			        stateChangeListener));
		}

		if (downloads.isEmpty()) {
			multipleFileDownload.setState(TransferState.Completed);
			return multipleFileDownload;
		}

		// Notify all state changes waiting for the downloads to all be queued
		// to wake up and continue.
		synchronized (allTransfersQueuedLock) {
			allTransfersQueuedLock.allQueued = true;
			allTransfersQueuedLock.notifyAll();
		}

		return multipleFileDownload;
	}

	private static final class AllDownloadsQueuedLock {
		private volatile boolean allQueued = false;
	}

	private static final class StartDownloadLock {
		private volatile boolean downloadReady = false;
	}

	private static final class MultipleFileTransferStateChangeListener
	        implements TransferStateChangeListener {

		private final AllDownloadsQueuedLock allTransfersQueuedLock;
		private final MultipleFileTransfer multipleFileTransfer;

		public MultipleFileTransferStateChangeListener(
		        AllDownloadsQueuedLock allTransfersQueuedLock,
		        MultipleFileTransfer multipleFileDownload) {
			this.allTransfersQueuedLock = allTransfersQueuedLock;
			this.multipleFileTransfer = multipleFileDownload;
		}

		public void transferStateChanged(Transfer upload, TransferState state) {

			// There's a race here: we can't start monitoring the state of
			// individual transfers until we have added all the transfers to the
			// list, or we may incorrectly report completion.
			synchronized (allTransfersQueuedLock) {
				if (!allTransfersQueuedLock.allQueued) {
					try {
						allTransfersQueuedLock.wait();
					} catch (InterruptedException e) {
						throw new AmazonClientException(
						        "Couldn't wait for all downloads to be queued");
					}
				}
			}

			synchronized (multipleFileTransfer) {
				if (multipleFileTransfer.getState() == state
				        || multipleFileTransfer.isDone())
					return;

				/*
				 * If we're not already in a terminal state, allow a transition
				 * to a non-waiting state. Mark completed if this download is
				 * completed and the monitor says all of the rest are as well.
				 */
				if (state == TransferState.InProgress) {
					multipleFileTransfer.setState(state);
				} else if (multipleFileTransfer.getMonitor().isDone()) {
					multipleFileTransfer.collateFinalState();
				} else {
					multipleFileTransfer.setState(TransferState.InProgress);
				}
			}
		}
	};

	/**
	 * Uploads all files in the directory given to the bucket named, optionally
	 * recursing for all subdirectories.
	 * <p>
	 * S3 will overwrite any existing objects that happen to have the same key,
	 * just as when uploading individual files, so use with caution.
	 * 
	 * @param bucketName
	 *            The name of the bucket to upload objects to.
	 * @param virtualDirectoryKeyPrefix
	 *            The key prefix of the virtual directory to upload to. Use the
	 *            null or empty string to upload files to the root of the
	 *            bucket.
	 * @param directory
	 *            The directory to upload.
	 * @param includeSubdirectories
	 *            Whether to include subdirectories in the upload. If true,
	 *            files found in subdirectories will be included with an
	 *            appropriate concatenation to the key prefix.
	 */
	public MultipleFileUpload uploadDirectory(String bucketName,
	        String virtualDirectoryKeyPrefix, File directory,
	        boolean includeSubdirectories) {

		if (directory == null || !directory.exists()
		        || !directory.isDirectory()) {
			throw new IllegalArgumentException(
			        "Must provide a directory to upload");
		}

		if (virtualDirectoryKeyPrefix == null
		        || virtualDirectoryKeyPrefix.length() == 0) {
			virtualDirectoryKeyPrefix = "";
		} else if (!virtualDirectoryKeyPrefix.endsWith("/")) {
			virtualDirectoryKeyPrefix = virtualDirectoryKeyPrefix + "/";
		}

		TransferProgressImpl transferProgress = new TransferProgressImpl();
		ProgressListener listener = new TransferProgressUpdatingListener(
		        transferProgress);

		List<UploadImpl> uploads = new LinkedList<UploadImpl>();
		MultipleFileUploadImpl multipleFileUpload = new MultipleFileUploadImpl(
		        "Uploading etc", transferProgress, null,
		        virtualDirectoryKeyPrefix, bucketName, uploads);
		multipleFileUpload.setMonitor(new MultipleFileTransferMonitor(
		        multipleFileUpload, uploads));

		final AllDownloadsQueuedLock allTransfersQueuedLock = new AllDownloadsQueuedLock();
		MultipleFileTransferStateChangeListener stateChangeListener = new MultipleFileTransferStateChangeListener(
		        allTransfersQueuedLock, multipleFileUpload);

		long totalSize = 0;
		List<File> files = new LinkedList<File>();
		listFiles(directory, files, includeSubdirectories);
		if (files.isEmpty()) {
			multipleFileUpload.setState(TransferState.Completed);
		}
		for (File f : files) {
			totalSize += f.length();
			String key = f.getAbsolutePath()
			        .substring(directory.getAbsolutePath().length() + 1)
			        .replaceAll("\\\\", "/");
			uploads.add((UploadImpl) upload(
			        new PutObjectRequest(bucketName, virtualDirectoryKeyPrefix
			                + key, f).withProgressListener(listener),
			        stateChangeListener));
		}

		transferProgress.setTotalBytesToTransfer(totalSize);

		// Notify all state changes waiting for the uploads to all be queued
		// to wake up and continue
		synchronized (allTransfersQueuedLock) {
			allTransfersQueuedLock.allQueued = true;
			allTransfersQueuedLock.notifyAll();
		}

		return multipleFileUpload;
	}

	/**
	 * Lists files in the directory given and adds them to the result list
	 * passed in, optionally adding subdirectories recursively.
	 */
	private void listFiles(File dir, List<File> results,
	        boolean includeSubDirectories) {
		File[] found = dir.listFiles();
		if (found != null) {
			for (File f : found) {
				if (f.isDirectory()) {
					if (includeSubDirectories) {
						listFiles(f, results, includeSubDirectories);
					}
				} else {
					results.add(f);
				}
			}
		}
	}

	/**
	 * <p>
	 * Aborts any multipart uploads that were initiated before the specified
	 * date.
	 * </p>
	 * <p>
	 * This method is useful for cleaning up any interrupted multipart uploads.
	 * <code>TransferManager</code> attempts to abort any failed uploads, but in
	 * some cases this may not be possible, such as if network connectivity is
	 * completely lost.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the multipart uploads to
	 *            abort.
	 * @param date
	 *            The date indicating which multipart uploads should be aborted.
	 */
	public void abortMultipartUploads(String bucketName, Date date)
	        throws AmazonServiceException, AmazonClientException {
		MultipartUploadListing uploadListing = s3
		        .listMultipartUploads(appendUserAgent(
		                new ListMultipartUploadsRequest(bucketName), USER_AGENT));
		do {
			for (MultipartUpload upload : uploadListing.getMultipartUploads()) {
				if (upload.getInitiated().compareTo(date) < 0) {
					s3.abortMultipartUpload(appendUserAgent(
					        new AbortMultipartUploadRequest(bucketName, upload
					                .getKey(), upload.getUploadId()),
					        USER_AGENT));
				}
			}

			ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(
			        bucketName).withUploadIdMarker(
			        uploadListing.getNextUploadIdMarker()).withKeyMarker(
			        uploadListing.getNextKeyMarker());
			uploadListing = s3.listMultipartUploads(appendUserAgent(request,
			        USER_AGENT));
		} while (uploadListing.isTruncated());
	}

	/**
	 * Forcefully shuts down this TransferManager instance - currently executing
	 * transfers will not be allowed to finish. Callers should use this method
	 * when they either:
	 * <ul>
	 * <li>have already verified that their transfers have completed by checking
	 * each transfer's state
	 * <li>need to exit quickly and don't mind stopping transfers before they
	 * complete.
	 * </ul>
	 * <p>
	 * Callers should also remember that uploaded parts from an interrupted
	 * upload may not always be automatically cleaned up, but callers can use
	 * {@link #abortMultipartUploads(String, Date)} to clean up any upload
	 * parts.
	 */
	public void shutdownNow() {
		threadPool.shutdownNow();
		timedThreadPool.shutdownNow();

		if (s3 instanceof LunacloudStorageClient) {
			((LunacloudStorageClient) s3).shutdown();
		}
	}

	public <X extends AmazonWebServiceRequest> X appendUserAgent(X request,
	        String userAgent) {
		request.getRequestClientOptions().addClientMarker(USER_AGENT);
		return request;
	}

	private static final String USER_AGENT = TransferManager.class.getName()
	        + "/" + VersionInfoUtils.getVersion();

	private static final String DEFAULT_DELIMITER = "/";
}
