/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.owasp.dependencytrack.metrics;

/**
 * Helper class for enhancing metrics.
 *
 * @author Steve Springett
 * @since 3.0.0
 */
public final class Metrics {

    private Metrics() { }

    public static double inheritedRiskScore(int high, int medium, int low) {
        return inheritedRiskScore(0, high, medium, low);
    }

    public static double inheritedRiskScore(int critical, int high, int medium, int low) {
        return (double) ((critical * 10) + (high * 5) + (medium * 3) + (low * 1));
    }

    public static double vulnerableComponentRatio(int vulnerabilities, int vulnerableComponents) {
        double ratio = 0.0;
        if (vulnerableComponents > 0) {
            ratio = (double) vulnerabilities / vulnerableComponents;
        }
        return ratio;
    }
}
