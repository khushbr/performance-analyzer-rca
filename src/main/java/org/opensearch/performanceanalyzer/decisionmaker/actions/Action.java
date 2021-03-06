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
 * Copyright 2020-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package org.opensearch.performanceanalyzer.decisionmaker.actions;


import java.util.List;
import java.util.Map;
import org.opensearch.performanceanalyzer.rca.store.rca.cluster.NodeKey;

public interface Action {

    /**
     * Returns true if the configured action is actionable, false otherwise.
     *
     * <p>Examples of non-actionable actions are resource configurations where limits have been
     * reached.
     */
    boolean isActionable();

    /** Time to wait since last recommendation, before suggesting this action again */
    long coolOffPeriodInMillis();

    /** Returns a list of nodes impacted by this action. */
    List<NodeKey> impactedNodes();

    /** Returns a map of nodes to ImpactVector of this action on that node */
    Map<NodeKey, ImpactVector> impact();

    /** Returns action name */
    String name();

    /** Returns a summary for the configured action */
    String summary();

    /** Returns if this action is explicitly muted through configuration */
    boolean isMuted();
}
