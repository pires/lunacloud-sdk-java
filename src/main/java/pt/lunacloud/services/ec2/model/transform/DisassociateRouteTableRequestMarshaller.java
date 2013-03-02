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
package pt.lunacloud.services.ec2.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.ec2.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Disassociate Route Table Request Marshaller
 */
public class DisassociateRouteTableRequestMarshaller implements Marshaller<Request<DisassociateRouteTableRequest>, DisassociateRouteTableRequest> {

    public Request<DisassociateRouteTableRequest> marshall(DisassociateRouteTableRequest disassociateRouteTableRequest) {

        if (disassociateRouteTableRequest == null) {
		    throw new AmazonClientException("Invalid argument passed to marshall(...)");
		}

        Request<DisassociateRouteTableRequest> request = new DefaultRequest<DisassociateRouteTableRequest>(disassociateRouteTableRequest, "AmazonEC2");
        request.addParameter("Action", "DisassociateRouteTable");
        request.addParameter("Version", "2012-12-01");

        if (disassociateRouteTableRequest.getAssociationId() != null) {
            request.addParameter("AssociationId", StringUtils.fromString(disassociateRouteTableRequest.getAssociationId()));
        }


        return request;
    }
}
