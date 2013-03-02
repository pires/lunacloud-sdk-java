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
package pt.lunacloud.services.rds.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.rds.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Create D B Snapshot Request Marshaller
 */
public class CreateDBSnapshotRequestMarshaller implements Marshaller<Request<CreateDBSnapshotRequest>, CreateDBSnapshotRequest> {

    public Request<CreateDBSnapshotRequest> marshall(CreateDBSnapshotRequest createDBSnapshotRequest) {

        if (createDBSnapshotRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<CreateDBSnapshotRequest> request = new DefaultRequest<CreateDBSnapshotRequest>(createDBSnapshotRequest, "AmazonRDS");
        request.addParameter("Action", "CreateDBSnapshot");
        request.addParameter("Version", "2013-01-10");

        if (createDBSnapshotRequest.getDBSnapshotIdentifier() != null) {
            request.addParameter("DBSnapshotIdentifier", StringUtils.fromString(createDBSnapshotRequest.getDBSnapshotIdentifier()));
        }
        if (createDBSnapshotRequest.getDBInstanceIdentifier() != null) {
            request.addParameter("DBInstanceIdentifier", StringUtils.fromString(createDBSnapshotRequest.getDBInstanceIdentifier()));
        }


        return request;
    }
}
