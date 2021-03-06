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

package org.opensearch.performanceanalyzer.jvm;


import org.junit.Test;
import org.opensearch.performanceanalyzer.hwnet.NetworkInterface;
import org.opensearch.performanceanalyzer.os.OSGlobals;

public class ThreadListTests {
    // XXX: standalone test code
    public static class HelloRunnable implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName("duMMy-thread");
            long i = 0;
            while (true) {
                synchronized (HelloRunnable.class) {
                    String.valueOf(i++);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
        (new Thread(new HelloRunnable())).start();
        (new Thread(new HelloRunnable())).start();
        runOnce();
    }

    private static void runOnce() throws InterruptedException {
        String params[] = new String[0];
        while (true) {
            ThreadList.runThreadDump(OSGlobals.getPid(), params);
            ThreadList.LOGGER.info(ThreadList.getNativeTidMap().values());

            /*GCMetrics.runOnce();
            HeapMetrics.runOnce();
            ThreadCPU.runOnce();
            ThreadDiskIO.runOnce();
            ThreadSched.runOnce();
            NetworkE2E.runOnce();
            Disks.runOnce();*/
            NetworkInterface.runOnce();

            Thread.sleep(ThreadList.samplingInterval);
        }
    }

    // - to enhance
    @Test
    public void testMetrics() {}
}
