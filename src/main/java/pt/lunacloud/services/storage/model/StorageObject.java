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
 * 
 * Portions copyright 2006-2009 James Murty. Please see LICENSE.txt 
 * for applicable license terms and NOTICE.txt for applicable notices. 
 */
package pt.lunacloud.services.storage.model;

import java.io.InputStream;

import pt.lunacloud.services.storage.LunacloudStorage;

/**
 * Represents an object stored in Lunacloud Storage. This object contains the
 * data content and the object metadata stored by Lunacloud Storage, such as
 * content type, content length, etc.
 * 
 * @see ObjectMetadata
 */
public class StorageObject {
	/** The key under which this object is stored */
	private String key = null;

	/** The name of the bucket in which this object is contained */
	private String bucketName = null;

	/** The metadata stored by Lunacloud Storage for this object */
	private ObjectMetadata metadata = new ObjectMetadata();

	/** The stream containing the contents of this object from Storage */
	private StorageObjectInputStream objectContent;

	/** The redirect location for this object */
	private String redirectLocation;

	/**
	 * Gets the metadata stored by Lunacloud Storage for this object. The
	 * {@link ObjectMetadata} object includes any custom user metadata supplied
	 * by the caller when the object was uploaded, as well as HTTP metadata such
	 * as content length and content type.
	 * 
	 * @return The metadata stored by Lunacloud Storage for this object.
	 * 
	 * @see StorageObject#getObjectContent()
	 */
	public ObjectMetadata getObjectMetadata() {
		return metadata;
	}

	/**
	 * Sets the object metadata for this object.
	 * <p>
	 * <b>NOTE:</b> This does not update the object metadata stored in Lunacloud
	 * Storage, but only updates this object in local memory. To update an
	 * object's metadata in S3, use
	 * {@link LunacloudStorage#copyObject(CopyObjectRequest)} to copy the object
	 * to a new (or the same location) and specify new object metadata then.
	 * 
	 * @param metadata
	 *            The new metadata to set for this object in memory.
	 */
	public void setObjectMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Gets an input stream containing the contents of this object. Callers
	 * should close this input stream as soon as possible, because the object
	 * contents aren't buffered in memory and stream directly from Lunacloud
	 * Storage.
	 * 
	 * @return An input stream containing the contents of this object.
	 * 
	 * @see StorageObject#getObjectMetadata()
	 * @see StorageObject#setObjectContent(InputStream)
	 */
	public StorageObjectInputStream getObjectContent() {
		return objectContent;
	}

	/**
	 * Sets the input stream containing this object's contents.
	 * 
	 * @param objectContent
	 *            The input stream containing this object's contents.
	 * 
	 * @see StorageObject#getObjectContent()
	 */
	public void setObjectContent(StorageObjectInputStream objectContent) {
		this.objectContent = objectContent;
	}

	/**
	 * Sets the input stream containing this object's contents.
	 * 
	 * @param objectContent
	 *            The input stream containing this object's contents. Will get
	 *            wrapped in an {@link StorageObjectInputStream}.
	 * @see StorageObject#getObjectContent()
	 */
	public void setObjectContent(InputStream objectContent) {
		setObjectContent(new StorageObjectInputStream(objectContent,
		        this.objectContent != null ? this.objectContent
		                .getHttpRequest() : null));
	}

	/**
	 * Gets the name of the bucket in which this object is contained.
	 * 
	 * @return The name of the bucket in which this object is contained.
	 * 
	 * @see StorageObject#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket in which this object is contained.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing this object.
	 * 
	 * @see StorageObject#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the key under which this object is stored.
	 * 
	 * @return The key under which this object is stored.
	 * 
	 * @see StorageObject#setKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which this object is stored.
	 * 
	 * @param key
	 *            The key under which this object is stored.
	 * 
	 * @see StorageObject#getKey()
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the redirect location for this object.
	 * 
	 */
	public String getRedirectLocation() {
		return this.redirectLocation;
	}

	/**
	 * Sets the redirect location for this object.
	 * 
	 * @param redirectLocation
	 *            the redirect location for that object.
	 */
	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "StorageObject [key=" + getKey() + ",bucket="
		        + (bucketName == null ? "<Unknown>" : bucketName) + "]";
	}

}