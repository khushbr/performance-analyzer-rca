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

package org.opensearch.performanceanalyzer.collectors;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.function.Supplier;
import org.opensearch.performanceanalyzer.jvm.GarbageCollectorInfo;
import org.opensearch.performanceanalyzer.metrics.AllMetrics.GCInfoDimension;
import org.opensearch.performanceanalyzer.metrics.MetricsConfiguration;
import org.opensearch.performanceanalyzer.metrics.MetricsProcessor;
import org.opensearch.performanceanalyzer.metrics.PerformanceAnalyzerMetrics;

/**
 * A collector that collects info about the current garbage collectors for various regions in the
 * heap.
 */
public class GCInfoCollector extends PerformanceAnalyzerMetricsCollector
        implements MetricsProcessor {

    private static final int SAMPLING_TIME_INTERVAL =
            MetricsConfiguration.CONFIG_MAP.get(GCInfoCollector.class).samplingInterval;
    private static final int EXPECTED_KEYS_PATH_LENGTH = 0;

    public GCInfoCollector() {
        super(SAMPLING_TIME_INTERVAL, "GCInfo");
    }

    @Override
    void collectMetrics(long startTime) {
        // Zero the string builder
        value.setLength(0);

        // first line is the timestamp
        value.append(PerformanceAnalyzerMetrics.getJsonCurrentMilliSeconds())
                .append(PerformanceAnalyzerMetrics.sMetricNewLineDelimitor);

        for (Map.Entry<String, Supplier<String>> entry :
                GarbageCollectorInfo.getGcSuppliers().entrySet()) {
            value.append(new GCInfo(entry.getKey(), entry.getValue().get()).serialize())
                    .append(PerformanceAnalyzerMetrics.sMetricNewLineDelimitor);
        }

        saveMetricValues(value.toString(), startTime);
    }

    @Override
    public String getMetricsPath(long startTime, String... keysPath) {
        if (keysPath != null && keysPath.length != EXPECTED_KEYS_PATH_LENGTH) {
            throw new RuntimeException("keys length should be " + EXPECTED_KEYS_PATH_LENGTH);
        }

        return PerformanceAnalyzerMetrics.generatePath(
                startTime, PerformanceAnalyzerMetrics.sGcInfoPath);
    }

    public static class GCInfo extends MetricStatus {
        private String memoryPool;
        private String collectorName;

        public GCInfo() {}

        public GCInfo(final String memoryPool, final String collectorName) {
            this.memoryPool = memoryPool;
            this.collectorName = collectorName;
        }

        @JsonProperty(GCInfoDimension.Constants.MEMORY_POOL_VALUE)
        public String getMemoryPool() {
            return memoryPool;
        }

        @JsonProperty(GCInfoDimension.Constants.COLLECTOR_NAME_VALUE)
        public String getCollectorName() {
            return collectorName;
        }
    }
}
