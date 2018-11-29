package net.ssehub.dbCreator.testRobot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TestRobotImporter {
    private static final File ERROR_REPORTS_FOLDER = new File("data");
    public static final int MAX_THREADS = 8;

    public static void main(String[] args) throws IOException, SQLException {
        long time = System.currentTimeMillis();
        
        File[] reports = ERROR_REPORTS_FOLDER.listFiles();
        
        // Process first file to create metric table
        ErrorReportsConverter reader = new ErrorReportsConverter(true);
        System.out.println("Processing 1st of " + reports.length + ": " + reports[0].getName());
        reader.readFile(reports[0]);
        reader.close();
        
        // Convert all others in separate threads
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 1; i < reports.length; i++) {
            final int fileID = i;
            
            executor.submit(() -> {
                System.out.println("Processing " + fileID + " of " + reports.length + ": " + reports[fileID].getName());
                try {
                    ErrorReportsConverter converter = new ErrorReportsConverter(false);
                    converter.readFile(reports[fileID]);
                    converter.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            System.out.println("STATUS: " + executor.getQueue().size() + " files still pending.");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                // No action needed
            }
        }
        
        time = System.currentTimeMillis() - time;
        System.out.println("Importing data into DB took: " + (time / 1000) + " sec.");
    }

}
