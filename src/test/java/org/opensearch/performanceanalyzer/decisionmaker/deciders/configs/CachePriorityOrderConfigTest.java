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
import org.junit.Assert;
import org.junit.Test;
import org.opensearch.performanceanalyzer.rca.framework.core.RcaConf;

public class CachePriorityOrderConfigTest {

    @Test
    public void testConfigOverrides() throws Exception {
        final String configStr =
                "{"
                        + "\"decider-config-settings\": { "
                        + "\"cache-type\": { "
                        + "\"priority-order\": [\"test-fielddata-cache\", "
                        + "\"test-shard-request-cache\", \"test-query-cache\", \"test-bitset-filter-cache\"] "
                        + "} "
                        + "} "
                        + "} ";
        RcaConf conf = new RcaConf();
        conf.readConfigFromString(configStr);
        DeciderConfig deciderConfig = new DeciderConfig(conf);
        CachePriorityOrderConfig cachePriorityOrderConfig =
                deciderConfig.getCachePriorityOrderConfig();
        Assert.assertNotNull(cachePriorityOrderConfig);
        Assert.assertEquals(
                Arrays.asList(
                        "test-fielddata-cache",
                        "test-shard-request-cache",
                        "test-query-cache",
                        "test-bitset-filter-cache"),
                cachePriorityOrderConfig.getPriorityOrder());
    }

    @Test
    public void testDefaults() throws Exception {
        final String configStr = "{}";
        RcaConf conf = new RcaConf();
        conf.readConfigFromString(configStr);
        DeciderConfig deciderConfig = new DeciderConfig(conf);
        CachePriorityOrderConfig cachePriorityOrderConfig =
                deciderConfig.getCachePriorityOrderConfig();
        Assert.assertNotNull(cachePriorityOrderConfig);
        Assert.assertEquals(
                CachePriorityOrderConfig.DEFAULT_PRIORITY_ORDER,
                cachePriorityOrderConfig.getPriorityOrder());
    }
}
