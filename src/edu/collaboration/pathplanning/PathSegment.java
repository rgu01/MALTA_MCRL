package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Command;
import com.afarcloud.thrift.CommandType;
import com.afarcloud.thrift.TaskCommandStatus;

import edu.collaboration.model.structure.UPPAgentVehicle;

/**
 * 
 * @author rgu01
 * A path segment is a straight line connecting two nodes
 * It should not go through any obstacles in the environment
 */
public class PathSegment {
	private double weight;
	public Node origin;
	public Node end;
	
	public PathSegment(double lat1, double lon1, double lat2, double lon2)
	{
		this.weight = 1.0;
		this.origin = new Node(lat1,lon1);
		this.end = new Node(lat2,lon2);
	}
	
	public PathSegment(Node o, Node e)
	{
		this.weight = 1.0;
		this.origin = o;
		this.end = e;
	}
	
	public Command createNewMove(int moveID, UPPAgentVehicle agent, long startingTime)
	{
		Command move = new Command();
		move.id = moveID;
		move.commandType = CommandType.NAV_WAYPOINT;
		move.commandStatus = TaskCommandStatus.Running;
		move.params = new ArrayList<Double>();
		move.addToParams(0);
		move.addToParams(0);
		move.addToParams(0);
		move.addToParams(0);
		move.addToParams(this.end.getPosition().latitude);
		move.addToParams(this.end.getPosition().longitude);
		move.addToParams(0);
		move.startTime = startingTime;
		move.endTime = startingTime + (int) (this.directLength() / agent.vehicle.maxSpeed);
		
		return move;
	}
	
	public double directLength()
	{
		double c = 0;
		
		c = Math.pow(origin.lon - end.lon, 2) + Math.pow(origin.lat - end.lat, 2);
		c = this.weight * Math.sqrt(c);
		
		return c;
	}
	
	public boolean isIntersect(PathSegment ps)
	{
		double l1x1 = this.origin.lon;
		double l1y1 = this.origin.lat;
		double l1x2 = this.end.lon;
		double l1y2 = this.end.lat;
		double l2x1 = ps.origin.lon;
		double l2y1 = ps.origin.lat;
		double l2x2 = ps.end.lon;
		double l2y2 = ps.end.lat;

        if (Math.max(l1x1,l1x2) < Math.min(l2x1 ,l2x2)
            || Math.max(l1y1,l1y2) < Math.min(l2y1,l2y2)
            || Math.max(l2x1,l2x2) < Math.min(l1x1,l1x2)
            || Math.max(l2y1,l2y2) < Math.min(l1y1,l1y2))
        {
            return false;
        }

        if ((((l1x1 - l2x1) * (l2y2 - l2y1) - (l1y1 - l2y1) * (l2x2 - l2x1)) 
              * ((l1x2 - l2x1) * (l2y2 - l2y1) - (l1y2 - l2y1) * (l2x2 - l2x1))) > 0
            || (((l2x1 - l1x1) * (l1y2 - l1y1) - (l2y1 - l1y1) * (l1x2 - l1x1)) 
              * ((l2x2 - l1x1) * (l1y2 - l1y1) - (l2y2 - l1y1) * (l1x2 - l1x1))) > 0)
        {
            return false;
        }
        return true;
	}
	
	@Override
	public String toString()
	{
		return this.origin.toString() + "->" + this.end.toString();
	}

}
