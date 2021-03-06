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

package org.opensearch.performanceanalyzer.rca.net;


import com.google.common.collect.Sets;
import java.util.Objects;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.performanceanalyzer.AppContext;
import org.opensearch.performanceanalyzer.CertificateUtils;
import org.opensearch.performanceanalyzer.config.PluginSettings;
import org.opensearch.performanceanalyzer.grpc.SubscribeResponse;
import org.opensearch.performanceanalyzer.net.GRPCConnectionManager;
import org.opensearch.performanceanalyzer.rca.framework.util.InstanceDetails;

public class SubscribeResponseHandlerTest {
    private static final InstanceDetails.Ip HOST = new InstanceDetails.Ip("127.0.0.1");
    private static final InstanceDetails.Id HOST_ID = new InstanceDetails.Id("host1");
    private static final String GRAPH_NODE = "TEST";

    private SubscriptionManager subscriptionManager;
    private NodeStateManager nodeStateManager;
    private SubscribeResponseHandler uut;

    private String oldCertificateFile;
    private String oldPrivateKeyFile;

    @Before
    public void setup() {
        ClassLoader classLoader = getClass().getClassLoader();

        oldCertificateFile =
                PluginSettings.instance().getProperty(CertificateUtils.CERTIFICATE_FILE_PATH);
        oldPrivateKeyFile =
                PluginSettings.instance().getProperty(CertificateUtils.PRIVATE_KEY_FILE_PATH);

        PluginSettings.instance()
                .overrideProperty(
                        CertificateUtils.CERTIFICATE_FILE_PATH,
                        Objects.requireNonNull(classLoader.getResource("tls/server/localhost.crt"))
                                .getFile());
        PluginSettings.instance()
                .overrideProperty(
                        CertificateUtils.PRIVATE_KEY_FILE_PATH,
                        Objects.requireNonNull(classLoader.getResource("tls/server/localhost.key"))
                                .getFile());

        GRPCConnectionManager grpcConnectionManager = new GRPCConnectionManager(true);
        subscriptionManager = new SubscriptionManager(grpcConnectionManager);
        nodeStateManager = new NodeStateManager(new AppContext());

        InstanceDetails remoteInstance = new InstanceDetails(HOST_ID, HOST, -1);
        uut =
                new SubscribeResponseHandler(
                        subscriptionManager, nodeStateManager, remoteInstance, GRAPH_NODE);
    }

    @After
    public void tearDown() {
        if (oldCertificateFile != null) {
            PluginSettings.instance()
                    .overrideProperty(CertificateUtils.CERTIFICATE_FILE_PATH, oldCertificateFile);
        }

        if (oldPrivateKeyFile != null) {
            PluginSettings.instance()
                    .overrideProperty(CertificateUtils.PRIVATE_KEY_FILE_PATH, oldPrivateKeyFile);
        }
    }

    @Test
    public void testOnNext() {
        // Test that onNext() properly processes a successful subscription message
        SubscribeResponse success =
                SubscribeResponse.newBuilder()
                        .setSubscriptionStatus(SubscribeResponse.SubscriptionStatus.SUCCESS)
                        .build();
        uut.onNext(success);
        Assert.assertEquals(
                subscriptionManager.getPublishersForNode(GRAPH_NODE), Sets.newHashSet(HOST_ID));
        Assert.assertEquals(
                SubscribeResponse.SubscriptionStatus.SUCCESS,
                nodeStateManager.getSubscriptionStatus(GRAPH_NODE, HOST_ID));

        // Test that onNext() properly processes a tag mismatch subscription message
        SubscribeResponse mismatch =
                SubscribeResponse.newBuilder()
                        .setSubscriptionStatus(SubscribeResponse.SubscriptionStatus.TAG_MISMATCH)
                        .build();
        uut.onNext(mismatch);
        Assert.assertEquals(
                SubscribeResponse.SubscriptionStatus.TAG_MISMATCH,
                nodeStateManager.getSubscriptionStatus(GRAPH_NODE, HOST_ID));
        SubscribeResponse unknown = SubscribeResponse.newBuilder().build();
        uut.onNext(unknown); // This line is included for branch coverage
    }

    @Test
    public void testOnError() {
        /* No-op */
        uut.onError(new Exception("no-op"));
    }

    @Test
    public void testOnCompleted() {
        /* No-op */
        uut.onCompleted();
    }
}
