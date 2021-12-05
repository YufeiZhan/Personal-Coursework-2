package uk.ac.ed.inf;

/**
 * Class emblematic of the visual representation of a line from a LongLat point to another.
 */
public class Segment {
  
  //  ------------------------------------------- Fields & Constructor -------------------------------------------
  private LongLat start;
  private LongLat end;
  
  public Segment(LongLat start, LongLat end){
    this.start = start;
    this.end = end;
  }
  //  ---------------------------------------------- Main Functions ----------------------------------------------
  
  /**
   * Determine whether 2 segments intersect by using the idea of orientation.
   *
   * Reference: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
   *
   * @param segment another segment
   * @return true if intersect, or false otherwise
   */
  public boolean intersect(Segment segment){
    
    // four orientations required for further use
    Orientation o1 = getOrientation(start,end,segment.getStartPt());
    Orientation o2 = getOrientation(start,end,segment.getEndPt());
    Orientation o3 = getOrientation(segment.getStartPt(),segment.getEndPt(),start);
    Orientation o4 = getOrientation(segment.getStartPt(),segment.getEndPt(),end);
    
    // all possible cases for intersection
    boolean generalCase = o1 != o2 && o3 != o4;
    boolean specialCase1 = o1 == Orientation.COLLINEAR && onSegment(start,segment.getStartPt(),end);
    boolean specialCase2 = o2 == Orientation.COLLINEAR && onSegment(start,segment.getEndPt(),end);
    boolean specialCase3 = o3 == Orientation.COLLINEAR && onSegment(segment.getStartPt(),start,segment.getEndPt());
    boolean specialCase4 = o4 == Orientation.COLLINEAR && onSegment(segment.getStartPt(),end,segment.getEndPt());
    boolean satisfiable = generalCase || specialCase1 || specialCase2 || specialCase3 || specialCase4;
    
    return satisfiable ? true : false;
  }
  
  public LongLat getStartPt(){
    return start;
  }
  
  public LongLat getEndPt(){
    return end;
  }
  
  //  ---------------------------------------------- Helper Functions ----------------------------------------------
  
  /**
   * Given that three points are collinear, determine whether a point is on a segment.
   *
   * Reference: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
   *
   * @param start one end of the segment
   * @param p point
   * @param end the other end of the segment
   * @return true if point p on the segment, or false otherwise
   */
  private Boolean onSegment(LongLat start, LongLat p, LongLat end){
    
    return (p.getX() <= Math.max(start.getX(),end.getX()) && p.getX() >= Math.min(start.getX(), end.getX())) &&
            (p.getY() <= Math.max(start.getY(),end.getY()) && p.getY() >= Math.min(start.getY(), end.getY()));
  }
  
  /**
   * Calculate the orientation of an ordered triplet of points.
   * The orientation depends on the value of the formula: (y2 - y1)*(x3 - x2) - (y3 - y2)*(x2 - x1)
   * - if value = 0, then COLLINEAR
   * - if value > 0, then CLOCKWISE
   * - if value < 0, then ANTICLOCKWISE
   *
   * Reference: https://www.geeksforgeeks.org/orientation-3-ordered-points/
   *
   * @param p1 first LongLat point
   * @param p2 second LongLat point
   * @param p3 third LongLat point
   * @return Orientation of the ordered triplet
   */
  private Orientation getOrientation(LongLat p1, LongLat p2, LongLat p3){
    double formula = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) - (p3.getY() - p2.getY()) * (p2.getX() - p1.getX());
    
    if (formula == 0){
      return Orientation.COLLINEAR;
    }
    
    return (formula > 0) ? Orientation.CLOCKWISE : Orientation.ANTICLOCKWISE;
  }
  
  /**
   * Possible orientations of an ordered triplet of points.
   */
  enum Orientation { CLOCKWISE, ANTICLOCKWISE, COLLINEAR }
}
