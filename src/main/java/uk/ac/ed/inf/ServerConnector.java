package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class connecting to the running server to retrieve the latest raw data for further use.
 * It can return the latest Json/GeoJson String for the following:
 * - no-fly zones (GeoJson)
 * - landmarks (GeoJson)
 * - What3Words address (Json)
 * - menus (Json)
 *
 * @author Selina Zhan (s1953505)
 */
public class ServerConnector {
  
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Protocol for web server */
  public static final String PROTOCOL = "http";
  /** Machine name for the server of the service */
  public static final String MACHINE = "localhost";
  /** Valid status code before getting the web content */
  public static final int VALID_STATUS_CODE = 200;
  /** Sole http client for accessing the web server and its content */
  private static final HttpClient CLIENT = HttpClient.newHttpClient();
  
  //  ----------------------------------------- Fields & Constructor ----------------------------------------
  /** port of the running web server */
  public final String port;
  
  /**
   * Server constructor
   *
   * @param port port to access the web server
   */
  public ServerConnector(String port){
    this.port = port;
  }
  
  //  -------------------------------------------- Main Functions -------------------------------------------
  /**
   * Retrieve the latest Json String for the menu
   *
   * @return Json String if valid, quit the application otherwise
   */
  public String getMenuStr() {
    String urlString = PROTOCOL + "://" + MACHINE + ":" + port + "/menus/menus.json";
    return getContent(urlString);
  }
  
  /**
   * Retrieve the latest GeoJson String for no fly zones
   *
   * @return GeoJson String if valid, quit the application otherwise
   */
  public String getNoFlyZoneStr(){
    String urlString = PROTOCOL + "://" + MACHINE + ":" + port + "/buildings/no-fly-zones.geojson";
    return getContent(urlString);
  }
  
  /**
   * Retrieve the latest GeoJson String for landmarks
   *
   * @return GeoJson String if valid, quit the application otherwise
   */
  public String getLandmarkStr(){
    String urlString = PROTOCOL + "://" + MACHINE + ":" + port + "/buildings/landmarks.geojson";
    return getContent(urlString);
  }
  
  /**
   * Retrieve the latest Json String for coordinate from What3Words address
   *
   * @param word1 first word of the What3Words address
   * @param word2 second word of the What3Words address
   * @param word3 third word of the What3Words address
   * @return Json String if valid, quit the application otherwise
   */
  public String getWhat3WordsStr(String word1, String word2, String word3){
    String urlString = PROTOCOL + "://" + MACHINE + ":" + port + "/words/" +
                        word1 + "/" + word2 + "/" + word3 + "/details.json";
    return getContent(urlString);
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  /**
   * Retrieve Json/GeoJson string from the running web server using HTTP client.
   *
   * @param url path of the destination file
   * @return Json/GeoJson String if valid, quit the application otherwise
   */
  private String getContent(String url){
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
  
    HttpResponse<String> response = null;
    try {
      response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      System.err.println("FATAl ERROR: Unable to connect to web server " + MACHINE +
              " at port " + port + ". Exit the application.");
      e.printStackTrace();
      System.exit(1);
    }
  
    /** Exit the application from the unrecoverable error */
    if (response == null | response.statusCode() != VALID_STATUS_CODE){
      System.err.println("FATAl ERROR: Invalid web server response code " +
              response.statusCode() + ". Exit the application.");
      System.exit(1);
    }
    
    System.out.println("SUCCESS: information retrieved from the server.");
    return response.body();
  }
}
