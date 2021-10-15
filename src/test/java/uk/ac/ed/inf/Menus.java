package uk.ac.ed.inf;

public class Menus {
  
  static final int DELIVERY_CHARGE = 50;
  
  public String machine;
  public String port;
  
  public Menus(String name, String port){
    machine = name;
    this.port = port;
  }
  
//  Todo: implementation
  public int getDeliveryCost(String... items){
    int totalCost = 0;
    
    for (String item : items){
      int itemCost = 0;
      
      totalCost = totalCost + itemCost;
    }
    return 0;
  }
}
