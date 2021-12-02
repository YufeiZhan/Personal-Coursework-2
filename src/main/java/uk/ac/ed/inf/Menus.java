package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

/**
 * Class responsible for retrieving the latest info regarding the shops and
 * their food on sale for the delivery service from the active web server.
 *
 * @author Selina Zhan (s1953505)
 */
public class Menus {
  
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Fixed delivery charge for every order */
  public static final int DELIVERY_CHARGE = 50;
  /** Valid status code before getting the web content */
  public static final int VALID_STATUS_CODE = 200;
  /** Sole client for accessing the web server and its content */
  private static final HttpClient client = HttpClient.newHttpClient();
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /** Field definitions */
  /** Name of the machine and the port of the running web server */
  public final String machine;
  public final String port;
  /** Info of shops updated on daily basis */
  private ArrayList<Shop> shops;
  private HashMap<String,Integer> prices;
  
  /**
   * Menu constructor for the delivery service
   *
   * @param name machine name of the web server
   * @param port port to access the web server
   */
  public Menus(String name, String port) {
    machine = name;
    this.port = port;
    prices = new HashMap<String,Integer>();
    updateShops();
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
  
  /**
   * Update shops from the running web server on daily basis
   */
  public void updateShops() {
    String menuJSON = getMenuJSON();
    shops = parseMenuJSON(menuJSON);
    updatePriceMapping();
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
//  /**
//   * Return price of an item by searching through each shop and its menu
//   *
//   * @param item the single item to look for
//   * @return price of the item in pence if found, 0 otherwise
//   */
//  public int getItemPrice(String item) {
//    for(Shop shop : shops) {
//      for(Shop.Item menuItem : shop.menu) {
//        if (menuItem.item.equals(item)) {
//          return menuItem.pence;
//        }
//      }
//    }
//
//    /** Case when item not found */
//    System.err.println("ERROR: invalid item input " + item + ". Skip the item.");
//    return 0;
//  }
  
  /**
   * Unmarshal retrieved JSON data from string into object.
   *
   * @param menuJSON String data from the web server
   * @return unmarshalled object
   */
  private ArrayList<Shop> parseMenuJSON(String menuJSON) {
    Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
    ArrayList<Shop> shopList = new Gson().fromJson(menuJSON,listType);
    return shopList;
  }
  
  /**
   * Retrieve JSON string from the running web server using HTTP client.
   *
   * @return JSON string if valid, quit the application otherwise
   */
  private String getMenuJSON() {
    String urlString = "http://" + machine + ":" + port + "/menus/menus.json";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
    
    HttpResponse<String> response = null;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      System.err.println("FATAl ERROR: Unable to connect to " + machine +
                         " at port " + port + ". Exit the application.");
      e.printStackTrace();
      System.exit(1);
    }
    
    /** Exit the application from the unrecoverable error */
    if (response == null | response.statusCode() != VALID_STATUS_CODE){
      System.err.println("FATAl ERROR: Invalid response code " +
                          response.statusCode() + ". Exit the application.");
      System.exit(1);
    }
    
    return response.body();
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
