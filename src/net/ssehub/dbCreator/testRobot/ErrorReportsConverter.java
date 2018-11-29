package net.ssehub.dbCreator.testRobot;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import net.ssehub.dbCreator.DBUtils;
import net.ssehub.dbCreator.Runner;
import net.ssehub.dbCreator.tables.BugTable;
import net.ssehub.dbCreator.tables.ErrMeasuresTable;
import net.ssehub.dbCreator.tables.FunctionTable;
import net.ssehub.dbCreator.tables.MetricsTable;
import net.ssehub.kernel_haven.io.excel.ExcelBook;
import net.ssehub.kernel_haven.io.excel.ExcelSheetReader;

public class ErrorReportsConverter {
    private MetricsTable metrics;
    private FunctionTable functions;
    private BugTable bugs;
    private ErrMeasuresTable bugMeasures;
    private int metricsColumn = 0;
    private String[] metricsNames;
    private Connection con;
    private boolean handleMetrics;
    /**
     * Row number (0-based), bugID.
     */
    private Map<Integer, Integer> bugIDs;

    public ErrorReportsConverter(boolean handleMetrics) throws SQLException, IOException {
        this.con = DBUtils.createConnection();
        metrics = new MetricsTable(con);
        functions = new FunctionTable(con);
        bugs = new BugTable(con);
        bugMeasures = new ErrMeasuresTable(con);
        bugIDs = new HashMap<>();
        this.handleMetrics = handleMetrics;
    }
    
    public void readFile(File srcFile) {
        String fileName = srcFile.getName();
        int pos = fileName.lastIndexOf('.');
        if (pos >= 0) {
            fileName = fileName.substring(0, pos);
        }
        
        
        try (ExcelBook wb = new ExcelBook(srcFile);
            ExcelSheetReader reader = wb.getReader(0)) {
            String[][] content = reader.readFull();
            
            processHeader(content[0]);
            
            try {
                Runner.LOGGER.logError("  Add functions.");
                for (int i = 1; i < content.length; i++) {
                    // Add all functions, may appear multiple times -> commit each at once
                    String[] row = content[i];
                    functions.add(row[4], row[6]);
                }
                
                
                Runner.LOGGER.logError("  Add bugs.");
                for (int i = 1; i < content.length; i++) {
                    // Add all bug reports
                    String[] row = content[i];
                    
                    // Add Bug reports to bug table, should not exist
                    Integer id = bugs.add(row[0], row[1], row[2], row[3], row[5], fileName, row[4], row[6]);
                    bugIDs.put(i, id);
                }
                
                Runner.LOGGER.logError("  Process data");
                try {
                    con.setAutoCommit(false);
                } catch (SQLException e2) {
                    Runner.LOGGER.logException("Could not set SQL autocommit to " + false, e2);
                }
                for (int i = 1; i < content.length; i++) {
                    if (i % 10 == 1) {
                        if (i == 11 || i == 111) {
                            Runner.LOGGER.logError("    Processing " + i + "th data row of " + content.length + " rows.");
                        } else {
                            Runner.LOGGER.logError("    Processing " + i + "st data row of " + content.length + " rows.");
                        }
                    }

                    // Add metric values
                    String[] row = content[i];
                    
                    // Add Bug reports to bug table, should not exist
                    Integer id = bugIDs.get(i);
                    if (null != id) {
                        for (int j = metricsColumn; j < row.length; j++) {
                            bugMeasures.addBatch(metricsNames[j - metricsColumn], id, row[j]);
                        }
                    }
                    
                    bugMeasures.executeBatch();
                }
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e2) {
                    Runner.LOGGER.logException("Could not set SQL autocommit to " + true, e2);
                }
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not send SQL data", e);
            } catch (ParseException e) {
                Runner.LOGGER.logException("Could not parse date", e);
            }
            
        } catch (IOException e) {
            Runner.LOGGER.logException("Could not read Excel WB: " + srcFile.getAbsolutePath(), e);
        }
    }

    private void processHeader(String[] row) {
        if (handleMetrics) {
            try {
                con.setAutoCommit(false);
            } catch (SQLException e2) {
                Runner.LOGGER.logException("Could not set SQL autocommit to " + false, e2);
            }
        }
        int column = -1;
        for (String headerElem : row) {
            column++;
            if (metricsColumn == 0 && headerElem.equals("LoC")) {
                metricsColumn = column;
                int nMetrics = row.length - column;
                metricsNames = new String[nMetrics];
                Runner.LOGGER.logError("  Header lists " + nMetrics + " metrics.");
                
                // Now, it is the index within the array
                column = 0;
            }
            
            if (metricsColumn != 0) {
                metricsNames[column] = headerElem;
                if (handleMetrics) {
                    try {
                        metrics.addBatch(headerElem);
                    } catch (SQLException e) {
                        Runner.LOGGER.logException("Could not commit batch", e);
                    }
                }
            }
        }
        
        if (handleMetrics) {
            try {
                metrics.executeBatch();
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not execute batch", e);
            }
        }
        
        try {
            con.setAutoCommit(true);
        } catch (SQLException e2) {
            Runner.LOGGER.logException("Could not set SQL autocommit to " + true, e2);
        }
    }
    
    public void close() {
        if (null != con) {
            try {
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not commit remaining data", e);
            }
            
            try {
                con.close();
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not close SQL connection", e);
            }
            
            con = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        //close();
        super.finalize();
    }
}
