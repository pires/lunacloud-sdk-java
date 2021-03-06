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
package pt.lunacloud.transform;

import pt.lunacloud.LunacloudServiceException;
import pt.lunacloud.transform.JsonErrorUnmarshaller;
import pt.lunacloud.util.json.JSONObject;

public class GlacierErrorUnmarshaller extends JsonErrorUnmarshaller {

    public GlacierErrorUnmarshaller() {}

    protected GlacierErrorUnmarshaller(Class<? extends LunacloudServiceException> exceptionClass) {
        super(exceptionClass);
    }

    public String parseErrorCode(JSONObject json) throws Exception {
        if (json.has("code")) {
            String type = json.getString("code");
            int separator = type.lastIndexOf("#");
            return type.substring(separator + 1);
        }

        return null;
    }
}
