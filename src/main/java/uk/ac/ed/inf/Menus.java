package uk.ac.ed.inf;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;

/**
 * Class storing the info regarding the shops and their food on sale for the delivery service.
 *
 * @author Selina Zhan (s1953505)
 */
public class Menus {
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Fixed delivery charge for every order */
  public static final int DELIVERY_CHARGE = 50;
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /** Field definitions */
  /** Info of shops updated on daily basis */
  private ArrayList<Shop> shops;
  /** Data structure for easy retrieval of item price */
  private HashMap<String,Integer> prices = new HashMap<>();
  
  /**
   * Menu constructor for the delivery service
   */
  public Menus() {
    parseMenuJSON(App.server.getMenuStr());
    updatePriceMapping();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  /**
   * Calculate the total delivery cost for all items in a single order.
   *
   * @param items String names of the items
   * @return total cost of an order in pence
   */
  public int getDeliveryCost(String... items) {
    //every order contains fixed delivery charge
    int totalCost = DELIVERY_CHARGE;
    
    for (String item : items) {
      totalCost += prices.get(item);
    }
    return totalCost;
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  /**
   * Unmarshal retrieved JSON data from String into object.
   *
   * @param menuJSON String data from the web server
   * @return unmarshalled object
   */
  private void parseMenuJSON(String menuJSON) {
    Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
    ArrayList<Shop> shopList = new Gson().fromJson(menuJSON,listType);
    shops = shopList;
  }
  
  
  /**
   * Update HashMap object to look up prices efficiently.
   */
  private void updatePriceMapping(){
    for (Shop shop : shops){
      for (Shop.Item item: shop.menu){
        prices.put(item.item,item.pence);
      }
    }
  }
}
