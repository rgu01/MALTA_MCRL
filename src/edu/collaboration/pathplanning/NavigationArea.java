package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Vehicle;

import MercatoerProjection.SphericalMercator;
import edu.collaboration.pathplanning.dali.CoordinatesTuple;

public class NavigationArea {
	public static double threshold;
	public int missionTimeLimit = 0;
	public List<Node> boundry;
	public List<Node> milestones;
	public List<Obstacle> obstacles;

	// A square navigation area
	public NavigationArea(Mission plan) {
		int number = 4;
		double minSpeed = Double.MAX_VALUE;
		Node vertices[] = new Node[number];
		double maxLat, minLat, maxLon, minLon;		
		double top_left_lon = 0, top_left_lat = 0, top_right_lon = 0, top_right_lat = 0, bot_right_lon = 0,
				bot_right_lat = 0, bot_left_lon = 0, bot_left_lat = 0;
		SphericalMercator sphericalMercator = new SphericalMercator();
		
		for (Vehicle v : plan.getVehicles()) {
			if (v.getMaxSpeed() < minSpeed) {
				minSpeed = v.getMaxSpeed();
			}
		}
		// Navigation Area
		top_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(3).longitude);
		top_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(3).latitude);
		top_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(2).longitude);
		top_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(2).latitude);
		bot_right_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(1).longitude);
		bot_right_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(1).latitude);
		bot_left_lon = sphericalMercator.xAxisProjection(plan.getNavigationArea().area.get(0).longitude);
		bot_left_lat = sphericalMercator.yAxisProjection(plan.getNavigationArea().area.get(0).latitude);
		vertices[0] = new Node(top_right_lat, top_right_lon);
		vertices[1] = new Node(bot_left_lat, bot_left_lon);
		vertices[2] = new Node(top_left_lat, top_left_lon);
		vertices[3] = new Node(bot_right_lat, bot_right_lon);
		maxLat = vertices[0].lat; minLat = vertices[0].lat; maxLon = vertices[0].lon; minLon = vertices[0].lon;

		for (int i = 0; i < number; i++) {
			if (maxLat < vertices[i].lat) {
				maxLat = vertices[i].lat;
			} else if (minLat > vertices[i].lat) {
				minLat = vertices[i].lat;
			}
			if (maxLon < vertices[i].lon) {
				maxLon = vertices[i].lon;
			} else if (minLon > vertices[i].lon) {
				minLon = vertices[i].lon;
			}
		}
		
		Node topLeft = new Node(maxLat, minLon), topRight = new Node(maxLat, maxLon), 
			 botLeft = new Node(minLat, minLon), botRight = new Node(minLat, maxLon);
		double lon_dif = topLeft.lon - botRight.lon;
		double lat_dif = topLeft.lat - botRight.lat;
		double total_dif = lon_dif > lat_dif ? lon_dif : lat_dif;
		double total_time = total_dif/minSpeed;
		
		NavigationArea.threshold = total_dif / total_time;
		this.boundry = new ArrayList<Node>();
		this.milestones = new ArrayList<Node>();
		this.obstacles = new ArrayList<Obstacle>();
		boundry.add(topLeft);// top left
		boundry.add(botLeft);// bottom left
		boundry.add(botRight);// bottom right
		boundry.add(topRight);// top right
		
		//time limit of the entire mission: seconds
		int start = plan.getNavigationArea().startTime;
		int end = plan.getNavigationArea().endTime;
		this.missionTimeLimit = end - start;
	}

	public boolean contains(Node n) {
		Node topRight = this.boundry.get(3);
		Node botLeft = this.boundry.get(1);
		if (n.lon > topRight.lon || n.lat > topRight.lat) {
			return false;
		}
		if (n.lon < botLeft.lon || n.lat < botLeft.lat) {
			return false;
		}

		return true;
	}

	public boolean collide(Node start, Node goal)
	{
		for(Obstacle obs:this.obstacles)
		{
			if(obs.block(start,goal))
			{
				return true;
			}
		}
		return false;
	}
}
