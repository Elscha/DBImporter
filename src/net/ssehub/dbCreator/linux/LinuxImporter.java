package net.ssehub.dbCreator.linux;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import net.ssehub.dbCreator.Runner;

public class LinuxImporter {
    private static final File LINUX_REPORTS_FOLDER = new File("linux");
    
    
    public static void main(String[] args) throws IOException, SQLException {
        if (args != null && args.length > 0) {
            long time = System.currentTimeMillis();
            String version = args[0];
            File srcFile = LINUX_REPORTS_FOLDER.listFiles()[0];
            
            Runner.LOGGER.logInfo("Import Linux " + version + " measures of " + srcFile.getName());
            LinuxConverter converter = new LinuxConverter(version);
            converter.readFile(srcFile);
            converter.close();
            time = System.currentTimeMillis() - time;
            time /= 1000;
            Runner.LOGGER.logInfo("Import Linux " + version + " finished in " + time + "sec.");
        }
    }

}
