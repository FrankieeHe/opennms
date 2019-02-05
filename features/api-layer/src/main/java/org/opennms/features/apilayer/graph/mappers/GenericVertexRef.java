/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.apilayer.graph.mappers;

import java.util.Objects;

import org.opennms.netmgt.graph.api.VertexRef;

public class GenericVertexRef implements VertexRef {
    private final org.opennms.integration.api.v1.graph.VertexRef vertexRef;

    public GenericVertexRef(org.opennms.integration.api.v1.graph.VertexRef vertexRef) {
        this.vertexRef = Objects.requireNonNull(vertexRef);
    }

    @Override
    public String getNamespace() {
        return vertexRef.getNamespace();
    }

    @Override
    public String getId() {
        return vertexRef.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericVertexRef that = (GenericVertexRef) o;
        return Objects.equals(vertexRef, that.vertexRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexRef);
    }

    @Override
    public String toString() {
        return "GenericVertexRef{" +
                "vertexRef=" + vertexRef +
                '}';
    }
}
