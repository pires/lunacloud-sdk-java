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
package pt.lunacloud.http;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import pt.lunacloud.LunacloudClientException;
import pt.lunacloud.LunacloudServiceException;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.util.XpathUtils;


/**
 * Implementation of HttpResponseHandler that handles only error responses from
 * Amazon Web Services. A list of unmarshallers is passed into the constructor,
 * and while handling a response, each unmarshaller is tried, in order, until
 * one is found that can successfully unmarshall the error response.  If no
 * unmarshaller is found that can unmarshall the error response, a generic
 * AmazonServiceException is created and populated with the AWS error response
 * information (error message, AWS error code, AWS request ID, etc).
 */
public class DefaultErrorResponseHandler
        implements HttpResponseHandler<LunacloudServiceException> {

    /**
     * The list of error response unmarshallers to try to apply to error
     * responses.
     */
    private List<Unmarshaller<LunacloudServiceException, Node>> unmarshallerList;

    /**
     * Constructs a new DefaultErrorResponseHandler that will handle error
     * responses from Amazon services using the specified list of unmarshallers.
     * Each unmarshaller will be tried, in order, until one is found that can
     * unmarshall the error response.
     *
     * @param unmarshallerList
     *            The list of unmarshallers to try using when handling an error
     *            response.
     */
    public DefaultErrorResponseHandler(
            List<Unmarshaller<LunacloudServiceException, Node>> unmarshallerList) {
        this.unmarshallerList = unmarshallerList;
    }

    /* (non-Javadoc)
     * @see com.amazonaws.http.HttpResponseHandler#handle(com.amazonaws.http.HttpResponse)
     */
    public LunacloudServiceException handle(HttpResponse errorResponse)
            throws Exception {
        Document document;
        try {
            document = XpathUtils.documentFrom(errorResponse.getContent());
        } catch (SAXParseException e) {
            LunacloudServiceException exception =
                new LunacloudServiceException(String.format("Unable to unmarshall error response (%s)", e.getMessage()), e);
            exception.setErrorCode(String.format("%s %s", errorResponse.getStatusCode(), errorResponse.getStatusText()));
            exception.setErrorType(LunacloudServiceException.ErrorType.Unknown);
            exception.setStatusCode(errorResponse.getStatusCode());

            return exception;
        }

        /*
         * We need to select which exception unmarshaller is the correct one to
         * use from all the possible exceptions this operation can throw.
         * Currently we rely on the unmarshallers to return null if they can't
         * unmarshall the response, but we might need something a little more
         * sophisticated in the future.
         */
        for (Unmarshaller<LunacloudServiceException, Node> unmarshaller : unmarshallerList) {
            LunacloudServiceException ase = unmarshaller.unmarshall(document);
            if (ase != null) {
                ase.setStatusCode(errorResponse.getStatusCode());
                return ase;
            }
        }

        throw new LunacloudClientException("Unable to unmarshall error response from service");
    }

    /**
     * Since this response handler completely consumes all the data from the
     * underlying HTTP connection during the handle method, we don't need to
     * keep the HTTP connection open.
     *
     * @see pt.lunacloud.http.HttpResponseHandler#needsConnectionLeftOpen()
     */
    public boolean needsConnectionLeftOpen() {
        return false;
    }

}