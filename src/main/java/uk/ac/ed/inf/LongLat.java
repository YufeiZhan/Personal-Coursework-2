package uk.ac.ed.inf;

//TODO: fill java docs and OOP practice document

public class LongLat {
  //TODO： constant 放哪儿比较好？要用static吗？
  static final double MIN_LATITUDE = 55.942617;
  static final double MAX_LATITUDE = 55.946233;
  static final double MAX_LONGITUDE = -3.184319;
  static final double MIN_LONGITUDE = -3.192473;
  static final int HOVERING_STATE = -999;
  static final int MIN_FLYING_ANGLE = 0;
  static final int MAX_FLYING_ANGLE = 350;
  static final double MOVE_LENGTH = 0.00015;
  
  public double longitude;
  public double latitude;
  
  public LongLat(double longitude, double latitude){
    this.longitude = longitude;
    this.latitude = latitude;
  }
  
  public boolean isConfined(){
    boolean isValidLongitude = ((longitude > MIN_LONGITUDE) & (longitude < MAX_LONGITUDE));
    boolean isValidLatitude = ((latitude > MIN_LATITUDE) & (latitude < MAX_LATITUDE));
    
    return isValidLatitude & isValidLongitude;
  }
  
  public double distanceTo(LongLat point){
    double l1 = point.latitude-latitude;
    double l2 = point.longitude-longitude;
    double distance = Math.sqrt( Math.pow(l1,2) + Math.pow(l2,2));
    
    return distance;
  }
  
  public boolean closeTo(LongLat point){
    return distanceTo(point) < MOVE_LENGTH;
  }
  
  public LongLat nextPosition(int angle){
    
    boolean isHovering = (angle == HOVERING_STATE);
    boolean isFlying = (angle >= MIN_FLYING_ANGLE) & (angle <= MAX_FLYING_ANGLE);
    boolean isMultipleOfTen = (angle % 10 == 0);
    boolean isWithinRange = isFlying & isMultipleOfTen ;
    boolean validAngle = isWithinRange | isHovering;
    
    if (validAngle){
      if (isHovering){
        return this;
      } else{ //it's flying
        double[] newCoord = calculateNewCoord(this,angle);
        LongLat newPoint = new LongLat(newCoord[0],newCoord[1]);
        
        return newPoint;
      }
    } else{ //invalid angle situation
      System.out.println("ERROR: invalid angle input. The original position is returned.");
      return this;
    }
  }
  
  
  
  /**
   * Use conversion formula of polar coordinates to first calculate the delta x and delta y,
   * which are then added onto the old coordinates to form the new coordinate -
   * delta x = cos(angle)*{@value MOVE_LENGTH} & delta y = sin(angle)*{@value MOVE_LENGTH} where angles are measured in radians.
   *
   * Reference to the mathematical formula: <a href="https://mathinsight.org/polar_coordinates">Polar Coordinates</a>
   *
   * @param point
   * @param angle
   * @return
   */
  private double[] calculateNewCoord(LongLat point, int angle){
      double oldLong = point.longitude;
      double oldLat = point.latitude;
      double newLong = oldLong + Math.cos(Math.toRadians(angle))*MOVE_LENGTH;
      double newLat = oldLat + Math.sin(Math.toRadians(angle))*MOVE_LENGTH;
      
      return new double[] {newLong,newLat};
  }
  
  
}
