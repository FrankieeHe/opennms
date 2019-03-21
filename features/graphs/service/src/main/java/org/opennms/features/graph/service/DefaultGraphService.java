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

package org.opennms.features.graph.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opennms.features.graph.api.Edge;
import org.opennms.features.graph.api.Graph;
import org.opennms.features.graph.api.GraphContainer;
import org.opennms.features.graph.api.Vertex;
import org.opennms.features.graph.api.info.GraphContainerInfo;
import org.opennms.features.graph.api.info.GraphInfo;
import org.opennms.features.graph.api.service.GraphContainerProvider;
import org.opennms.features.graph.api.service.GraphService;

public class DefaultGraphService implements GraphService {

    private List<GraphContainerProvider> graphContainerProviders = new ArrayList<>();

    @Override
    public List<GraphContainerInfo> getGraphContainerInfos() {
        return graphContainerProviders.stream().map(cp -> cp.getContainerInfo()).collect(Collectors.toList());
    }

    @Override
    public GraphContainerInfo getGraphContainerInfo(String containerId) {
        return getGraphContainerInfos().stream().filter(ci -> ci.getId().equalsIgnoreCase(containerId)).findAny().orElse(null);
    }

    @Override
    public GraphContainer getGraphContainer(String containerId) {
        final Optional<GraphContainerProvider> any = graphContainerProviders.stream().filter(cp -> cp.getContainerInfo().getId().equalsIgnoreCase(containerId)).findAny();
        if (any.isPresent()) {
            return any.get().loadGraphContainer(); // TODO MVR implement lazy loading of graphs
        }
        return null;
    }

    @Override
    public GraphInfo getGraphInfo(String graphNamespace) {
        // TODO MVR this is not equalsIgnoreCase which is used everywhere else
        final Optional<GraphContainerInfo> any = getGraphContainerInfos().stream().filter(ci -> ci.getNamespaces().contains(graphNamespace)).findAny();
        if (any.isPresent()) {
            return getGraphContainerInfo(any.get().getId()).getGraphInfo(graphNamespace);
        }
        return null;
    }

    @Override
    public <V extends Vertex, E extends Edge> Graph<V, E> getGraph(String containerId, String graphNamespace) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <V extends Vertex, E extends Edge> Graph<V, E> getGraph(String namespace) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onBind(GraphContainerProvider graphContainerProvider, Map<String, String> props) {
        graphContainerProviders.add(graphContainerProvider);
    }

    public void onUnbind(GraphContainerProvider graphContainerProvider, Map<String, String> props) {
        graphContainerProviders.remove(graphContainerProvider);
    }
}
