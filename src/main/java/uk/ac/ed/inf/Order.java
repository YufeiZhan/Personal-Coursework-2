package uk.ac.ed.inf;

import java.util.ArrayList;
import java.sql.Date;
import java.util.Objects;

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
  
  /**
   * Orders are equal when they have the same unique order number.
   *
   * @param o the other order
   * @return true if the same order, or false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order)) return false;
    Order order = (Order) o;
    return getOrderNo().equals(order.getOrderNo());
  }
  
  /**
   * Hashcode generates only on the order number
   *
   * @return the integer hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(getOrderNo());
  }
  
  /**
   * Print the required information of the order.
   *
   * @return String
   */
  @Override
  public String toString() {
    return "Order{"  +
            ", destination='" + destination + '\'' +
            ", items=" + items +
            '}';
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------

}
