/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.opensearch.performanceanalyzer.rca.net.tasks;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.opensearch.performanceanalyzer.grpc.FlowUnitMessage;
import org.opensearch.performanceanalyzer.rca.GradleTaskForRca;
import org.opensearch.performanceanalyzer.rca.framework.util.InstanceDetails;
import org.opensearch.performanceanalyzer.rca.net.NodeStateManager;
import org.opensearch.performanceanalyzer.rca.net.ReceivedFlowUnitStore;

@Category(GradleTaskForRca.class)
public class FlowUnitRxTaskTest {

    private static final String TEST_GRAPH_NODE = "testGraphNode";
    private static final String TEST_OPEN_SEARCH_NODE = "testOpenSearchNode";

    private FlowUnitRxTask testFlowUnitRxTask;
    private FlowUnitMessage testFlowUnitMessage =
            FlowUnitMessage.newBuilder()
                    .setGraphNode(TEST_GRAPH_NODE)
                    .setNode(TEST_OPEN_SEARCH_NODE)
                    .build();

    @Mock private NodeStateManager mockNodeStateManager;

    @Mock private ReceivedFlowUnitStore mockReceivedFlowUnitStore;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        testFlowUnitRxTask =
                new FlowUnitRxTask(
                        mockNodeStateManager, mockReceivedFlowUnitStore, testFlowUnitMessage);
    }

    @Test
    public void testEnqueueSuccess() {
        when(mockReceivedFlowUnitStore.enqueue(TEST_GRAPH_NODE, testFlowUnitMessage))
                .thenReturn(true);

        testFlowUnitRxTask.run();

        verify(mockNodeStateManager)
                .updateReceiveTime(
                        eq(new InstanceDetails.Id(TEST_OPEN_SEARCH_NODE)),
                        eq(TEST_GRAPH_NODE),
                        anyLong());
    }
}
