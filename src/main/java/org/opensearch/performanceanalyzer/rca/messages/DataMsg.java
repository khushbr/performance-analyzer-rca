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
 * Copyright 2019-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package org.opensearch.performanceanalyzer.rca.messages;


import java.util.List;
import org.opensearch.performanceanalyzer.rca.framework.core.GenericFlowUnit;

public class DataMsg {
    String sourceGraphNode;
    List<String> destinationGraphNodes;
    List<? extends GenericFlowUnit> flowUnits;

    public DataMsg(
            String sourceGraphNode,
            List<String> destinationNodes,
            List<? extends GenericFlowUnit> flowUnits) {
        this.sourceGraphNode = sourceGraphNode;
        this.destinationGraphNodes = destinationNodes;
        this.flowUnits = flowUnits;
    }

    public String getSourceGraphNode() {
        return sourceGraphNode;
    }

    public List<String> getDestinationGraphNodes() {
        return destinationGraphNodes;
    }

    public List<? extends GenericFlowUnit> getFlowUnits() {
        return flowUnits;
    }

    @Override
    public String toString() {
        return String.format("Data::from: '%s', to: %s", sourceGraphNode, destinationGraphNodes);
    }
}
