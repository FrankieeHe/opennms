/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012-2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.plugins.topo.bsm;

import java.util.ArrayList;
import java.util.List;

import org.opennms.features.topology.api.topo.AbstractVertex;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.netmgt.bsm.service.model.BusinessServiceDTO;

class BusinessServiceVertex extends AbstractVertex {

    private List<VertexRef> children = new ArrayList<>();

    public BusinessServiceVertex(BusinessServiceDTO businessService) {
        this(String.valueOf(businessService.getId()), businessService.getName());
    }

    /**
     * Creates a new {@link BusinessServiceVertex}.
     * @param id the unique id of this vertex. Must be unique overall the namespace.
     */
    public BusinessServiceVertex(String id, String label) {
        super(BusinessServicesTopologyProvider.TOPOLOGY_NAMESPACE, id, label);
        setIconKey(null);
        setLocked(false);
        setSelected(false);
    }

    public void addChildren(AbstractVertex vertex) {
        if (!children.contains(vertex)) {
            children.add(vertex);
            vertex.setParent(this);
        }
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public List<VertexRef> getChildren() {
        return children;
    }


    public boolean isPartOf(String serviceId) {
        return serviceId != null && serviceId.equals(getRoot().getId());
    }

    public BusinessServiceVertex getRoot() {
        if (isRoot()) {
            return this;
        }
        return ((BusinessServiceVertex)getParent()).getRoot();
    }
}
