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

package org.opensearch.performanceanalyzer.metrics_generator.linux;


import java.util.Map;
import java.util.Set;
import org.opensearch.performanceanalyzer.hwnet.NetworkE2E;
import org.opensearch.performanceanalyzer.metrics_generator.TCPMetricsGenerator;

public class LinuxTCPMetricsGenerator implements TCPMetricsGenerator {

    private Map<String, double[]> map;

    @Override
    public Set<String> getAllDestionationIps() {
        return map.keySet();
    }

    @Override
    public int getNumberOfFlows(final String ip) {
        return (int) map.get(ip)[0];
    }

    @Override
    public double getTransmitQueueSize(String ip) {
        return map.get(ip)[1];
    }

    @Override
    public double getReceiveQueueSize(String ip) {
        return map.get(ip)[2];
    }

    @Override
    public double getCurrentLost(String ip) {
        return map.get(ip)[3];
    }

    @Override
    public double getSendCongestionWindow(String ip) {
        return map.get(ip)[4];
    }

    @Override
    public double getSlowStartThreshold(String ip) {
        return map.get(ip)[5];
    }

    @Override
    public void addSample() {
        NetworkE2E.addSample();
    }

    public void setTCPMetrics(final Map<String, double[]> metrics) {
        map = metrics;
    }
}
