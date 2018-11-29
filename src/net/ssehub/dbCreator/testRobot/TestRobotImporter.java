package net.ssehub.dbCreator.testRobot;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import net.ssehub.dbCreator.Runner;

public class TestRobotImporter {
    private static final File ERROR_REPORTS_FOLDER = new File("data");
    public static final int MAX_THREADS = 8;

    public static void main(String[] args) throws IOException, SQLException {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler () {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Runner.LOGGER.logException("Uncaught exception in thread " + t.getName(), e);
            }            
        });
        
        long time = System.currentTimeMillis();
        
        File[] reports = ERROR_REPORTS_FOLDER.listFiles();
        
        // Process first file to create metric table
        ErrorReportsConverter reader = new ErrorReportsConverter(true);
        Runner.LOGGER.logInfo("Processing 1st of " + reports.length + ": " + reports[0].getName());
        reader.readFile(reports[0]);
        reader.close();
        
        // Convert all others in separate threads
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 1; i < reports.length; i++) {
            final int fileID = i;
            
            executor.submit(() -> {
                String fileName = reports[fileID].getName();
                int pos = fileName.lastIndexOf('.');
                if (pos >= 0) {
                    fileName = fileName.substring(0, pos);
                }
                String thName = fileName + " (" + fileID +"/" +reports.length + ")";
                Thread.currentThread().setName(thName);
                Runner.LOGGER.logInfo("Processing " + fileID + " of " + reports.length + ": " + reports[fileID].getName());
                try {
                    ErrorReportsConverter converter = new ErrorReportsConverter(false);
                    converter.readFile(reports[fileID]);
                    converter.close();
                } catch (SQLException e) {
                    Runner.LOGGER.logException("Could not create ErrorReportsConverter due to SQL", e);
                } catch (IOException e) {
                    Runner.LOGGER.logException("Could not create ErrorReportsConverter due to IO errors", e);
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Runner.LOGGER.logInfo("STATUS: " + executor.getQueue().size() + " files still pending.");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                // No action needed
            }
        }
        
        time = System.currentTimeMillis() - time;
        Runner.LOGGER.logInfo("Importing data into DB took: " + (time / 1000) + " sec.");
    }

}
