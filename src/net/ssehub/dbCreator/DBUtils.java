package net.ssehub.dbCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

public class DBUtils {
    private static final File SETTINGS_FILE = new File("res/db_con.properties");
//    private static final File DB_CREATION_FILE = new File("res/genDB.sql");
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    
    public static Connection createConnection() throws IOException {
        // Load settings from text file
        if (!SETTINGS_FILE.exists()) {
            throw new IOException(SETTINGS_FILE.getAbsolutePath() + " does not exist");
        }
        Properties settings = new Properties();
        try (InputStream in = new FileInputStream(SETTINGS_FILE)) {
            settings.load(in);
        }
        
        // Establish connection
        Connection con = null;
        try {
            String url = settings.getProperty("url");
            String user = settings.getProperty("user");
            String pw = settings.getProperty("password");
            
            if (null != pw && !pw.isEmpty()) {
                con = DriverManager.getConnection(url + "?user=" + user + "&password=" + pw);
            } else {
                con = DriverManager.getConnection(url + "?user=" + user);
            }    
        } catch (SQLException e){
            throw new IOException(e);
        }
        
        return con;
    }
    
    public static Timestamp toDate(String date) {
        int pos = date.indexOf(',');
        date = date.substring(pos + 2);
        
        Timestamp result = null;
        try {
            java.util.Date d = DATE_FORMAT.parse(date);
            result = new Timestamp(d.getTime());
            
            if (result.getYear() > 2100) {
                Runner.LOGGER.logWarning("Invalid timestamp. Converted " + date + " into " + result);
                result = null;
            }
        } catch (ParseException e) {
            // No action needed, if not parseable, we commit nothing (is allowed by DB)
            Runner.LOGGER.logWarning("Invalid timestamp. Could not parse " + date);
        }
        
        return result;
    }
}
