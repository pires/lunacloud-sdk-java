/*
 * Copyright 2011-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package pt.lunacloud.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import pt.lunacloud.LunacloudClientException;
import pt.lunacloud.LunacloudServiceException;
import pt.lunacloud.LunacloudServiceException.ErrorType;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.util.json.JSONObject;


public class JsonErrorResponseHandler implements HttpResponseHandler<LunacloudServiceException> {

    /**
     * The list of error response unmarshallers to try to apply to error
     * responses.
     */
    private List<Unmarshaller<LunacloudServiceException, JSONObject>> unmarshallerList;

    public JsonErrorResponseHandler(List<Unmarshaller<LunacloudServiceException, JSONObject>> exceptionUnmarshallers) {
        this.unmarshallerList = exceptionUnmarshallers;
    }

    public LunacloudServiceException handle(HttpResponse response) throws Exception {
        String streamContents = readStreamContents(response.getContent());
        JSONObject jsonErrorMessage;
        try {
            String s = streamContents;
            if (s.length() == 0 || s.trim().length() == 0) s = "{}";
            jsonErrorMessage = new JSONObject(s);
        } catch (Exception e) {
            throw new LunacloudClientException("Unable to parse error response: '" + streamContents + "'", e);
        }

        LunacloudServiceException ase = runErrorUnmarshallers(response, jsonErrorMessage);
        if (ase == null) return null;

        ase.setServiceName(response.getRequest().getServiceName());
        ase.setStatusCode(response.getStatusCode());
        if (response.getStatusCode() < 500) {
            ase.setErrorType(ErrorType.Client);
        } else {
            ase.setErrorType(ErrorType.Service);
        }

        for (Entry<String, String> headerEntry : response.getHeaders().entrySet()) {
            if (headerEntry.getKey().equalsIgnoreCase("X-Amzn-RequestId")) {
                ase.setRequestId(headerEntry.getValue());
            }
        }

        return ase;
    }

    protected LunacloudServiceException runErrorUnmarshallers(HttpResponse errorResponse, JSONObject json) throws Exception {
        /*
         * We need to select which exception unmarshaller is the correct one to
         * use from all the possible exceptions this operation can throw.
         * Currently we rely on the unmarshallers to return null if they can't
         * unmarshall the response, but we might need something a little more
         * sophisticated in the future.
         */
        for (Unmarshaller<LunacloudServiceException, JSONObject> unmarshaller : unmarshallerList) {
            LunacloudServiceException ase = unmarshaller.unmarshall(json);
            if (ase != null) {
                ase.setStatusCode(errorResponse.getStatusCode());
                return ase;
            }
        }

        return null;
    }

    public boolean needsConnectionLeftOpen() {
        return false;
    }

    private String readStreamContents(final InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line);
            }

            return sb.toString();
        } catch (Exception e) {
            try {stream.close();} catch (Exception ex) {}
            throw new LunacloudClientException("Unable to read error response: " + e.getMessage(), e);
        }
    }

}
