package uk.ac.ed.inf;

import java.awt.desktop.SystemSleepEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

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
        DatabaseConnector database = new DatabaseConnector(databasePort);
        
//        test
        LongLat forestHill = new LongLat(LongLat.MIN_LONGITUDE,LongLat.MAX_LATITUDE);
        LongLat meadow = new LongLat(LongLat.MIN_LONGITUDE,LongLat.MIN_LATITUDE);
        LongLat KFC = new LongLat(LongLat.MAX_LONGITUDE,LongLat.MAX_LATITUDE);
        Segment vertical = new Segment(forestHill,meadow);
        Segment horizontal = new Segment(forestHill,KFC);
        Segment point = new Segment(forestHill,forestHill);
        Segment line = new Segment(meadow,KFC);
        Segment p2 = new Segment(KFC,KFC);
        
        
        LongLat destination = new LongLat(-3.18933,55.943389);
        LongLat destination2 = new LongLat(-3.188367,55.945356);
        LongLat pickUp = new LongLat( -3.191065,55.945626);
        LongLat landmark = new LongLat(-3.191594,55.943658);
        Segment segment = new Segment(destination,pickUp);
        PathPlanner planner = new PathPlanner(date,server,database);
        
        planner.run();
        System.out.println("Total number of orders: " + database.queryOrdersByDate(date).size());
        Drawer.produceFile(date,database);
        
////         test database input
//        planner.run();
//        database.testDeliveries();
//        database.testFlightPath();
    
//         test file output
//        Drawer.produceFile(date,planner.generateTrajectory());
        
//        //test bearingTO
//        LongLat origin = new LongLat(0,0);
//        System.out.println(origin.bearingTo(origin) == 0);
//        LongLat pp1 = new LongLat(1,1);
//        LongLat pp2 = new LongLat(-1,1);
//        LongLat pp3 = new LongLat(-1,-1);
//        LongLat pp4 = new LongLat(1,-1);
//        System.out.println(origin.bearingTo(pp1) == 45);
//        System.out.println(origin.bearingTo(pp2) == 180-45);
//        System.out.println(origin.bearingTo(pp3) == 180+45);
//        System.out.println(origin.bearingTo(pp4) == 360-45);
//        LongLat pp5 = new LongLat(1,0);
//        LongLat pp6 = new LongLat(-5,0);
//        LongLat pp7 = new LongLat(0,4);
//        LongLat pp8 = new LongLat(0,-7);
//        System.out.println(origin.bearingTo(pp5) == 0);
//        System.out.println(origin.bearingTo(pp6) == 180);
//        System.out.println(origin.bearingTo(pp7) == 90);
//        System.out.println(origin.bearingTo(pp8) == 270);
        
//        System.out.println("Landmarks: " +planner.getLandmarks().toString());
//        System.out.println("Destinations : " +planner.getAllDestinations().toString());
//        System.out.println("Shops : " +planner.getAllShopLocations().toString());
//        System.out.println("SP: " + Drone.STARTING_POINT.toString());
        
//        System.out.println(database.queryOrdersByDate(date).toString());
//        planner.allOrderTrajectory();
        

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
        
//        //test landmarks
//        PathPlanner landmarkPlanner = new PathPlanner(date, server, database);
//        ArrayList<LongLat> landmarks = landmarkPlanner.getLandmarks();
//        for (LongLat mark : landmarks){
//            System.out.println("Landmark: (" + mark.getX() + "," + mark.getY() + ")");
//        }
        
//        //test getAllDestinations
//        PathPlanner destinationPlanner = new PathPlanner(date, server, database);
//        for (LongLat p : destinationPlanner.getAllDestinations()){
//            System.out.println("(" + p.getX() + "," + p.getY() + ")");
//        }
//
//        System.out.println("Second method");
//
//        for (LongLat p : destinationPlanner.getAllDestinations2()){
//            System.out.println("(" + p.getX() + "," + p.getY() + ")");
//        }

        
        
//        //test getAllShopLocations
//        PathPlanner shopPlanner = new PathPlanner(date, server, database);
//        System.out.println(server.getMenuStr());
//        for (LongLat l : shopPlanner.getAllShopLocations()){
//            System.out.println("(" + l.getX() + "," + l.getY() + ")");
//        }

//        PathPlanner pathPlanner = new PathPlanner(date, server, database);
//        System.out.println(pathPlanner.convertW3WToLongLat("pest.round.peanut").getLongitude());
//        System.out.println(pathPlanner.convertW3WToLongLat("pest.round.peanut").getLatitude());

    
//        // test if 2 LongLat is the same
//        LongLat pp1 = new LongLat(1,2);
//        LongLat pp2 = new LongLat(1,2);
//        LongLat pp0 = new LongLat(2,3);
//        System.out.println(pp1.equals(pp2) == true);
//        System.out.println(pp1.equals(pp0) == false);
//        System.out.println("Hashcode of pp1: " + pp1.hashCode());
//        System.out.println("Hashcode of pp2: " + pp2.hashCode());
//
//
//        // test if 2 Segments is the same
//        LongLat pp3 = new LongLat(3,4);
//        Segment s1 = new Segment(pp1,pp3);
//        Segment s2 = new Segment(pp3,pp1);
//        Segment s3 = new Segment(pp1,pp2);
//        System.out.println(s1.equals(s2) == true);
//        System.out.println(s1.equals(s3) == false);
//        System.out.println("Hashcode of s1: " + s1.hashCode());
//        System.out.println("Hashcode of s2: " + s2.hashCode());
//
//        // test HashMap used for segments
//        HashMap<Segment,Integer> map = new HashMap<>();
//        map.put(s1,10);
//        System.out.println(map.containsKey(s1) ==  true);
//        System.out.println(map.containsKey(s2) ==  true);
//        map.put(s2,5);
//        System.out.println(map.get(s1) == 5 == true);
//        System.out.println(map.get(s2) == 5 == true);
//
//        // test hashmap remove behavior
//        map.remove(s1);
//        System.out.println(map.containsKey(s2) == false);
    
        
    }
}
