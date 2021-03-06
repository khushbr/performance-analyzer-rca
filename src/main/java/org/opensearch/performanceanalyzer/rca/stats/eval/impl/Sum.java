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


import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.opensearch.performanceanalyzer.rca.stats.eval.Statistics;
import org.opensearch.performanceanalyzer.rca.stats.eval.impl.vals.AggregateValue;

public class Sum implements IStatistic<AggregateValue> {
    private AtomicLong sum;
    private boolean empty;

    public Sum() {
        sum = new AtomicLong(0L);
        empty = true;
    }

    @Override
    public Statistics type() {
        return Statistics.SUM;
    }

    @Override
    public void calculate(String key, Number value) {
        sum.addAndGet(value.longValue());
        empty = false;
    }

    @Override
    public List<AggregateValue> get() {
        return Collections.singletonList(new AggregateValue(sum, type()));
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }
}
