package uk.ac.ed.inf;



/**
 * Hello world!
 *
 */
public class App{
    
    private static String day;
    private static String month;
    private static String year;
    private static String websitePort;
    private static String databasePort;
    private static Menus menu;
    private static ServerConnector server;
    
    
    public static void main(String[] args) {
        day = args[0];
        month = args[1];
        year = args[2];
        websitePort = args[3];
        databasePort = args[4];
        server = new ServerConnector(websitePort);
        System.out.println(server.getWhat3WordsStr("blocks","found","civic"));
        menu = new Menus(server.getMenuStr());
        System.out.println(menu.getDeliveryCost("Flaming tiger latte"));
    }
}
