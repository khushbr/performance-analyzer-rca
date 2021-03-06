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

package org.opensearch.performanceanalyzer.rca.framework.api;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.PerformanceAnalyzerApp;
import org.opensearch.performanceanalyzer.rca.framework.api.flow_units.ResourceFlowUnit;
import org.opensearch.performanceanalyzer.rca.framework.core.NonLeafNode;
import org.opensearch.performanceanalyzer.rca.framework.metrics.ExceptionsAndErrors;
import org.opensearch.performanceanalyzer.rca.framework.metrics.RcaGraphMetrics;
import org.opensearch.performanceanalyzer.rca.scheduler.FlowUnitOperationArgWrapper;

public abstract class Rca<T extends ResourceFlowUnit> extends NonLeafNode<T> {
    private static final Logger LOG = LogManager.getLogger(Rca.class);

    public Rca(long evaluationIntervalSeconds) {
        super(0, evaluationIntervalSeconds);
    }

    /**
     * fetch flowunits from local graph node
     *
     * @param args The wrapper around the flow unit operation.
     */
    @Override
    public void generateFlowUnitListFromLocal(FlowUnitOperationArgWrapper args) {
        LOG.debug("rca: Executing fromLocal: {}", this.getClass().getSimpleName());

        long startTime = System.currentTimeMillis();

        T result;
        try {
            result = this.operate();
        } catch (Exception ex) {
            LOG.error("Exception in operate.", ex);
            PerformanceAnalyzerApp.ERRORS_AND_EXCEPTIONS_AGGREGATOR.updateStat(
                    ExceptionsAndErrors.EXCEPTION_IN_OPERATE, name(), 1);
            result = (T) T.generic();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        PerformanceAnalyzerApp.RCA_GRAPH_METRICS_AGGREGATOR.updateStat(
                RcaGraphMetrics.GRAPH_NODE_OPERATE_CALL, this.name(), duration);

        setLocalFlowUnit(result);
    }

    /**
     * This method specifies what needs to be done when the current node is muted for throwing
     * exceptions.
     */
    @Override
    public void handleNodeMuted() {
        setLocalFlowUnit((T) T.generic());
    }

    @Override
    public void persistFlowUnit(FlowUnitOperationArgWrapper args) {
        long startTime = System.currentTimeMillis();
        for (final T flowUnit : getFlowUnits()) {
            try {
                args.getPersistable().write(this, flowUnit);
            } catch (Exception ex) {
                LOG.error("Caught exception while persisting node: {}", args.getNode().name(), ex);
                PerformanceAnalyzerApp.ERRORS_AND_EXCEPTIONS_AGGREGATOR.updateStat(
                        ExceptionsAndErrors.EXCEPTION_IN_PERSIST, name(), 1);
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        PerformanceAnalyzerApp.RCA_GRAPH_METRICS_AGGREGATOR.updateStat(
                RcaGraphMetrics.RCA_PERSIST_CALL, this.name(), duration);
    }
}
