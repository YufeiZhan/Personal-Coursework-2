package uk.ac.ed.inf;

import java.util.ArrayList;
import java.sql.Date;

/**
 * Class encapsulating all the info about the orders retrieved from the database.
 */
public class Order {
  
  public final String orderNo;
  private final Date date;
  private final String studentID;
  private final String[] destination;
  private final ArrayList<String> items;
  
  
  public Order(String order, Date deliveryDate, String customer, String deliverTo, ArrayList<String> items){
    orderNo = order;
    date = deliveryDate;
    studentID = customer;
    destination = deliverTo.split(".");
    this.items = items;
  }
}
