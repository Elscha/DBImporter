package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import net.ssehub.dbCreator.Runner;

public class ErrMeasuresTable {
    private PreparedStatement insertMeasurement;
    
    public ErrMeasuresTable(Connection con) throws SQLException {
        createInsertStmt(con);
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_error_measures (metric_id, bug_id, value) "
            + "VALUES((SELECT metric_id from tbl_metric WHERE metric_name=?), ?, ?);";
        insertMeasurement = con.prepareStatement(sql);
    }
    
    public void addAndInsert(String metricName, int bug_id, String value)
        throws SQLException, ParseException {
        
        // Prepare
        insertMeasurement.setString(1, metricName);
        insertMeasurement.setInt(2, bug_id);
        insertMeasurement.setDouble(3, Double.valueOf(value));
        
        // Insert into DB
        insertMeasurement.executeUpdate();
    }
    
    public void addBatch(String metricName, int bug_id, String value) throws SQLException {
        
        // Prepare
        insertMeasurement.setString(1, metricName);
        insertMeasurement.setInt(2, bug_id);
        insertMeasurement.setDouble(3, Double.valueOf(value));
        
        // Insert into DB
        insertMeasurement.addBatch();
    }
    
    public void executeBatch() {
        try {
            insertMeasurement.executeBatch();
        } catch (SQLException e) {
            Runner.LOGGER.logException("Could not execute batch", e);
        }
    }
}
