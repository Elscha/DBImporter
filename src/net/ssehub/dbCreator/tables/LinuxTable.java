package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class LinuxTable {
    private PreparedStatement insertLinux;
    private PreparedStatement getLinux;
    private Connection con;
    
    public LinuxTable(Connection con) throws SQLException {
        createInsertStmt(con);
        this.con = con;
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_linux (version) VALUES(?);";
        insertLinux = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        sql = "SELECT linux_id FROM tbl_linux WHERE version = ?";
        getLinux = con.prepareStatement(sql);
    }
    
    public Integer add(String version) throws SQLException {
        Integer id = null;
        try {
            insertLinux.setString(1, version);
            // Insert into DB
            int affectedRows  = insertLinux.executeUpdate();
            
            // Retrieve result
            if (affectedRows == 0) {
                System.err.println("Could not add Linux version: " + version);
            } else {
                try (ResultSet generatedKeys = insertLinux.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        id = generatedKeys.getInt(1);
                    }
                    else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Drop duplicates silently
            getLinux.setString(1, version);
            ResultSet rs = getLinux.executeQuery();
            rs.first();
            id = rs.getInt(1);
        }
        
        return id;
    }
    
    public void close() {
        if (null != con) {
            try {
                insertLinux.close();
                getLinux.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con =  null;
        }
    }
}
