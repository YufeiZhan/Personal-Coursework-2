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
  
  public Drone(){
    currentLocation = STARTING_POINT;
    battery = FULL_BATTERY;
    deliveredOrders = new ArrayList<>();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  public void move(LongLat newLocation){
    currentLocation = newLocation;
    battery = battery - 1;
  }
  
  public void orderDelivered(Order o){
    deliveredOrders.add(o);
  }
  public LongLat getCurrentLocation(){
    return currentLocation;
  }
  
  public int getBattery(){
    return battery;
  }
  
  public ArrayList<Order> getDeliveredOrders(){
    return deliveredOrders;
  }
  

  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  
}
