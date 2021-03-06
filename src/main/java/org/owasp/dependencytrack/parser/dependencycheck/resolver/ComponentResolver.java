/*
 * This file is part of Dependency-Track.
 *
 * Dependency-Track is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Dependency-Track is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Dependency-Track. If not, see http://www.gnu.org/licenses/.
 */
package org.owasp.dependencytrack.parser.dependencycheck.resolver;

import org.owasp.dependencytrack.model.Component;
import org.owasp.dependencytrack.parser.dependencycheck.model.Dependency;
import org.owasp.dependencytrack.persistence.QueryManager;

/**
 * Attempts to resolve an existing Dependency-Track Component from a
 * Dependency-Check Dependency.
 */
public class ComponentResolver implements IResolver {

    /**
     * {@inheritDoc}
     */
    public Component resolve(Dependency dependency) {
        try (QueryManager qm = new QueryManager()) {
            Component component = qm.getComponentByHash(dependency.getMd5());
            if (component != null) {
                return component;
            }
            component = qm.getComponentByHash(dependency.getSha1());
            if (component != null) {
                return component;
            }
        }
        return null;
    }
}
