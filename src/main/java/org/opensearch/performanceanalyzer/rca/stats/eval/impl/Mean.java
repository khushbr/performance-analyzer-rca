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

package org.opensearch.performanceanalyzer.rca.stats.eval.impl;


import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.opensearch.performanceanalyzer.rca.stats.eval.Statistics;
import org.opensearch.performanceanalyzer.rca.stats.eval.impl.vals.AggregateValue;

public class Mean implements IStatistic<AggregateValue> {
    private BigInteger sum;
    private long count;

    private boolean empty;

    public Mean() {
        this.sum = BigInteger.ZERO;
        this.count = 0;
        this.empty = true;
    }

    @Override
    public Statistics type() {
        return Statistics.MEAN;
    }

    @Override
    public void calculate(String key, Number value) {
        synchronized (this) {
            BigInteger bdValue = BigInteger.valueOf(value.longValue());
            sum = sum.add(bdValue);
            count += 1;
        }
        empty = false;
    }

    @Override
    public List<AggregateValue> get() {
        double ret = 0.0;
        synchronized (this) {
            if (count != 0) {
                ret = sum.doubleValue() / count;
            }
        }
        return Collections.singletonList(new AggregateValue(ret, type()));
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }
}
