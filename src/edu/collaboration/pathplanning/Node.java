package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Task;

import MercatoerProjection.SphericalMercator;

public class Node {
	public double lon;
	public double lat;
	public Node parent;
	public Task task;
	public int id;
	//
	public Node(int id, Task task)
	{
		SphericalMercator sphericalMercator = new SphericalMercator();
		this.task = task;
		this.parent = null;
		this.lon = sphericalMercator.xAxisProjection(task.area.area.get(0).longitude);
		this.lat = sphericalMercator.yAxisProjection(task.area.area.get(0).latitude);
		this.id = id;
	}
	
	public Node(double lat, double lon)
	{
		this.lon = lon;
		this.lat = lat;
		this.parent = null;
		this.task = null;
		this.id = -1;
	}	
	
	public Node(Position position)
	{
		SphericalMercator sphericalMercator = new SphericalMercator();
		this.parent = null;
		this.lon = sphericalMercator.xAxisProjection(position.longitude);
		this.lat = sphericalMercator.yAxisProjection(position.latitude);
		this.parent = null;
		this.task = null;
		this.id = -1;
	}
	
	public Position getPosition()
	{
		SphericalMercator sphericalMercator = new SphericalMercator();
		return new Position(sphericalMercator.x2lon(this.lon), sphericalMercator.y2lat(this.lat), 0.0);
	}
	
	public List<Node> neighbors()
	{
		List<Node> ns = new ArrayList<Node>();
		
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon));
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon - NavigationArea.threshold));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon));
		ns.add(new Node(this.lat - NavigationArea.threshold, this.lon + NavigationArea.threshold));
		ns.add(new Node(this.lat, this.lon + NavigationArea.threshold));
		ns.add(new Node(this.lat + NavigationArea.threshold, this.lon + NavigationArea.threshold));
		
		return ns;
	}
	

@Override
	public int hashCode() {
		int result = 17;
		result = (int) (result * 31 + this.lat);
		result = (int) (result * 31 + this.lon);
		
		return result;
	}
	
	@Override
	public boolean equals(Object o)
	{
		// If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Node or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Node)) { 
            return false; 
        } 
          
        // typecast o to Node so that we can compare data members  
        Node ns = (Node) o; 
        
        double dif_lat = Math.abs(this.lat - ns.lat);
        double dif_lon = Math.abs(this.lon - ns.lon);
		
		return dif_lat<=0.0001 && dif_lon<=0.0001;
	}
	
	public String toString()
	{
		return "Node " + this.id + " :(" + this.lon + ", " + this.lat + ")";
	}
	
	public boolean near(Node ns)
	{
		PathSegment ps = new PathSegment(this, ns);
		
		return ps.directLength() <= 2*NavigationArea.threshold;
	}
	
	@Override
	public Node clone()
	{
		Node copy = new Node(this.lat, this.lon);
		copy.parent = this.parent.clone();
		
		return copy;
	}
}
