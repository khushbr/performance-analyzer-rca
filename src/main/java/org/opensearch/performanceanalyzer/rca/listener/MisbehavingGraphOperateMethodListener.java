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

package org.opensearch.performanceanalyzer.rca.listener;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.rca.framework.core.Stats;
import org.opensearch.performanceanalyzer.rca.framework.metrics.ExceptionsAndErrors;
import org.opensearch.performanceanalyzer.rca.stats.listeners.IListener;
import org.opensearch.performanceanalyzer.rca.stats.measurements.MeasurementSet;

public class MisbehavingGraphOperateMethodListener implements IListener {
    private static final Logger LOG =
            LogManager.getLogger(MisbehavingGraphOperateMethodListener.class);
    /**
     * A map to keep track of the graohNodeName and the number of times it threw an exception in the
     * {@code operate()} method.
     */
    Map<String, AtomicInteger> map;

    public static final int TOLERANCE_LIMIT = 1;

    public MisbehavingGraphOperateMethodListener() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public Set<MeasurementSet> getMeasurementsListenedTo() {
        return new HashSet<>(Arrays.asList(ExceptionsAndErrors.EXCEPTION_IN_OPERATE));
    }

    @Override
    public void onOccurrence(MeasurementSet measurementSet, Number value, String key) {
        if (!key.isEmpty()) {
            AtomicInteger count = map.putIfAbsent(key, new AtomicInteger(1));
            int newCount = 1;
            if (count != null) {
                newCount = count.incrementAndGet();
            }
            if (newCount > TOLERANCE_LIMIT) {
                if (Stats.getInstance().addToMutedGraphNodes(key)) {
                    LOG.warn(
                            "Node {} got muted for throwing one or more of '{}' more than {} times.",
                            key,
                            getMeasurementsListenedTo(),
                            TOLERANCE_LIMIT);
                }
            }
        }
    }
}
