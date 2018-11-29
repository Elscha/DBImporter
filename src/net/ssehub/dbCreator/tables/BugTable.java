package net.ssehub.dbCreator.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import net.ssehub.dbCreator.DBUtils;
import net.ssehub.dbCreator.Runner;

public class BugTable {
    private PreparedStatement insertFunction;
    
    public BugTable(Connection con) throws SQLException {
        createInsertStmt(con);
    }

    private void createInsertStmt(Connection con) throws SQLException {
        String sql = "INSERT INTO tbl_bugs (bug_date, bug_repository, bug_commit, bug_severity, bug_line, bug_source, function_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, (SELECT function_id from tbl_functions WHERE function_path=? AND function_name=?));";
        insertFunction = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
    
    public Integer add(String date, String repo, String commit, String serverity, String lineNo, String source,
        String path, String function) throws SQLException, ParseException {
        
        // Prepare
        insertFunction.setObject(1, DBUtils.toDate(date));
        insertFunction.setString(2, repo);
        
        // max commit length is set to 40 chars. However, some commits contain branches and, thus are longer
        if (commit.length() > 40) {
            commit = commit.substring(0, 39);
        }
        
        insertFunction.setString(3, commit);
        insertFunction.setString(4, serverity);
        insertFunction.setDouble(5, Double.valueOf(lineNo));
        insertFunction.setString(6, source);
        insertFunction.setString(7, path);
        insertFunction.setString(8, function);
        
        // Insert into DB
        int affectedRows  = insertFunction.executeUpdate();
        
        // Retrieve result
        Integer id = null;
        if (affectedRows == 0) {
            Runner.LOGGER.logError("Could not add bug report at " + date);
        } else {
            try (ResultSet generatedKeys = insertFunction.getGeneratedKeys()) {
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
    
}
