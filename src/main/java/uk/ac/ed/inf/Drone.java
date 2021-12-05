package uk.ac.ed.inf;

import java.util.ArrayList;

//TODO: comment
/**
 *
 * @author Selina Zhan (s1953505)
 */
public class Drone {
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Daily maximum number of steps a drone can take */
  public static final int FULL_BATTERY = 1500;
  /** Starting location */
  public static final LongLat STARTING_POINT = new LongLat(-3.186874, 55.944494);
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  
  private LongLat currentLocation;
  private int battery;
  private ArrayList<Order> deliveredOrders;
  private ArrayList<LongLat> trajectory;
  
  public Drone(){
    LongLat currentLocation = STARTING_POINT;
    battery = FULL_BATTERY;
    deliveredOrders = new ArrayList<>();
    trajectory = new ArrayList<>();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  
  public LongLat getCurrentLocation(){
    return currentLocation;
  }
  
  public void deliverAnOrder(Order o){
    //TODO: to be implemented
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  
}
