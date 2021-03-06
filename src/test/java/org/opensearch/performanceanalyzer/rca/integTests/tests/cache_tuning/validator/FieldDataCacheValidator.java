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

package org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.validator;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.opensearch.performanceanalyzer.rca.framework.api.summaries.HotClusterSummary;
import org.opensearch.performanceanalyzer.rca.framework.api.summaries.HotNodeSummary;
import org.opensearch.performanceanalyzer.rca.framework.api.summaries.HotResourceSummary;
import org.opensearch.performanceanalyzer.rca.integTests.framework.api.IValidator;
import org.opensearch.performanceanalyzer.rca.integTests.tests.util.JsonParserUtil;
import org.opensearch.performanceanalyzer.rca.store.rca.cluster.FieldDataCacheClusterRca;

public class FieldDataCacheValidator implements IValidator {
    long startTime;

    public FieldDataCacheValidator() {
        startTime = System.currentTimeMillis();
    }

    /**
     * {"rca_name":"FieldDataCacheClusterRca", "timestamp":1596557050522, "state":"unhealthy",
     * "HotClusterSummary":[ {"number_of_nodes":1,"number_of_unhealthy_nodes":1} ]}
     */
    @Override
    public boolean checkJsonResp(JsonElement response) {
        JsonArray array = response.getAsJsonObject().get("data").getAsJsonArray();
        if (array.size() == 0) {
            return false;
        }
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            if (object.get("rca_name")
                    .getAsString()
                    .equals(FieldDataCacheClusterRca.RCA_TABLE_NAME)) {
                return checkClusterRca(object);
            }
        }
        return false;
    }

    /**
     * {"rca_name":"FieldDataCacheClusterRca", "timestamp":1596557050522, "state":"unhealthy",
     * "HotClusterSummary":[{"number_of_nodes":1,"number_of_unhealthy_nodes":1}] }
     */
    private boolean checkClusterRca(final JsonObject rcaObject) {
        if (!"unhealthy".equals(rcaObject.get("state").getAsString())) {
            return false;
        }
        Assert.assertEquals(
                1,
                JsonParserUtil.getSummaryJsonSize(
                        rcaObject, HotClusterSummary.HOT_CLUSTER_SUMMARY_TABLE));
        JsonObject clusterSummaryJson =
                JsonParserUtil.getSummaryJson(
                        rcaObject, HotClusterSummary.HOT_CLUSTER_SUMMARY_TABLE, 0);
        Assert.assertNotNull(clusterSummaryJson);
        Assert.assertEquals(1, clusterSummaryJson.get("number_of_unhealthy_nodes").getAsInt());

        Assert.assertEquals(
                1,
                JsonParserUtil.getSummaryJsonSize(
                        clusterSummaryJson, HotNodeSummary.HOT_NODE_SUMMARY_TABLE));
        JsonObject nodeSummaryJson =
                JsonParserUtil.getSummaryJson(
                        clusterSummaryJson, HotNodeSummary.HOT_NODE_SUMMARY_TABLE, 0);
        Assert.assertNotNull(nodeSummaryJson);
        Assert.assertEquals(
                "DATA_0",
                nodeSummaryJson
                        .get(HotNodeSummary.SQL_SCHEMA_CONSTANTS.NODE_ID_COL_NAME)
                        .getAsString());
        Assert.assertEquals(
                "127.0.0.1",
                nodeSummaryJson
                        .get(HotNodeSummary.SQL_SCHEMA_CONSTANTS.HOST_IP_ADDRESS_COL_NAME)
                        .getAsString());

        Assert.assertEquals(
                1,
                JsonParserUtil.getSummaryJsonSize(
                        nodeSummaryJson, HotResourceSummary.HOT_RESOURCE_SUMMARY_TABLE));
        JsonObject resourceSummaryJson =
                JsonParserUtil.getSummaryJson(
                        nodeSummaryJson, HotResourceSummary.HOT_RESOURCE_SUMMARY_TABLE, 0);
        Assert.assertNotNull(resourceSummaryJson);
        Assert.assertEquals(
                "field data cache",
                resourceSummaryJson
                        .get(HotResourceSummary.SQL_SCHEMA_CONSTANTS.RESOURCE_TYPE_COL_NAME)
                        .getAsString());
        return true;
    }
}
