package net.ssehub.dbCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.ssehub.dbCreator.linux.LinuxImporter;
import net.ssehub.dbCreator.testRobot.TestRobotImporter;
import net.ssehub.kernel_haven.util.Logger;

public class Runner {
    
    public static Logger LOGGER = Logger.get();
    
    static {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm").format(Calendar.getInstance().getTime());
        File logFile = new File("./DB-Import" + timeStamp + ".log");
        try {
            FileOutputStream fOut = new FileOutputStream(logFile);
            LOGGER.addTarget(fOut);
        } catch (FileNotFoundException e) {
            System.out.println("Could not create log file at " + logFile.getAbsolutePath() + " due to: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        if (args != null && args.length > 0) {
            LinuxImporter.main(args);
        } else {
            TestRobotImporter.main(args);
        }

    }

}
