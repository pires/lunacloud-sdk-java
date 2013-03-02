/*
 * Copyright 2012-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package pt.lunacloud.auth;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.lunacloud.AmazonClientException;


/**
 * {@link LunacloudCredentialsProvider} implementation that chains together multiple
 * credentials providers. When a caller requests credentials from this provider,
 * it calls all the providers in the chain, in the original order specified,
 * until one can provide credentials, and then returns those credentials. If all
 * of the credential providers in the chain have been called, and none of them
 * can provide credentials, then this class will throw an exception indicated
 * that no credentials are available.
 */
public class AWSCredentialsProviderChain implements LunacloudCredentialsProvider {

    private static final Log log = LogFactory.getLog(AWSCredentialsProviderChain.class);

    private List<LunacloudCredentialsProvider> credentialsProviders =
        new LinkedList<LunacloudCredentialsProvider>();


    /**
     * Constructs a new AWSCredentialsProviderChain with the specified
     * credential providers. When credentials are requested from this provider,
     * it will call each of these credential providers in the same order
     * specified here until one of them returns AWS security credentials.
     *
     * @param credentialsProviders
     *            The chain of credentials providers.
     */
    public AWSCredentialsProviderChain(LunacloudCredentialsProvider... credentialsProviders) {
        if (credentialsProviders == null || credentialsProviders.length == 0)
            throw new IllegalArgumentException("No credential providers specified");

        for (LunacloudCredentialsProvider provider : credentialsProviders) {
            this.credentialsProviders.add(provider);
        }
    }

    public LunacloudCredentials getCredentials() {
        for (LunacloudCredentialsProvider provider : credentialsProviders) {
            try {
                LunacloudCredentials credentials = provider.getCredentials();

                if (credentials.getLunacloudAccessKeyId() != null &&
                    credentials.getLunacloudSecretKey() != null) {
                    log.debug("Loading credentials from " + provider.toString());
                    return credentials;
                }
            } catch (Exception e) {
                // Ignore any exceptions and move onto the next provider
                log.debug("Unable to load credentials from " + provider.toString() +
                          ": " + e.getMessage());
            }
        }

        throw new AmazonClientException("Unable to load AWS credentials from any provider in the chain");
    }

    public void refresh() {
        for (LunacloudCredentialsProvider provider : credentialsProviders) {
            provider.refresh();
        }
    }
}