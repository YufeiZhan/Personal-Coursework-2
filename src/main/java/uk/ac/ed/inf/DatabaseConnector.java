package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

//TODO: retrieval done, but storage still in this class?
/**
 * Class connecting to the database for real-time info retrieval and info storage.
 *
 * @author Selina Zhan (s1953505)
 */
public class DatabaseConnector {
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Protocol for database */
  public static final String PROTOCOL = "jdbc:derby";
  /** Machine name for the server of the service */
  public static final String MACHINE = "localhost";
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /** port of the database */
  public final String port;
  private Connection conn;
  private Statement statement;
  
  /**
   * Database Constructor
   *
   * @param port port to access the database
   */
  public DatabaseConnector(String port) {
    this.port = port;
    setUp();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
//  TODO: comments
  
  public ArrayList<Order> queryOrdersByDate(Date date){
    ArrayList<Order> orderList = new ArrayList<>();
    
    try{
      final String orderQuery = "select * from orders where deliveryDate=(?)";
      PreparedStatement psOrderQuery = conn.prepareStatement(orderQuery);
      psOrderQuery.setDate(1,date);
      ResultSet rs = psOrderQuery.executeQuery();
      while(rs.next()){
        String order = rs.getString("orderNo");
        Date deliveryDate = rs.getDate("deliveryDate");
        String customer = rs.getString("customer");
        String deliverTo = rs.getString("deliverTo");
        ArrayList<String> items = queryItemsByOrder(order);
        orderList.add(new Order(order,deliveryDate,customer,deliverTo,items));
      }
    } catch (SQLException e){
      System.err.println("ERROR: Unable to retrieve info from the database 'orders'.");
      e.printStackTrace();
      System.exit(1);
    }
    
    return orderList;
  }
  
  public ArrayList<String> queryItemsByOrder(String orderNo){
    ArrayList<String> itemList = new ArrayList<>();
    
    try{
      final String itemQuery = "select * from orderDetails where orderNo=(?)";
      PreparedStatement psItemQuery = conn.prepareStatement(itemQuery);
      psItemQuery.setString(1,orderNo);
      ResultSet rs = psItemQuery.executeQuery();
      while(rs.next()){
        itemList.add(rs.getString("item"));
      }
//      TODO: exit here or not?
    } catch (SQLException e){
      System.err.println("ERROR: Unable to retrieve info from the database 'orderDetails'.");
      e.printStackTrace();
      System.exit(1);
    }
    
    return itemList;
  }
  
  
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  
//  TODO
  /**
   * Setting up initial connection to the database.
   */
  private void setUp(){
    try{
      String jdbcString = PROTOCOL + "://" + MACHINE + ":" + port + "/derbyDB";
      conn = DriverManager.getConnection(jdbcString);
      statement = conn.createStatement();
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to connect to database " + MACHINE +
              " at port " + port + ". Exit the application.");
      e.printStackTrace();
      System.exit(1);
    }
  }
}
