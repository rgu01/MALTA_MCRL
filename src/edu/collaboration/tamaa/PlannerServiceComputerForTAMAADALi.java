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

/**
 * This class exits in order to treat all the info related to the paths creation for the hardware ( Linux program a.k.a TAMMAA-DALi ) 
 * @author claire
 */
public class PlannerServiceComputerForTAMAADALi {

	private boolean UseMultiTargetPathPlanning;
	public Algo algo = null;
	private ArrayList<Long> execTimes = new ArrayList<Long>();
	private List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
	
	/**
	 * Constructor by default
	 */
	public PlannerServiceComputerForTAMAADALi(){
	}
	
	/**
	 * Cosntructor 
	 * @param UseMultiTargetPathPlanning
	 * @param algo
	 */
	public PlannerServiceComputerForTAMAADALi(boolean UseMultiTargetPathPlanning, Algo algo)
	{
		this.UseMultiTargetPathPlanning = UseMultiTargetPathPlanning;
		this.algo = algo;
	}
	
	/**
	 * This methods is the one calling the different methods for the construction of our paths : computePathsMultiTarget(...) or computePathsSingleTarget(...).
	 * Those methods will actually fill a list of UPPAgentVehicle with their possible paths for each individual. 
	 * This list is an attribute of PlannerServiceComputerForTAMAADALi so we have get back that list. 
	 * @param plan
	 * @param as
	 * @param nArea
	 */
	public void computePaths(Mission plan, PathPlanningAlgorithm as, NavigationArea nArea) {
		if (this.UseMultiTargetPathPlanning && ((this.algo == Algo.Dali || this.algo == Algo.DaliStar))) {
			computePathsMultiTarget(plan, as, nArea);
		} else {
			computePathsSingleTarget(plan, as, nArea);
		}		
	}
	
	/**
	 * Compute the paths when we have a single target and fill the attribute agents ( a list of UPPAgentVehicle ) with the correct info for each individual.
	 * @param plan
	 * @param as
	 * @param nArea
	 */
	private void computePathsSingleTarget(Mission plan, PathPlanningAlgorithm as, NavigationArea nArea) {
		int agentID = 0, milestoneID = 1;// 0 is for the starting position
		HashMap<Node, HashMap<Node, Path>> computedPaths = new HashMap<Node, HashMap<Node, Path>>();
		for (Vehicle v : plan.getVehicles()) {
			milestoneID = 1;
			List<Node> milestones = new ArrayList<Node>();
			UPPAgentVehicle agent = new UPPAgentVehicle(agentID++);
			agent.missionTimeLimit = nArea.missionTimeLimit;
			agent.vehicle = v;
			milestones.add(agent.getStartNode());
			for (Task task : plan.tasks) {
				// one vehicle assumed begin
				task.missionId = (int) task.altitude;
				//
				if (task.assignedVehicleId == 0) {
					task.assignedVehicleId = v.id;
				}
				// one vehicle assumed over
				if (agent.canDoTask(task)) {
					Node milestone = new Node(milestoneID++, task);
					milestones.add(milestone);
					agent.addTask(milestone);
				}
			}
			List<Path> paths = new ArrayList<Path>();
			for (Node n1 : milestones) {
				for (Node n2 : milestones) {
					if (!n1.equals(n2)) {
						Path path = new Path(n1, n2);
						if (!agent.isPathExist(path)) {
							if (computedPaths.containsKey(n1) && computedPaths.get(n1).containsKey(n2)) {
								path = computedPaths.get(n1).get(n2);
							} else {
								long startTime1 = System.nanoTime();
								path = as.calculate(n1, n2, v.maxSpeed);
								execTimes.add(System.nanoTime() - startTime1);
								if (!computedPaths.containsKey(n1)) {
									computedPaths.put(n1, new HashMap<Node, Path>());
								}
								computedPaths.get(n1).put(n2, path);
							}
						}

						if (path != null && !paths.contains(path)) {
							paths.add(path);
						}
					}
				}
			}
			agent.paths = paths;
			this.agents.add(agent);
		}
	}
	
	/**
	 * Compute the paths when we have a multi target and fill the attribute agents ( a list of UPPAgentVehicle ) with the correct info for each individual.
	 * @param plan
	 * @param as
	 * @param nArea
	 */
	private void computePathsMultiTarget(Mission plan, PathPlanningAlgorithm as, NavigationArea nArea) {
		int agentID = 0, milestoneID = 1;// 0 is for the starting position
		HashMap<Node, HashMap<Node, Path>> computedPaths = new HashMap<Node, HashMap<Node, Path>>();
		for (Vehicle v : plan.getVehicles()) {
			milestoneID = 1;
			List<Node> milestones = new ArrayList<Node>();
			UPPAgentVehicle agent = new UPPAgentVehicle(agentID++);
			agent.missionTimeLimit = nArea.missionTimeLimit;
			agent.vehicle = v;
			milestones.add(agent.getStartNode());
			for (Task task : plan.tasks) {
				// one vehicle assumed begin
				task.missionId = (int) task.altitude;
				//
				if (task.assignedVehicleId == 0) {
					task.assignedVehicleId = v.id;
				}
				// one vehicle assumed over
				if (agent.canDoTask(task)) {
					Node milestone = new Node(milestoneID++, task);
					milestones.add(milestone);
					agent.addTask(milestone);
				}
			}
			List<Path> paths = new ArrayList<Path>();
			for (Node n1 : milestones) {
				List<Node> destinations = new ArrayList<Node>();
				for (Node n2 : milestones) {
					if (!n1.equals(n2)) {
						Path path = new Path(n1, n2);
						if (!agent.isPathExist(path)) {
							if (computedPaths.containsKey(n1) && computedPaths.get(n1).containsKey(n2)) {
								path = computedPaths.get(n1).get(n2);
								if (path != null && !paths.contains(path)) {
									paths.add(path);
								}
							} else {
								destinations.add(n2);
							}
						}
					}
				}
				if (destinations.size() != 0) {
					long startTime1 = System.nanoTime();
					List<Path> pathsFromN = ((Dali)as).calculateSingleSource(n1, destinations, v.maxSpeed);
					execTimes.add(System.nanoTime() - startTime1);
					if (!computedPaths.containsKey(n1)) {
						computedPaths.put(n1, new HashMap<Node, Path>());
					}
					for (Path p : pathsFromN) {
						computedPaths.get(n1).put(p.end, p);
						if (p != null && !paths.contains(p)) {
							paths.add(p);
						}
					}
				}
			}
			agent.paths = paths;
			this.agents.add(agent);
		}
	}
	
	/**
	 * We call to get our list of UPPAgentVehicle
	 * @return the UPPAgentVehicle list of this class
	 */
	public List<UPPAgentVehicle> getAgentsComputed()
	{
		return this.agents;
	}
}