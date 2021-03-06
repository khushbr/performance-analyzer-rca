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

package org.opensearch.performanceanalyzer.decisionmaker.deciders;


import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.rca.framework.api.flow_units.ResourceFlowUnit;
import org.opensearch.performanceanalyzer.rca.framework.api.summaries.HotClusterSummary;
import org.opensearch.performanceanalyzer.rca.store.rca.admission_control.AdmissionControlClusterRca;

public class AdmissionControlDecider extends Decider {

    private static final Logger LOG = LogManager.getLogger(AdmissionControlDecider.class);
    private static final String NAME = "AdmissionControlDecider";

    private int counter = 0;
    private AdmissionControlClusterRca admissionControlClusterRca;

    public AdmissionControlDecider(
            long evalIntervalSeconds,
            int decisionFrequency,
            AdmissionControlClusterRca admissionControlClusterRca) {
        super(evalIntervalSeconds, decisionFrequency);
        this.admissionControlClusterRca = admissionControlClusterRca;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Decision operate() {
        Decision decision = new Decision(Instant.now().toEpochMilli(), NAME);
        counter += 1;
        if (counter < decisionFrequency) {
            return decision;
        }
        counter = 0;
        for (ResourceFlowUnit<HotClusterSummary> flowUnit :
                admissionControlClusterRca.getFlowUnits()) {
            if (flowUnit.hasResourceSummary()) {
                LOG.warn("encountered an unhealthy admissionControlClusterRca flowUnit");
                return decision;
            }
        }
        LOG.warn("no unhealthy admissionControlClusterRca flowUnits");
        return decision;
    }
}
