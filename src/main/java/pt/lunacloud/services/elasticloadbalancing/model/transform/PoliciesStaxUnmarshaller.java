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

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.events.XMLEvent;

import pt.lunacloud.services.elasticloadbalancing.model.*;
import pt.lunacloud.transform.MapEntry;
import pt.lunacloud.transform.StaxUnmarshallerContext;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.transform.SimpleTypeStaxUnmarshallers.*;



/**
 * Policies StAX Unmarshaller
 */
public class PoliciesStaxUnmarshaller implements Unmarshaller<Policies, StaxUnmarshallerContext> {

    public Policies unmarshall(StaxUnmarshallerContext context) throws Exception {
        Policies policies = new Policies();
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;

        
        if (context.isStartOfDocument()) targetDepth += 2;
        

        while (true) {
            XMLEvent xmlEvent = context.nextEvent();
            if (xmlEvent.isEndDocument()) return policies;

            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("AppCookieStickinessPolicies/member", targetDepth)) {
                    policies.getAppCookieStickinessPolicies().add(AppCookieStickinessPolicyStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (context.testExpression("LBCookieStickinessPolicies/member", targetDepth)) {
                    policies.getLBCookieStickinessPolicies().add(LBCookieStickinessPolicyStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (context.testExpression("OtherPolicies/member", targetDepth)) {
                    policies.getOtherPolicies().add(StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
            } else if (xmlEvent.isEndElement()) {
                if (context.getCurrentDepth() < originalDepth) {
                    return policies;
                }
            }
        }
    }

    private static PoliciesStaxUnmarshaller instance;
    public static PoliciesStaxUnmarshaller getInstance() {
        if (instance == null) instance = new PoliciesStaxUnmarshaller();
        return instance;
    }
}
    