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


import com.fasterxml.jackson.annotation.JsonProperty;
import org.opensearch.performanceanalyzer.metrics.AllMetrics.TCPDimension;
import org.opensearch.performanceanalyzer.metrics.AllMetrics.TCPValue;

public class TCPStatus extends MetricStatus {

    private String dest;

    private int numFlows;

    private double txQ;

    private double rxQ;

    private double curLost;

    private double sndCWND;

    // make this field private so that Jackson uses getter method name
    private double ssThresh;

    public TCPStatus() {}

    public TCPStatus(
            String dest,
            int numFlows,
            double txQ,
            double rxQ,
            double curLost,
            double sndCWND,
            double sSThresh) {
        super();
        this.dest = dest;
        this.numFlows = numFlows;
        this.txQ = txQ;
        this.rxQ = rxQ;
        this.curLost = curLost;
        this.sndCWND = sndCWND;
        this.ssThresh = sSThresh;
    }

    @JsonProperty(TCPDimension.Constants.DEST_VALUE)
    public String getDest() {
        return dest;
    }

    @JsonProperty(TCPValue.Constants.NUM_FLOWS_VALUE)
    public int getNumFlows() {
        return numFlows;
    }

    @JsonProperty(TCPValue.Constants.TXQ_VALUE)
    public double getTxQ() {
        return txQ;
    }

    @JsonProperty(TCPValue.Constants.RXQ_VALUE)
    public double getRxQ() {
        return rxQ;
    }

    @JsonProperty(TCPValue.Constants.CUR_LOST_VALUE)
    public double getCurLost() {
        return curLost;
    }

    @JsonProperty(TCPValue.Constants.SEND_CWND_VALUE)
    public double getSndCWND() {
        return sndCWND;
    }

    @JsonProperty(TCPValue.Constants.SSTHRESH_VALUE)
    public double getSsThresh() {
        return ssThresh;
    }
}
