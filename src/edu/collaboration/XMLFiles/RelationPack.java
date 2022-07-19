package edu.collaboration.XMLFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Task;

import edu.collaboration.model.structure.UPPAgentVehicle;

/**
 * This class is used for "WritingProcess.java" and provide all info the be send back into a String format.
 * <b>RelationPack</b> class initialize all relations.
 * @author claire
 */
public class RelationPack {
	
	/**
	 * the size of the goal range
	 */
	private int SizeOfGoals = 1000;
	/**
	 * The number of all tasks
	 */
	private int numberOfTask =-1;
	/**
	 * the number of all agents
	 */
	private int numberOfAgents = -1;
	/**
	 * the number of maximum precondition
	 */
	private int numberOfPreconditions=-1; 

	/**
	 * The maximum different locations a task can be related to 
	 */
	int maxDiffLoc = 0;	
	
	/**
	 * precondition_t's ID/Name for task without preconditions
	 */
	private String PreconditionNameForEmptyOnes = "";
	
	/**
	 * HashMap of (Integer, ArrayList of Integer ) := key : task's id && value = ArrayList of the equipment id 
	 */
	private HashMap<Integer, ArrayList<Integer>> TaskEquipment = new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * HashMap of (String, HashMap of (String,Integer) )  := key : task's ID/name && value : HashMap of (String,Integer)
	 * \\
	 * HashMap of (String,Integer)  := key : agent's ID/Name && value : task id												
	 */
	private HashMap<String, HashMap<String,Integer>> TasksID = new HashMap<String, HashMap<String,Integer>>();
	
	/**
	 * HashMap of (Integer, Integer) := key : old device id && value : new device id
	 */
	private HashMap<Integer, Integer> DevicesID_Old_and_New = new HashMap<Integer, Integer>();
	
	/**
	 * HashMap of (String, Integer) := key : milestone's ID/name && value : taskid_t's id
	 */
	private HashMap<String, Integer> MilestoneTask = new HashMap<String, Integer> ();
	
	/**
	 * HashMap of (Integer, String) := key : task's id && value : task_t required as a precondition
	 */
	private HashMap<Integer, String> TaskAndTaskPrecondition = new HashMap<Integer, String>();
	
	/**
	 * HashMap of (Integer,  ArrayList of String )  := key : equipment id (new) && value : all milestones ID/names where the device is required
	 */
	private HashMap<Integer, ArrayList<String>> DeviceIDMilestones = new HashMap<Integer, ArrayList<String>>();
	
	/**
	 * HashMap of (String, String) := key : milestones' ID/name && value : device_t ID/Name 
	 */
	private HashMap<String, String> MilestonesDevice_tName = new HashMap<String, String>();
	
	/**
	 * HashMap of (String, String) := key : Taskid_t' ID/name && value : Task_t ID/Name 
	 */
	private HashMap<String, String> Taskid_tLinkedTask_t = new HashMap<String, String>();
	
	/**
	 * HashMap of (Integer, String) := key : task's id && value : precondition's ID/Name
	 */
	private HashMap<Integer, String> TaskAndPreconditionName = new HashMap<Integer, String>();
	
	
	/*************************************************************************************************/
	/***************************************  -  CONSTRUCTOR  -  *************************************/
	/*************************************************************************************************/
	public RelationPack() {
		
	}
	
	/**
	 * 
	 * @param numberOfTask
	 * @param numberOfAgents
	 * @param maxDiffLoc
	 * @param SizeOfGoals
	 * @param TaskEquipment
	 * @param DevicesID_Old_and_New
	 * @param MilestoneTask 
	 * @param TasksID
	 */
	public RelationPack(int numberOfTask , int numberOfAgents, int maxDiffLoc, int SizeOfGoals, int numberofPreconditions, HashMap<Integer, ArrayList<Integer>> TaskEquipment, HashMap<Integer, Integer> DevicesID_Old_and_New, HashMap<String, HashMap<String,Integer>> TasksID, HashMap<String, Integer> MilestoneTask ) {
		this.numberOfTask = numberOfTask;
		this.numberOfAgents = numberOfAgents;
		this.maxDiffLoc = maxDiffLoc;
		this.SizeOfGoals = SizeOfGoals;
		this.numberOfPreconditions = numberofPreconditions;
		this.TaskEquipment = TaskEquipment;
		this.DevicesID_Old_and_New = DevicesID_Old_and_New;
		this.TasksID = TasksID;
		this.MilestoneTask = MilestoneTask;
	}
	
	
	/*************************************************************************************************/
	/************************************  -  GETTER And SETTER  -  **********************************/
	/*************************************************************************************************/
	
	public HashMap<Integer, String> GetTaskAndTaskPrecondition(){return this.TaskAndTaskPrecondition;}
	
	public HashMap<Integer, ArrayList<String>> GeTDeviceIDMilestones() {return this.DeviceIDMilestones;}
	
	public HashMap<String, String> GetMilestonesDevice_tName() {return this.MilestonesDevice_tName;}
	
	public HashMap<String, String> GetTaskid_tLinkedTask_t() {return this.Taskid_tLinkedTask_t;}
	
	public HashMap<Integer, String> GetTaskAndPreconditionName(){return this.TaskAndPreconditionName;}
	
	public String Get_PreconditionNameForEmptyOnes() { return this.PreconditionNameForEmptyOnes;}
	
	
	/*************************************************************************************************/
	/****************************************  -  FUNCTIONS  -  **************************************/
	/*************************************************************************************************/
	
	/**
	 * In order to have all the info in our XML for the connections = the second part. 
	 * @param plan : Mission
	 * @param agents : List of UPPAgentVehicle
	 * @param AgentEquipment : HashMap of (String, ArrayList of Integer)
	 * @param AllDevicesUsed : ArrayList of Integer
	 * @param IdEquipmentString : HashMap of (Integer, String)
	 * @param AgentsLocation : HashMap of (Integer, String) 
	 * @param PreconditionTaskAndState : HashMap of (Integer, String)
	 * @return the String with all the info
	 */
	public String relation_pack(Mission plan, List<UPPAgentVehicle> agents, HashMap<String, ArrayList<Integer>> AgentEquipment, ArrayList<Integer> AllDevicesUsed, HashMap<Integer, String> IdEquipmentString, HashMap<String, String> AgentsLocation)
	{
		//The String that we will be returned 
		String toReturn;
		//We will need to browse the task info 
		List<Task> listTask = plan.getTasks();
		//We will need the device id, so let's initialize the attribute AllDevicesUsed with the function RetreiveDeviceId
		RetreiveDeviceId( AllDevicesUsed);
		
		//**********************************Let's start with the device_t:
		toReturn = "/**devices*/"+"\n";
		ArrayList<Integer> ListEquipId = new ArrayList<Integer>();
		//Will allow us to get the maximum of locations a device can be related to 
		int counter;
		
		//For each device let's see to how many tasks it is related to.
		//For each id associated to a device
		for (int i=0; i<AllDevicesUsed.size(); i++) 
		{
			//We initialize the counter to 0 for every new device
			counter=0;
			//We will need the link between a device and its location(s)
			ArrayList<String> milestoneid_tLinked = new ArrayList<String>();
			//We retrieve all the devices that are needed for a task
			for ( Map.Entry<Integer,ArrayList<Integer>> entryTaskEquipment : this.TaskEquipment.entrySet() )
			{
				//The id of the task
				int key = entryTaskEquipment.getKey();
				//An ArrayList with the id device linked to the current task
				ListEquipId = entryTaskEquipment.getValue();		
				//If our device id is related to a key we write our info 
				if ( ListEquipId.contains( AllDevicesUsed.get(i) ) )
				{
					//We write our info on our device_t
					toReturn += "const device_t DV_"+IdEquipmentString.get(AllDevicesUsed.get(i)).substring(3)+"_LOC"+counter+" = {"+IdEquipmentString.get(AllDevicesUsed.get(i)) +", "+GiveMilestoneWithTask(key)+"};"+"\n";
					//We create a link between a Milestone and the ID/Name of the device_t linked 
					this.MilestonesDevice_tName.put(GiveMilestoneWithTask(key), "DV_"+IdEquipmentString.get(AllDevicesUsed.get(i)).substring(3)+"_LOC"+counter);
					//We increase counter
					counter ++;
					//We add the ID/Name of the milestone related to the task
					milestoneid_tLinked.add(GiveMilestoneWithTask(key));
				}
			}
			//We want to know the maximum 
			maxDiffLoc = Math.max(counter, maxDiffLoc);
			//We create a link between a device id (new id) and the milestone(s) it is related to
			this.DeviceIDMilestones.put(this.DevicesID_Old_and_New.get(AllDevicesUsed.get(i)), milestoneid_tLinked);
		}
		
		
		//**********************************Let's continue with the task_t:
		toReturn += "\n"+"/**tasks and their milestones*/"+"\n";
		int count=0;
		//We browse the map of TasksID and it's inner map to in order to get the info 
		for ( Map.Entry<String, HashMap<String,Integer>> entryTaskID : this.TasksID.entrySet() )
		{
			//For every task 
			for ( Map.Entry<String,Integer> entryTaskID2 : entryTaskID.getValue().entrySet() )
			{
				//We have the agent ID/Name and the task id 
				//Given the function GiveDeviceNameWithTask(TaskId), only with the task id, it will return all the milestones ID/Name related to that task
				//We can add the info 
				toReturn += "const task_t "+entryTaskID.getKey().substring(3)+" = {"+entryTaskID.getKey()+", UNKOWNDEVICE, {"+ GiveDeviceNameWithTask(entryTaskID2.getValue(), AllDevicesUsed.size());
				toReturn += "}};"+"\n";
				//Then we need to create a link between the different format of task
				this.Taskid_tLinkedTask_t.put(entryTaskID.getKey(), entryTaskID.getKey().substring(3));
				count++;
			}
		}
		
		//**********************************Let's continue with the preconditions of tasks:
		toReturn += "\n"+"/**preconditions of tasks*/"+"\n";
		count=0;
		//Function maybe 
		for (int i=0; i<this.numberOfTask; i++) 	
		{
			String precondition = listTask.get(i).precondition;
			//If there is a precondition to this task
			if (precondition != null)
			{
				if (! precondition.equals(""))
				{
					//We have to verify that the agent can accomplish the task
					for ( Map.Entry<String,ArrayList<Integer>> agentsbrowse : AgentEquipment.entrySet()) {
						//So we compare the devices
						boolean contains = CanAgentDo(agentsbrowse.getValue(),this.TaskEquipment.get(i));
						//If it's possible
						if (contains) {
							//We parse the precondition
							ArrayList<String> PreconditionInfos = parsePrecondition(precondition, agentsbrowse.getKey());
							//And we write the precondition
							int diff = this.numberOfPreconditions - PreconditionInfos.size();
							toReturn += "const precondition_t PRE_ToTaskId"+i+"_"+agentsbrowse.getKey()+"[NOPRECONS] = {";
							//We get the info related
							for (int j=0; j<PreconditionInfos.size() ; j++)
							{
								toReturn += PreconditionInfos.get(j);
								if ( j!= (PreconditionInfos.size()-1) ) {
									toReturn += ", ";
								}
							}
							//If there are not as many info as the maximal possibility we had those none existing possibilities by hand
							if ( diff != 0 ){
								toReturn+= ",";
								for (int k=0; k<diff ; k++)
								{
									toReturn+= "PRECON_TAUTOLOGY";
									if ( k!= (diff-1) ) {
										toReturn+= ", ";
									}
								}
							}	
							toReturn += "};"+"\n";
							//We add the info to our map
							this.TaskAndPreconditionName.put(i,"PRE_ToTaskId"+i+"_"+agentsbrowse.getKey());
						}
					}
				}
			}
			//If there is no precondition we still need to get back the info 
			else if (precondition == null || precondition.equals("")) {
				//We still verify if the agent can accomplish the task
				for ( Map.Entry<String,ArrayList<Integer>> agentsbrowse : AgentEquipment.entrySet()) {
					boolean contains = CanAgentDo(agentsbrowse.getValue(),this.TaskEquipment.get(i));
					if (contains) {
						//We write the info if it's possible but only with the precondition "by default" 
						toReturn += "const precondition_t PRE_ToTaskId"+i+"_"+agentsbrowse.getKey()+"[NOPRECONS] = {";
						for (int j=0; j<this.numberOfPreconditions-1 ; j++)
						{
							toReturn+= "PRECON_TAUTOLOGY,";
						}
						toReturn+= "PRECON_TAUTOLOGY};"+"\n";
						//We add the info to our map 
						this.TaskAndPreconditionName.put(i,"PRE_ToTaskId"+i+"_"+agentsbrowse.getKey());
						//We also add the NAME/ID of those precondition_t variable
						this.PreconditionNameForEmptyOnes += "PRE_ToTaskId"+i+"_"+agentsbrowse.getKey();
					}
				}
			}
		}
		
		
		//**********************************Let's continue with the collaboration_t:
		//As this info will be changed, we use an external function
		toReturn+= "\n"+CollaborationAgentsInfo()+"\n";
		
		//**********************************Let's continue with the parameters = goals:
		//As this info will be changed, we use an external function
		toReturn+= "\n"+ParametersMission()+"\n"; 
		
		//**********************************Let's continue with the information of agents
		toReturn += "\n"+"/**initial information of agents*/";
		String[] AllAgentNames = new String[this.numberOfAgents];
		//For all agents
		for (int i=0; i<this.numberOfAgents; i++)
		{
			//We get back the name of their initial location ( thanks to our map the ID/Name of the correct Milestone) 
			String LocationName = AgentsLocation.get(agents.get(i).vehicle.getName());
			//We add the info 
			toReturn += "\n"+"const agent_t INIT_"+agents.get(i).vehicle.name+ " = {"+LocationName+", TASK_IDLE, {";
			for (int j=0; j<this.numberOfTask-1 ; j++)
			{
				toReturn +=" UNSTARTED,";
			}
			toReturn += " UNSTARTED";
			toReturn += "}, {SLEEP}};";
			//And we add the name off each agent to AllAgentNames
			AllAgentNames[i] = "INIT_"+agents.get(i).vehicle.name; 
		}
		//Then we can write all those agent_t names, we only have to read AllAgentNames
		toReturn += "\n"+"const agent_t INIT_AGENTS[NOAGENTS] = {" ;
		for (int i=0; i<(this.numberOfAgents-1); i++)
		{
			toReturn += AllAgentNames[i] +", ";
		}
		toReturn += AllAgentNames[this.numberOfAgents-1]+"};"+"\n";
		
		toReturn+= "\n";
		
		return toReturn;		
	}
	
	
	/***********- Methods for variables with an uncertain format as the current time -************/
	
	/**
	 * @return a String with all the info related to the parameters ( goals ) for the mission
	 */
	private String ParametersMission()
	{
		String toReturn = "/**parameters of the mission*/"+"\n";
		
		toReturn += "const goalrange_t GOAL = "+this.SizeOfGoals+";";
		return toReturn;
	}
	
	/**
	 * @return a String with all the info related to the collaboration between agents
	 */
	private String CollaborationAgentsInfo()
	{
		return "/**collaboration between agents*/ "+"/*if NOAGENTS - 1 sup to 1, fill the unused elements with COL_NULL*/";
	}
	
	
	/*******************************- Shorten methods will help -*********************************/
	
	/**
	 * This method allow use to retrieve all the device id's used into the class attribute : AllDevicesUsed
	 */
	private void RetreiveDeviceId(ArrayList<Integer> AllDevicesUsed) 
	{
		ArrayList<Integer> ListId;
		//Let's use our HashMap which contains all the devices id BUT WARNING DUPLICATE !
		for ( Map.Entry<Integer,ArrayList<Integer>> entry : this.TaskEquipment.entrySet() )
		{
			ListId = entry.getValue();
			for (int i=0; i<ListId.size(); i++)
			{	
				//If we didn't already insert the id 
				if (! AllDevicesUsed.contains(ListId.get(i)) )
					//We do
					AllDevicesUsed.add(ListId.get(i));
			}
		}
	}
	
	/**
	 * @param TaskId : Integer
	 * @return a String : the milestone ID, so the name, for the task. Namely it's location.
	 */
	private String GiveMilestoneWithTask(Integer TaskId)
	{
		String Return ="";
		//We browse the map connecting the Milestone's ID/Name and the taskid_t
		for (Map.Entry<String,Integer> entryMilestoneTask : this.MilestoneTask.entrySet() )
		{
			//We return the name of the milestone when we have the correct task
			if ( Objects.equals(TaskId, entryMilestoneTask.getValue()))
			{
				Return += entryMilestoneTask.getKey();
			}
		}
		return Return;
	}
	
	/**
	 * The idea is to get the devices associated to a task thanks to the class attribute <i>TaskEquipment</i>.  
	 * From there, we extract the milestones ID related to the device with the class attribute <i>DeviceIDMilestones</i>.
	 * @param TaskId : Integer 
	 * @return a String : the milestones ID related to a device.
	 */
	private String GiveDeviceNameWithTask(Integer TaskId, int NbOfDevices)
	{
		String Return = "";
		//We get the devices associated to a task
		ArrayList<Integer> Devices = this.TaskEquipment.get(TaskId);
		//For each of them
		for (int i=0; i<Devices.size() ; i++)
		{
			//The id of the device (the old id)
			int idDevice =Devices.get(i);
			//We get back the new id 
			int newId = this.DevicesID_Old_and_New.get(idDevice);
			//I get the Milestones linked
			ArrayList<String> MilestonesLinked = this.DeviceIDMilestones.get(newId);
			//This integer will let us now if we will have to add none existing milestone ID
			int diff = NbOfDevices - MilestonesLinked.size();
			//We get the info related
			for (int j=0; j<MilestonesLinked.size() ; j++)
			{
				Return+= this.MilestonesDevice_tName.get(MilestonesLinked.get(j));
				if ( j!= (MilestonesLinked.size()-1) ) {
					Return+= ",";
				}
			}
			//If there are not as many info as the maximal possibility we had those none existing possibilities by hand
			if ( diff != 0 ){
				Return+= ",";
				for (int k=0; k<diff ; k++)
				{
					Return+= "UNKOWNDEVICE";
					if ( k!= (diff-1) ) 
					{
						Return+= ",";
					}
				}
			}	
		}
		return Return;
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
	 * 
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
	
	/**
	 * @param precondition : String 
	 * @param AgentName : String agent's name
	 * @return ArrayList of String : return all the info for each logical step in the precondition
	 */
	private ArrayList<String> parsePrecondition(String precondition, String AgentName)
	{
		String test = precondition;
		ArrayList<String> Precondition = new ArrayList<String>();
				
		//If there is an OR or an AND
		if ( (test.indexOf("AND")!=-1) || (test.indexOf("OR")!=-1)  )
		{
			//We get the index for each logical possibility 
			int ANDIndex = test.indexOf("AND"), NOTIndex = test.indexOf("NOT"), ORIndex = test.indexOf("OR");
			String Logic="";
			
			//We get the first info but we have to take in account that it might be a negation
			if ( NOTIndex == 0 )
			{
				String state = test.substring(4,5);
				int ID = Integer.parseInt(test.substring(5,6));
				Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNOT, "+ Connection_Logic(ANDIndex,ORIndex) +", " + formatedState(state)+"}");
			}
			else {
				String state = test.substring(0,1);
				int ID = Integer.parseInt(test.substring(1,2));
				Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNULL, "+ Connection_Logic(ANDIndex,ORIndex) +", " + formatedState(state)+"}");
			}
			
			//We parse the rest
			while (ANDIndex > 1 || ORIndex > 1)
			{	
				//The closest is a And or an OR ?
				int min = min(ANDIndex, ORIndex);
				//Return the logical String related to the closest: AND or OR
				Logic = Logic(ANDIndex,ORIndex);
				//We cut the initial String of the precondition to the localization of the logical noun ( AND or OR )
				test = test.substring(min+Logic.length());			
						
				//Return the new closest logical String: AND or OR
				ANDIndex = test.indexOf("AND");
				ORIndex = test.indexOf("OR");
				Logic = Logic(ANDIndex,ORIndex);
				
				//We must test about the possible negation every single time 
				//So we check if there is a negation before the next logical connection ( hidden in Logic )
				if ( test.contains("NOT") && ( test.indexOf("NOT")<test.indexOf(Logic) ||  test.indexOf(Logic)==-1 ) )
				{
					//If there is a negation, we get back the info
					test = test.substring(test.indexOf("NOT"));
					String state = test.substring(4,5);
					int ID = Integer.parseInt(test.substring(5,6));
					ANDIndex = test.indexOf("AND");
					ORIndex = test.indexOf("OR");
					Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNOT, "+ Connection_Logic(ANDIndex,ORIndex) +", " + formatedState(state)+"}");
				}
				else {
					String state = test.substring(1,2);
					int ID = Integer.parseInt(test.substring(2,3));
					ANDIndex = test.indexOf("AND");
					ORIndex = test.indexOf("OR");
					Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNULL, "+ Connection_Logic(ANDIndex,ORIndex) +", " + formatedState(state)+"}");
				}
						
			}			
		}
		//It means it is only a single info 
		else 
		{	
			if ( test.contains("NOT") )
			{
				String state = test.substring(4,5);
				int ID = Integer.parseInt(test.substring(5,6));
				Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNOT, "+"LNULL, " + formatedState(state)+"}");
			}
			else {
				String state = test.substring(0,1);
				int ID = Integer.parseInt(test.substring(1,2));
				Precondition.add("{"+AgentName+", "+getTaskID(ID,AgentName)+", LNULL, "+"LNULL, " + formatedState(state)+"}");
			}
			
		}
		
		System.out.println("From the Array");
		for (int i=0; i<Precondition.size()-1 ; i++) {
			System.out.println(Precondition.get(i)+",");
		}
		System.out.println(Precondition.get(Precondition.size()-1));
		return Precondition;
	}
	
	/**
	 * 
	 * @param state : F or S 
	 * @return the real status : FINISHED or STARTED
	 */
	public static String formatedState(String state) {
		String Status = "";
		if (state.equals("F")){
			Status = "FINISHED";
		}
		else if (state.equals("S")){
			Status = "STARTED";
		}
		return Status;
	}
	
	/**
	 * 
	 * @param AndIndex : the index of the upcoming "AND"
	 * @param OrIndex : the index of the upcoming "OR"
	 * @return the next logical connection : LAND/LOR/LNULL
	 */
	public static String Connection_Logic(Integer AndIndex, Integer OrIndex) {
		String Connection_Logic = "";	
			
		//If one of them doesn't appear anymore yet the other one does
		if ( AndIndex <= 2 && OrIndex > 1)
		{
			Connection_Logic = "LOR";
		}
		else if ( OrIndex <= 1 && AndIndex > 2)
		{
			Connection_Logic = "LAND";
		}
		//If there are both of them 
		else if ( AndIndex < OrIndex && AndIndex >=0 && OrIndex >= 0 )
		{
			Connection_Logic = "LAND";
		}
		else if ( AndIndex > OrIndex && AndIndex >=0 && OrIndex >= 0 )
		{
			Connection_Logic = "LOR";
		}
		//If none of them are left
		else 
		{
			Connection_Logic = "LNULL";
		}
		
		
		return Connection_Logic;
	}
	
	/**
	 * 
	 * @param AndIndex : the index of the upcoming "AND"
	 * @param OrIndex : the index of the upcoming "OR"
	 * @return the logical String related to the closest connection: AND or OR
	 */
	public static String Logic(Integer AndIndex, Integer OrIndex) {
		String Logic = "AND";
		//We use the method Connection_Logic() to ease our work
		if ( Connection_Logic(AndIndex, OrIndex).equals("LOR") )
		{
			Logic = "OR";
		}
		else if( Connection_Logic(AndIndex, OrIndex).equals("LAND") )
		{
			Logic = "AND";
		}
		return Logic;
	}
	
	/**
	 * 
	 * @param AndIndex : the index of the upcoming "AND"
	 * @param OrIndex : the index of the upcoming "OR"
	 * @return return the index of the closest logical connector ( AND/OR ). If there is one of them left.
	 */
	public static Integer min(Integer AndIndex, Integer OrIndex) {
		Integer min = 0;
		//If only one of them is left
		if ( AndIndex <= 2 && OrIndex > 1)
		{
			min = OrIndex;
		}
		else if ( OrIndex <= 1 && AndIndex > 2)
		{
			min = AndIndex;
		}
		//If there are both of them 
		else if ( AndIndex < OrIndex )
		{
			min = AndIndex;
		}
		else if ( AndIndex > OrIndex )
		{
			min = OrIndex;
		}
		//If none of them are left
		else 
		{
			min = 0;
		}
		return min;
	}

}
