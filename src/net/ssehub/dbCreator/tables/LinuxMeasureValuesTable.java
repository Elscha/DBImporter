package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.ssehub.dbCreator.Runner;

public class LinuxMeasureValuesTable {
    private PreparedStatement insertLinuxMeasure;
    
    public LinuxMeasureValuesTable(Connection con) throws SQLException {
        createInsertStmt(con);
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_linux_measure_values (measure_id, metric_id, value) VALUES(?, "
            + "(SELECT metric_id FROM tbl_metric WHERE metric_name=?), ?);";
        insertLinuxMeasure = con.prepareStatement(sql);
    }
    
    
    private void prepareStatement(int measureID, String metric, String value) throws SQLException {
        insertLinuxMeasure.setInt(1, measureID);
        insertLinuxMeasure.setString(2, metric);
        insertLinuxMeasure.setDouble(3, Double.valueOf(value));
    }
    
    public void addBatch(int measureID, String metric, String value) throws SQLException {
        prepareStatement(measureID, metric, value);
        insertLinuxMeasure.addBatch();
    }
    
    public void executeBatch() {
        try {
            insertLinuxMeasure.executeBatch();
        } catch (SQLException e) {
            Runner.LOGGER.logException("Could not execute batch", e);
        }
    }
}
