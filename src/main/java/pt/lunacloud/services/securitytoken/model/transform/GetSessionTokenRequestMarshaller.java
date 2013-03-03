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
package pt.lunacloud.services.securitytoken.model.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lunacloud.LunacloudClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.services.securitytoken.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringUtils;


/**
 * Get Session Token Request Marshaller
 */
public class GetSessionTokenRequestMarshaller implements Marshaller<Request<GetSessionTokenRequest>, GetSessionTokenRequest> {

    public Request<GetSessionTokenRequest> marshall(GetSessionTokenRequest getSessionTokenRequest) {

        if (getSessionTokenRequest == null) {
		    throw new LunacloudClientException("Invalid argument passed to marshall(...)");
		}

        Request<GetSessionTokenRequest> request = new DefaultRequest<GetSessionTokenRequest>(getSessionTokenRequest, "AWSSecurityTokenService");
        request.addParameter("Action", "GetSessionToken");
        request.addParameter("Version", "2011-06-15");

        if (getSessionTokenRequest.getDurationSeconds() != null) {
            request.addParameter("DurationSeconds", StringUtils.fromInteger(getSessionTokenRequest.getDurationSeconds()));
        }
        if (getSessionTokenRequest.getSerialNumber() != null) {
            request.addParameter("SerialNumber", StringUtils.fromString(getSessionTokenRequest.getSerialNumber()));
        }
        if (getSessionTokenRequest.getTokenCode() != null) {
            request.addParameter("TokenCode", StringUtils.fromString(getSessionTokenRequest.getTokenCode()));
        }


        return request;
    }
}
