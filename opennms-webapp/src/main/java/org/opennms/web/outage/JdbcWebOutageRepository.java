package org.opennms.web.outage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.opennms.web.outage.OutageFactory.OutageType;
import org.opennms.web.outage.OutageFactory.SortStyle;
import org.opennms.web.outage.filter.Filter;
import org.opennms.web.outage.filter.OutageCriteria;
import org.opennms.web.outage.filter.OutageIdFilter;
import org.opennms.web.outage.filter.OutageCriteria.BaseOutageCriteriaVisitor;
import org.opennms.web.outage.filter.OutageCriteria.OutageCriteriaVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcWebOutageRepository implements WebOutageRepository {

    @Autowired
    SimpleJdbcTemplate m_simpleJdbcTemplate;
    
    public int countMatchingOutages(OutageCriteria criteria) {
        String sql = getSql("SELECT COUNT(OUTAGEID) as OUTAGECOUNT FROM OUTAGES LEFT OUTER JOIN NODE USING (NODEID) LEFT OUTER JOIN SERVICE USING (SERVICEID)", criteria);
        return queryForInt(sql, paramSetter(criteria));
    }

    public Outage[] getMatchingOutages(OutageCriteria criteria) {
        String sql = getSql("SELECT OUTAGES.*, NODE.NODELABEL, IPINTERFACE.IPHOSTNAME, SERVICE.SERVICENAME, "
                            + "NOTIFICATIONS.NOTIFYID, NOTIFICATIONS.ANSWEREDBY FROM OUTAGES "
                            + "JOIN NODE USING(NODEID) "
                            + "JOIN IPINTERFACE ON OUTAGES.NODEID=IPINTERFACE.NODEID AND OUTAGES.IPADDR=IPINTERFACE.IPADDR "
                            + "JOIN IFSERVICES ON OUTAGES.NODEID=IFSERVICES.NODEID AND OUTAGES.IPADDR=IFSERVICES.IPADDR AND "
                            + "OUTAGES.SERVICEID=IFSERVICES.SERVICEID "
                            + "LEFT OUTER JOIN SERVICE ON OUTAGES.SERVICEID=SERVICE.SERVICEID "
                            + "LEFT OUTER JOIN NOTIFICATIONS ON SVCLOSTEVENTID=NOTIFICATIONS.EVENTID", criteria);
        return getOutages(sql, paramSetter(criteria));
    }

    public Outage getOutage(int outageId) {
        OutageCriteria criteria = new OutageCriteria(new OutageIdFilter(outageId));
        Outage[] outages = getMatchingOutages(criteria);
        if (outages.length == 0) {
            return null;
        } else {
            return outages[0];
        }
    }

    private Outage[] getOutages(String sql, PreparedStatementSetter setter) {
        List<Outage> outages = queryForList(sql, setter, new OutageMapper());
        return outages.toArray(new Outage[0]);
    }

    private String getSql(final String selectClause, final OutageCriteria criteria) {
        final StringBuilder buf = new StringBuilder(selectClause);

        criteria.visit(new OutageCriteriaVisitor<RuntimeException>() {

            boolean first = true;

            public void and(StringBuilder buf) {
                if (first) {
                    buf.append(" WHERE ");
                    first = false;
                } else {
                    buf.append(" AND ");
                }
            }

            public void visitOutageType(OutageType outageType) {
                and(buf);
                buf.append(outageType.getClause());
            }


            public void visitFilter(Filter filter) {
                and(buf);
                buf.append(filter.getParamSql());
            }

            public void visitSortStyle(SortStyle sortStyle) {
                buf.append(" ");
                buf.append(sortStyle.getOrderByClause());
            }

            public void visitLimit(int limit, int offset) {
                buf.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
            }

        });

        return buf.toString();
    }

    private int queryForInt(String sql, PreparedStatementSetter setter) throws DataAccessException {
        Number number = (Number) queryForObject(sql, setter, new SingleColumnRowMapper(Integer.class));
        return (number != null ? number.intValue() : 0);
    }

    @SuppressWarnings("unchecked")
    private Object queryForObject(String sql, PreparedStatementSetter setter, RowMapper rowMapper) throws DataAccessException {
        return DataAccessUtils.requiredSingleResult((List) jdbc().query(sql, setter, new RowMapperResultSetExtractor(rowMapper, 1)));
    }


    @SuppressWarnings("unchecked")
    private <T> List<T> queryForList(String sql, PreparedStatementSetter setter, ParameterizedRowMapper<T> rm) {
        return (List<T>) jdbc().query(sql, setter, new RowMapperResultSetExtractor(rm));
    }
    
    private JdbcOperations jdbc() {
        return m_simpleJdbcTemplate.getJdbcOperations();
    }

    private PreparedStatementSetter paramSetter(final OutageCriteria criteria, final Object... args) {
        return new PreparedStatementSetter() {
            int paramIndex = 1;
            public void setValues(final PreparedStatement ps) throws SQLException {
                for(Object arg : args) {
                    ps.setObject(paramIndex, arg);
                    paramIndex++;
                }
                criteria.visit(new BaseOutageCriteriaVisitor<SQLException>() {
                    @Override
                    public void visitFilter(Filter filter) throws SQLException {
                        paramIndex += filter.bindParam(ps, paramIndex);
                    }
                });
            }
        };
    }
    
    private static class OutageMapper implements ParameterizedRowMapper<Outage> {
        public Outage mapRow(ResultSet rs, int rowNum) throws SQLException {
            Outage outage = new Outage();
            outage.outageId = rs.getInt("outageID");
            outage.lostServiceEventId = ((Integer) rs.getObject("svcLostEventID"));
            outage.regainedServiceEventId = ((Integer) rs.getObject("svcRegainedEventID"));
            outage.nodeId = rs.getInt("nodeID");
            outage.ipAddress = ((String) rs.getObject("ipAddr"));
            outage.serviceId = rs.getInt("serviceID");
            outage.lostServiceTime = getTimestamp("ifLostService", rs);
            outage.regainedServiceTime = getTimestamp("ifRegainedService", rs);
            outage.suppressTime = getTimestamp("suppressTime", rs);
            outage.suppressedBy = ((String) rs.getObject("suppressedBy"));
            
            outage.hostname = ((String) rs.getObject("ipHostname"));
            outage.lostServiceNotificationAcknowledgedBy = ((String) rs.getObject("answeredBy"));
            outage.lostServiceNotificationId = ((Integer) rs.getObject("notifyId"));
            outage.nodeLabel = ((String) rs.getObject("nodeLabel"));
            outage.serviceName = ((String) rs.getObject("serviceName"));

            return outage;
        }
        
        private Date getTimestamp(String field, ResultSet rs) throws SQLException{
            if(rs.getTimestamp(field) != null){
                return new Date(rs.getTimestamp(field).getTime());
            }else{
                return null;
            }
        }
    }

}
