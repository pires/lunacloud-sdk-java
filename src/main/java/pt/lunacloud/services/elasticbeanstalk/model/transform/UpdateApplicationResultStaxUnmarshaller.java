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

package pt.lunacloud.services.elasticbeanstalk.model.transform;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.events.XMLEvent;

import pt.lunacloud.services.elasticbeanstalk.model.*;
import pt.lunacloud.transform.MapEntry;
import pt.lunacloud.transform.StaxUnmarshallerContext;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.transform.SimpleTypeStaxUnmarshallers.*;



/**
 * Update Application Result StAX Unmarshaller
 */
public class UpdateApplicationResultStaxUnmarshaller implements Unmarshaller<UpdateApplicationResult, StaxUnmarshallerContext> {

    public UpdateApplicationResult unmarshall(StaxUnmarshallerContext context) throws Exception {
        UpdateApplicationResult updateApplicationResult = new UpdateApplicationResult();
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;

        
        if (context.isStartOfDocument()) targetDepth += 2;
        

        while (true) {
            XMLEvent xmlEvent = context.nextEvent();
            if (xmlEvent.isEndDocument()) return updateApplicationResult;

            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("Application", targetDepth)) {
                    updateApplicationResult.setApplication(ApplicationDescriptionStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
            } else if (xmlEvent.isEndElement()) {
                if (context.getCurrentDepth() < originalDepth) {
                    return updateApplicationResult;
                }
            }
        }
    }

    private static UpdateApplicationResultStaxUnmarshaller instance;
    public static UpdateApplicationResultStaxUnmarshaller getInstance() {
        if (instance == null) instance = new UpdateApplicationResultStaxUnmarshaller();
        return instance;
    }
}
    