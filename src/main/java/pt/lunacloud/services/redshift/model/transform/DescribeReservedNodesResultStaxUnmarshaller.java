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

package pt.lunacloud.services.redshift.model.transform;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.events.XMLEvent;

import pt.lunacloud.services.redshift.model.*;
import pt.lunacloud.transform.MapEntry;
import pt.lunacloud.transform.StaxUnmarshallerContext;
import pt.lunacloud.transform.Unmarshaller;
import pt.lunacloud.transform.SimpleTypeStaxUnmarshallers.*;



/**
 * Describe Reserved Nodes Result StAX Unmarshaller
 */
public class DescribeReservedNodesResultStaxUnmarshaller implements Unmarshaller<DescribeReservedNodesResult, StaxUnmarshallerContext> {

    public DescribeReservedNodesResult unmarshall(StaxUnmarshallerContext context) throws Exception {
        DescribeReservedNodesResult describeReservedNodesResult = new DescribeReservedNodesResult();
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;

        
        if (context.isStartOfDocument()) targetDepth += 2;
        

        while (true) {
            XMLEvent xmlEvent = context.nextEvent();
            if (xmlEvent.isEndDocument()) return describeReservedNodesResult;

            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("Marker", targetDepth)) {
                    describeReservedNodesResult.setMarker(StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (context.testExpression("ReservedNodes/ReservedNode", targetDepth)) {
                    describeReservedNodesResult.getReservedNodes().add(ReservedNodeStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
            } else if (xmlEvent.isEndElement()) {
                if (context.getCurrentDepth() < originalDepth) {
                    return describeReservedNodesResult;
                }
            }
        }
    }

    private static DescribeReservedNodesResultStaxUnmarshaller instance;
    public static DescribeReservedNodesResultStaxUnmarshaller getInstance() {
        if (instance == null) instance = new DescribeReservedNodesResultStaxUnmarshaller();
        return instance;
    }
}
    