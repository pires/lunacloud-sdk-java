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
package pt.lunacloud.services.storage.internal;


import pt.lunacloud.AmazonWebServiceResponse;
import pt.lunacloud.auth.AWSRefreshableSessionCredentials;
import pt.lunacloud.http.HttpResponse;
import pt.lunacloud.services.storage.Headers;
import pt.lunacloud.services.storage.model.ObjectMetadata;
import pt.lunacloud.services.storage.model.StorageObject;
import pt.lunacloud.services.storage.model.StorageObjectInputStream;
import pt.lunacloud.util.BinaryUtils;

/**
 * S3 HTTP response handler that knows how to pull S3 object content and
 * metadata out of an HTTP response and unmarshall it into an S3Object object.
 */
public class S3ObjectResponseHandler extends AbstractS3ResponseHandler<StorageObject> {

    /**
     * @see pt.lunacloud.http.HttpResponseHandler#handle(pt.lunacloud.http.HttpResponse)
     */
    public AmazonWebServiceResponse<StorageObject> handle(HttpResponse response) throws Exception {
        /*
         * TODO: It'd be nice to set the bucket name and key here, but the
         *       information isn't easy to pull out of the response/request
         *       currently.
         */
        StorageObject object = new StorageObject();
        AmazonWebServiceResponse<StorageObject> awsResponse = parseResponseMetadata(response);
        if (response.getHeaders().get(Headers.REDIRECT_LOCATION) != null) {
            object.setRedirectLocation(response.getHeaders().get(Headers.REDIRECT_LOCATION));
        }
        ObjectMetadata metadata = object.getObjectMetadata();
        populateObjectMetadata(response, metadata);
        boolean hasServerSideCalculatedChecksum = !ServiceUtils.isMultipartUploadETag(metadata.getETag());
        boolean responseContainsEntireObject = response.getHeaders().get("Content-Range") == null;

        if (hasServerSideCalculatedChecksum && responseContainsEntireObject) {
            byte[] expectedChecksum = BinaryUtils.fromHex(metadata.getETag());
            object.setObjectContent(new StorageObjectInputStream(new ChecksumValidatingInputStream(response.getContent(), expectedChecksum, object.getBucketName() + "/" + object.getKey()), response
                    .getHttpRequest()));
        } else {
            object.setObjectContent(new StorageObjectInputStream(response.getContent(), response.getHttpRequest()));
        }

        awsResponse.setResult(object);
        return awsResponse;
    }

    /**
     * Returns true, since the entire response isn't read while this response
     * handler handles the response. This enables us to keep the underlying HTTP
     * connection open, so that the caller can stream it off.
     *
     * @see pt.lunacloud.http.HttpResponseHandler#needsConnectionLeftOpen()
     */
    @Override
    public boolean needsConnectionLeftOpen() {
        return true;
    }

}
