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
 * Detach Load Balancer From Subnets Request Marshaller
 */
public class DetachLoadBalancerFromSubnetsRequestMarshaller implements Marshaller<Request<DetachLoadBalancerFromSubnetsRequest>, DetachLoadBalancerFromSubnetsRequest> {

    public Request<DetachLoadBalancerFromSubnetsRequest> marshall(DetachLoadBalancerFromSubnetsRequest detachLoadBalancerFromSubnetsRequest) {

        if (detachLoadBalancerFromSubnetsRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<DetachLoadBalancerFromSubnetsRequest> request = new DefaultRequest<DetachLoadBalancerFromSubnetsRequest>(detachLoadBalancerFromSubnetsRequest, "AmazonElasticLoadBalancing");
        request.addParameter("Action", "DetachLoadBalancerFromSubnets");
        request.addParameter("Version", "2012-06-01");

        if (detachLoadBalancerFromSubnetsRequest.getLoadBalancerName() != null) {
            request.addParameter("LoadBalancerName", StringUtils.fromString(detachLoadBalancerFromSubnetsRequest.getLoadBalancerName()));
        }

        java.util.List<String> subnetsList = detachLoadBalancerFromSubnetsRequest.getSubnets();
        int subnetsListIndex = 1;

        for (String subnetsListValue : subnetsList) {
            if (subnetsListValue != null) {
                request.addParameter("Subnets.member." + subnetsListIndex, StringUtils.fromString(subnetsListValue));
            }

            subnetsListIndex++;
        }


        return request;
    }
}
