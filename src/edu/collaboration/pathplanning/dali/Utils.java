package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;

public class Utils {

	public static List<CoordinatesTuple> neighbourLocations(double lat, double lon) {
		ArrayList<CoordinatesTuple> res = new ArrayList<CoordinatesTuple>();
		res.add(new CoordinatesTuple(lat, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon + NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon));
		res.add(new CoordinatesTuple(lat + NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon - NavigationArea.threshold));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon ));
		res.add(new CoordinatesTuple(lat - NavigationArea.threshold, lon + NavigationArea.threshold));
		return res;
	}
	
	public static boolean isInside(double lat, double lon, Node topLeft, Node bottomRight) {
		return ( lat < topLeft.lat && lat > bottomRight.lat &&
				lon > topLeft.lon && lon < bottomRight.lon);
	}
	
    static boolean isInsidePolygon(double lat, double lon, List<Node> nodes) 
    { 
    	int count = 0;
        for (int i =0; i < nodes.size(); i++)
        { 
        	Node n1 = nodes.get(i);
        	Node n2 = nodes.get((i+1)%(nodes.size()));

        	if ((lat == n1.lat && lon == n1.lon) || (lat == n2.lat && lon == n2.lon)) {//of of the 2 nodes
        		return true;
        	}
        	if (lat == n1.lat && lat == n2.lat) { //horisontal segment
        		return (lon <= Math.max(n1.lon, n2.lon) && lon >= Math.min(n1.lon, n2.lon));
        	}
        	//non-horisontal case
        	if (lat < Math.max(n1.lat, n2.lat) && lat > Math.min(n1.lat, n2.lat)) {
        		double side = (lon-n1.lon) * (n2.lat - n1.lat) - (n2.lon-n1.lon) * (lat-n1.lat);
        		if (side == 0) {return true;}
        		if (side * (n2.lat - n1.lat) < 0) {count++;}
    		}
        }
        return (count % 2 == 1);
    } 
	
    static boolean isInside2(double lat, double lon, List<CoordinatesTuple> nodes) 
    { 
    	int count = 0;
        for (int i =0; i < nodes.size(); i++)
        { 
        	CoordinatesTuple n1 = nodes.get(i);
        	CoordinatesTuple n2 = nodes.get((i+1)%(nodes.size()));

        	if ((lat == n1.lat && lon == n1.lon) || (lat == n2.lat && lon == n2.lon)) {//of of the 2 nodes
        		return true;
        	}
        	if (lat == n1.lat && lat == n2.lat) { //horisontal segment
        		return (lon <= Math.max(n1.lon, n2.lon) && lon >= Math.min(n1.lon, n2.lon));
        	}
        	//non-horisontal case
        	if (lat < Math.max(n1.lat, n2.lat) && lat > Math.min(n1.lat, n2.lat)) {
        		double side = (lon-n1.lon) * (n2.lat - n1.lat) - (n2.lon-n1.lon) * (lat-n1.lat);
        		if (side == 0) {return true;}
        		if (side <0) {count++;}
    		}
        }
        return (count % 2 == 1);
    } 
    
//	public static boolean isInside(double lat, double lon, CoordinatesTuple topLeft, CoordinatesTuple bottomRight) {
//		return ( lat < topLeft.lat && lat > bottomRight.lat &&
//				lon > topLeft.lon && lon < bottomRight.lon);
//	}
}
