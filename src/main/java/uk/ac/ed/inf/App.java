package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App{
    
    public static void main(String[] args) {
        /** Process command-line arguments */
        String day = args[0];
        String month = args[1];
        String year = args[2];
        Date date = Date.valueOf(year + "-" + month + "-" + day);
        String websitePort = args[3];
        String databasePort = args[4];
        
        /** Set up real-time servers */
        ServerConnector server = new ServerConnector(websitePort);
        Database database = new Database(databasePort);
        
//        test
        LongLat forestHill = new LongLat(LongLat.MIN_LONGITUDE,LongLat.MAX_LATITUDE);
        LongLat meadow = new LongLat(LongLat.MIN_LONGITUDE,LongLat.MIN_LATITUDE);
        LongLat KFC = new LongLat(LongLat.MAX_LONGITUDE,LongLat.MAX_LATITUDE);
        Segment vertical = new Segment(forestHill,meadow);
        Segment horizontal = new Segment(forestHill,KFC);
        Segment point = new Segment(forestHill,forestHill);
        Segment line = new Segment(meadow,KFC);
        Segment p2 = new Segment(KFC,KFC);
//        System.out.println(Segment.getOrientation(forestHill,KFC,meadow) == Segment.Orientation.CLOCKWISE);
//        System.out.println(Segment.getOrientation(forestHill,meadow,KFC) == Segment.Orientation.ANTICLOCKWISE);
//        System.out.println(Segment.getOrientation(forestHill,meadow,forestHill) == Segment.Orientation.COLLINEAR);
//        System.out.println(Segment.getOrientation(forestHill,meadow,meadow) == Segment.Orientation.COLLINEAR);

//        System.out.println(vertical.intersect(horizontal) == true);
//        System.out.println(point.intersect(horizontal) == true);
//        System.out.println(point.intersect(vertical) == true);
//        System.out.println(line.intersect(point) == false);
//        System.out.println(line.intersect(vertical)==true);
//        System.out.println(line.intersect(p2) == true);
//        System.out.println(vertical.intersect(p2)==false);

//        // test getNoFlyZone
//        PathPlanner planner = new PathPlanner(date, server, database);
//        ArrayList<ArrayList<Segment>> zones = planner.getNoFlyZones();
//        for (ArrayList<Segment> zone : zones){
//            System.out.println("--Zone with " + zone.size() + " number of borders");
//            for (Segment segment : zone) {
//                System.out.println("Start Point: " + segment.getStartPt().toString() + "; End Point: " + segment.getEndPt().toString());
//            }
//        }
//
//        //test intersection function
//        Segment seg = new Segment(new LongLat(-3.189708441495905,55.94468426846805),new LongLat(-3.1897084414,55.94468426846805));
//        System.out.println(planner.intersect(seg) == true);
        

//        PathPlanner pathPlanner = new PathPlanner(date, server, database);
////        System.out.println(pathPlanner.convertW3WToLongLat("pest.round.peanut").getLongitude());
////        System.out.println(pathPlanner.convertW3WToLongLat("pest.round.peanut").getLatitude());

    
        
    }
}
