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

package org.opensearch.performanceanalyzer.rca.store.rca.hot_node;


import org.opensearch.performanceanalyzer.rca.framework.api.Metric;
import org.opensearch.performanceanalyzer.rca.framework.api.summaries.ResourceUtil;

/**
 * This RCA can be used to calculate total cpu usage(combines all physical cores) and give a list of
 * top operations consuming most of cpu resource to downstream vertices
 */
public class HighCpuRca extends GenericResourceRca {
    private static final double CPU_USAGE_THRESHOLD = 0.7;

    public <M extends Metric> HighCpuRca(final int rcaPeriod, final M cpuUsageGroupByOperation) {
        super(rcaPeriod, ResourceUtil.CPU_USAGE, 0, cpuUsageGroupByOperation);
        int cores = Runtime.getRuntime().availableProcessors();
        this.setThreshold(CPU_USAGE_THRESHOLD * (double) cores);
    }
}
