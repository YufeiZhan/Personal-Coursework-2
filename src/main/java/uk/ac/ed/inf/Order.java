package uk.ac.ed.inf;

import java.util.ArrayList;
import java.sql.Date;

/**
 * Class encapsulating all raw info about an order from the databases.
 *
 * @author Selina Zhan (s1953505)
 */
public class Order {
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  
  public final String orderNo;
  private final Date date;
  private final String studentID;
  private final String destination;
  private final ArrayList<String> items;
  
  
  public Order(String order, Date deliveryDate, String customer, String deliverTo, ArrayList<String> items){
    orderNo = order;
    date = deliveryDate;
    studentID = customer;
    destination = deliverTo;
    this.items = items;
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  
  public String getOrderNo(){
    return orderNo;
  }
  
  public Date getDate(){
    return date;
  }
  
  public String getDestination(){
    return destination;
  }
  
  public ArrayList<String> getItems(){
    return items;
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------

}
