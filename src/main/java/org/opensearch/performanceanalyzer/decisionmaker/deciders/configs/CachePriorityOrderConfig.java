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

package org.opensearch.performanceanalyzer.decisionmaker.deciders.configs;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.opensearch.performanceanalyzer.rca.framework.core.Config;
import org.opensearch.performanceanalyzer.rca.framework.core.NestedConfig;

/** "cache-type": { "priority-order": ["fielddata-cache", "shard-request-cache"] } */
public class CachePriorityOrderConfig {
    private static final String PRIORITY_ORDER_CONFIG_NAME = "priority-order";
    private static String FIELDDATA_CACHE = "fielddata-cache";
    private static String SHARD_REQUEST_CACHE = "shard-request-cache";
    public static final List<String> DEFAULT_PRIORITY_ORDER =
            Collections.unmodifiableList(Arrays.asList(FIELDDATA_CACHE, SHARD_REQUEST_CACHE));
    private Config<List<String>> priorityOrder;

    public CachePriorityOrderConfig(NestedConfig configs) {
        priorityOrder =
                new Config(
                        PRIORITY_ORDER_CONFIG_NAME,
                        configs.getValue(),
                        DEFAULT_PRIORITY_ORDER,
                        List.class);
    }

    public List<String> getPriorityOrder() {
        return priorityOrder.getValue();
    }
}
