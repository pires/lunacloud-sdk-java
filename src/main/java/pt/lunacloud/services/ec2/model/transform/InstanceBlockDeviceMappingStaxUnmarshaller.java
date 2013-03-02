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

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.events.XMLEvent;

import pt.lunacloud.services.ec2.model.*;
import pt.lunacloud.transform.MapEntry;
import pt.lunacloud.transform.StaxUnmarshallerContext;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.transform.SimpleTypeStaxUnmarshallers.*;



/**
 * Instance Block Device Mapping StAX Unmarshaller
 */
public class InstanceBlockDeviceMappingStaxUnmarshaller implements Unmarshaller<InstanceBlockDeviceMapping, StaxUnmarshallerContext> {

    public InstanceBlockDeviceMapping unmarshall(StaxUnmarshallerContext context) throws Exception {
        InstanceBlockDeviceMapping instanceBlockDeviceMapping = new InstanceBlockDeviceMapping();
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;

        
        if (context.isStartOfDocument()) targetDepth += 1;
        

        while (true) {
            XMLEvent xmlEvent = context.nextEvent();
            if (xmlEvent.isEndDocument()) return instanceBlockDeviceMapping;

            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("deviceName", targetDepth)) {
                    instanceBlockDeviceMapping.setDeviceName(StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (context.testExpression("ebs", targetDepth)) {
                    instanceBlockDeviceMapping.setEbs(EbsInstanceBlockDeviceStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
            } else if (xmlEvent.isEndElement()) {
                if (context.getCurrentDepth() < originalDepth) {
                    return instanceBlockDeviceMapping;
                }
            }
        }
    }

    private static InstanceBlockDeviceMappingStaxUnmarshaller instance;
    public static InstanceBlockDeviceMappingStaxUnmarshaller getInstance() {
        if (instance == null) instance = new InstanceBlockDeviceMappingStaxUnmarshaller();
        return instance;
    }
}
    