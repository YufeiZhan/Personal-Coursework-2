package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App{
    
    protected static ServerConnector server;
    
    private static DatabaseConnector database;
    
    private static Date date;
    private static String websitePort;
    private static String databasePort;
    private static Menus menu;
    
    
    public static void main(String[] args) {
        /** Processing command-line arguments */
        String day = args[0];
        String month = args[1];
        String year = args[2];
        date = Date.valueOf(year + "-" + month + "-" + day);
        websitePort = args[3];
        databasePort = args[4];
        
//        /** Set up */
        server = new ServerConnector(websitePort);
        database = new DatabaseConnector(databasePort);
        ArrayList<Order> orders = database.queryOrdersByDate(date);
        
    }
}
