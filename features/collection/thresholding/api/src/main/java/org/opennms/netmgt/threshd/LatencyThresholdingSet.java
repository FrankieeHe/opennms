/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
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

import java.util.List;
import java.util.Map;

import org.opennms.netmgt.dao.api.IfLabel;
import org.opennms.netmgt.dao.api.ResourceStorageDao;
import org.opennms.netmgt.rrd.RrdRepository;
import org.opennms.netmgt.xml.event.Event;

/**
 * <p>LatencyThresholdingSet class.</p>
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 * @version $Id: $
 */
public interface LatencyThresholdingSet extends ThresholdingSet {

    /**
     * <p>Constructor for LatencyThresholdingSet.</p>
     *
     * @param nodeId a int.
     * @param hostAddress a {@link java.lang.String} object.
     * @param serviceName a {@link java.lang.String} object.
     * @param repository a {@link org.opennms.netmgt.rrd.RrdRepository} object.
     * @param interval a long.
     * @throws ThresholdInitializationException 
     */
    LatencyThresholdingSet create(int nodeId, String hostAddress, String serviceName, String location, RrdRepository repository, ResourceStorageDao resourceStorageDao)
            throws ThresholdInitializationException;

    /*
     * Latency thresholds use ds-type="if"
     * Returns true if any attribute of the service is involved in any of defined thresholds.
     */
    /**
     * <p>hasThresholds</p>
     *
     * @param attributes a {@link java.util.Map} object.
     * @return a boolean.
     */
    public boolean hasThresholds(Map<String, Double> attributes);

    /*
     * Apply thresholds definitions for specified service using attributesMap as current values.
     * Return a list of events to be send if some thresholds must be triggered or be rearmed.
     */
    public List<Event> applyThresholds(String svcName, Map<String, Double> attributes, IfLabel ifLabelDao);

}
