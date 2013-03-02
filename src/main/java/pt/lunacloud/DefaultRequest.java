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
package pt.lunacloud;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import pt.lunacloud.http.HttpMethodName;


/**
 * Default implementation of the {@linkplain pt.lunacloud.Request} interface.
 * <p>
 * This class is only intended for internal use inside the AWS client libraries.
 * Callers shouldn't ever interact directly with objects of this class.
 */
public class DefaultRequest<T> implements Request<T> {

    /** The resource path being requested */
    private String resourcePath;

    /** Map of the parameters being sent as part of this request */
    private Map<String, String> parameters = new HashMap<String, String>();

    /** Map of the headers included in this request */
    private Map<String, String> headers = new HashMap<String, String>();

    /** The service endpoint to which this request should be sent */
    private URI endpoint;

    /** The name of the service to which this request is being sent */
    private String serviceName;

    /**
     * The original, user facing request object which this internal request
     * object is representing
     */
    private final AmazonWebServiceRequest originalRequest;

    /** The HTTP method to use when sending this request. */
    private HttpMethodName httpMethod = HttpMethodName.POST;

    /** An optional stream from which to read the request payload. */
	private InputStream content;
	
	/** An optional time offset to account for clock skew */
	private int timeOffset;

    /**
     * Constructs a new DefaultRequest with the specified service name and the
     * original, user facing request object.
     *
     * @param serviceName
     *            The name of the service to which this request is being sent.
     * @param originalRequest
     *            The original, user facing, AWS request being represented by
     *            this internal request object.
     */
    public DefaultRequest(AmazonWebServiceRequest originalRequest, String serviceName) {
        this.serviceName = serviceName;
        this.originalRequest = originalRequest;
    }

    /**
     * Constructs a new DefaultRequest with the specified service name and no
     * specified original, user facing request object.
     *
     * @param serviceName
     *            The name of the service to which this request is being sent.
     */
    public DefaultRequest(String serviceName) {
        this(null, serviceName);
    }


    /**
     * Returns the original, user facing request object which this internal
     * request object is representing.
     *
     * @return The original, user facing request object which this request
     *         object is representing.
     */
    public AmazonWebServiceRequest getOriginalRequest() {
        return originalRequest;
    }

    /**
     * @see pt.lunacloud.Request#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * @see pt.lunacloud.Request#getHeaders()
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @see pt.lunacloud.Request#setResourcePath(java.lang.String)
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * @see pt.lunacloud.Request#getResourcePath()
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @see pt.lunacloud.Request#addParameter(java.lang.String, java.lang.String)
     */
    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }

    /**
     * @see pt.lunacloud.Request#getParameters()
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @see pt.lunacloud.Request#withParameter(java.lang.String, java.lang.String)
     */
    public Request<T> withParameter(String name, String value) {
        addParameter(name, value);
        return this;
    }

    /**
     * @see pt.lunacloud.Request#getHttpMethod()
     */
    public HttpMethodName getHttpMethod() {
    	return httpMethod;
    }

    /**
     * @see pt.lunacloud.Request#setHttpMethod(pt.lunacloud.http.HttpMethodName)
     */
    public void setHttpMethod(HttpMethodName httpMethod) {
		this.httpMethod = httpMethod;
    }

    /**
     * @see pt.lunacloud.Request#setEndpoint(java.net.URI)
     */
    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @see pt.lunacloud.Request#getEndpoint()
     */
    public URI getEndpoint() {
        return endpoint;
    }

    /**
     * @see pt.lunacloud.Request#getServiceName()
     */
    public String getServiceName() {
        return serviceName;
    }

	/** 
	 * @see pt.lunacloud.Request#getContent() 
	 */
	public InputStream getContent() {
		return content;
	}

	/**
	 * @see pt.lunacloud.Request#setContent(java.io.InputStream)
	 */
	public void setContent(InputStream content) {
		this.content = content;
	}

	/**
     * @see pt.lunacloud.Request#setHeaders(java.util.Map)
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    /**
     * @see pt.lunacloud.Request#setParameters(java.util.Map)
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }
    
    /**
     * @see pt.lunacloud.Request#getTimeOffset
     */
    public int getTimeOffset() {
        return timeOffset;
    }
    
    /**
     * @see com.amazonaws.Request#setTimeOffset(int);
     */
    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }
    
    /**
     * @see com.amazonaws.Request#withTimeOffset(int);
     */
    public Request<T> withTimeOffset(int timeOffset) {
        setTimeOffset(timeOffset);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(getHttpMethod().toString() + " ");
        builder.append(getEndpoint().toString() + " ");

        builder.append("/"
                + (getResourcePath() != null ? getResourcePath() : "")
                + " ");

        if (!getParameters().isEmpty()) {
            builder.append("Parameters: (");
            for (String key : getParameters().keySet()) {
                String value = getParameters().get(key);
                builder.append(key + ": " + value + ", ");
            }
            builder.append(") ");
        }

        if (!getHeaders().isEmpty()) {
            builder.append("Headers: (");
            for (String key : getHeaders().keySet()) {
                String value = getHeaders().get(key);
                builder.append(key + ": " + value + ", ");
            }
            builder.append(") ");
        }

        return builder.toString();
    }
	
}
