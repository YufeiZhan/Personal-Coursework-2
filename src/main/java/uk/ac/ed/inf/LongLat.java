package uk.ac.ed.inf;


/**
 * Class emblematic of the visual representation of the coordinate of the delivery system
 * and its relevant utility methods for the delivery service.
 *
 * @author Selina Zhan (s1953505)
 */
public class LongLat {
  //  ---------------------------------------------- Constants ----------------------------------------------
  /** Confinement area */
  public static final double MIN_LATITUDE = 55.942617;
  public static final double MAX_LATITUDE = 55.946233;
  public static final double MAX_LONGITUDE = -3.184319;
  public static final double MIN_LONGITUDE = -3.192473;
  /** Valid State of the drone */
  public static final int HOVERING_STATE = -999;
  public static final int MIN_FLYING_ANGLE = 0;
  public static final int MAX_FLYING_ANGLE = 350;
  /** Defined length of drone's one move in degrees */
  public static final double MOVE_LENGTH = 0.00015;
  /** Distance tolerance for evaluating closeness */
  public static final double DISTANCE_TOLERANCE = 0.00015;
  
  //  ---------------------------------------------- Fields & Constructor ----------------------------------------------
  /** Field definitions */
  private final double x; // longitude
  private final double y; // latitude
  
  /**
   * LongLat constructor for coordinates on map
   *
   * @param longitude longitude of the position point in degrees
   * @param latitude latitude of the position point in degrees
   */
  public LongLat(double longitude, double latitude) {
    this.x = longitude;
    this.y = latitude;
  }
  
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  /**
   * Check whether a point is within the defined confinement area
   *
   * @return true if within confinement area, false otherwise
   */
  public boolean isConfined() {
    boolean isValidLongitude = ( (x > MIN_LONGITUDE) & (x < MAX_LONGITUDE) );
    boolean isValidLatitude = ( (y > MIN_LATITUDE) & (y < MAX_LATITUDE) );
    
    return isValidLongitude & isValidLatitude;
  }
  
  /**
   * Compute Pythagorean distance between points in degrees.
   *
   * @param point the other LongLat position point
   * @return the distance in degrees
   */
  public double distanceTo(LongLat point) {
    double l1 = point.x - x;
    double l2 = point.y - y;
    double distance = Math.sqrt( Math.pow(l1,2) + Math.pow(l2,2) );
    
    return distance;
  }
  
  /**
   * Check whether 2 points are considered close enough.
   * It is close enough if the distance is strictly less than {@value DISTANCE_TOLERANCE}.
   *
   * @param point the other LongLat position point
   * @return true if within tolerance, false otherwise
   */
  public boolean closeTo(LongLat point){
    return distanceTo(point) < DISTANCE_TOLERANCE;
  }
  
  /**
   * Compute the next position of the drone given an angle.
   * Drone can either fly within range or hover for its next move.
   * When flying, 0 <= angle <= 350 and should be multiple of 10.
   *
   * @param angle either a valid angle to fly or the code to hover
   * @return the new LongLat point for a valid move, the current LongLat point otherwise
   */
  public LongLat nextPosition(int angle) {
    boolean isFlying = (angle >= MIN_FLYING_ANGLE) & (angle <= MAX_FLYING_ANGLE);
    boolean isMultipleOfTen = (angle % 10 == 0);
    boolean isValidAngle = isFlying & isMultipleOfTen ;
    boolean isHovering = (angle == HOVERING_STATE);
    boolean isValidMove = isValidAngle | isHovering;
    
    if (isValidMove) {
      if (isHovering) { //position stays unchanged
        return this;
      } else{ //flying changes position
        LongLat newPoint = calculateNewCoord(this,angle);
        return newPoint;
      }
    } else { //invalid move
      System.err.println("ERROR: invalid angle input for drone's next move. No move executed.");
      return this;
    }
  }
  
  
  public double getX(){
    return x;
  }
  
  public double getY(){
    return y;
  }
  
  
  public String toString(){
    return ("(" + x + "," + y + ")");
  }
  
  //---------------------------------------------- Helper Functions ----------------------------------------------
  /**
   * Compute the coordinates of the move for the flying type of movement.
   *
   * Use the conversion formula of polar coordinates:
   * - delta x = cos(angle)*{@value MOVE_LENGTH}
   * - delta y = sin(angle)*{@value MOVE_LENGTH}
   * where angles are measured in radians.
   *
   * Reference to the mathematical formula:
   * <a href="https://mathinsight.org/polar_coordinates">Polar Coordinates</a>
   *
   * @param point the old LongLat position point
   * @param angle valid angle to fly for next move
   * @return the new LongLat position point after the move
   */
  private LongLat calculateNewCoord(LongLat point, int angle) {
      double oldLong = point.x;
      double oldLat = point.y;
      double newLong = oldLong + Math.cos(Math.toRadians(angle)) * MOVE_LENGTH;
      double newLat = oldLat + Math.sin(Math.toRadians(angle)) * MOVE_LENGTH;
      LongLat newPoint = new LongLat(newLong, newLat);
      
      return newPoint;
  }
}
