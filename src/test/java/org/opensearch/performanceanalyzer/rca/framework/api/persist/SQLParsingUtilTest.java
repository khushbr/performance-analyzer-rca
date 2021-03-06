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
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package org.opensearch.performanceanalyzer.rca.framework.api.persist;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataTypeException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SQLParsingUtilTest {
    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String DATA_FIELD = "DATA_FIELD";

    @Mock private Result<Record> result;

    @Mock private Field<String> field;

    @Mock private Record record;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReadDataFromSqlResult() {
        // method should return Double.NaN when result is null
        assertEquals(
                Double.NaN,
                SQLParsingUtil.readDataFromSqlResult(null, field, FIELD_NAME, DATA_FIELD),
                0);

        // method should return Double.NaN when it encounters an IllegalArgumentException
        when(result.getValues(field)).thenReturn(Lists.newArrayList("no", "matches", "here"));
        assertEquals(
                Double.NaN,
                SQLParsingUtil.readDataFromSqlResult(result, field, FIELD_NAME, DATA_FIELD),
                0);

        // method should return Double.NaN when it encounters an DataTypeException
        when(result.getValues(field)).thenReturn(Lists.newArrayList("no", FIELD_NAME, "nope"));
        when(result.get(1)).thenReturn(record);
        when(record.getValue(anyString(), eq(Double.class)))
                .thenThrow(new DataTypeException("EXCEPTION"));
        assertEquals(
                Double.NaN,
                SQLParsingUtil.readDataFromSqlResult(result, field, FIELD_NAME, DATA_FIELD),
                0);

        // method should return the matched record value when all parameters are valid
        when(record.getValue(anyString(), eq(Double.class))).thenReturn(Math.E);
        assertEquals(
                Math.E,
                SQLParsingUtil.readDataFromSqlResult(result, field, FIELD_NAME, DATA_FIELD),
                0);
    }
}
