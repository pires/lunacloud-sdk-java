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
package pt.lunacloud.services.redshift.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.redshift.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Describe Cluster Parameters Request Marshaller
 */
public class DescribeClusterParametersRequestMarshaller implements Marshaller<Request<DescribeClusterParametersRequest>, DescribeClusterParametersRequest> {

    public Request<DescribeClusterParametersRequest> marshall(DescribeClusterParametersRequest describeClusterParametersRequest) {

        if (describeClusterParametersRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<DescribeClusterParametersRequest> request = new DefaultRequest<DescribeClusterParametersRequest>(describeClusterParametersRequest, "AmazonRedshift");
        request.addParameter("Action", "DescribeClusterParameters");
        request.addParameter("Version", "2012-12-01");

        if (describeClusterParametersRequest.getParameterGroupName() != null) {
            request.addParameter("ParameterGroupName", StringUtils.fromString(describeClusterParametersRequest.getParameterGroupName()));
        }
        if (describeClusterParametersRequest.getSource() != null) {
            request.addParameter("Source", StringUtils.fromString(describeClusterParametersRequest.getSource()));
        }
        if (describeClusterParametersRequest.getMaxRecords() != null) {
            request.addParameter("MaxRecords", StringUtils.fromInteger(describeClusterParametersRequest.getMaxRecords()));
        }
        if (describeClusterParametersRequest.getMarker() != null) {
            request.addParameter("Marker", StringUtils.fromString(describeClusterParametersRequest.getMarker()));
        }


        return request;
    }
}
