/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019-2019 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.graph.api;

import java.util.Objects;

import org.opennms.netmgt.graph.api.generic.GenericEdge;

public class AbstractEdge<V extends Vertex> implements Edge {

    private final VertexRef source;
    private final VertexRef target;
    private String namespace;
    private String id;
    private String label;

    public AbstractEdge(V source, V target) {
        this((VertexRef) source, (VertexRef) target);
    }

    public AbstractEdge(V source, VertexRef target) {
        this((VertexRef) source, target);
    }

    public AbstractEdge(VertexRef source, V target) {
        this(target, source);
    }

    protected AbstractEdge(VertexRef owning, VertexRef target) {
        this.source = Objects.requireNonNull(owning);
        this.target = Objects.requireNonNull(target);
        this.namespace = owning.getNamespace();
        this.id = owning.getId() + "->" + target.getId();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public VertexRef getSource() {
        return source;
    }

    @Override
    public VertexRef getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public GenericEdge asGenericEdge() {
        final GenericEdge genericEdge = new GenericEdge(source, target);
        genericEdge.setLabel(getLabel());
        genericEdge.setId(getId());
        genericEdge.setNamespace(getNamespace());
        // TODO MVR tooltip
//        if (getTooltip() != null) {
//            genericEdge.setProperty(GenericProperties.TOOLTIP, getTooltip());
//        }
        return genericEdge;
    }

    @Override
    public String toString() {
        return asGenericEdge().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEdge<?> that = (AbstractEdge<?>) o;
        return Objects.equals(source.getId(), that.source.getId())
                && Objects.equals(source.getNamespace(), that.source.getNamespace())
                && Objects.equals(target.getId(), that.target.getId())
                && Objects.equals(target.getNamespace(), that.target.getNamespace())
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(id, that.id)
                && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source.getId(), source.getNamespace(), target.getId(), target.getNamespace(), namespace, id, label);
    }
}
