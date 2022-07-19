package edu.collaboration.tamaa;

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
 * This class exists in order to recompute as much as we need the plan send back by the hardware
 * @author claire
 */
public class PlannerServiceComputerForMMT {
	
	private static int InitialTaskID = 21;
	
	private String uppaalAddress;
	private int uppaalPort;
	private boolean ownModel;
	private String ownModelAddress;
	
	public Algo algo = null;
	private ArrayList<Long> execTimes = new ArrayList<Long>();
	private List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();
	
	private List<Path> passAnomalyPaths = new ArrayList<Path>();
	private List<Integer> passAnomalyPathsTime = new ArrayList<Integer>();
	private int NbRecomputeUnsuccess = 1;
	private int NbRecomputeTimedAnomalies = 5;
	private static int moveID = 0;
	
	/**
	 * Constructor by default
	 */
	public PlannerServiceComputerForMMT() {
	}
	
	/**
	 * Constructor
	 * @param uppaalAddress
	 * @param uppaalPort
	 * @param ownModel
	 * @param ownModelAddress
	 * @param algo
	 * @param agents
	 */
	public PlannerServiceComputerForMMT(String uppaalAddress, int uppaalPort, boolean ownModel, String ownModelAddress, Algo algo, List<UPPAgentVehicle> agents)
	{
		this.uppaalAddress = uppaalAddress;
		this.uppaalPort = uppaalPort;
		this.ownModel = ownModel;
		this.ownModelAddress = ownModelAddress;
		this.algo = algo;
		this.agents = agents;
	}
	
	/**
	 * @param plan
	 * @param as
	 * @param nArea
	 * @return a boolean. Will, actually, always return true because the plan will be recompute each time we found an issue until everything is good.
	 */
	public boolean SuccessfullyGenerated(Mission plan, PathPlanningAlgorithm as, NavigationArea nArea)
	{
		boolean so = false;
		try 
		{
			// Calculating a mission plan that includes paths and task schedule
			boolean success = generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			// If the calculation fails, we recompute by considering the temporary obstacles (a.k.a., anomalies) and 
			// the users' preference (a.k.a., preferred locations)
			while ((!success && NbRecomputeUnsuccess > 0)
					|| (passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies > 0)) {
				if (passAnomalyPaths.size() != 0 && NbRecomputeTimedAnomalies > 0)
					success = recomputePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime, nArea);
				else
					success = recomputeWithoutPreferedLocations(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			}
			so = success;
		}
		catch (Exception ex) {
			System.out.println("final xerror");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return so;
	}
	
	/**
	 * 
	 * @param plan
	 * @param as
	 * @param passAnomalyPaths
	 * @param passAnomalyPathsTime
	 * @return a boolean : to verify if the plan was generated with other paths
	 * @throws Exception
	 */
	private boolean recomputeWithoutPreferedLocations(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths, List<Integer> passAnomalyPathsTime) throws Exception {
		NbRecomputeUnsuccess--;
		NbRecomputeTimedAnomalies = 5;
		if (this.algo == Algo.AStar || this.algo == Algo.AStar2) {
			return false;
		}
		Dali dali = (Dali) as;
		dali.setUsePreferedAreas(false);
		for (UPPAgentVehicle agent : this.agents) {
			for (int i = 0; i < agent.paths.size(); i++) {
				Path path = agent.paths.get(i);
				long startTime1 = System.nanoTime();
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, 0);
				execTimes.add(System.nanoTime() - startTime1);
				agent.paths.set(i, newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}
	
	/**
	 * 
	 * @param plan
	 * @param as
	 * @param passAnomalyPaths
	 * @param passAnomalyPathsTime
	 * @param nArea
	 * @return a boolean : to verify if the plan was generated with other paths
	 * @throws Exception
	 */
	private boolean recomputePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths,List<Integer> passAnomalyPathsTime, NavigationArea nArea) throws Exception {
		NbRecomputeTimedAnomalies--;
		if (this.algo == Algo.AStar || this.algo == Algo.AStar2) {
			passAnomalyPaths.clear();
			return false;
		}
		Dali dali = (Dali) as;
		dali.setCheckAnomalies(true);
		cleanPlan(plan);
		for (UPPAgentVehicle agent : this.agents) {
			for (int i = 0; i < passAnomalyPaths.size(); i++) {
				Path path = passAnomalyPaths.get(i);
				int startTime = passAnomalyPathsTime.get(i);
				agent.missionTimeLimit = nArea.missionTimeLimit;
				agent.paths.removeIf(oldpath -> oldpath.start == path.start && oldpath.end == path.end);
				long startTime1 = System.nanoTime();
				Path newPath = dali.calculate(path.start, path.end, agent.vehicle.maxSpeed, startTime);
				execTimes.add(System.nanoTime() - startTime1);
				agent.paths.add(newPath);
			}
		}
		return generatePlan(plan, as, passAnomalyPaths, passAnomalyPathsTime);
	}
	
	/**
	 * 
	 * @param plan
	 * @param as
	 * @param passAnomalyPaths
	 * @param passAnomalyPathsTime
	 * @return boolean that will ensure us that we can parse the XML file sent by Linux was parsed
	 * @throws Exception
	 */
	private boolean generatePlan(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths, List<Integer> passAnomalyPathsTime) throws Exception {
		//boolean success = true;
		boolean success = callUppaal();
		if (!success) {
			String show = "Time out: server does not respond!";
			// JOptionPane.showMessageDialog(null, show, "Error: Time Out",
			// JOptionPane.PLAIN_MESSAGE);
		} else {
			passAnomalyPaths.clear();
			passAnomalyPathsTime.clear();
			success = parseXML(plan, as, passAnomalyPaths, passAnomalyPathsTime);
			if (!success) {
				String show = "No mission plan is found!";
				// JOptionPane.showMessageDialog(null, show, "Warning: Dissatisfied",
				// JOptionPane.PLAIN_MESSAGE);
			}
		}
		return success;
	}

	/**
	 * @param plan
	 */
	private void cleanPlan(Mission plan) {
		plan.commands.clear();
		plan.tasks.removeIf(x -> x.getTaskTemplate().getTaskType().name() == "TRANSIT");
	}
	
	/**
	 * 
	 * @param plan
	 * @return a new ID ( unicity is verified ) 
	 */
	private int newTaskID(Mission plan)
	{
		int maxID = 0;
		
		for (Task task : plan.tasks) {
			if(maxID < task.id)
			{
				maxID = task.id;
			}
		}
		
		return maxID + 1;
	}
	
	/**
	 * 
	 * @param plan
	 * @param as
	 * @param passAnomalyPaths
	 * @param passAnomalyPathsTime
	 * @return a boolean : testifies of the file send from UPPAAL is in the correct format, policies included (?).
	 */
	private boolean parseXML(Mission plan, PathPlanningAlgorithm as, List<Path> passAnomalyPaths, List<Integer> passAnomalyPathsTime) {
		TaskSchedulePlan taskPlan = TaskScheduleParser.parse();
		TaskScheduleState states;
		AgentState agentState;
		TaskScheduleAction action;
		// multiple vehicles
		Task[] movement = new Task[this.agents.size()];
		//Task[] execution = new Task[this.agents.size()];
		List<Command>[] segments = new ArrayList[this.agents.size()];
		Integer[] startTime = new Integer[this.agents.size()];
		Node[] currentNode = new Node[this.agents.size()];
		Node[] targetNode = new Node[this.agents.size()];
		Node[] waypoint = new Node[this.agents.size()];
		Node[] lastPosition = new Node[this.agents.size()];
		Integer[] lastPostionTime = new Integer[this.agents.size()];

		if (taskPlan.satisfied) {
			for (int index = 0; index < taskPlan.length(); index++) {
				states = taskPlan.states.get(index);
				action = taskPlan.actions.get(index);
				for (UPPAgentVehicle agent : this.agents) {
					if (agent.ID == action.agentID) {
						agentState = states.getAgentState(agent.ID);
						// start to move
						if (action.type.equals(TaskScheduleAction.StrMoveStart)) {
							if (!agentState.currentPosition.equals("initial")) {
								currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
								if (startTime[agent.ID] == null) {
									startTime[agent.ID] = 0;
								}
								movement[agent.ID] = this.startMove(currentNode[agent.ID], agent, startTime[agent.ID]);
								if (as instanceof Dali) {
									lastPosition[agent.ID] = currentNode[agent.ID];
									lastPostionTime[agent.ID] = startTime[agent.ID];
								}
							}
						}
						// finish a move
						else if (action.type.equals(TaskScheduleAction.StrMoveFinish)) {
							targetNode[agent.ID] = agent.getMilestone(action.target);
							if (as instanceof Dali) {
								Path currentPath = agent.findPath(lastPosition[agent.ID].getPosition(),
										targetNode[agent.ID].getPosition());
								if (((Dali) as).pathEntersAnomaly(currentPath, lastPostionTime[agent.ID],
										agent.vehicle.maxSpeed)) {
									passAnomalyPaths.add(currentPath);
									passAnomalyPathsTime.add(lastPostionTime[agent.ID]);
								}
								
							}
							segments[agent.ID] = this.finishMove(movement[agent.ID], agent, targetNode[agent.ID],
									(int) action.time, as);
							startTime[agent.ID] += (int) action.time;
						}
						// start an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskStart)) {
							currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
							Task newTask = null;
							for (Task task : plan.tasks) {
								waypoint[agent.ID] = new Node(task.getArea().area.get(0));
								if (task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint[agent.ID].equals(currentNode[agent.ID])) {
									if(task.endTime == 0)
									{
										//this task is not assigned yet
										task.assignedVehicleId = agent.vehicle.id;
										task.startTime = startTime[agent.ID];
										movement[agent.ID].parentTaskId = task.id;
										//execution[agent.ID] = task;
										break;
									}
									else
									{
										newTask = task.deepCopy();
										newTask.id = this.newTaskID(plan);
										newTask.assignedVehicleId = agent.vehicle.id;
										newTask.startTime = startTime[agent.ID];
										newTask.endTime = 0;
										movement[agent.ID].parentTaskId = newTask.id;
										break;
									}
								}
							}
							if(newTask != null)
							{
								plan.addToTasks(newTask);
							}
							// add movement going to this task's milestone
							plan.addToTasks(movement[agent.ID]);
							for (Command segment : segments[agent.ID]) {
								plan.addToCommands(segment);
							}
						}
						// finish an execution
						else if (action.type.equals(TaskScheduleAction.StrTaskFinish)) {
							currentNode[agent.ID] = agent.getMilestone(agentState.currentPosition);
							//execution[agent.ID].assignedVehicleId = agent.vehicle.id;
							//execution[agent.ID].endTime = startTime[agent.ID] + (int) action.time;
							//newly added
							for (Task task : plan.tasks) {
								waypoint[agent.ID] = new Node(task.getArea().area.get(0));
								if (task.assignedVehicleId == agent.vehicle.id
										&& task.taskTemplate.taskType.equals(TaskType.INSPECT)
										&& waypoint[agent.ID].equals(currentNode[agent.ID])
										&& task.endTime == 0) {
									task.endTime = startTime[agent.ID] + (int) action.time;
									startTime[agent.ID] = (int) task.endTime;
									break;
								}
							}
						}
					}
				}
			}
		}

		return taskPlan.satisfied;
	}
	
	/**
	 * 
	 * @return a boolean : in order to verify if the connection and the model was correctly generated
	 * @throws Exception
	 */
	private boolean callUppaal() throws Exception {
		boolean result = true;
		// Generate the UPPAAL model for generating mission plans
		// @Claire, this function will be replaced by your function of generating the new UPPAAL models
		UPPAgentGenerator.run(this.agents); 
		// Send the UPPAAL model to the server
		TransferFile trans = new TransferFile(this.uppaalAddress, this.uppaalPort);
		if(!this.ownModel) {
			trans.sendFile(UPPAgentGenerator.outputXML);
		}
		else {
			trans.sendFile(this.ownModelAddress);
			//trans.sendFile("./model/special use case - no monitors.xml");
		}
		// The model is transferred
		trans.close();
		// Receive the result of mission planning from the server
		trans = new TransferFile(this.uppaalAddress, this.uppaalPort);		
		trans.receiveFile(TaskScheduleParser.planPath);
		// An XML file that stores resulting mission plan is received
		if (trans.isClosed() && trans.timeOut) {
			result = false;
		}
		trans.close();
		return result;
	}
	
	
	/**
	 * 
	 * @param node
	 * @param agent
	 * @param startTime
	 * @return a task
	 */
	private Task startMove(Node node, UPPAgentVehicle agent, long startTime) {
		int taskID = InitialTaskID++;
		if(taskID == 25)
		{
			taskID = 25;
		}
		Task transit = new Task();
		Orientation bearing = new Orientation();
		List<EquipmentType> requiredTypes = new java.util.ArrayList<EquipmentType>();
		Position start = node.getPosition();

		transit.setAssignedVehicleId(agent.vehicle.id);
		transit.altitude = 0;
		transit.assignedVehicleId = agent.vehicle.id;
		bearing.roll = 0;
		bearing.pitch = 0;
		bearing.yaw = 0;
		transit.bearing = bearing;

		transit.area = new Region();
		transit.area.area = new java.util.ArrayList<Position>();
		transit.area.area.add(start);

		transit.startTime = startTime;
		transit.range = 0;
		transit.id = taskID;

		transit.setTaskTemplate(new TaskTemplate(TaskType.TRANSIT, "", TaskRegionType.Column, requiredTypes));
		transit.speed = agent.vehicle.maxSpeed;
		transit.startTime = startTime;
		transit.taskStatus = TaskCommandStatus.Running;
		transit.taskTemplate.description = "Start to move";

		return transit;
	}
	
	/**
	 * 
	 * @param transit
	 * @param agent
	 * @param endPoint
	 * @param duration
	 * @param as
	 * @return a list of command
	 */
	private List<Command> finishMove(Task transit, UPPAgentVehicle agent, Node endPoint, int duration, PathPlanningAlgorithm as) {
		Position startPoint = transit.area.area.get(0);
		Path path = agent.findPath(startPoint, endPoint.getPosition());
		Node start = path.start;
		long startTime = transit.startTime;
		List<Command> movement = new ArrayList<Command>();
		List<Node> segments = path.segments;
		if (this.algo == Algo.Dali || this.algo == Algo.DaliStar) {
			segments = ((Dali) as).pathStraightener(path, transit.startTime, agent.vehicle.maxSpeed);
		}
		transit.area.area.add(endPoint.getPosition());
		transit.endTime = transit.startTime + duration;
		for (Node end : segments) {
		//for (Node end : path.segments) {
			if (!end.equals(start)) {
				Command move = new PathSegment(start, end).createNewMove(moveID++, agent, startTime);
				move.setRelatedTask(transit);
				movement.add(move);
				start = end;
				startTime = move.endTime;
			}
		}

		return movement;
	}
	
	/**
	 * In order to get the size of the different anomalies 
	 * @return the size of passAnomalyPaths
	 */
	public int getpassAnomalyPathsSize() {
		return passAnomalyPaths.size();
	}
}
