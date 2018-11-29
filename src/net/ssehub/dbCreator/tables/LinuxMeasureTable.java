package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.ssehub.dbCreator.Runner;

public class LinuxMeasureTable {
    private PreparedStatement insertLinuxMeasure;
    
    public LinuxMeasureTable(Connection con) throws SQLException {
        createInsertStmt(con);
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_linux_measure (linux_id, function_id, linux_measure_line) "
            + "VALUES(?, ?, ?);";
        insertLinuxMeasure = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
    
    
    private void prepareStatement(int linuxID, int functionID, String line) throws SQLException {
        insertLinuxMeasure.setInt(1, linuxID);
        insertLinuxMeasure.setInt(2, functionID);
        insertLinuxMeasure.setInt(3, Integer.valueOf(line));
    }
    
    public Integer add(int linuxID, int functionID, String line) throws SQLException {
        Integer id = null;
        prepareStatement(linuxID, functionID, line);
        // Insert into DB
        int affectedRows  = insertLinuxMeasure.executeUpdate();
        
        // Retrieve result
        if (affectedRows == 0) {
            Runner.LOGGER.logError("Could not add Linux measure for " + linuxID + " and function " + functionID);
        } else {
            try (ResultSet generatedKeys = insertLinuxMeasure.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        
        return id;
    }
    
    public void addBatch(int linuxID, int functionID, String line) throws SQLException {
        prepareStatement(linuxID, functionID, line);
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
