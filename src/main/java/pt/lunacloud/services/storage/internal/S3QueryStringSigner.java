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

import java.util.Date;

import pt.lunacloud.LunacloudClientException;
import pt.lunacloud.Request;
import pt.lunacloud.auth.LunacloudCredentials;
import pt.lunacloud.auth.AWSSessionCredentials;
import pt.lunacloud.auth.AbstractAWSSigner;
import pt.lunacloud.auth.SigningAlgorithm;


public class S3QueryStringSigner<T> extends AbstractAWSSigner {

    /**
     * The HTTP verb (GET, PUT, HEAD, DELETE) the request to sign
     * is using.
     *
     * TODO: We need to know the HTTP verb in order to
     *       create the authentication signature, but we don't
     *       have easy access to it through the request object.
     *
     *       Maybe it'd be better for the S3 signer (or all signers?)
     *       to work directly off of the HttpRequest instead of
     *       the Request object?
     */
    private final String httpVerb;

    /**
     * The canonical resource path portion of the S3 string to sign.
     * Examples: "/", "/<bucket name>/", or "/<bucket name>/<key>"
     *
     * TODO: We don't want to hold the resource path as member data in the S3
     *       signer, but we need access to it and can't get it through the
     *       request yet.
     */
    private final String resourcePath;

    private final Date expiration;

    public S3QueryStringSigner(String httpVerb, String resourcePath, Date expiration) {
        this.httpVerb = httpVerb;
        this.resourcePath = resourcePath;
        this.expiration = expiration;

        if (resourcePath == null)
            throw new IllegalArgumentException("Parameter resourcePath is empty");
    }

    public void sign(Request<?> request, LunacloudCredentials credentials) throws LunacloudClientException {
        LunacloudCredentials sanitizedCredentials = sanitizeCredentials(credentials);

        if ( sanitizedCredentials instanceof AWSSessionCredentials ) {
            addSessionCredentials(request, (AWSSessionCredentials) sanitizedCredentials);
        }
        
        String expirationInSeconds = Long.toString(expiration.getTime() / 1000L);

        String canonicalString = RestUtils.makeS3CanonicalString(
                httpVerb, resourcePath, request, expirationInSeconds);
        
        String signature = super.signAndBase64Encode(canonicalString, sanitizedCredentials.getLunacloudSecretKey(), SigningAlgorithm.HmacSHA1);

        request.addParameter("AWSAccessKeyId", sanitizedCredentials.getLunacloudAccessKeyId());
        request.addParameter("Expires", expirationInSeconds);
        request.addParameter("Signature", signature);
    }

    @Override
    protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials) {
        request.addParameter("x-amz-security-token", credentials.getSessionToken());
    }
}
