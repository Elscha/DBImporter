package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class FunctionTable {
    private PreparedStatement insertFunction;
    private Connection con;
    
    public FunctionTable(Connection con) throws SQLException {
        createInsertStmt(con);
        this.con =  con;
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_functions (function_path, function_name) VALUES(?, ?);";
        insertFunction = con.prepareStatement(sql);
    }
    
    public void add(String path, String function) throws SQLException {
        try {
            insertFunction.setString(1, path);
            insertFunction.setString(2, function);
            insertFunction.execute();
        } catch (SQLIntegrityConstraintViolationException e) {
            // Drop duplicates silently
        }
    }
    
    public Integer getID(String path, String function) throws SQLException {
        String sql = "SELECT function_id FROM tbl_functions WHERE function_path = ? AND function_name = ?;";
        PreparedStatement getFunction = con.prepareStatement(sql);
        
        getFunction.setString(1, path);
        getFunction.setString(2, function);
        ResultSet rs = getFunction.executeQuery();
        
        rs.first();
        Integer result = rs.getInt(1);
        if (result == 0) {
            result =  null;
        }
        
        getFunction.close();
        return result;
    }
    
}
