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

package org.opennms.netmgt.enlinkd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="user_defined_links")
public class UserDefinedLink {

    private Integer nodeIdA;
    private String componentLabelA;
    private Integer nodeIdZ;
    private String componentLabelZ;
    private String linkId;
    private String linkLabel;
    private String owner;
    private Integer dbId;

    @Column(name="node_id_a", nullable = false)
    public Integer getNodeIdA() {
        return nodeIdA;
    }

    public void setNodeIdA(Integer nodeIdA) {
        this.nodeIdA = nodeIdA;
    }

    @Column(name="component_label_a")
    public String getComponentLabelA() {
        return componentLabelA;
    }

    public void setComponentLabelA(String componentLabelA) {
        this.componentLabelA = componentLabelA;
    }

    @Column(name="node_id_z", nullable = false)
    public Integer getNodeIdZ() {
        return nodeIdZ;
    }

    public void setNodeIdZ(Integer nodeIdZ) {
        this.nodeIdZ = nodeIdZ;
    }

    @Column(name="component_label_z")
    public String getComponentLabelZ() {
        return componentLabelZ;
    }

    public void setComponentLabelZ(String componentLabelZ) {
        this.componentLabelZ = componentLabelZ;
    }

    @Column(name="link_id", nullable = false)
    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    @Column(name="link_label")
    public String getLinkLabel() {
        return linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    @Column(name="owner", nullable = false)
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Id
    @Column(name="id", nullable = false)
    @SequenceGenerator(name = "opennmsSequence", sequenceName = "opennmsNxtId")
    @GeneratedValue(generator = "opennmsSequence")
    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    @Override
    public String toString() {
        return "UserDefinedLink{" +
                "nodeIdA=" + nodeIdA +
                ", componentLabelA='" + componentLabelA + '\'' +
                ", nodeIdZ=" + nodeIdZ +
                ", componentLabelZ='" + componentLabelZ + '\'' +
                ", linkId='" + linkId + '\'' +
                ", linkLabel='" + linkLabel + '\'' +
                ", owner='" + owner + '\'' +
                ", dbId=" + dbId +
                '}';
    }
}
