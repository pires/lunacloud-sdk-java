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
package pt.lunacloud.services.opsworks.model.transform;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.List;

import pt.lunacloud.AmazonClientException;
import pt.lunacloud.DefaultRequest;
import pt.lunacloud.Request;
import pt.lunacloud.http.HttpMethodName;
import pt.lunacloud.services.opsworks.model.*;
import pt.lunacloud.transform.Marshaller;
import pt.lunacloud.util.StringInputStream;
import pt.lunacloud.util.StringUtils;
import pt.lunacloud.util.json.*;


/**
 * Describe Service Errors Request Marshaller
 */
public class DescribeServiceErrorsRequestMarshaller implements Marshaller<Request<DescribeServiceErrorsRequest>, DescribeServiceErrorsRequest> {

    

    public Request<DescribeServiceErrorsRequest> marshall(DescribeServiceErrorsRequest describeServiceErrorsRequest) {
    if (describeServiceErrorsRequest == null) {
        throw new AmazonClientException("Invalid argument passed to marshall(...)");
    }

        Request<DescribeServiceErrorsRequest> request = new DefaultRequest<DescribeServiceErrorsRequest>(describeServiceErrorsRequest, "AWSOpsWorks");
        String target = "OpsWorks_20130218.DescribeServiceErrors";
        request.addHeader("X-Amz-Target", target);
        request.addHeader("Content-Type", "application/x-amz-json-1.1");

        
        request.setHttpMethod(HttpMethodName.POST);


        String uriResourcePath = ""; 

        uriResourcePath = uriResourcePath.replaceAll("//", "/");

        if (uriResourcePath.contains("?")) {
            String queryString = uriResourcePath.substring(uriResourcePath.indexOf("?") + 1);
            uriResourcePath    = uriResourcePath.substring(0, uriResourcePath.indexOf("?"));

            for (String s : queryString.split("[;&]")) {
                String[] nameValuePair = s.split("=");
                if (nameValuePair.length == 2) {
                    request.addParameter(nameValuePair[0], nameValuePair[1]);
                } else {
                    request.addParameter(s, null);
                }
            }
        }

        request.setResourcePath(uriResourcePath);


        
        try {
          StringWriter stringWriter = new StringWriter();
          JSONWriter jsonWriter = new JSONWriter(stringWriter);

          
            
          jsonWriter.object();
          
            if (describeServiceErrorsRequest.getStackId() != null) {
                jsonWriter.key("StackId").value(describeServiceErrorsRequest.getStackId());
            }
            if (describeServiceErrorsRequest.getInstanceId() != null) {
                jsonWriter.key("InstanceId").value(describeServiceErrorsRequest.getInstanceId());
            }

            java.util.List<String> serviceErrorIdsList = describeServiceErrorsRequest.getServiceErrorIds();
            if (serviceErrorIdsList != null && serviceErrorIdsList.size() > 0) {

                jsonWriter.key("ServiceErrorIds");
                jsonWriter.array();

                for (String serviceErrorIdsListValue : serviceErrorIdsList) {
                    if (serviceErrorIdsListValue != null) {
                        jsonWriter.value(serviceErrorIdsListValue);
                    }
                }
                jsonWriter.endArray();
            }

          jsonWriter.endObject();
          

          String snippet = stringWriter.toString();
          byte[] content = snippet.getBytes("UTF-8");
          request.setContent(new StringInputStream(snippet));
          request.addHeader("Content-Length", Integer.toString(content.length));
        } catch(Throwable t) {
          throw new AmazonClientException("Unable to marshall request to JSON: " + t.getMessage(), t);
        }
        

        return request;
    }

    private String getString(String s) {
        if (s == null) return "";
        return s;
    }
}
