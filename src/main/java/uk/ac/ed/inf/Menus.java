package uk.ac.ed.inf;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import com.google.gson.Gson;

/**
 * Class storing the info regarding the shops and their food on sale from Json String.
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
  /** Data structure for easy retrieval of shop locaation */
  private HashMap<String,String> locations = new HashMap<>();
  
  /**
   * Menu constructor for the delivery service
   */
  public Menus(String menuString) {
    shops = parseMenuJSON(menuString);
    updateMapping();
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  /**
   * Calculate the total delivery cost for all items in a single order.
   *
   * @param items String names of the items
   * @return total cost of an order in pence
   */
  public int getDeliveryCost(ArrayList<String> items) {
    //every order contains fixed delivery charge
    int totalCost = DELIVERY_CHARGE;
    
    for (String item : items) {
      totalCost += prices.get(item);
    }
    return totalCost;
  }
  
//  TODO: comments
  /**
   *
   * @param items
   * @return
   */
  public Object[] getShopLocations(String... items){
    HashSet<String> shopLocations = new HashSet<>();
    for (String item:items){
      shopLocations.add(locations.get(item));
    }
    
    return shopLocations.toArray();
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  /**
   * Unmarshal retrieved JSON data from String into object.
   *
   * @return unmarshalled object
   */
  private ArrayList<Shop> parseMenuJSON(String menuString) {
    Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
    ArrayList<Shop> shopList = new Gson().fromJson(menuString,listType);
    return shopList;
  }
  
  
//  TODO: 换成getter吗？修改comment
  /**
   * Update HashMap object to look up prices efficiently.
   */
  private void updateMapping(){
    for (Shop shop : shops){
      for (Shop.Item item: shop.menu){
        prices.put(item.item,item.pence);
        locations.put(item.item,shop.location);
      }
    }
  }
}
