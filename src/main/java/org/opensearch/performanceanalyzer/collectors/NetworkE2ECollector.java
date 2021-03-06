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

package org.opensearch.performanceanalyzer.collectors;


import java.util.HashMap;
import java.util.Map;
import org.opensearch.performanceanalyzer.OSMetricsGeneratorFactory;
import org.opensearch.performanceanalyzer.metrics.MetricsConfiguration;
import org.opensearch.performanceanalyzer.metrics.MetricsProcessor;
import org.opensearch.performanceanalyzer.metrics.PerformanceAnalyzerMetrics;
import org.opensearch.performanceanalyzer.metrics_generator.OSMetricsGenerator;
import org.opensearch.performanceanalyzer.metrics_generator.TCPMetricsGenerator;

public class NetworkE2ECollector extends PerformanceAnalyzerMetricsCollector
        implements MetricsProcessor {
    private static final int sTimeInterval =
            MetricsConfiguration.CONFIG_MAP.get(NetworkE2ECollector.class).samplingInterval;

    public NetworkE2ECollector() {
        super(sTimeInterval, "NetworkE2ECollector");
    }

    @Override
    public void collectMetrics(long startTime) {
        OSMetricsGenerator generator = OSMetricsGeneratorFactory.getInstance();
        if (generator == null) {
            return;
        }
        TCPMetricsGenerator tcpMetricsGenerator = generator.getTCPMetricsGenerator();
        tcpMetricsGenerator.addSample();
        saveMetricValues(getMetrics(tcpMetricsGenerator), startTime);
    }

    @Override
    public String getMetricsPath(long startTime, String... keysPath) {
        // throw exception if keys.length is not equal to 0
        if (keysPath.length != 0) {
            throw new RuntimeException("keys length should be 0");
        }

        return PerformanceAnalyzerMetrics.generatePath(
                startTime, PerformanceAnalyzerMetrics.sTCPPath);
    }

    private Map<String, TCPStatus> getMetricsMap(TCPMetricsGenerator tcpMetricsGenerator) {
        Map<String, TCPStatus> map = new HashMap<>();

        for (String dest : tcpMetricsGenerator.getAllDestionationIps()) {
            TCPStatus tcpStatus =
                    new TCPStatus(
                            dest,
                            tcpMetricsGenerator.getNumberOfFlows(dest),
                            tcpMetricsGenerator.getTransmitQueueSize(dest),
                            tcpMetricsGenerator.getReceiveQueueSize(dest),
                            tcpMetricsGenerator.getCurrentLost(dest),
                            tcpMetricsGenerator.getSendCongestionWindow(dest),
                            tcpMetricsGenerator.getSlowStartThreshold(dest));

            map.put(dest, tcpStatus);
        }

        return map;
    }

    private String getMetrics(TCPMetricsGenerator tcpMetricsGenerator) {

        Map<String, TCPStatus> map = getMetricsMap(tcpMetricsGenerator);
        value.setLength(0);

        // first line is the timestamp
        value.append(PerformanceAnalyzerMetrics.getJsonCurrentMilliSeconds())
                .append(PerformanceAnalyzerMetrics.sMetricNewLineDelimitor);

        for (TCPStatus tcpStatus : map.values()) {
            value.append(tcpStatus.serialize())
                    .append(PerformanceAnalyzerMetrics.sMetricNewLineDelimitor);
        }

        return value.toString();
    }
}
