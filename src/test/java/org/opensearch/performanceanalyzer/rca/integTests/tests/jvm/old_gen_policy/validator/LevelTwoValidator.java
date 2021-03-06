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

package org.opensearch.performanceanalyzer.rca.integTests.tests.jvm.old_gen_policy.validator;


import com.google.gson.JsonArray;
import org.opensearch.performanceanalyzer.grpc.ResourceEnum;

public class LevelTwoValidator extends OldGenPolicyBaseValidator {

    /**
     * the default rca.conf prefer ingest over search. So here we will only get three actions :
     * search, fielddata, shard request.
     */
    @Override
    public boolean checkPersistedActions(JsonArray actionJsonArray) {
        return (checkModifyQueueAction(actionJsonArray, ResourceEnum.SEARCH_THREADPOOL)
                && checkModifyCacheAction(actionJsonArray, ResourceEnum.FIELD_DATA_CACHE)
                && checkModifyCacheAction(actionJsonArray, ResourceEnum.SHARD_REQUEST_CACHE));
    }
}
