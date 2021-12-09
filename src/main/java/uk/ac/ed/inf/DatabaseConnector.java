package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

//TODO: comment
/**
 * Class connecting to the database 'order' and 'orderDetails' to retrieve order information.
 *
 * @author Selina Zhan (s1953505)
 */
public class DatabaseConnector {
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Protocol for database */
  public static final String PROTOCOL = "jdbc:derby";
  /** Machine name for the server of the service */
  public static final String MACHINE = "localhost";
  /** Name of the database*/
  public static final String DATABASE = "derbyDB";
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /** port of the database */
  public final String port;
  private Connection conn;
  private Statement statement;
  
  /**
   * DatabaseConnector Constructor
   *
   * @param port port to access the database
   */
  public DatabaseConnector(String port) {
    this.port = port;
    setUp();
  }
  
  //  ---------------------------------------------- Read Data ----------------------------------------------
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
  
  public ArrayList<LongLat> queryFlightPaths(){
    ArrayList<LongLat> path = new ArrayList<>();
  
    try{
      final String itemQuery = "select * from flightpath";
      PreparedStatement psPathQuery = conn.prepareStatement(itemQuery);
      ResultSet rs = psPathQuery.executeQuery();
      while(rs.next()){
        LongLat p = new LongLat(rs.getDouble("fromLongitude"), rs.getDouble("fromLatitude"));
        path.add(p);
      }
//      TODO: exit here or not?
    } catch (SQLException e){
      System.err.println("ERROR: Unable to retrieve info from the database 'orderDetails'.");
      e.printStackTrace();
      System.exit(1);
    }
    
    return path;
  }
  
  //  ---------------------------------------------- Write Data ----------------------------------------------
  
  public void insertDeliveries(String orderNo, String deliveredTo, int deliveryCost){
    try{
      PreparedStatement psDelivery = conn.prepareStatement("insert into deliveries values (?,?,?)");
      psDelivery.setString(1,orderNo);
      psDelivery.setString(2,deliveredTo);
      psDelivery.setInt(3,deliveryCost);
      psDelivery.execute();
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to write to database flightpath. Exit the application.");
    }
  }
  
  public void insertPath(String orderNo, LongLat start, int angle, LongLat end){
    try{
      PreparedStatement psFlightPath = conn.prepareStatement("insert into flightpath values (?,?,?,?,?,?)");
      psFlightPath.setString(1,orderNo);
      psFlightPath.setDouble(2,start.getX());
      psFlightPath.setDouble(3,start.getY());
      psFlightPath.setInt(4,angle);
      psFlightPath.setDouble(5,end.getX());
      psFlightPath.setDouble(6,end.getY());
      psFlightPath.execute();
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to write to database flightpath. Exit the application.");
    }
  }
  
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  //  TODO: deletion, comments etc.
  
  public void testDeliveries(){
    try{
      final String deliveryQuery = "select * from deliveries";
      PreparedStatement psDQuery = conn.prepareStatement(deliveryQuery);
      ResultSet rs = psDQuery.executeQuery();
      while(rs.next()){
        System.out.println(rs.getString("orderNo") + " " + rs.getString("deliveredTo") + " " + rs.getInt("costInPence"));
        
      }
//      TODO: exit here or not?
    } catch (SQLException e){
      System.err.println("ERROR: Unable to retrieve info from the database 'orderDetails'.");
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public void testFlightPath(){
    try{
      final String flightQuery = "select * from flightpath";
      PreparedStatement psFQuery = conn.prepareStatement(flightQuery);
      ResultSet rs = psFQuery.executeQuery();
      while(rs.next()){
        System.out.println(rs.getString("orderNo") + " " + rs.getInt("angle"));
      }
//      TODO: exit here or not?
    } catch (SQLException e){
      System.err.println("ERROR: Unable to retrieve info from the database 'orderDetails'.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Setting up initial connection to the database for later use.
   */
  private void setUp(){
    try{
      String jdbcString = PROTOCOL + "://" + MACHINE + ":" + port + "/" + DATABASE;
      conn = DriverManager.getConnection(jdbcString);
      statement = conn.createStatement();
      createDeliveriesTable();
      createPathTable();
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to connect to database " + MACHINE +
              " at port " + port + ". Exit the application.");
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public void createDeliveriesTable()  {
    try {
      // drop table if it already exists
      DatabaseMetaData databaseMetaData = conn.getMetaData();
      ResultSet resultSet = databaseMetaData.getTables(null,null,"DELIVERIES",null);
      if(resultSet.next()){
        statement.execute("drop table deliveries");
      }
      
      //create the deliveries table
      statement.execute("create table deliveries(orderNo char(8), " +
              "deliveredTo varchar(19), costInPence int)");
      
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to create database deliveries. Exit the application.");
    }
  }
  
  public void createPathTable(){
    try {
      // drop table if it already exists
      DatabaseMetaData databaseMetaData = conn.getMetaData();
      ResultSet resultSet = databaseMetaData.getTables(null,null,"FLIGHTPATH",null);
      if(resultSet.next()){
        statement.execute("drop table flightpath");
      }
      
      //create the flightpath table
      statement.execute("create table flightpath(orderNo char(8), " +
              "fromLongitude double, fromLatitude double, angle integer, " +
              "toLongitude double, toLatitude double)");
      
    } catch (SQLException e){
      System.err.println("FATAl ERROR: Unable to create database flightpath. Exit the application.");
    }
  }
}
