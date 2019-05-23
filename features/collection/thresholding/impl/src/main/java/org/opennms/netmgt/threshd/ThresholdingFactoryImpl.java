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

package org.opennms.netmgt.threshd;

import org.opennms.netmgt.collection.api.ServiceParameters;
import org.opennms.netmgt.dao.api.ResourceStorageDao;
import org.opennms.netmgt.rrd.RrdRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ThresholdingFactoryImpl implements ThresholdingFactory {

    @Autowired
    private ThresholdingEventProxy eventProxy;

    public ThresholdingEventProxy getEventProxy() {
        return eventProxy;
    }

    public void setEventProxy(ThresholdingEventProxy eventProxy) {
        this.eventProxy = eventProxy;
    }

    public ThresholdingVisitor createThresholder() {
        // FIXME - pass through CollectorThresholdingSet
        return new ThresholdingVisitorImpl(null);
    }

    @Override
    public ThresholdingVisitor createThresholder(int nodeId, String hostAddress, String serviceName, RrdRepository repo, ServiceParameters svcParams,
            ResourceStorageDao resourceStorageDao)
            throws ThresholdInitializationException {
        return ThresholdingVisitorImpl.create(nodeId, hostAddress, serviceName, repo, svcParams, resourceStorageDao);
    }

    @Override
    public LatencyThresholdingSet getLatencyThresholdingSet(int nodeId, String hostAddress, String serviceName, String location, RrdRepository repository,
            ResourceStorageDao resourceStorageDao) throws ThresholdInitializationException {
        return new LatencyThresholdingSetImpl(nodeId, hostAddress, serviceName, location, repository, resourceStorageDao, eventProxy);
    }
}
