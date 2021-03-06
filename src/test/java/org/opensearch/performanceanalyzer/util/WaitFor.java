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

package org.opensearch.performanceanalyzer.util;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class allows you to wait at most a specified duration until a condition evaluates to true
 */
public class WaitFor {
    /**
     * Waits at most the specified time for the given task to evaluate to true
     *
     * @param task The task which we hope evaluates to true before the time limit
     * @param maxWait The max amount of time to wait for the task to evaluate for true
     * @param unit The time unit of the maxWait parameter
     * @throws Exception If the time limit expires before the task evaluates to true
     */
    public static void waitFor(Callable<Boolean> task, long maxWait, TimeUnit unit)
            throws Exception {
        waitFor(task, maxWait, unit, "waitFor timed out before task evaluated to true");
    }

    /**
     * Waits at most the specified time for the given task to evaluate to true
     *
     * @param task The task which we hope evaluates to true before the time limit
     * @param maxWait The max amount of time to wait for the task to evaluate for true
     * @param unit The time unit of the maxWait parameter
     * @param message The exception message to print if waitFor times out
     * @throws Exception If the time limit expires before the task evaluates to true
     */
    public static void waitFor(Callable<Boolean> task, long maxWait, TimeUnit unit, String message)
            throws Exception {
        long maxWaitMillis = TimeUnit.MILLISECONDS.convert(maxWait, unit);
        do {
            Instant start = Instant.now();
            if (task.call()) {
                return;
            }
            // Wait for a fixed amount before calling the task again
            Thread.sleep(100L);
            maxWaitMillis -= Duration.between(start, Instant.now()).toMillis();
        } while (maxWaitMillis >= 0);
        // Check the task one last time before throwing an exception
        if (!task.call()) {
            throw new TimeoutException(message);
        }
    }
}
