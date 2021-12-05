package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;


// TODO: to delete
//  final keyword is used to prevent extension of the class since it's a utility class
//  Reference: https://www.vojtechruzicka.com/avoid-utility-classes/

/**
 * A utility class that helps read the GeoJSON map.
 *
 * @author Selina Zhan (s1953505)
 */
public final class GeoParser {
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /**
   * Private constructor to prevent instantiation
   */
  private GeoParser(){ // the default constructor is not allowed so prevent instantiation
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  //  ---------------------------------------------- Static Functions ----------------------------------------------
  
  /**
   * Get the list of feature from the source GeoJSON string.
   *
   * @param source GeoJSON string
   * @return list of features
   */
  public static List<Feature> getFeatures(String source){
    return FeatureCollection.fromJson(source).features();
  }
  
  //  TODO: assume only three are used?
  /**
   * Get all the coordinates from a feature and turn them into a ArrayList of LongLats
   *
   * @param feature a specific feature
   * @return LongLat coordinates of the feature
   */
  public static ArrayList<LongLat> getCoordinates(Feature feature){
    
    ArrayList<LongLat> coordinates = new ArrayList<LongLat>();
    Geometry geo = feature.geometry();
    
    // downcast Geometry into Polygon
    if (geo instanceof Polygon){
      Polygon polygon = (Polygon) feature.geometry();
      List<Point> points = polygon.coordinates().get(0);
      for (Point p : points){
        coordinates.add(new LongLat(p.coordinates().get(0), p.coordinates().get(1)));
      }
    };
    
    // downcast Geometry into LineString
    if (geo instanceof LineString){
      LineString lineString = (LineString) feature.geometry();
      List<Point> points = lineString.coordinates();
      for (Point p : points){
        coordinates.add(new LongLat(p.longitude(), p.latitude()));
      }
    };
  
    // downcast Geometry into Point
    if (geo instanceof Point){
      Point p = (Point) feature.geometry();
      coordinates.add(new LongLat(p.longitude(), p.latitude()));
    };
    
    return coordinates;
  }

}
