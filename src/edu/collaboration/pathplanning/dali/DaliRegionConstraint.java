package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.afarcloud.thrift.RegionType;

import java.util.List;

import edu.collaboration.pathplanning.Node;

public class DaliRegionConstraint {

	CoordinatesTuple center;
	public static double startTime;
	public static double endTime;
	double priority;
	boolean isDesirable;
	RegionType regionType;
	List<Node> corners;
	
	public DaliRegionConstraint(List<Node> corners, double priority, double start, double end, RegionType rt) {
		this.corners = new ArrayList<Node>(corners);
		selectVertices(corners);
		setPriority(priority, rt);
		this.startTime = start;
		this.endTime = end;		
	}
	
	public DaliRegionConstraint(List<Node> corners, double priority, RegionType rt) {
		this.corners = new ArrayList<Node>(corners);
		selectVertices(corners);
		setPriority(priority, rt);
		this.startTime = -1;
		this.endTime = -1;
	}
	
	private void setPriority(double priority, RegionType rt) {
		if (priority <1) { /// cancel wrong input
			priority = 1;
		}
		this.priority = priority;
		isDesirable = priority > 0;
		this.regionType = rt;
	}
	
	private void selectVertices(List<Node> corners) {
		double maxLat = corners.get(0).lat, minLat = corners.get(0).lat, 
				   maxLon = corners.get(0).lon, minLon = corners.get(0).lon;

		Node temp;
		for (int i = 1; i < corners.size(); i++) {
			temp = corners.get(i);
			if (maxLat < temp.lat) {
				maxLat = temp.lat;
			} else if (minLat > temp.lat) {
				minLat = temp.lat;
			}
			if (maxLon < temp.lon) {
				maxLon = temp.lon;
			} else if (minLon > temp.lon) {
				minLon = temp.lon;
			}
		}

		center = new CoordinatesTuple((maxLat + minLat)/2, (maxLon + minLon)/2);
	}

}