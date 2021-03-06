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


import org.junit.Assert;
import org.opensearch.performanceanalyzer.AppContext;
import org.opensearch.performanceanalyzer.decisionmaker.actions.ModifyCacheMaxSizeAction;
import org.opensearch.performanceanalyzer.grpc.ResourceEnum;
import org.opensearch.performanceanalyzer.rca.integTests.framework.api.IValidator;
import org.opensearch.performanceanalyzer.rca.persistence.actions.PersistedAction;

public class FieldDataCacheDeciderValidator implements IValidator {
    AppContext appContext;
    long startTime;

    public FieldDataCacheDeciderValidator() {
        appContext = new AppContext();
        startTime = System.currentTimeMillis();
    }

    /**
     * {"actionName":"ModifyCacheMaxSize", "resourceValue":10, "timestamp":"1599257910923",
     * "nodeId":{node1}, "nodeIp":{1.1.1.1}, "actionable":1, "coolOffPeriod": 300000 "muted": 1
     * "summary": "Id":"DATA_0","Ip":"127.0.0.1","resource":10,"desiredCacheMaxSizeInBytes":100000,
     * "currentCacheMaxSizeInBytes":10000,"coolOffPeriodInMillis":300000,"canUpdate":true}
     */
    @Override
    public boolean checkDbObj(Object object) {
        if (object == null) {
            return false;
        }
        PersistedAction persistedAction = (PersistedAction) object;
        return checkPersistedAction(persistedAction);
    }

    /**
     * {"actionName":"ModifyCacheMaxSize", "resourceValue":10, "timestamp":"1599257910923",
     * "nodeIds":{node1}, "nodeIps":{1.1.1.1}, "actionable":1, "coolOffPeriod": 300000 "muted": 1
     * "summary": "Id":"DATA_0","Ip":"127.0.0.1","resource":10,"desiredCacheMaxSizeInBytes":100000,
     * "currentCacheMaxSizeInBytes":10000,"coolOffPeriodInMillis":300000,"canUpdate":true}
     */
    private boolean checkPersistedAction(final PersistedAction persistedAction) {
        ModifyCacheMaxSizeAction modifyCacheMaxSizeAction =
                ModifyCacheMaxSizeAction.fromSummary(persistedAction.getSummary(), appContext);
        Assert.assertEquals(ModifyCacheMaxSizeAction.NAME, persistedAction.getActionName());
        Assert.assertEquals("{DATA_0}", persistedAction.getNodeIds());
        Assert.assertEquals("{127.0.0.1}", persistedAction.getNodeIps());
        Assert.assertEquals(
                ModifyCacheMaxSizeAction.Builder.DEFAULT_COOL_OFF_PERIOD_IN_MILLIS,
                persistedAction.getCoolOffPeriod());
        Assert.assertTrue(persistedAction.isActionable());
        Assert.assertFalse(persistedAction.isMuted());
        Assert.assertEquals(ResourceEnum.FIELD_DATA_CACHE, modifyCacheMaxSizeAction.getCacheType());
        Assert.assertEquals(10000, modifyCacheMaxSizeAction.getCurrentCacheMaxSizeInBytes());
        Assert.assertEquals(100000, modifyCacheMaxSizeAction.getDesiredCacheMaxSizeInBytes());
        return true;
    }
}
