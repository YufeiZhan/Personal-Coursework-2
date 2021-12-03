package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;


//TODO: how useful is this class?
public class GeoParser {
  
  FeatureCollection fc;
  
  public GeoParser(String JsonStr){
    fc = FeatureCollection.fromJson(JsonStr);
  }
  
  public List<Feature> getFeatures(){
    return fc.features();
  }
  
//  TODO: only three are used?
  /**
   * Downcast a feature into its specific type
   *
   * @param f
   * @return
   */
  public Geometry getGeometry(Feature f){
    Geometry geo = f.geometry();
    
    if (geo instanceof Polygon){
      return (Polygon) geo;
    };
    if (geo instanceof LineString){
      return (LineString) geo;
    };
    if (geo instanceof Point){
      return (Point) geo;
    };
    
    return null;
  }
  

}
