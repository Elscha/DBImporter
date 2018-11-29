package net.ssehub.dbCreator.linux;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.ssehub.dbCreator.DBUtils;
import net.ssehub.dbCreator.Runner;
import net.ssehub.dbCreator.tables.FunctionTable;
import net.ssehub.dbCreator.tables.LinuxMeasureTable;
import net.ssehub.dbCreator.tables.LinuxMeasureValuesTable;
import net.ssehub.dbCreator.tables.LinuxTable;
import net.ssehub.kernel_haven.util.io.csv.CsvReader;

public class LinuxConverter {
    
    private LinuxTable linux;
    private FunctionTable functions;
    private LinuxMeasureTable measureTable;
    private LinuxMeasureValuesTable valuesTable;
    private Connection con;
    private String version;
    private String[] metricsNames;
    
    public LinuxConverter(String version) throws IOException, SQLException {
        con = DBUtils.createConnection();
        linux = new LinuxTable(con);
        functions = new FunctionTable(con);
        measureTable = new LinuxMeasureTable(con);
        valuesTable = new LinuxMeasureValuesTable(con);
        this.version = version;
    }
    
    public void readFile(File sourceFile) throws SQLException {
        Integer linuxID = linux.add(version);
        linux.close();
        
        if (null != linuxID) {
            try (CsvReader in = new CsvReader(new FileReader(sourceFile))) {
                String[] line;
                int rowIndex = -1;
                while ((line = in.readNextRow()) != null) {
                    rowIndex++;
                    
                    if (rowIndex == 0) {
                        int nMetrics = line.length - 3;
                        metricsNames = new String[nMetrics];
                        System.arraycopy(line, 3, metricsNames, 0, nMetrics);
                    } else {
                        
                        if (rowIndex % 100 == 0) {
                            Runner.LOGGER.logInfo("  Processing " + rowIndex + "th line.");
                        }
                        
                        functions.add(line[0], line[2]);
                        Integer functionID = functions.getID(line[0], line[2]);
                        Integer measureID = measureTable.add(linuxID, functionID, line[1]);
                        
                        if (null != functionID && null != measureID) {
                            con.setAutoCommit(false);
                            
                            for (int i = 3; i < line.length; i++) {
                                String metric = metricsNames[i - 3];
                                valuesTable.addBatch(measureID, metric, line[i]);
                            }
                            
                            valuesTable.executeBatch();
                            con.setAutoCommit(true);
                        }
                    }
                }
            } catch (IOException e) {
                Runner.LOGGER.logException("Could not read line from " + sourceFile.getAbsolutePath(), e);
            }
        }
    }

    public void close() {
        if (null != con) {
            try {
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not commit data", e);
            }
            
            try {
                con.close();
            } catch (SQLException e) {
                Runner.LOGGER.logException("Could not close SQL connection", e);
            }
            
            con = null;
        }
    }
}
