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


import java.util.List;
import org.opensearch.performanceanalyzer.decisionmaker.actions.Action;

/**
 * A DecisionPolicy evaluates a subset of observation summaries for a Decider, and returns a list of
 * recommended Actions. They abstract out a subset of the decision making process for a decider.
 *
 * <p>Decision policies are invoked by deciders and never scheduled directly by the RCA framework.
 */
public interface DecisionPolicy {

    List<Action> evaluate();
}
