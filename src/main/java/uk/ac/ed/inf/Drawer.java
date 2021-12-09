package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;

import java.util.ArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for producing a visual representation of the flightpath of the drone
 */
public final class Drawer {
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /**
   * Private constructor to prevent instantiation
   */
  private Drawer(){ // the default constructor is not allowed so prevent instantiation
    throw new UnsupportedOperationException("Drawer is a utility class and cannot be instantiated");
  }
  
  
  //  ---------------------------------------------- Static Functions ----------------------------------------------
  
  public static void produceFile(Date date, DatabaseConnector db){
    String fileName = "drone-" + date.toString() + ".geojson";
    Path path = Paths.get(fileName);
    byte[] content = generateGeoJson(db.queryFlightPaths()).getBytes();
  
    try {
      Files.write(path,content);
    
    } catch (IOException e) {
      System.out.println("File generation failed.");
      e.printStackTrace();
    }
  }
  /**
   * Generate the GeoJSON file of the flightpath in the current working directory.
   *
   * @param date the date on which the orders are considered
   * @param trajectory ArrayList of LongLat points of the complete flightpath trajectory
   */
  public static void produceFile(Date date, ArrayList<LongLat> trajectory){
    String fileName = "drone-" + date.toString() + ".geojson";
    Path path = Paths.get(fileName);
    byte[] content = generateGeoJson(trajectory).getBytes();
    
    try {
      Files.write(path,content);
      
    } catch (IOException e) {
      System.out.println("File generation failed.");
      e.printStackTrace();
    }
  }
  
  
  /**
   * Generate GeoJSON String for further producing file
   *
   * @param trajectory
   */
  private static String generateGeoJson(ArrayList<LongLat> trajectory){
    ArrayList<Point> pl = new ArrayList<>();
    
    for(LongLat point : trajectory){
      Point p = Point.fromLngLat(point.getX(),point.getY());
      pl.add(p);
    }
    
    Geometry x = LineString.fromLngLats(pl);
    Feature f = Feature.fromGeometry(x);
    FeatureCollection fc = FeatureCollection.fromFeature(f);
    
    String result = fc.toJson();
    
    return result;
  }
}
