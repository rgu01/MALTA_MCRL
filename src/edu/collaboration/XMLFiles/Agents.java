package edu.collaboration.XMLFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Task;

import MercatoerProjection.SphericalMercator;
import edu.collaboration.model.structure.UPPAgentVehicle;
import edu.collaboration.pathplanning.Path;

import edu.collaboration.pathplanning.Node;

/**
 * This class is used for "WritingProcess.java" and provide all info the be send back into a String format.
 * <b>Agent</b> class initialize all functions.
 * @author claire
 */
public class Agents {
	
	/**
	 * The number of all tasks
	 */
	private int numberOfTask =-1;

	/**
	 * The maximum different locations a task can be related to 
	 */
	int maxDiffLoc =0;	
	
	/**
	 * precondition_t's ID/Name for task without preconditions
	 */
	private String PreconditionNameForEmptyOnes = "";
	
	/**
	 * HashMap of (String, HashMap of (String,Integer))  := key : task's ID/name && value : HashMap of (String,Integer)
	 * \\
	 * HashMap of (String,Integer)  := key : agent's ID/Name && value : task id												
	 */
	private HashMap<String, HashMap<String,Integer>> TasksID = new HashMap<String, HashMap<String,Integer>>();
	
	/*************************************************************************************************/
	/***************************************  -  CONSTRUCTOR  -  *************************************/
	/*************************************************************************************************/
	public Agents() {
		
	}
	
	/**
	 * Other Constructor
	 * @param numberOfTask : Integer
	 * @param maxDiffLoc : Integer
	 * @param SizeOfGoals : HashMap of (String, HashMap of (String,Integer))  := key : task's ID/name && value : HashMap of (String,Integer) \\ HashMap of (String,Integer)  := key : agent's ID/Name && value : task id	
	 */
	public Agents(int numberOfTask ,int maxDiffLoc, HashMap<String, HashMap<String,Integer>> TasksID, String PreconditionNameForEmptyOnes) {
		this.numberOfTask = numberOfTask;
		this.maxDiffLoc = maxDiffLoc;
		this.TasksID = TasksID;
		this.PreconditionNameForEmptyOnes = PreconditionNameForEmptyOnes;
	}
	
	/*************************************************************************************************/
	/****************************************  -  FUNCTIONS  -  **************************************/
	/*************************************************************************************************/
	
	/**
	 * We write the last part of our model, the initialization of functions : Movement, TasksWithConditions and finally the names.
	 * @param plan : Mission
	 * @param agents : List of (UPPAgentVehicle)
	 * @param TaskEquipment : HashMap of (Integer, ArrayList of Integer)
	 * @param AgentEquipment : HashMap of (String, ArrayList of Integer)
	 * @param AgentsMaxSpeed : HashMap of (String, Double)
	 * @param MilestonesID : HashMap of (String, Position)
	 * @param TaskAndTaskPrecondition : HashMap of (Integer, String) 
	 * @param MilestoneTask : HashMap of (String, Integer)
	 * @param Taskid_tLinkedTask_t : HashMap of (String, String)
	 * @param TaskAndPreconditionName : HashMap of (Integer, String)
	 * @param AgentIniPosition : HashMap of (String, Position)
	 * @return a String with everything
	 */
	public String FunctionsIni(Mission plan, List<UPPAgentVehicle> agents, HashMap<Integer, ArrayList<Integer>> TaskEquipment, HashMap<String, ArrayList<Integer>> AgentEquipment, HashMap<String, Double> AgentsMaxSpeed, HashMap<String, Position> MilestonesID, HashMap<Integer, String> TaskAndTaskPrecondition, HashMap<String, Integer> MilestoneTask, HashMap<String, String> Taskid_tLinkedTask_t, HashMap<Integer, String> TaskAndPreconditionName, HashMap<String, Position> AgentIniPosition)
	{
		String toReturn = "/** Agents */"+ "\n"+"Rong = Referee(INIT_AGENTS, GOAL);"+"\n";
		
		//**********************************Let's start with the Movement function:
		toReturn += "\n" + "/** Movement */";
		
		ArrayList<String> MovementNames = new ArrayList<String>();
		int round = -1;
		//We get the movements per agents
		for ( Map.Entry<String,ArrayList<Integer>> agentsbrowse : AgentEquipment.entrySet()) {
			toReturn += "\n"+"//"+ agentsbrowse.getKey() + "\n";
			//For each milestone
			for ( Map.Entry<String,Integer> entryMil1 : MilestoneTask.entrySet() ) {
				round++;
				//We check if the precondition_t's ID/NAME associated to this task is actually the NAME for a task without precondition
				if (this.PreconditionNameForEmptyOnes.contains(TaskAndPreconditionName.get(entryMil1.getValue())))
				{
					//If there is no precondition, we have to add movement from the initial position to the milestone's task
					double timePosIini = CalculTime(agentsbrowse.getKey(),MilestonesID.get(entryMil1.getKey()), AgentIniPosition.get("ID_Agent"+agentsbrowse.getKey().substring(3)+"_LOCATION"), AgentsMaxSpeed.get(agentsbrowse.getKey()), agents );
					toReturn += "m_movement"+round+"_"+entryMil1.getKey().substring(3,8)+"_IDLE_"+agentsbrowse.getKey().substring(3)+" = Movement("+ agentsbrowse.getKey() +","+ entryMil1.getKey() +",ID_Agent"+agentsbrowse.getKey().substring(3)+"_LOCATION,"+ (int) timePosIini +","+ Taskid_tLinkedTask_t.get(getTaskID(entryMil1.getValue(),agentsbrowse.getKey())) +", TASK_IDLE);"+"\n";
					MovementNames.add("m_movement"+round+"_"+entryMil1.getKey().substring(3,8)+"_IDLE_"+agentsbrowse.getKey().substring(3));
					toReturn += "m_movement"+round+"_IDLE_"+agentsbrowse.getKey().substring(3)+"_"+entryMil1.getKey().substring(3,8)+" = Movement("+ agentsbrowse.getKey() +",ID_Agent"+agentsbrowse.getKey().substring(3)+"_LOCATION,"+ entryMil1.getKey() +","+ (int) timePosIini +", TASK_IDLE,"+ Taskid_tLinkedTask_t.get(getTaskID(entryMil1.getValue(),agentsbrowse.getKey()))+");"+"\n";
					MovementNames.add("m_movement"+round+"_IDLE_"+agentsbrowse.getKey().substring(3)+"_"+entryMil1.getKey().substring(3,8));
				}
				//We connect it with the others
				for ( Map.Entry<String,Integer> entryMil2 : MilestoneTask.entrySet()) {
					//But they need to be different
					if ( !entryMil1.getKey().equals(entryMil2.getKey()) ){						
						//We want to know if the agent possess the required equipment for both tasks located at the 2 milestones => we use the functions CanAgentDoTasks().
						boolean contains = CanAgentDoTasks(agentsbrowse.getValue(),TaskEquipment.get(entryMil1.getValue()), TaskEquipment.get(entryMil2.getValue()));
						//If this agent can do it :
						if ( contains ){
							//We measure the time between the 2 milestones 
							double time = CalculTime(agentsbrowse.getKey(),MilestonesID.get(entryMil1.getKey()), MilestonesID.get(entryMil2.getKey()), AgentsMaxSpeed.get(agentsbrowse.getKey()), agents );
							//Then we add the info forth and back
							// 		-> forth 
							toReturn += "m_movement"+round+"_"+entryMil1.getKey().substring(3,8)+"_"+entryMil2.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3)+" = Movement("+ agentsbrowse.getKey() +","+ entryMil1.getKey() +","+ entryMil2.getKey() +","+ (int) time +","+ Taskid_tLinkedTask_t.get(getTaskID(entryMil1.getValue(),agentsbrowse.getKey())) +","+ Taskid_tLinkedTask_t.get(getTaskID(entryMil2.getValue(),agentsbrowse.getKey()))+");"+"\n";
							//toReturn += "m_movement"+entryMil1.getKey().substring(3,8)+"_"+entryMil2.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3)+" = Movement("+ agentsbrowse.getKey() +","+ entryMil1.getKey() +","+ entryMil2.getKey() +","+ time +","+ getTaskID(entryMil1.getValue(),agentsbrowse.getKey()) +","+ getTaskID(entryMil2.getValue(),agentsbrowse.getKey())+");"+"\n";
							//We don't forget to add the name of our function
							MovementNames.add("m_movement"+round+"_"+entryMil1.getKey().substring(3,8)+"_"+entryMil2.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3));
							// 		-> back
							toReturn += "m_movement"+round+"_"+entryMil2.getKey().substring(3,8)+"_"+entryMil1.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3)+" = Movement("+ agentsbrowse.getKey() +","+ entryMil2.getKey() +","+ entryMil1.getKey() +","+ (int) time +","+ Taskid_tLinkedTask_t.get(getTaskID(entryMil2.getValue(),agentsbrowse.getKey())) +","+ Taskid_tLinkedTask_t.get(getTaskID(entryMil1.getValue(),agentsbrowse.getKey()))+");"+"\n";
							//toReturn += "m_movement"+entryMil2.getKey().substring(3,8)+"_"+entryMil1.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3)+" = Movement("+ agentsbrowse.getKey() +","+ entryMil2.getKey() +","+ entryMil1.getKey() +","+ time +","+ getTaskID(entryMil2.getValue(),agentsbrowse.getKey()) +","+ getTaskID(entryMil1.getValue(),agentsbrowse.getKey())+");"+"\n";
							MovementNames.add("m_movement"+round+"_"+entryMil2.getKey().substring(3,8)+"_"+entryMil1.getKey().substring(3,8)+"_"+agentsbrowse.getKey().substring(3));
						}
					}
				}
			}
		}
		
		//**********************************Let's start with the Movement function:
		toReturn += "\n" + "/** Tasks */"+"\n";
		
		//We will need info only available in Plan
		List<Task> listTask = plan.getTasks();
		ArrayList<String> TasksfunctionName = new ArrayList<String>();
		int i=0;
		//For all the tasks
		for (int itask=0; itask<this.numberOfTask ; itask++){
			//For each agent
			for ( Map.Entry<String,ArrayList<Integer>> agentsbrowse : AgentEquipment.entrySet()) {
				//We want to know if this agent possess the required equipment => we use the functions CanAgentDo().
				boolean contains = CanAgentDo(agentsbrowse.getValue(),TaskEquipment.get(itask));
				//We assume there is no collaboration between agents
				//If this agent can do it :
				if ( contains ){
					//And if there are preconditions
					if (TaskAndPreconditionName.get(itask)!= null )
					{
						toReturn += "t_TaskExecution"+getTaskID(itask,agentsbrowse.getKey()).substring(3,8)+"_"+agentsbrowse.getKey().substring(3)+" = TaskExecution("+agentsbrowse.getKey()+","+listTask.get(itask).bcet+","+listTask.get(itask).wcet+","+Taskid_tLinkedTask_t.get(getTaskID(itask,agentsbrowse.getKey()))+", "+TaskAndPreconditionName.get(itask)+", "+0+", "+"false"+");"+"\n";
						TasksfunctionName.add("t_TaskExecution"+getTaskID(itask,agentsbrowse.getKey()).substring(3,8)+"_"+agentsbrowse.getKey().substring(3));
						i++;
					}
				}
			}
		}
		
		
		//**********************************Let's finish with a recap of the names:
		toReturn += "\n" + "system Rong,"+"\n"+"/**map*/"+"\n";
		//Then we write down the name of all our info
		for (int it=0; it<MovementNames.size(); it++)
		{
			toReturn += MovementNames.get(it)+",";
			if ( it%2 != 0 && it!=0)
			{
				toReturn += "\n";
			}
		}
		toReturn += "\n"+"\n"+"/**tasks*/"+"\n";
		
		for (int it=0; it<(TasksfunctionName.size()-1);it++)
		{
			toReturn += TasksfunctionName.get(it)+",";
			if ( it%2 != 0 && it!=0)
			{
				toReturn += "\n";
			}
		}
		toReturn+= TasksfunctionName.get(TasksfunctionName.size()-1)+";"+"\n"+"\n";
		
		return toReturn;
	}
	
	/*******************************- Shorten methods will help -*********************************/
	
	/**
	 * @param EquiAgents : ArrayList of Integer
	 * @param EquiListTask1 : ArrayList of Integer
	 * @param EquiListTask2 : ArrayList of Integer
	 * @return boolean which will tell us if a agent is equipped with the good requirement for both tasks
	 */
	private boolean CanAgentDoTasks(ArrayList<Integer> EquiAgents, ArrayList<Integer> EquiListTask1, ArrayList<Integer> EquiListTask2) {
		boolean can = false;
		boolean task1doable = false, task2doable = false;
		
		//We need to know if the agent has the equipment required for both task
		for ( int i=0; i<EquiAgents.size(); i++) {
			//Test for the first task
			if (EquiListTask1.contains(EquiAgents.get(i))) 
			{
				task1doable = true;
			}
			//Test for the second one task
			if (EquiListTask2.contains(EquiAgents.get(i)))
			{
				task2doable = true;
			}
		}
		
		if (task1doable && task2doable)
		{
			can = true;
		}
		
		return can;
	}
	
	/**
	 * @param EquiAgents : ArrayList of Integer
	 * @param EquiListTask : ArrayList of Integer
	 * @return boolean which will tell us if a agent is equipped with the good requirement for the task
	 */
	private boolean CanAgentDo(ArrayList<Integer> EquiAgents, ArrayList<Integer> EquiListTask) {
		boolean can = false;
		boolean task1doable = false;
		
		//We need to know if the agent has the equipment required for task
		for ( int i=0; i<EquiAgents.size(); i++) {
			//Test for the first task
			if (EquiListTask.contains(EquiAgents.get(i)))
			{
				task1doable = true;
			}
		}
		
		if ( task1doable )
		{
			can = true;
		}
		
		return can;
	}
	
	/**
	 * Return the time between the milestones, given the vehicle
	 * @param AgentID_Name : String
	 * @param doubles : Double[] position info
	 * @param doubles2 : Double[] position info 
	 * @param maxSpeed : double maximum speed for the vehicle 
	 * @param agents : List of UPPAgentVehicle
	 * @return double the time 
	 */
	private double CalculTime(String AgentID_Name, Position PositionS, Position PositionF, Double maxSpeed, List<UPPAgentVehicle> agents ) {
		double time = 0;
		
		//We need to re calculate the coordinates in Position variable into Node, problem of unit
		List<Node> NewNodes = new ArrayList<Node>();
		SphericalMercator sphericalMercator = new SphericalMercator();
		double lat[] = { 0.0, 0.0}, lon[] = { 0.0, 0.0};
		
		lon[1] = sphericalMercator.xAxisProjection(PositionS.getLongitude());
		lat[1] = sphericalMercator.yAxisProjection(PositionS.getLatitude());
		lon[0] = sphericalMercator.xAxisProjection(PositionF.getLongitude());
		lat[0] = sphericalMercator.yAxisProjection(PositionF.getLatitude());
		NewNodes.add(new Node(lat[0], lon[0]));
		NewNodes.add(new Node(lat[1], lon[1]));
		
		//Now that we have our new Nodes we can start the comparison
		//For all agents
		for ( UPPAgentVehicle it : agents ) {
			//When we found the good one
			if ( it.vehicle.name.equals(AgentID_Name.substring(3)))
			{
				//We can take the path
				for (Path parsePath : it.paths) {
					//If we found the correct path
					if ( ( parsePath.start.equals(NewNodes.get(0)) && parsePath.end.equals(NewNodes.get(1)) ) || ( parsePath.end.equals(NewNodes.get(0)) && parsePath.start.equals(NewNodes.get(1)) ))
					{
						//We get back the traveling time 
						time = it.getTravelingTime(parsePath.length());
					}
				}
			}
		}
		NewNodes.clear();
		
		return time;
	}
	
	/**
	 * @param taskid : Integer
	 * @param AgentName : String
	 * @return the ID/name of the task given an task identification number and the name of the agent related
	 */
	private String getTaskID(Integer taskid, String AgentName) {
		String name ="";
		
		//We browse the map until we find the correct one
		for (Map.Entry<String, HashMap<String,Integer>> entry : this.TasksID.entrySet()) {
			for ( Map.Entry<String,Integer> entry2 : entry.getValue().entrySet() )
			{
				//If we have the correct taskid and the correct AgentName
				if ( (entry2.getValue() == taskid) && (entry2.getKey().equals(AgentName.substring(3))) )
				{	
					//We give back the taks's ID/Name
					name = entry.getKey();
				}
			}
			
		}
		return name;
	}
	
}
