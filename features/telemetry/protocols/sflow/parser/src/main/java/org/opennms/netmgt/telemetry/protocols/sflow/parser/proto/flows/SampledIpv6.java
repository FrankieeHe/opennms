/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.telemetry.protocols.sflow.parser.proto.flows;

import java.nio.ByteBuffer;

import org.bson.BsonWriter;
import org.opennms.netmgt.telemetry.common.utils.BufferUtils;
import org.opennms.netmgt.telemetry.protocols.sflow.parser.InvalidPacketException;

import com.google.common.base.MoreObjects;

// struct sampled_ipv6 {
//    unsigned int length;     /* The length of the IP packet excluding
//                                lower layer encapsulations */
//    unsigned int protocol;   /* IP next header
//                                (for example, TCP = 6, UDP = 17) */
//    ip_v6 src_ip;            /* Source IP Address */
//    ip_v6 dst_ip;            /* Destination IP Address */
//    unsigned int src_port;   /* TCP/UDP source port number or equivalent */
//    unsigned int dst_port;   /* TCP/UDP destination port number or equivalent */
//    unsigned int tcp_flags;  /* TCP flags */
//    unsigned int priority;   /* IP priority */
// };

public class SampledIpv6 implements FlowData {
    public final long length;
    public final long protocol;
    public final IpV6 src_ip;
    public final IpV6 dst_ip;
    public final long src_port;
    public final long dst_port;
    public final long tcp_flags;
    public final long priority;

    public SampledIpv6(final ByteBuffer buffer) throws InvalidPacketException {
        this.length = BufferUtils.uint32(buffer);
        this.protocol = BufferUtils.uint32(buffer);
        this.src_ip = new IpV6(buffer);
        this.dst_ip = new IpV6(buffer);
        this.src_port = BufferUtils.uint32(buffer);
        this.dst_port = BufferUtils.uint32(buffer);
        this.tcp_flags = BufferUtils.uint32(buffer);
        this.priority = BufferUtils.uint32(buffer);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("length", this.length)
                .add("protocol", this.protocol)
                .add("src_ip", this.src_ip)
                .add("dst_ip", this.dst_ip)
                .add("src_port", this.src_port)
                .add("dst_port", this.dst_port)
                .add("tcp_flags", this.tcp_flags)
                .add("priority", this.priority)
                .toString();
    }

    @Override
    public void writeBson(final BsonWriter bsonWriter) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeInt32("length", (int) this.length);
        bsonWriter.writeInt32("protocol", (int) this.protocol);
        bsonWriter.writeName("src_ip");
        this.src_ip.writeBson(bsonWriter);
        bsonWriter.writeName("dst_ip");
        this.dst_ip.writeBson(bsonWriter);
        bsonWriter.writeInt32("src_port", (int) this.src_port);
        bsonWriter.writeInt32("dst_port", (int) this.dst_port);
        bsonWriter.writeInt32("tcp_flags", (int) this.tcp_flags);
        bsonWriter.writeInt32("priority", (int) this.priority);
        bsonWriter.writeEndDocument();
    }
}
