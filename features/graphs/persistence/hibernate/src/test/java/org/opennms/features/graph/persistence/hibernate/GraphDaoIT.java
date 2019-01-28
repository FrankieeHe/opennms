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

package org.opennms.features.graph.persistence.hibernate;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.features.graph.api.generic.GenericProperties;
import org.opennms.netmgt.dao.api.GenericPersistenceAccessor;
import org.opennms.netmgt.topology.EdgeEntity;
import org.opennms.netmgt.topology.GraphContainerEntity;
import org.opennms.netmgt.topology.GraphEntity;
import org.opennms.netmgt.topology.VertexEntity;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionOperations;

import com.google.common.collect.Lists;

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:/META-INF/opennms/applicationContext-minimal-conf.xml",
        "classpath:/META-INF/opennms/applicationContext-soa.xml",
        "classpath:/META-INF/opennms/applicationContext-dao.xml",
        "classpath*:/META-INF/opennms/component-dao.xml",
        "classpath:/META-INF/opennms/mockEventIpcManager.xml",
        "classpath:/META-INF/opennms/applicationContext-databasePopulator.xml" })
@JUnitConfigurationEnvironment
@JUnitTemporaryDatabase
// TODO MVR rename test
public class GraphDaoIT {

    private static final String CONTAINER_ID = "unique-container-id";
    private static final String CONTAINER_DESCRIPTION = "Container for '" + CONTAINER_ID + "' graph";
    private static final String CONTAINER_LABEL = "I am soooo unique \\o/";

    private static final String GRAPH_NAMESPACE = "dummy";
    private static final String GRAPH_LABEL = "Dummy Graph";
    private static final String GRAPH_DESCRIPTION = "I am not so unique, I may be replaced at any time :(";

    @Autowired
    private GenericPersistenceAccessor persistenceAccessor;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    public void verifyCRUD() {
        /*
         * Create
         */
        final GraphContainerEntity originalContainerEntity = new GraphContainerEntity();
        originalContainerEntity.setNamespace(CONTAINER_ID);
        originalContainerEntity.setProperty(GenericProperties.DESCRIPTION, String.class, CONTAINER_DESCRIPTION);
        originalContainerEntity.setProperty(GenericProperties.LABEL, String.class, CONTAINER_LABEL);

        final GraphEntity originalGraphEntity1 = createExampleGraph(GRAPH_NAMESPACE);
        final GraphEntity originalGraphEntity2 = createExampleGraph(GRAPH_NAMESPACE + "2");

        // Persist
        originalContainerEntity.getGraphs().add(originalGraphEntity1);
        originalContainerEntity.getGraphs().add(originalGraphEntity2);
        transactionOperations.execute(status -> {
            persistenceAccessor.save(originalContainerEntity);
            status.flush();
            return null;
        });

        // Verify Container
        transactionOperations.execute(status -> {
            final GraphContainerEntity persistedGraphContainerEntity = persistenceAccessor.get(GraphContainerEntity.class, originalContainerEntity.getId());
            Assert.assertEquals(CONTAINER_LABEL, persistedGraphContainerEntity.getLabel());
            Assert.assertEquals(CONTAINER_DESCRIPTION, persistedGraphContainerEntity.getDescription());
            Assert.assertEquals(CONTAINER_ID, persistedGraphContainerEntity.getNamespace());
            Assert.assertEquals(2, persistedGraphContainerEntity.getGraphs().size());

            // Verify Graph
            for (String eachNamespace : Lists.newArrayList(GRAPH_NAMESPACE, GRAPH_NAMESPACE + "2")) {
                final GraphEntity originalGraphEntity = originalContainerEntity.getGraph(eachNamespace);
                final GraphEntity persistedGraphEntity = persistedGraphContainerEntity.getGraph(eachNamespace);
                Assert.assertNotNull(persistedGraphEntity);
                Assert.assertEquals(eachNamespace, persistedGraphEntity.getNamespace());
                Assert.assertEquals(originalGraphEntity.getLabel(), persistedGraphEntity.getLabel());
                Assert.assertEquals(originalGraphEntity.getDescription(), persistedGraphEntity.getDescription());
                Assert.assertEquals(3, persistedGraphEntity.getVertices().size());
                Assert.assertEquals(1, persistedGraphEntity.getEdges().size());
            }
            return null;
        });

        /*
         * Update
         */
        transactionOperations.execute(status -> {
            final GraphContainerEntity persistedGraphContainerEntity = persistenceAccessor.get(GraphContainerEntity.class, originalContainerEntity.getId());

            // Add new graph to the container
            final GraphEntity graphEntity3 = createExampleGraph(GRAPH_NAMESPACE + "3");
            graphEntity3.setProperty(GenericProperties.LABEL, String.class, "Ultimate " + GRAPH_LABEL);
            persistedGraphContainerEntity.getGraphs().add(graphEntity3);

            // Remove existing graph.
            persistedGraphContainerEntity.removeGraph(GRAPH_NAMESPACE + "2");

            // Update existing graph
            final GraphEntity graph = persistedGraphContainerEntity.getGraph(GRAPH_NAMESPACE);
            graph.setProperty(GenericProperties.LABEL, String.class,"New " + GRAPH_LABEL);

            // Adding new relations cause edges to be empty, so we test this by adding a new vertex
            final VertexEntity vertex = new VertexEntity();
            vertex.setNamespace(GRAPH_NAMESPACE);
            vertex.setProperty(GenericProperties.ID, String.class, "v4");
            vertex.setProperty(GenericProperties.LABEL, String.class, "Vertex 4");
            graph.addVertex(vertex);

            // The same is for adding an edge. It is simply removed
            final EdgeEntity edge = new EdgeEntity();
            edge.setNamespace(GRAPH_NAMESPACE);
            edge.setSource(graph.getVertices().get(1));
            edge.setTarget(graph.getVertices().get(2));
            graph.addEdge(edge);

            persistenceAccessor.update(persistedGraphContainerEntity);
            return null;
        });

        // Verify Container
        transactionOperations.execute(status -> {
            final GraphContainerEntity persistedGraphContainerEntity = persistenceAccessor.get(GraphContainerEntity.class, originalContainerEntity.getId());
            Assert.assertEquals(CONTAINER_LABEL, persistedGraphContainerEntity.getLabel());
            Assert.assertEquals(CONTAINER_DESCRIPTION, persistedGraphContainerEntity.getDescription());
            Assert.assertEquals(CONTAINER_ID, persistedGraphContainerEntity.getNamespace());
            Assert.assertEquals(2, persistedGraphContainerEntity.getGraphs().size());

            // Verify Graphs
            try {
                persistedGraphContainerEntity.getGraph(GRAPH_NAMESPACE + "2");
                Assert.fail("Expected element to not be present");
            } catch (NoSuchElementException ex) {
                // expected
            }

            // Verify <GRAPH_NAMESPACE>
            GraphEntity persistedGraphEntity = persistedGraphContainerEntity.getGraph(GRAPH_NAMESPACE);
            Assert.assertNotNull(persistedGraphEntity);
            Assert.assertEquals(GRAPH_NAMESPACE, persistedGraphEntity.getNamespace());
            Assert.assertEquals("New " + GRAPH_LABEL, persistedGraphEntity.getLabel());
            Assert.assertEquals(GRAPH_DESCRIPTION, persistedGraphEntity.getDescription());
            Assert.assertEquals(4, persistedGraphEntity.getVertices().size());
            Assert.assertEquals(2, persistedGraphEntity.getEdges().size());

            // Verify <GRAPH_NAMESPACE>3
            persistedGraphEntity = persistedGraphContainerEntity.getGraph(GRAPH_NAMESPACE + "3");
            Assert.assertNotNull(persistedGraphEntity);
            Assert.assertEquals(GRAPH_NAMESPACE + "3", persistedGraphEntity.getNamespace());
            Assert.assertEquals("Ultimate " + GRAPH_LABEL, persistedGraphEntity.getLabel());
            Assert.assertEquals(GRAPH_DESCRIPTION, persistedGraphEntity.getDescription());
            Assert.assertEquals(3, persistedGraphEntity.getVertices().size());
            Assert.assertEquals(1, persistedGraphEntity.getEdges().size());

            return null;
        });

        /*
         * Delete
         */
        transactionOperations.execute(status -> {
            // Load the entity before deleting, otherwise it is detached and will throw an exception
            final GraphContainerEntity graphContainerEntity = persistenceAccessor.get(GraphContainerEntity.class, originalContainerEntity.getId());
            persistenceAccessor.delete(graphContainerEntity);
            return null;
        });
        transactionOperations.execute(status -> {
            Assert.assertEquals(0, persistenceAccessor.find("Select g from GraphContainerEntity g").size());
            Assert.assertEquals(0, persistenceAccessor.find("Select g from GraphEntity g").size());
            Assert.assertEquals(0, persistenceAccessor.find("Select v from VertexEntity v").size());
            Assert.assertEquals(0, persistenceAccessor.find("Select e from EdgeEntity e").size());
            Assert.assertEquals(0, persistenceAccessor.find("Select p from PropertyEntity p").size());
            return null;
        });
    }

    private GraphEntity createExampleGraph(final String namespace) {
        final GraphEntity graph = new GraphEntity();
        graph.setNamespace(namespace);
        graph.setProperty(GenericProperties.LABEL, String.class, GRAPH_LABEL);
        graph.setProperty(GenericProperties.DESCRIPTION, String.class, GRAPH_DESCRIPTION);


        final VertexEntity v1 = new VertexEntity();
        v1.setNamespace(namespace);
        v1.setProperty(GenericProperties.ID, String.class, "v1");
        v1.setProperty(GenericProperties.LABEL, String.class, "Vertex 1");

        final VertexEntity v2 = new VertexEntity();
        v2.setNamespace(namespace);
        v2.setProperty(GenericProperties.ID, String.class, "v2");
        v2.setProperty(GenericProperties.LABEL, String.class, "Vertex 2");

        final VertexEntity v3 = new VertexEntity();
        v3.setNamespace(GRAPH_NAMESPACE);
        v3.setProperty(GenericProperties.ID, String.class, "v3");
        v3.setProperty(GenericProperties.LABEL, String.class, "Vertex 3");

        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);

        final EdgeEntity edge = new EdgeEntity();
        edge.setNamespace(namespace);
        edge.setSource(v1);
        edge.setTarget(v2);
        graph.addEdge(edge);

        return graph;
    }
}
