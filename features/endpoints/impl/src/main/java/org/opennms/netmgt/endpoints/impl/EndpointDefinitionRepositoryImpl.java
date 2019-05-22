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

package org.opennms.netmgt.endpoints.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.opennms.netmgt.endpoints.api.EndpointDefinition;
import org.opennms.netmgt.endpoints.api.EndpointDefinitionRepository;
import org.opennms.netmgt.endpoints.api.EndpointType;

public class EndpointDefinitionRepositoryImpl implements EndpointDefinitionRepository {

    private final List<EndpointDefinition> endpoints = new ArrayList<>();

    @Override
    public List<EndpointDefinition> findAll() {
        return endpoints.stream().map(s -> new EndpointDefinition(s)).collect(Collectors.toList());
    }

    @Override
    public List<EndpointDefinition> findByType(final EndpointType endpointType) {
        Objects.requireNonNull(endpointType);
        return endpoints.stream().filter(s -> endpointType == s.getType()).map(s -> new EndpointDefinition(s)).collect(Collectors.toList());
    }

    @Override
    public EndpointDefinition get(long endpointId) throws NoSuchElementException {
        Objects.requireNonNull(endpointId);
        return endpoints.stream()
                .filter(e -> Objects.equals(e.getId(), endpointId))
                .findAny().orElseThrow(() -> new NoSuchElementException("Could not find endpoint with id '" + endpointId + "'"));
    }

    @Override
    public EndpointDefinition get(String endpointUid) throws NoSuchElementException {
        Objects.requireNonNull(endpointUid);
        return endpoints.stream()
                .filter(e -> Objects.equals(endpointUid, e.getUid()))
                .findAny().orElseThrow(() -> new NoSuchElementException("Could not find endpoint with uid '" + endpointUid + "'"));
    }

    private EndpointDefinition find(long endpointId) {
        Objects.requireNonNull(endpointId);
        return endpoints.stream()
                .filter(e -> Objects.equals(endpointId, e.getId()))
                .findAny()
                .orElse(null);
    }

    @Override
    public EndpointDefinition saveOrUpdate(final EndpointDefinition endpointDefinition) {
        Objects.requireNonNull(endpointDefinition);

        // TODO MVR add validation
        final EndpointDefinition persistedEndpoint = endpointDefinition.getId() != null ? find(endpointDefinition.getId()) : null;
        if (persistedEndpoint != null) {
            persistedEndpoint.merge(endpointDefinition);
            return get(endpointDefinition.getId());
        } else {
            final EndpointDefinition newDefinition = new EndpointDefinition(endpointDefinition);
            newDefinition.setId(Long.valueOf(endpoints.size() + 1));
            endpoints.add(newDefinition);
            return get(newDefinition.getId());
        }
    }

    @Override
    public boolean delete(EndpointDefinition endpointDefinition) throws NoSuchElementException {
        Objects.requireNonNull(endpointDefinition);
        final EndpointDefinition persistedDefinition = get(endpointDefinition.getId());
        if (persistedDefinition != null) {
            return endpoints.remove(persistedDefinition);
        }
        return false;
    }
}
