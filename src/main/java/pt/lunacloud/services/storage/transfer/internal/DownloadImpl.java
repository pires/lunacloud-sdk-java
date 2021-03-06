/*
 * Copyright 2012-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package pt.lunacloud.services.storage.transfer.internal;

import java.io.IOException;

import pt.lunacloud.services.storage.model.ObjectMetadata;
import pt.lunacloud.services.storage.model.StorageObject;
import pt.lunacloud.services.storage.transfer.Download;
import pt.lunacloud.services.storage.transfer.TransferProgress;


public class DownloadImpl extends AbstractTransfer implements Download {
    
    StorageObject s3Object;

    public DownloadImpl(String description, TransferProgress transferProgress,
            ProgressListenerChain progressListenerChain, StorageObject s3Object, TransferStateChangeListener listener) {
        super(description, transferProgress, progressListenerChain, listener);
        this.s3Object = s3Object;
    }
    
    /**
     * Returns the ObjectMetadata for the object being downloaded.
     *
     * @return The ObjectMetadata for the object being downloaded.
     */
    public ObjectMetadata getObjectMetadata() {
        return s3Object.getObjectMetadata();
    }

    /**
     * The name of the bucket where the object is being downloaded from.
     *
     * @return The name of the bucket where the object is being downloaded from.
     */
    public String getBucketName() {
        return s3Object.getBucketName();
    }

    /**
     * The key under which this object was stored in Amazon S3.
     *
     * @return The key under which this object was stored in Amazon S3.
     */
    public String getKey() {
        return s3Object.getKey();
    }

    /**
     * Cancels this download.
     *
     * @throws IOException
     */
    public synchronized void abort() throws IOException {
    	
    	this.monitor.getFuture().cancel(true);
    	
    	  if ( s3Object != null ) {
              s3Object.getObjectContent().abort();
    	      }
        setState(TransferState.Canceled);
    }
    
    /**
     *  Set the S3 object to download.
     */
    public synchronized void setS3Object(StorageObject s3Object) {
    	this.s3Object = s3Object;
    }


    
}