package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
	public List<Node> vertices = new ArrayList<Node>();
	public double startTime;
	public double endTime;
	
	public Obstacle(List<Node> vertices)
	{
		this.startTime = -1;
		this.endTime = -1;
		orderVertices(vertices);
	}
	
	public Obstacle(List<Node> vertices, double start, double end)
	{
		this.startTime = start;
		this.endTime = end;
		orderVertices(vertices);
	}
	
	private void orderVertices(List<Node> vertices)
	{
		for(int i = 0; i < vertices.size(); i++)
		{
			this.vertices.add(vertices.get(i));
		}
		/*Node temp = null;
		double maxLat = vertices.get(0).lat, minLat = vertices.get(0).lat, 
			   maxLon = vertices.get(0).lon, minLon = vertices.get(0).lon;

		for (int i = 0; i < vertices.size(); i++) {
			temp = vertices.get(i);
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
		
		Node topLeft = new Node(maxLat, minLon), topRight = new Node(maxLat, maxLon), 
			 botLeft = new Node(minLat, minLon), botRight = new Node(minLat, maxLon);
		
		this.vertices = new ArrayList<Node>();
		this.vertices.add(topLeft);// top left
		this.vertices.add(botLeft);// bottom left
		this.vertices.add(botRight);// bottom right
		this.vertices.add(topRight);// top right*/
	}

	public boolean block(Node start, Node goal)
	{
		PathSegment line = new PathSegment(start, goal), sideOfObstacle;
		
		for(int i = 0; i < vertices.size(); i++)
		{
			if(i < vertices.size()-1)
			{
				sideOfObstacle = new PathSegment(vertices.get(i), vertices.get(i+1));
			}
			else
			{
				sideOfObstacle = new PathSegment(vertices.get(i), vertices.get(0));
			}
			if(sideOfObstacle.isIntersect(line))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		String result = "";
		for(Node node:this.vertices)
		{
			result += node.toString() + "; ";
		}
		
		return result;
	}
}
