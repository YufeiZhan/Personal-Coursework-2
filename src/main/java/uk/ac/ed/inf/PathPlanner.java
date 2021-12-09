package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Class responsible for planning the flightpath for the drone of the delivery service on a specific day.
 *
 * @author Selina Zhan (s1953505)
 */
public class PathPlanner {
//  TODO: Javadoc comment
  //  ---------------------------------------------- Constants ----------------------------------------------
  
//  have the orders in hand Order(orderNo, deliverDate, customerID, items, destination)
//  have the menu (shops, getDeliveryCost()， getShopLocations())
//  have the server (getNoFlyZone, getLandmarks, getW3W(1,2,3))
//  have LongLat (isConfined, distanceTo, closeTo, nextPosition)
//
//  how to pick orders? how to range orders?
//  using orders, menu, no-fly-zones, landmarks to get algorithm
  
  private static final String DUMMY_ORDER_NUM = "--------";
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  private final Date deliveryDate;
  private final ServerConnector server;
  private final DatabaseConnector database;
  private Menus menus;
  private ArrayList<ArrayList<Segment>> noFlyZones; //a list of each zone's borders
  private ArrayList<LongLat> landmarks;
  
  private ArrayList<Order> orders; //store orders to be dispatched
  private Drone drone;
  private Map map;
  
  
  public PathPlanner(Date date, ServerConnector server, DatabaseConnector database){
    deliveryDate = date;
    this.server = server;
    this.database = database;
    menus = new Menus(server.getMenuStr());
    orders = database.queryOrdersByDate(date);
    drone = new Drone();
    
    noFlyZones = getNoFlyZones();
    landmarks = getLandmarks();
    
    map = new Map(noFlyZones, landmarks, getAllDestinations(), getAllShopLocations());
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------

  public void run() {
    int totalOrderValue = getOrdersValue(orders); //total value of all placed orders
    
    Order currentO = pickOrder();
    while(currentO != null) {
      System.out.println("Current order being delivered: " + currentO.getOrderNo());
      deliverOrder(currentO);
      currentO = pickOrder();
    }
    
    returnBack();
    
    System.out.println("The final position of the drone: " + drone.getCurrentLocation());
    System.out.println("Is it at AT: " + drone.getCurrentLocation().closeTo(drone.STARTING_POINT));
    System.out.println("The energy left: " + drone.getBattery());
    System.out.println("The number of orders left: " + orders.size());
    System.out.println("The number of orders delivered: " + drone.getDeliveredOrders().size());
    System.out.println("Delivered value: " + getOrdersValue(drone.getDeliveredOrders()));
    System.out.println("Total value: " + totalOrderValue);
    System.out.println("The mercenary percentage: " +  (double) getOrdersValue(drone.getDeliveredOrders()) / totalOrderValue );
  }
  
  public void returnBack(){
    ArrayList<LongLat> path = map.computeTrajectory(drone.getCurrentLocation(),drone.STARTING_POINT);
    
    if (path.size() == 2){
      flyStraight(DUMMY_ORDER_NUM,path.get(0),path.get(1));
    } else{
      for (int i = 0; i < path.size()  -1; i++){
        flyStraight(DUMMY_ORDER_NUM,path.get(i),path.get(i+1));
      }
    }
    
  }
  
  public void deliverOrder(Order o){
    ArrayList<LongLat> stopbys = getStopBy(o);
    System.out.println(" - finish computing stopbys: " + stopbys.toString());
    
    for (int i = 0; i < stopbys.size()  -1; i++){
      flyStraight(o.getOrderNo(),stopbys.get(i),stopbys.get(i+1));
    }
    
    drone.orderDelivered(o);
    database.insertDeliveries(o.getOrderNo(),o.getDestination(),menus.getDeliveryCost(o.getItems()));
    orders.remove(o);
  }
  
  public void flyStraight(String orderNo, LongLat start, LongLat end){
    LongLat currentLocation = start;
    
    while (! currentLocation.closeTo(end)) {
      int bearing = (int) Math.round(currentLocation.bearingTo(end) / 10) * 10 % 360;
      System.out.println(bearing);
      LongLat newLocation = currentLocation.nextPosition(bearing);
      drone.move(newLocation);
      database.insertPath(orderNo, currentLocation, bearing, newLocation);
      currentLocation = newLocation;
    }
    
    System.out.println(" - each end of fly straight: " + currentLocation);
  }
  
//  TODO: suppose to

  /**
   * Get all stop by points in sequence for an order.
   * Each stop by point can be reached in a straight path without crossing any no-fly-zones.
   *
   * Note that since there's already repetition of visited shops, which accounts for hovering action,
   * so there's no need for extra hovering.
   *
   * @param o an order
   * @return ArrayList of LongLat stop by points of the order in sequence
   */
  public ArrayList<LongLat> getStopBy(Order o) {
    LongLat start = drone.getCurrentLocation();
    LongLat destination = convertW3WToLongLat(o.getDestination());
    ArrayList<LongLat> shops = getOrderShops(o);
    ArrayList<LongLat> stopBy = new ArrayList<>();
    
    System.out.println("- getStopBy(): shops are " + shops.toString() + "; start is " + start);
  
    if (shops.size() == 1) { // only one shop to visit
      stopBy.addAll(map.computeTrajectory(start, shops.get(0)));
      System.out.println("- when 1 shop & add the first part: " + stopBy.toString());
      stopBy.addAll(map.findTrajectory(shops.get(0), destination));
      System.out.println("- when 1 shop & add the second part: " + stopBy.toString());
  
    } else { //two shops to visit
      LongLat shop1 = shops.get(0);
      LongLat shop2 = shops.get(1);
    
      double distance1 = computeDistance(map.computeTrajectory(drone.getCurrentLocation(), shop1)) //from drone to shop1
              + computeDistance(map.findTrajectory(shop1, shop2)) //from shop1 to shop2
              + computeDistance(map.findTrajectory(shop2, destination)); //from shop2 to student
    
      double distance2 = computeDistance(map.computeTrajectory(drone.getCurrentLocation(), shop2)) //from drone to shop1
              + computeDistance(map.findTrajectory(shop2, shop1)) //from shop1 to shop2
              + computeDistance(map.findTrajectory(shop1, destination)); //from shop2 to student
    
      if (distance1 <= distance2) {
        stopBy.addAll(map.computeTrajectory(start, shop1));
        System.out.println("- when 2 shops & add the first part: " + stopBy.toString());
        stopBy.addAll(map.findTrajectory(shop1, shop2));
        System.out.println("- when 2 shops & add the second part: " + stopBy.toString());
        stopBy.addAll(map.findTrajectory(shop2, destination));
        System.out.println("- when 2 shops & add the third part: " + stopBy.toString());
  
      } else {
        stopBy.addAll(map.computeTrajectory(start, shop2));
        System.out.println("- when 2 shops & add the first part: " + stopBy.toString());
        stopBy.addAll(map.findTrajectory(shop2, shop1));
        System.out.println("- when 2 shops & add the second part: " + stopBy.toString());
        stopBy.addAll(map.findTrajectory(shop1, destination));
        System.out.println("- when 2 shops & add the third part: " + stopBy.toString());
  
      }
    }
    return stopBy;
  }
  
  //TODO: test getTotalValue()
  /**
   * Calculate the total value of the ArrayList of the order.
   *
   * @return the total value of orders in the ArrayList
   */
  public int getOrdersValue(ArrayList<Order> orders){
    int total = 0;
    for (Order o : orders){
      total = total + menus.getDeliveryCost(o.getItems());
    }
    
    return total;
  }
  
  /**
   * Compute the distance for a valid trajectory.
   *
   * @param trajectory trajectory of LongLat points
   * @return double distance of the trajectory
   */
  public double computeDistance(ArrayList<LongLat> trajectory){
    double distance = 0;
    for(int i = 0; i < trajectory.size() - 1; i++){
      distance = distance + trajectory.get(i).distanceTo(trajectory.get(i+1));
    }
    
    return distance;
  }
  
  
  //  ---------------------------------------------- Algorithm ----------------------------------------------

//  TODO: to complete and then test
  /**
   * Pick the best next order from the rest of orders.
   *
   * @return the best next order if exists, or null otherwise
   */
  public Order pickOrder(){
    System.out.println("Available orders for picking: " + getAllOrderNums(orders));
    // no orders left
    if (orders.size() == 0){
      return null;
    }
    
    // at least 1 order left
    Order bestOrder = null;
    double maxValue = 0;
    for (Order o : orders){
      System.out.println(" - for each order's validity: " + canBeDispatched(o));
      //if an order cannot be dispatched at all, the system does not consider it
      double curValue = canBeDispatched(o) ? computePercentValue(o) : 0;
      System.out.println(" - for " + o.getOrderNo() + "predicted value: " + curValue);
      if (curValue > maxValue){
        bestOrder = o;
        maxValue = curValue;
      }
    }
    
    return bestOrder;
  }
  
  /**
   * Based on the current location of the drone, compute the current percent value of an order.
   * Percent Value = delivery cost / distance required.
   *
   * @param o an order
   * @return the percent value of the order
   */
  public double computePercentValue(Order o){
    LongLat destination = convertW3WToLongLat(o.getDestination());
    ArrayList<LongLat> shops = getOrderShops(o);
    int orderValue = menus.getDeliveryCost(o.getItems());
    double distance = 0;
  
    if (shops.size() == 1) { // only need to visit 1 shop
      LongLat shop = shops.get(0);
      distance = drone.getCurrentLocation().distanceTo(shop) //from drone to shop
              + computeDistance(map.findTrajectory(shop,destination)); //from shop to student
    } else{ // need to visit 2 shops
      LongLat shop1 = shops.get(0);
      LongLat shop2 = shops.get(1);
  
      double distance1 = drone.getCurrentLocation().distanceTo(shop1) //from drone to shop1
              + computeDistance(map.findTrajectory(shop1,shop2)) //from shop1 to shop2
              + computeDistance(map.findTrajectory(shop2,destination)); //from shop2 to student
  
      double distance2 = drone.getCurrentLocation().distanceTo(shop2) //from drone to shop1
              + computeDistance(map.findTrajectory(shop2,shop1)) //from shop1 to shop2
              + computeDistance(map.findTrajectory(shop1,destination)); //from shop2 to student
      
      distance = distance1 <= distance2 ? distance1 : distance2;
    }
    
    return orderValue / distance;
    
  }
  
  /**
   * Determine whether an order have enough energy to dispatch an order and return back to the
   * starting point.
   *
   * @param o the order to be determined
   * @return true if the current battery level can support the trip, or false otherwise
   */
  public Boolean canBeDispatched(Order o){
    LongLat destination = convertW3WToLongLat(o.getDestination());
    ArrayList<LongLat> shops = getOrderShops(o);
    double batteryNeeded = 0;
    
    if (shops.size() == 1){ // only need to visit 1 shop
      LongLat shop = shops.get(0);
      double distance = drone.getCurrentLocation().distanceTo(shop) //from drone to shop
                        + computeDistance(map.findTrajectory(shop,destination)) //from shop to student
                        + computeDistance(map.findTrajectory(Drone.STARTING_POINT, destination)); //back
      batteryNeeded = distance/LongLat.MOVE_LENGTH + 2; // 2 for getting food from shop and giving food to student
    } else { // need to visit 2 shops
      LongLat shop1 = shops.get(0);
      LongLat shop2 = shops.get(1);
      
      double distance1 = drone.getCurrentLocation().distanceTo(shop1) //from drone to shop1
                         + computeDistance(map.findTrajectory(shop1,shop2)) //from shop1 to shop2
                         + computeDistance(map.findTrajectory(shop2,destination)) //from shop2 to student
                         + computeDistance(map.findTrajectory(Drone.STARTING_POINT,destination)); // back
  
      double distance2 = drone.getCurrentLocation().distanceTo(shop2) //from drone to shop1
              + computeDistance(map.findTrajectory(shop2,shop1)) //from shop1 to shop2
              + computeDistance(map.findTrajectory(shop1,destination)) //from shop2 to student
              + computeDistance(map.findTrajectory(Drone.STARTING_POINT,destination)); // back
      
      // 3 for getting food from 2 shops and giving food to student
      batteryNeeded = distance1 <= distance2 ? distance1/LongLat.MOVE_LENGTH + 3 : distance2/LongLat.MOVE_LENGTH + 3;
    }
    
    return drone.getBattery() >= batteryNeeded;
  }
  

//  /**
//   * Compute the total score for a specific order from three individual score components.
//   * The score ranges from 1 to 125 where a higher score indicates a better current choice.
//   *
//   * @param o an order
//   * @return total score for a specific order
//   */
//  public double totalScore(Order o){
//    return scoreOne(o) * scoreTwo(o) * scoreThree(o);
//  }
  
//  /**
//   * Scoring a specific order based on how far the drone has to go to its pickup location.
//   * The score ranges from 1 to 5 where a higher score indicates closer location.
//   *
//   * @param order
//   * @return
//   */
//  public double scoreOne(Order order){
//    return 1;
//  }
//
//  /**
//   * Scoring a specific order based on the percentage value per distance.
//   * The score ranges from 1 to 5 where a higher score indicates a worthier order.
//   *
//   * @param order
//   * @return
//   */
//  public double scoreTwo(Order order){
//    return 1;
//  }
  
//  /**
//   * Scoring a specific order based on whether there are shops with worthy orders close by the destination.
//   * The score ranges from 1 to 5 where higher score indicates an easier next order after this order.
//   *
//   * @param order
//   * @return
//   */
//  public double scoreThree(Order order){
//    return 1;
//  }
  
//  /**
//   * Linear scaling function that maps the current score within the current range to
//   * the corresponding score of the intended range.
//   *
//   * @param a min of the intended range
//   * @param b max of the intended range
//   * @param c min of the current range
//   * @param d max of the current range
//   * @param score score under the current range
//   * @return score under the intended range
//   */
//  public double scaling(int a, int b, int c, int d, int score){
//    return a + (b - a) * (score - c) / (d - c);
//  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  //  TODO: turn into private
  
  private ArrayList<String> getAllOrderNums(ArrayList<Order> orders){
    ArrayList<String> nums = new ArrayList<>();
    for (Order o : orders){
      nums.add(o.getOrderNo());
    }
    return nums;
  }
  
  /**
   * Convert ArrayList of String locations into ArrayList of LongLat coordinates for future use.
   *
   * @param o an order
   * @return ArrayList of LongLat shop locations of the order
   */
  public ArrayList<LongLat> getOrderShops(Order o){
    ArrayList<String> strLocations = menus.getShopLocations(o.getItems());
    ArrayList<LongLat> shopLocations = new ArrayList<>();
    for (String loc : strLocations){
      shopLocations.add(convertW3WToLongLat(loc));
    }
    
    return shopLocations;
  }
  
  /**
   * Convert standard What3Words(W3W) String into corresponding LongLat coordinate
   *
   * @param address W3W address String
   * @return LongLat coordinate
   */
  public LongLat convertW3WToLongLat(String address){
    String[] words = address.split("\\.");

    String addressJson = server.getWhat3WordsStr(words[0],words[1],words[2]);
    What3Words coordinate = new Gson().fromJson(addressJson,What3Words.class);

    return new LongLat(coordinate.getLongitude(), coordinate.getLatitude());
  }
  
  /**
   * Retrieve raw geoJSON data from server and marshal no-fly-zones data
   * into several series of border Segments.
   *
   * @return a ArrayList of each zone's border segment
   */
  public ArrayList<ArrayList<Segment>> getNoFlyZones(){
    String geoJSON = server.getNoFlyZoneStr();
    List<Feature> features = GeoParser.getFeatures(geoJSON);
    
    ArrayList<ArrayList<Segment>> zones = new ArrayList<>();
    for (int i = 0; i < features.size(); i++){
      zones.add(new ArrayList<>()); // add a ArrayList for each zone
      ArrayList<LongLat> coordinates = GeoParser.getCoordinates(features.get(i));
      for(int j = 0; j < coordinates.size() - 1; j++){ // add borders to each zone
        Segment seg = new Segment(coordinates.get(j),coordinates.get(j+1));
        zones.get(i).add(seg);
      }
    }
    
    return zones;
  }
  
  
  /**
   * Retrieve raw geoJSON data from server and marshal landmarks data
   * into a list of LongLat coordinates.
   *
   * @return a ArrayList of landmarks' LongLat coordinates
   */
  public ArrayList<LongLat> getLandmarks(){
    String geoJSON = server.getLandmarkStr();
    List<Feature> features = GeoParser.getFeatures(geoJSON);
    
    ArrayList<LongLat> landmarks = new ArrayList<>();
    for(Feature f : features){
      ArrayList<LongLat> coordinates = GeoParser.getCoordinates(f);
      landmarks.add(coordinates.get(0));
    }
    
    return landmarks;
  }
  

//  public ArrayList<LongLat> getAllDestinations(){
//    HashSet<String> destinations = new HashSet<>(); // used because HashSet cannot compare object directly
//    ArrayList<LongLat> result = new ArrayList<>();
//
//    for (Order o : orders){
//      String w3w = o.getDestination();
//      if (! destinations.contains(w3w)){
//        destinations.add(w3w);
//        result.add(convertW3WToLongLat(w3w));
//      }
//    }
//
//    return result;
//  }
  
  /**
   * Collect all order destinations for the chosen day at the beginning of the planning.
   *
   * @return a ArrayList of unrepeated LongLat destination coordinates
   */
  public ArrayList<LongLat> getAllDestinations(){
    HashSet<LongLat> destinations = new HashSet<>(); // used because HashSet cannot compare object directly
    
    for (Order o : orders){
      String w3w = o.getDestination();
      destinations.add(convertW3WToLongLat(w3w));
    }
    
    ArrayList<LongLat> result = new ArrayList<>(destinations);
    return result;
  }
  
  
  
  /**
   * Collect all shop locations currently in cooperation with our system.
   *
   * @return a ArrayList of unrepeated LongLat shop coordinates
   */
  public ArrayList<LongLat> getAllShopLocations(){
    ArrayList<String> strLocations = menus.getAllShopLocations();
    ArrayList<LongLat> locations = new ArrayList<>();
    
    for (String str : strLocations){
      locations.add(convertW3WToLongLat(str));
    }
    
    return locations;
  }
  
}


//----
//public static class AStar{
//
//  private LongLat start;
//  private LongLat end;
//  private ArrayList<LongLat> openList = new ArrayList<>();
//  private ArrayList<LongLat> closedList = new ArrayList<>();
//
//
//  public AStar(LongLat start, LongLat end){
//    this.start = start;
//    this.end = end;
//    openList.add(start);
//    start.setF(calculateF(start));
//  }
//
//  public boolean run(){
//
//    while (openList.size() > 0){
//      LongLat currentNode = getNextPoint();
//      openList.remove(currentNode);
//      closedList.add(currentNode);
//      System.out.println(currentNode.toString());
//
//      //check whether it's the end point
//      if (currentNode.distanceTo(end) == 0){
//        return true;
//      }
//
//      //if haven't reach the end
//      for (LongLat p : getNeighbours(currentNode)){
//        System.out.println("Neighbor: " + p.toString());
//        System.out.println(!closedList.contains(p));
//        System.out.println(traversable(currentNode, p));
//        if (!closedList.contains(p) && traversable(currentNode, p)){
//          System.out.println(" - valid green point: "+ p.toString());
//          double fScore = calculateF(p);
//          if (openList.contains(p)){ //if already been traversed and have a f value
//            if (fScore < p.getF()){
//              p.setF(fScore);
//              p.setParent(currentNode);
//            }
//          } else{ //if new
//            p.setF(fScore);
//            p.setParent(currentNode);
//            openList.add(p);
//          }
//        }
//      }
//      System.out.println(openList);
//    }
//
//    return false;
//  }
//
//
//  private boolean traversable(LongLat start, LongLat end){
//    Segment step = new Segment(start, end);
//    System.out.println("isConfined: " + start.isConfined());
//    System.out.println("isConfined: " + end.isConfined());
//    System.out.println("no intersection: " + !intersect(step));
//
//    return start.isConfined() && end.isConfined() && !intersect(step);
//  }
//
//
//  private double calculateF(LongLat point){
//    return point.distanceTo(start) + point.distanceTo(end);
//  }
//
//  private ArrayList<LongLat> getNeighbours(LongLat point){
//    ArrayList<LongLat> neighbors = new ArrayList<>();
//    for(int i = 0; i <= 350; i = i + 60){
//      neighbors.add(point.nextPosition(i));
//    }
//    return neighbors;
//  }
//
//  private LongLat getNextPoint(){
//    LongLat next = openList.get(0);
//    double minF = openList.get(0).getF();
//
//    for (LongLat p : openList){
//      if (p.getF() < minF){
//        next = p;
//        minF = p.getF();
//      }
//    }
//
//    return next;
//  }
//
//}