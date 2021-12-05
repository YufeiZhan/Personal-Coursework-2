package uk.ac.ed.inf;

/**
 * Class encapsulating the What3Words(W3W) address for the web server's JSON data.
 *
 * @author Selina Zhan (s1953505)
 */
public class What3Words {
  private String country;
  private Square square;
  private String nearestPlace;
  private Coordinate coordinates; // Pythagorean representation of the location
  private String words; // three words concatenated with .
  private String language;
  private String map;
  
//  TODO: make class private or public?
  /**
   * Inner class encapsulating the square field
   */
  private class Square{
    Coordinate southwest;
    Coordinate northeast;
  }
  
  /**
   * Inner class encapsulating coordinates
   */
  private class Coordinate{
    double lng;
    double lat;
  }
  
  public double getLongitude(){
    return coordinates.lng;
  }
  
  public double getLatitude(){
    return coordinates.lat;
  }
  
  
}
