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
package pt.lunacloud.services.s3.transfer.internal;

import java.util.concurrent.Callable;

import pt.lunacloud.services.s3.LunacloudStorage;
import pt.lunacloud.services.s3.model.PartETag;
import pt.lunacloud.services.s3.model.UploadPartRequest;


public class UploadPartCallable implements Callable<PartETag> {
    private final LunacloudStorage s3;
    private final UploadPartRequest request;

    public UploadPartCallable(LunacloudStorage s3, UploadPartRequest request) {
        this.s3 = s3;
        this.request = request;
    }

    public PartETag call() throws Exception {
        return s3.uploadPart(request).getPartETag();
    }
}
