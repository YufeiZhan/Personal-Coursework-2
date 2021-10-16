package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Menus {
  
  static final int DELIVERY_CHARGE = 50;
  static final int VALID_STATUS = 200;
  private static final HttpClient client = HttpClient.newHttpClient();
  
  
  public String machine;
  public String port;
  
  public Menus(String name, String port){
    machine = name;
    this.port = port;
  }
  
//  Todo: implementation
  public int getDeliveryCost(String... items){
    int totalCost = DELIVERY_CHARGE;
    
    String menuJSON = getMenuJSON();
    ArrayList<Restaurant> restaurants = parseJSON(menuJSON);
    
    for (String item : items){
      
      for(Restaurant restaurant : restaurants){
        for(Menu menu : restaurant.menu){
          if (menu.item.equals(item)){
            totalCost += menu.pence;
            break;
          }
        }
      }
      
    }
    return totalCost;
  }
  
  private ArrayList<Restaurant> parseJSON(String menuJSON) {
    Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType();
    ArrayList<Restaurant> restaurantList = new Gson().fromJson(menuJSON,listType);
    return restaurantList;
  }
  
  private String getMenuJSON(){
    String urlString = "http://" + machine + ":" + port + "/menus/menus.json";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
    HttpResponse<String> response = null;
    
//    TODO: how to deal with the exception here; how to try catch block?? why can't return in try catch; interrupted exception & connect exception relationship; what are some the error types what do they represent.
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());

    } catch (java.net.ConnectException e){
      System.out.println("Fatal error: Unable to connect to " + machine + " at port " + port + ".");
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    if (response != null & response.statusCode() == VALID_STATUS){
      return response.body();
    } else{
      System.out.println("Invalid status code for http request.");
      return null;
    }
  }
}
