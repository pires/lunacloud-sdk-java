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
package pt.lunacloud.services.elasticache.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.elasticache.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Create Cache Parameter Group Request Marshaller
 */
public class CreateCacheParameterGroupRequestMarshaller implements Marshaller<Request<CreateCacheParameterGroupRequest>, CreateCacheParameterGroupRequest> {

    public Request<CreateCacheParameterGroupRequest> marshall(CreateCacheParameterGroupRequest createCacheParameterGroupRequest) {

        if (createCacheParameterGroupRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<CreateCacheParameterGroupRequest> request = new DefaultRequest<CreateCacheParameterGroupRequest>(createCacheParameterGroupRequest, "AmazonElastiCache");
        request.addParameter("Action", "CreateCacheParameterGroup");
        request.addParameter("Version", "2012-11-15");

        if (createCacheParameterGroupRequest.getCacheParameterGroupName() != null) {
            request.addParameter("CacheParameterGroupName", StringUtils.fromString(createCacheParameterGroupRequest.getCacheParameterGroupName()));
        }
        if (createCacheParameterGroupRequest.getCacheParameterGroupFamily() != null) {
            request.addParameter("CacheParameterGroupFamily", StringUtils.fromString(createCacheParameterGroupRequest.getCacheParameterGroupFamily()));
        }
        if (createCacheParameterGroupRequest.getDescription() != null) {
            request.addParameter("Description", StringUtils.fromString(createCacheParameterGroupRequest.getDescription()));
        }


        return request;
    }
}
