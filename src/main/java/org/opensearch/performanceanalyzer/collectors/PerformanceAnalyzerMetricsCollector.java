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


import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.core.Util;

public abstract class PerformanceAnalyzerMetricsCollector implements Runnable {
    enum State {
        HEALTHY,

        // This collector could not complete between two runs of
        // ScheduledMetricCollectorsExecutor. First occurrence of
        // this is considered a warning.
        SLOW,

        // A collector is muted if it failed to complete between two runs of
        // ScheduledMetricCollectorsExecutor. A muted collector is skipped.
        MUTED
    }

    private static final Logger LOG =
            LogManager.getLogger(PerformanceAnalyzerMetricsCollector.class);
    private int timeInterval;
    private long startTime;
    private String collectorName;
    protected StringBuilder value;
    protected State state;

    protected PerformanceAnalyzerMetricsCollector(int timeInterval, String collectorName) {
        this.timeInterval = timeInterval;
        this.collectorName = collectorName;
        this.value = new StringBuilder();
        this.state = State.HEALTHY;
    }

    private AtomicBoolean bInProgress = new AtomicBoolean(false);

    public int getTimeInterval() {
        return timeInterval;
    }

    public boolean inProgress() {
        return bInProgress.get();
    }

    public String getCollectorName() {
        return collectorName;
    }

    abstract void collectMetrics(long startTime);

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        bInProgress.set(true);
    }

    public void run() {
        try {
            Util.invokePrivileged(() -> collectMetrics(startTime));
        } catch (Exception ex) {
            // - should not be any...but in case, absorbing here
            // - logging...we shouldn't be doing as it will slow down; as well as fill up the log.
            // Need to
            // find a way to catch these
            LOG.error(
                    "Error In Collect Metrics: {} with ExceptionCode: {}",
                    () -> ex.toString(),
                    () -> StatExceptionCode.OTHER_COLLECTION_ERROR.toString());
            StatsCollector.instance().logException(StatExceptionCode.OTHER_COLLECTION_ERROR);
        } finally {
            bInProgress.set(false);
        }
    }

    @VisibleForTesting
    public StringBuilder getValue() {
        return value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
