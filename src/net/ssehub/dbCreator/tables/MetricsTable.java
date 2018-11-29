package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;



public class MetricsTable {
    
    private PreparedStatement insertMetric;
    private Map<String, Integer> metricIDs;
    
    
    public MetricsTable(Connection con) throws SQLException {
        createInsertStmt(con);
        metricIDs = new HashMap<>();
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_metric (metric_name) VALUES(?);";
        insertMetric = con.prepareStatement(sql);
    }
    
    public void addAndInsert(String metric) throws SQLException {
        
        if (!metricIDs.containsKey(metric)) {
            insertMetric.setString(1, metric);
            try {
                insertMetric.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                //Drop duplicates silently
            }
        }
        
    }
    
    public void addBatch(String metric) throws SQLException {
        
        if (!metricIDs.containsKey(metric)) {
            insertMetric.setString(1, metric);
            insertMetric.addBatch();
        }
    }
    
    public void executeBatch() throws SQLException {
        
        try {
            insertMetric.executeBatch();
        } catch (SQLIntegrityConstraintViolationException e) {
            //Drop duplicates silently
        }
    }
}
