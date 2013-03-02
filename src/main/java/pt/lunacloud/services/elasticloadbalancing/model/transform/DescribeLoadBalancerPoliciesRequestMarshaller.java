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
package pt.lunacloud.services.elasticloadbalancing.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.elasticloadbalancing.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Describe Load Balancer Policies Request Marshaller
 */
public class DescribeLoadBalancerPoliciesRequestMarshaller implements Marshaller<Request<DescribeLoadBalancerPoliciesRequest>, DescribeLoadBalancerPoliciesRequest> {

    public Request<DescribeLoadBalancerPoliciesRequest> marshall(DescribeLoadBalancerPoliciesRequest describeLoadBalancerPoliciesRequest) {

        if (describeLoadBalancerPoliciesRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<DescribeLoadBalancerPoliciesRequest> request = new DefaultRequest<DescribeLoadBalancerPoliciesRequest>(describeLoadBalancerPoliciesRequest, "AmazonElasticLoadBalancing");
        request.addParameter("Action", "DescribeLoadBalancerPolicies");
        request.addParameter("Version", "2012-06-01");

        if (describeLoadBalancerPoliciesRequest.getLoadBalancerName() != null) {
            request.addParameter("LoadBalancerName", StringUtils.fromString(describeLoadBalancerPoliciesRequest.getLoadBalancerName()));
        }

        java.util.List<String> policyNamesList = describeLoadBalancerPoliciesRequest.getPolicyNames();
        int policyNamesListIndex = 1;

        for (String policyNamesListValue : policyNamesList) {
            if (policyNamesListValue != null) {
                request.addParameter("PolicyNames.member." + policyNamesListIndex, StringUtils.fromString(policyNamesListValue));
            }

            policyNamesListIndex++;
        }


        return request;
    }
}
