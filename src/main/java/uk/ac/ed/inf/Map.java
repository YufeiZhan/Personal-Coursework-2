package uk.ac.ed.inf;

import java.util.*;

/**
 * Class emblematic of a visual map.
 *
 * It contains all relevant geographical info required for the PathPlanner class to design,
 * including constraints (no-fly-zones) and landmarks.
 */
public class Map {
  //  ------------------------------------------ Fields & Constructor ----------------------------------------------
  
  private final ArrayList<ArrayList<Segment>> noFlyZones;
  private final ArrayList<LongLat> landmarks;
  public final ArrayList<LongLat> destinations;
  public final ArrayList<LongLat> shops;
  
  //useful trajectories from shop/starting point to shop/student
  private HashMap<Segment,ArrayList<LongLat>> trajectory;
  
  public Map(ArrayList<ArrayList<Segment>> noFlyZones,ArrayList<LongLat> landmarks,
             ArrayList<LongLat> destinations, ArrayList<LongLat> shops){
    this.noFlyZones = noFlyZones;
    this.landmarks = landmarks;
    this.destinations = destinations;
    this.shops = shops;
  
    trajectory = computeTrajectory();
  }
  
  /**
   * Return any 'shop/starting point <-> shop/student' or 'shop <-> student' trajectory.
   *
   * @param start LongLat starting point of the drone or shop location
   * @param end LongLat location for either a shop or a student location
   * @return a ArrayList of LongLat points of the trajectory
   */
  public ArrayList<LongLat> findTrajectory(LongLat start, LongLat end){
    Segment segment = new Segment(start,end);
    return trajectory.get(segment);
  }
  
  /**
   * Compute all possible trajectories from shop to shop/student on the map.
   */
  public HashMap<Segment,ArrayList<LongLat>> computeTrajectory(){
    HashMap<Segment,ArrayList<LongLat>> trajectories = new HashMap<>();
    
    // trajectories from any shops to any pickup destinations
    for(LongLat shop : shops){
      for (LongLat destination : destinations){
        Segment segment = new Segment(shop,destination);
        trajectories.put(segment, computeTrajectory(shop,destination));
      }
    }
    
    // trajectories from any shops to shops, mainly for case where an order needs to visit 2 shops
    for(LongLat shop1 : shops){
      for (LongLat shop2 : shops){
        Segment segment = new Segment(shop1,shop2);
        trajectories.put(segment,computeTrajectory(shop1,shop2));
      }
    }
  
    // from starting point to each shop
    for(LongLat shop : shops){
      Segment segment = new Segment(Drone.STARTING_POINT,shop);
      trajectories.put(segment,computeTrajectory(Drone.STARTING_POINT,shop));
    }
  
    // from starting point to each student
    for(LongLat destination : destinations){
      Segment segment = new Segment(Drone.STARTING_POINT,destination);
      trajectories.put(segment,computeTrajectory(Drone.STARTING_POINT,destination));
    }
    
    return trajectories;
  }
  
  // 到destination：starting point 可以很好的cover gs，部分bristo square可能reach不到，可以把start当作shop来compute最短路径
  // 到shop：用trajectory function 把starting当作 destination，shop -》 starting point
  // 得到starting point到所有shop和destination的距离
  
//  /**
//   * Compute all possible trajectories from starting point to shop/student on the map
//   */
//  private HashMap<Segment,ArrayList<LongLat>> computeStartPointTrajectory(){
//    HashMap<Segment,ArrayList<LongLat>> toStartPointTrajectory = new HashMap<>();
//
//
//
//
//
//    return toStartPointTrajectory;
//  }
  

//   TODO：(暂时不考虑) 简化逻辑，如果有reachable landmark，test整个线路，如果都能走，那就最短路线，
//   如果有一些不能走，就考虑能走的；如果都不能走，那就要考虑先到别的landmarks可以reach到的点,然后再compute那个点到终点的路线
//   assume that很多点landmarks都是可以reach到的，所以离start最近的点有很大概率可以reach到合适的landmark
//   但假如出现了离start最近的点也不能reach landmark的可能性，那么很有可能系统就不work了
  
  
  //如果有直线距离，一步到位
  //如果无法一步到位，考虑landmark：优先考虑能走的，其次考虑距离最短的landmark
  //TODO：这个地图不会出现用不了landmark的情况，暂时不考虑了，没时间了；有空再加
  
  /**
   * Compute a single trajectory from a shop/starting point <-> a shop/student.
   *
   * @param shop shop locations or starting point
   * @param destination shop locations or student locations
   * @return a ArrayList of LongLat point of the trajectory
   */
  public ArrayList<LongLat> computeTrajectory(LongLat shop, LongLat destination){
    ArrayList<LongLat> trajectory = new ArrayList<>();
    trajectory.add(shop);
    
    Segment segment = new Segment(shop,destination);
    if (intersect(segment)) { //find a landmark to avoid no-fly-zone
      trajectory.add(probeLandmarks(shop, destination));
    }
    
    trajectory.add(destination);
    return trajectory;
    
//    ArrayList<LongLat> trajectory = new ArrayList<>();
//    trajectory.add(shop);
//    trajectory.add(destination);
//
//    System.out.println("check2");
//
//    boolean invalidTrajectory = true;
//
//    while(invalidTrajectory){
//      boolean valid = true;
//      for(int i=0; i<trajectory.size()-1; i++){
//        Segment s = new Segment(trajectory.get(i), trajectory.get(i+1));
//        if(PathPlanner.intersect(s)){
//          valid = false;
//          trajectory.add(i+1, findIntermediate(trajectory.get(i)));
//        }
//        System.out.println("check4");
//        System.out.println(trajectory.toString());
//      }
//
//      System.out.println(trajectory.toString());
//
//      if (valid == true){
//        invalidTrajectory = false;
//      }
//    }
//
//    return trajectory;
    
  }
  
  /**
   * Find a landmark if the shop is not directly reachable to the student
   * @param shop
   * @param destination
   * @return
   */
  public LongLat probeLandmarks(LongLat shop, LongLat destination){
    double minDistance = Double.POSITIVE_INFINITY;
    LongLat choice = null;
    
    for (LongLat mark : landmarks){
      Segment seg1 = new Segment(shop,mark);
      Segment seg2 = new Segment(mark,destination);
      double distance;
      if (intersect(seg1) || intersect(seg2)){
        continue;
      } else { //reachable landmark for both start and end point
        distance = seg1.distance() + seg2.distance();
        minDistance = distance < minDistance ? distance : minDistance;
        choice = mark;
      }
    }
    
    return choice;
  }
  
  /**
   * Determine whether a straight trajectory intersect no-fly-zones.
   *
   * @param trajectory a straight trajectory
   * @return true if intersect, or false otherwise
   */
  public  Boolean intersect(Segment trajectory){
    // check for each no-fly zone
    for (ArrayList<Segment> zone : noFlyZones){
      // check for each border of the perimeter
      for(Segment border : zone){
        if (border.intersect(trajectory)){
          return true;
        }
      }
    }
    
    return false;
  }
  
//  /**
//   * Find the next reachable point when there's no straight path.
//   * Probe landmarks and starting point first, then surrounding destinations/shops.
//   *
//   * @param start the point of interest
//   * @return a reachable point
//   */
//  public LongLat findIntermediate(LongLat start){
//    System.out.println("The point given: " + start);
//    System.out.println("check3");
//    LongLat reachable = null;
//
//    //TODO: 基本上点都能reach到landmarks
//
//    // probe landmarks
//    double distance = Double.POSITIVE_INFINITY;
//    for (LongLat mark : landmarks){
//      System.out.println("Compared landmarks" + mark.toString());
//      Segment s = new Segment(mark,start);
//      System.out.println("appropriate marks: " + (!start.equals(mark) && !start.equals(Drone.STARTING_POINT) && !PathPlanner.intersect(s) && s.distance()<distance));
//      if (!start.equals(mark) && !start.equals(Drone.STARTING_POINT) && !PathPlanner.intersect(s) && s.distance()<distance){
//        reachable = mark;
//        distance = s.distance();
//      }
//    }
//
////    System.out.println("reachable: " + reachable.toString());
//
//    // if no reachable landmarks, probe starting point
//    if (reachable == null && start != Drone.STARTING_POINT){
//      Segment seg = new Segment(start, Drone.STARTING_POINT);
//      if (!PathPlanner.intersect(seg)){
//        reachable = new LongLat(Drone.STARTING_POINT.getX(), Drone.STARTING_POINT.getY());
//      }
//    }
//
////    System.out.println("reachable: " + reachable.toString());
//
////    System.out.println("Order locations: " + orderLocations.toString());
//    // if no reachable landmarks & starting point, then probe the closest destinations and shops
//    if (reachable == null){
//      Collections.sort(orderLocations, (o1, o2) -> {
//        // sorting based on the given start point
//        if (o1.distanceTo(start) - o2.distanceTo(start) == 0){
//          return 0;
//        }
//        return (o1.distanceTo(start) - o2.distanceTo(start))<0 ? -1 : 1;
//      });
//
//      //TODO: risk is that 有两个很近的都接触不到的点，就会一直循环下去。
//      //TODO: 同时assume最近的点能不会intersect
//      System.out.println("Order locations: " + orderLocations.toString());
//      reachable = orderLocations.get(0).equals(start) ? orderLocations.get(1) : orderLocations.get(0);
//    }
//
////    System.out.println("reachable: " + reachable.toString());
//
//    return reachable;
//
//  }
  
//  private boolean reachableLandmarks(LongLat p){
//    boolean reachable = false;
//    for (LongLat mark : landmarks){
//      if(PathPlanner.intersect(new Segment(p,mark))){
//        reachable = true;
//      }
//    }
//
//    return reachable;
//  }
  

  
}
