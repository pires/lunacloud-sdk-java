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
package pt.lunacloud.services.storage.model;

import java.util.Date;

/**
 * Contains the summary of an object stored in an Lunacloud Storage bucket. This
 * object doesn't contain contain the object's full metadata or any of its
 * contents.
 * 
 * @see StorageObject
 */
public class StorageObjectSummary {

	/** The name of the bucket in which this object is stored */
	protected String bucketName;

	/** The key under which this object is stored */
	protected String key;

	/**
	 * Hex encoded MD5 hash of this object's contents, as computed by Lunacloud
	 * Storage
	 */
	protected String eTag;

	/** The size of this object, in bytes */
	protected long size;

	/**
	 * The date, according to Lunacloud Storage, when this object was last
	 * modified
	 */
	protected Date lastModified;

	/** The class of storage used by Lunacloud Storage to store this object */
	protected String storageClass;

	/**
	 * The owner of this object - can be null if the requester doesn't have
	 * permission to view object ownership information
	 */
	protected Owner owner;

	/**
	 * Gets the name of the Lunacloud Storage bucket in which this object is
	 * stored.
	 * 
	 * @return The name of the Lunacloud Storage bucket in which this object is
	 *         stored.
	 * 
	 * @see StorageObjectSummary#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the Lunacloud Storage bucket in which this object is
	 * stored.
	 * 
	 * @param bucketName
	 *            The name of the Lunacloud Storage bucket in which this object
	 *            is stored.
	 * 
	 * @see StorageObjectSummary#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the key under which this object is stored in Lunacloud Storage.
	 * 
	 * @return The key under which this object is stored in Lunacloud Storage.
	 * 
	 * @see StorageObjectSummary#setKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which this object is stored in Lunacloud Storage.
	 * 
	 * @param key
	 *            The key under which this object is stored in Lunacloud
	 *            Storage.
	 * 
	 * @see StorageObjectSummary#getKey()
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the hex encoded 128-bit MD5 hash of this object's contents as
	 * computed by Lunacloud Storage.
	 * 
	 * @return The hex encoded 128-bit MD5 hash of this object's contents as
	 *         computed by Lunacloud Storage.
	 * 
	 * @see StorageObjectSummary#setETag(String)
	 */
	public String getETag() {
		return eTag;
	}

	/**
	 * Sets the hex encoded 128-bit MD5 hash of this object's contents as
	 * computed by Lunacloud Storage.
	 * 
	 * @param eTag
	 *            The hex encoded 128-bit MD5 hash of this object's contents as
	 *            computed by Lunacloud Storage.
	 * 
	 * @see StorageObjectSummary#getETag()
	 */
	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	/**
	 * Gets the size of this object in bytes.
	 * 
	 * @return The size of this object in bytes.
	 * 
	 * @see 3ObjectSummary#setSize(long)
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the size of this object in bytes.
	 * 
	 * @param size
	 *            The size of this object in bytes.
	 * 
	 * @see StorageObjectSummary#getSize()
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Gets the date when, according to Lunacloud Storage, this object was last
	 * modified.
	 * 
	 * @return The date when, according to Lunacloud Storage, this object was
	 *         last modified.
	 * 
	 * @see StorageObjectSummary#setLastModified(Date)
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the date, according to Lunacloud Storage, this object was last
	 * modified.
	 * 
	 * @param lastModified
	 *            The date when, according to Lunacloud Storage, this object was
	 *            last modified.
	 * 
	 * @see StorageObjectSummary#getLastModified()
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * Gets the owner of this object. Returns <code>null</code> if the requester
	 * doesn't have {@link Permission#ReadAcp} permission for this object or
	 * owns the bucket in which it resides.
	 * 
	 * @return The owner of this object. Returns <code>null</code> if the
	 *         requester doesn't have permission to see object ownership.
	 * 
	 * @see StorageObjectSummary#setOwner(Owner)
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of this object.
	 * 
	 * @param owner
	 *            The owner of this object.
	 * 
	 * @see StorageObjectSummary#getOwner()
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * Gets the storage class used by Lunacloud Storage for this object.
	 * 
	 * @return The storage class used by Lunacloud Storage for this object.
	 * 
	 * @see StorageObjectSummary#setStorageClass(String)
	 */
	public String getStorageClass() {
		return storageClass;
	}

	/**
	 * Sets the storage class used by Lunacloud Storage for this object.
	 * 
	 * @param storageClass
	 *            The storage class used by Lunacloud Storage for this object.
	 * 
	 * @see StorageObjectSummary#getStorageClass()
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

}