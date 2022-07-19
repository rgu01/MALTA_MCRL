package edu.collaboration.tamaa;

//Check what I need to import 
import MercatoerProjection.SphericalMercator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import com.afarcloud.thrift.*;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import edu.collaboration.communication.TransferFile;
import edu.collaboration.model.structure.UPPAgentGenerator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.AStar2;
import edu.collaboration.pathplanning.dali.Dali;
import edu.collaboration.pathplanning.dali.DaliRegionConstraint;
import edu.collaboration.pathplanning.dali.DaliStar;
import edu.collaboration.pathplanning.xstar.*;
import edu.collaboration.tamaa.PlannerServiceHandler.Algo;
import edu.collaboration.taskscheduling.*;

import edu.collaboration.PathPlanningFactory.*;

/**
 * The class exists in order to read the info send by MMT to the Middleware.
 * @author claire
 */
public class PlannerServiceReader {
	//public Algo algo = null;
	public String algo;
	
	/**
	 * Constructor by default 
	 */
	public PlannerServiceReader() {
	}
	
	/**
	 * Constructor 
	 * @param algo : and Algo type of object in order to know the type of pathplanning algorithm chosen
	 */
	/*
	public PlannerServiceReader(Algo algo) {
		this.algo = algo;
	}
	*/
	public PlannerServiceReader(String algo) {
		this.algo = algo;
	}
	
	/**
	 * 
	 * @param plan
	 * @return Returns an Object array of 2. The first one is a NavigationArea variable in which we have all info related to the navigation.
	 * The second Object is an Algo variable, it creates the pathfinding algorithm chose according to the navigation area.
	 */
	public Object[] read(Mission plan){
		Object[] returns = new Object[2];
		
		double lat[] = { 0.0, 0.0, 0.0, 0.0 }, lon[] = { 0.0, 0.0, 0.0, 0.0 };
		SphericalMercator sphericalMercator = new SphericalMercator();
		// Obtain the navigation area
		NavigationArea nArea = new NavigationArea(plan);
		// Obtain the coordinates of the verdices of special areas, such as forbidden areas.
		List<Node> obsVertices = new ArrayList<Node>();
		// Obtain the special areas that Dali can cope with
		List<DaliRegionConstraint> regionPreferences = new ArrayList<DaliRegionConstraint>();
		// Iterate the list of forbidden areas, which actually includes all kinds of special areas
		for (Region forbidden : plan.getForbiddenArea()) {
			lon[3] = sphericalMercator.xAxisProjection(forbidden.getArea().get(3).longitude);
			lat[3] = sphericalMercator.yAxisProjection(forbidden.getArea().get(3).latitude);
			lon[2] = sphericalMercator.xAxisProjection(forbidden.getArea().get(2).longitude);
			lat[2] = sphericalMercator.yAxisProjection(forbidden.getArea().get(2).latitude);
			lon[1] = sphericalMercator.xAxisProjection(forbidden.getArea().get(1).longitude);
			lat[1] = sphericalMercator.yAxisProjection(forbidden.getArea().get(1).latitude);
			lon[0] = sphericalMercator.xAxisProjection(forbidden.getArea().get(0).longitude);
			lat[0] = sphericalMercator.yAxisProjection(forbidden.getArea().get(0).latitude);
			obsVertices.add(new Node(lat[0], lon[0]));
			obsVertices.add(new Node(lat[1], lon[1]));
			obsVertices.add(new Node(lat[2], lon[2]));
			obsVertices.add(new Node(lat[3], lon[3]));
			// Put each of the special areas into the corresponding list
			switch (forbidden.regionType) {
			case FORBIDDEN:
				nArea.obstacles
						.add(new Obstacle(obsVertices, (double) forbidden.startTime, (double) forbidden.endTime));
				break;
			case PREFERRED:
				regionPreferences
						.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.PREFERRED));
				break;
			case LESS_PREFERRED:
				regionPreferences
						.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.LESS_PREFERRED));
				break;
			case HEAT_REGION:
				regionPreferences
						.add(new DaliRegionConstraint(obsVertices, forbidden.intensity, RegionType.HEAT_REGION));
				break;
			default:
			}
			obsVertices.clear();
		}
		//The first object that will be returned: a navigationArea variable
		returns[0] = nArea;
		
		PathPlanningAlgorithm as = null;
		//A more friendly way to call our path planning algorithm: we call our factory
		PathPlannningFactory Factory = new PathPlannningFactory(); 
		
		//Then we create the parameters for the function. It's an array of object in which we put all the parameters that may be called for the construction. 
		// !!! Check getAlgo's description there is an order !!! 
		Object[] ParametersPathPlanningAlgorithm = new Object[3];
		ParametersPathPlanningAlgorithm[0] = this.algo;
		ParametersPathPlanningAlgorithm[1] = nArea;
		ParametersPathPlanningAlgorithm[2] = regionPreferences;
		//Returns the PathPlanningAlgorithm type we want
		as = Factory.getAlgo(ParametersPathPlanningAlgorithm);
		
		//The second object that will be returned: a PathPlanningAlgorithm variable
		returns[1] = as;
	
		return returns;
	}
	
}
