package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.*;

import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;
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
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  private Date deliveryDate;
  private ServerConnector server;
  private Menus menus;
  private ArrayList<Order> orders; //store all orders of the specified date
  private Drone drone;
  
  private ArrayList<ArrayList<Segment>> noFlyZones;
  
  
  public PathPlanner(Date date, ServerConnector server, Database database){
    deliveryDate = date;
    this.server = server;
    menus = new Menus(server.getMenuStr());
    orders = database.queryOrdersByDate(date);
    drone = new Drone();
    
    noFlyZones = getNoFlyZones();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  public void generateOptimalPath(){
  
  }
  
  public void drawPath(){
  
  }
  
  /**
   * Determine whether a potential movement of the drone enters any no-fly-zones.
   *
   * @param movement next potential movement of the drone
   * @return true if intersect, or false otherwise
   */
  public Boolean intersect(Segment movement){
    // check for each no-fly zone
    for (ArrayList<Segment> zone : noFlyZones){
      // check for each border of the perimeter
      for(Segment border : zone){
        if (border.intersect(movement)){
          return true;
        }
      }
    }
    
    return false;
  }
  
  
  
  //TODO: test getTotalValue()
  /**
   * Calculate the total value of the orders placed on the chosen day.
   *
   * @return the total value of orders placed
   */
  public int getTotalValue(){
    int total = 0;
    for (Order o : orders){
      total = total + menus.getDeliveryCost(o.getItems());
    }
    
    return total;
  }
  
  //  ---------------------------------------------- Algorithm ----------------------------------------------
//  TODO:
  
  /**
   * Pick the best next order based on the score from the rest of orders for the optimal filepath.
   * Assume there is at least one order left.
   *
   * @return the best next order
   */
  public Order pickOrder(){
    Order bestOrder = orders.get(0);
    
    double maxScore = 0;
    for (Order o : orders){
      if (totalScore(o) > maxScore){
        maxScore = totalScore(o);
        bestOrder = o;
      }
    }
    
    return bestOrder;
  }
  
  
  /**
   * Compute the total score for a specific order from three individual score components.
   * The score ranges from 1 to 125 where a higher score indicates a better current choice.
   *
   * @param o an order
   * @return total score for a specific order
   */
  public double totalScore(Order o){
    return scoreOne(o) * scoreTwo(o) * scoreThree(o);
  }
  
  /**
   * Scoring a specific order based on how far the drone has to go to its pickup location.
   * The score ranges from 1 to 5 where a higher score indicates closer location.
   *
   * @param order
   * @return
   */
  public double scoreOne(Order order){
    return 0;
  }
  
  /**
   * Scoring a specific order based on the percentage value per distance.
   * The score ranges from 1 to 5 where a higher score indicates a worthier order.
   *
   * @param order
   * @return
   */
  public double scoreTwo(Order order){
    return 0;
  }
  
  /**
   * Scoring a specific order based on whether there are shops with worthy orders close by the destination.
   * The score ranges from 1 to 5 where higher score indicates an easier next order after this order.
   *
   * @param order
   * @return
   */
  public double scoreThree(Order order){
    return 0;
  }
  
  /**
   * Linear scaling function that maps the current score within the current range to
   * the corresponding score of the intended range.
   *
   * @param a min of the intended range
   * @param b max of the intended range
   * @param c min of the current range
   * @param d max of the current range
   * @param score score under the current range
   * @return score under the intended range
   */
  public double scaling(int a, int b, int c, int d, int score){
    return a + (b - a) * (score - c) / (d - c);
  }
  
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  //  TODO: turn into private
  public LongLat convertW3WToLongLat(String address){

    String[] words = address.split("\\.");

    String addressJson = server.getWhat3WordsStr(words[0],words[1],words[2]);
    What3Words coordinate = new Gson().fromJson(addressJson,What3Words.class);

    return new LongLat(coordinate.getLongitude(), coordinate.getLatitude());
  }
  
  public ArrayList<ArrayList<Segment>> getNoFlyZones(){
    String geoJSON = server.getNoFlyZoneStr();
    List<Feature> features = GeoParser.getFeatures(geoJSON);
    
    ArrayList<ArrayList<Segment>> zones = new ArrayList<>();
    for (int i = 0; i < GeoParser.getFeatures(geoJSON).size(); i++){
      zones.add(new ArrayList<>()); // add a ArrayList for each feature
      ArrayList<LongLat> coordinates = GeoParser.getCoordinates(features.get(i));
      for(int j = 0; j < coordinates.size() - 1; j++){
        Segment seg = new Segment(coordinates.get(j),coordinates.get(j+1));
        zones.get(i).add(seg);
      }
    }
    
    return zones;
  }
}
