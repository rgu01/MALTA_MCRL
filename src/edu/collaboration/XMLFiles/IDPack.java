package edu.collaboration.XMLFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afarcloud.thrift.Equipment;
import com.afarcloud.thrift.EquipmentType;
import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Task;

import edu.collaboration.model.structure.UPPAgentVehicle;

/**
 * This class is used for "WritingProcess.java" and provide all info the be send back into a String format.
 * <b>IDPack</b> class initialize all IDs.
 * @author claire
 */
public class IDPack {
	
	// We have a lot of attributes
	private int numberOfTask = -1;
	private int numberOfAgents = -1;
	private int numberOfMilestonesTotal = -1;
	int maxDiffLoc = 0;	

	/**
	 * HashMap of (Integer, ArrayList of Integer) := key : task's id && value = ArrayList of the equipment id 
	 * 
	 */
	private HashMap<Integer, ArrayList<Integer>> TaskEquipment = new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * HashMap of (String, ArrayList of Integer )  := key : agent's ID/name && value : id of equipments
	 * 
	 */
	private HashMap<String, ArrayList<Integer>> AgentEquipment = new HashMap<String, ArrayList<Integer>>();
	
	/**
	 * HashMap of (String, Double) := key : agent's ID/name && value : the max speed
	 **/
	private HashMap<String, Double> AgentsMaxSpeed = new HashMap<String, Double >();
	
	/**
	 * HashMap of (Integer, Integer) := key : old device id && value : new device id
	 * 
	 */
	private HashMap<Integer, Integer> DevicesID_Old_and_New = new HashMap<Integer, Integer>();
	
	/**
	 * HashMap of (String, Position) := key : milestone's ID/name && value : milestone Position
	 * 
	 */
	private HashMap<String, Position> MilestonesID = new HashMap<String, Position>();
	
	/**
	 * HashMap of (String, Position) := key : milestone's ID/name && value : milestone Position
	 * 
	 */
	private HashMap<String, Position> AgentIniPosition = new HashMap<String, Position>();
	
	/**
	 * HashMap of (String, HashMap of (String,Integer))   := key : task's ID/name && value : HashMap of (String,Integer)
	 * \\
	 * HashMap of (String,Integer)  := key : agent's ID/Name && value : task id											
	 */
	private HashMap<String, HashMap<String,Integer>> TasksID = new HashMap<String, HashMap<String,Integer>>();
	
	/**
	 * HashMap of (Integer, String) := key : equipment's id (old) && value : equipment's ID/name
	 * 
	 */
	private HashMap<Integer, String> IdEquipmentString = new HashMap<Integer, String>();
	
	/**
	 * HashMap of (String, Integer) := key : milestone's ID/name && value : taskid_t's id
	 * 
	 */
	private HashMap<String, Integer> MilestoneTask = new HashMap<String, Integer> ();
	
	/**
	 * HashMap of (String, String) := key : agent's ID/name && value : ID/Name of it's Location 
	 * 
	 */
	private HashMap<String, String> AgentsLocation = new HashMap<String, String>();

	
	/*************************************************************************************************/
	/***************************************  -  CONSTRUCTOR  -  *************************************/
	/*************************************************************************************************/
	/**
	 * Constructor by default
	 */
	public IDPack() {		
	}
	
	/**
	 * Other constructor
	 * @param numberOfTask
	 * @param numberOfAgents
	 */
	public IDPack(int numberOfTask , int numberOfAgents, int numberOfMilestonesTotal) {
		this.numberOfTask = numberOfTask;
		this.numberOfAgents = numberOfAgents;
		this.numberOfMilestonesTotal = numberOfMilestonesTotal;
	}
	
	
	/*************************************************************************************************/
	/************************************  -  GETTER And SETTER  -  **********************************/
	/*************************************************************************************************/
	public int getNumberOfMilestones() {return this.numberOfMilestonesTotal;}
	
	public HashMap<Integer, ArrayList<Integer>> Get_TaskEquipment(){ return this.TaskEquipment; }
	
	public HashMap<String, ArrayList<Integer>> Get_AgentEquipment(){ return this.AgentEquipment; }
	
	public HashMap<String, Double> Get_AgentsMaxSpeed(){ return this.AgentsMaxSpeed; }
	
	public HashMap<Integer, Integer>  Get_DevicesID_Old_and_New(){ return this.DevicesID_Old_and_New; }
	
	public HashMap<String, Position> Get_MilestonesID(){ return this.MilestonesID; }
	
	public HashMap<String, Position> Get_AgentIniPosition(){ return this.AgentIniPosition; }
	
	public HashMap<String, HashMap<String,Integer>> Get_TasksID(){ return this.TasksID; }
	
	public HashMap<Integer, String> Get_IdEquipmentString(){ return this.IdEquipmentString; }
	
	public HashMap<String, Integer> Get_MilestoneTask(){ return this.MilestoneTask; }
	
	public HashMap<String, String> Get_AgentsLocation(){ return this.AgentsLocation; }	
	
	/*************************************************************************************************/
	/****************************************  -  FUNCTION  -  ***************************************/
	/*************************************************************************************************/ 
	/**
	 * In order to have all the ID info in our XML <=> <i>the first part</i>. ID of agents, ID of devices, ID of milestones, ID of tasks and ID of events. Furthermore, we initialize most of our maps.
	 * @param plan : Mission
	 * @param agents : List of UPPAgentVehicle
	 * @return String with the info to put into the XML file
	 */
	public String ID_pack( Mission plan, List<UPPAgentVehicle> agents)
	{
		//The String that we will be returned 
		String toReturn;
		//String in loops
		String toAdd;
		//We will need to browse the task info 
		List<Task> listTask = plan.getTasks();		
		
		//**********************************Let's start with the agents:
		toReturn = "/**ID of agents*/"+"\n";
		//We browse the info as long as we have agents
		for (int i=0; i<this.numberOfAgents; i++)
		{
			//We return info of our agent
			toReturn += "const agentid_t ID_"+ agents.get(i).vehicle.name +" = "+i+";"+"\n"; 
			//Then we fill some of our maps : 
			//	- The agent and their maximum speed
			this.AgentsMaxSpeed.put("ID_"+ agents.get(i).vehicle.name, agents.get(i).vehicle.maxSpeed);
			//	- The agent and all its equipment identification number ( in form of an Arraylist ) 
			ArrayList<Integer> equipmentsID = new ArrayList<Integer>();
			for (int j=0; j<agents.get(i).vehicle.equipments.size() ; j++)
			{
				equipmentsID.add(agents.get(i).vehicle.equipments.get(j).getType().getValue() );
			}
			this.AgentEquipment.put("ID_"+ agents.get(i).vehicle.name,equipmentsID );
		}
		
		//**********************************Let's continue with the devices:
		toReturn+= "\n"+"/**ID of devices*/"+"\n";
		//String in order to check for doubles
		String noDoubles = "" ;		
		List<EquipmentType> listEquipmentType = null;
		int equipmentid,k=0;
		
		//We browse the info as long as there are tasks
		for (int i=0; i<this.numberOfTask; i++)
		{
			//For each task we get the list of required equipmentType
			listEquipmentType = listTask.get(i).taskTemplate.getRequiredTypes();
			//We will get all the equipment id's for each task
			ArrayList<Integer> ListEquipId = new ArrayList<Integer>();
			
			for (int j=0; j<listEquipmentType.size(); j++)
			{
				//We get the identification of the device (old one, given by MMT)
				equipmentid = listEquipmentType.get(j).getValue();
				//We can full our array for the Hashmap :
				ListEquipId.add(equipmentid);
				//Get back the name of this equipment
				toAdd = (listEquipmentType.get(j).findByValue(equipmentid)).toString();				
				//We only add it if haven't done it already 
				if ( noDoubles.indexOf(toAdd) == -1)
				{
					//We return the info of the device
					toReturn += "const deviceid_t ID_"+toAdd+" = "+k+";"+"\n";
					//Add that we already have added this device to our list of names  
					noDoubles += toAdd;
					//Then we add the info to the maps : 
					//	 - the relation between old and new identification numbers of devices
					this.DevicesID_Old_and_New.put(equipmentid,k);
					//	 - the identification number of device (old one) and the ID/Name of the device
					this.IdEquipmentString.put(equipmentid,"ID_"+toAdd);
					k+=1;
				}
				
			} 
			//	 - then we link the task id to the list of the devices' id needed for this task
			this.TaskEquipment.put(i,ListEquipId);
		}
		
		//**********************************Let's continue with the milestones:
		toReturn+= "\n"+"/**ID of milestones*/"+"\n";
		k=0;
		//As long as we have task 
		for (int i=0; i<this.numberOfTask; i++)
		{
			//For each task we want to know the localization 
			List<Position> area = listTask.get(i).area.area;
			//We read the info with a loop because the position was registered in a list
			for (int j=0; j< area.size() ; j++)
			{
				//We return the info of the localisation
				toReturn += "const milestoneid_t ID_TASK"+i+"_MILESTONE"+" = "+k+";"+"\n"; 
				//Then we add the info to the maps : 
				//	 - Relation between ID/Name of the milestones and their position ( which is also a Position variable )
				MilestonesID.put("ID_TASK"+i+"_MILESTONE", area.get(j));
				//	 - Relation between ID/Name of the milestones and the id of the task
				this.MilestoneTask.put("ID_TASK"+i+"_MILESTONE", i);
				k+=1;
			}
		}
		//Now we need to add the initial position of our agents if they are not on a registered milestones 
		//For all agents
		for (int i=0; i<this.numberOfAgents; i++)
		{
			///We need to get the location on this agents:
			Position position = agents.get(i).vehicle.stateVector.getPosition();
			//It's ID with the function GetPositionNam()
			String LocationName = GetPositionName(position);
			//If the agent is in the middle of nowhere, we need to add the info
			if ( LocationName.equals("ID_SOMWHERE") )
			{
				//We return the info 
				toReturn += "const milestoneid_t ID_Agent"+agents.get(i).vehicle.getName()+"_LOCATION"+" = "+k+";"+"\n"; 
				this.numberOfMilestonesTotal ++;
				//We retrieve the ID/Name 
				LocationName = "ID_Agent"+agents.get(i).vehicle.getName()+"_LOCATION";
				k+=1;
			}
			//Then we link the ID/Name to the agent's name
			this.AgentsLocation.put(agents.get(i).vehicle.getName(), LocationName);
			this.AgentIniPosition.put(LocationName, position);
		}
		
		//**********************************Let's continue with the tasks:
		toReturn+= "\n"+"/**ID of tasks*/"+"\n";
		ArrayList<Integer> ListEquipId = new ArrayList<Integer>();	
		
		//For each agent related to this plan
		for (int itagent=0; itagent<this.numberOfAgents; itagent++)
		{
			k=0;
			//We browse all the task 
			for (int i=0; i<this.numberOfTask; i++)
			{
				//We get the list of equipment of the task
				ListEquipId = TaskEquipment.get(i);
				//We get the list of equipment of the vehicle
				List<Equipment> Listequip = agents.get(itagent).vehicle.getEquipments();
				//Now let's check if the task need one of those devices available on the vehicle
				for (Equipment equi : Listequip)
				{
					//Get the vehicle equipment id !! => it's the old identification number !! 
					int idEquiVehicle = equi.getType().getValue();
					//Then test if we have it in ListEquipId
					if (ListEquipId.contains(idEquiVehicle))
					{
						//We write the info about the task
						toReturn += "const taskid_t ID_TASK"+i+"_"+this.IdEquipmentString.get(idEquiVehicle).substring(3)+"_"+agents.get(itagent).vehicle.name+" = "+k+";"+"\n";
						//Then we add the info to the maps : 
						// 	 - We create a Map to link a Agent Name and the id of the task it can accomplish
						HashMap<String,Integer> AgentNameTaskID = new HashMap<String,Integer>();
						AgentNameTaskID.put(agents.get(itagent).vehicle.name, i);
						//	 - We add the previous map to the one that will link it with the ID/Name of the taskid_t
						TasksID.put("ID_TASK"+i+"_"+this.IdEquipmentString.get(idEquiVehicle).substring(3)+"_"+agents.get(itagent).vehicle.name, AgentNameTaskID);
						k+=1;
					}
				}
			}
		}
		
		//**********************************Let's continue with the events:
		//As this info will be changed, we use an external function
		toReturn+= "\n"+EventsInfo()+"\n";
		
		toReturn+= "\n";
		return toReturn;		
	}
	
	/*********************************************************************************************/
	/***********- Methods for variables with an uncertain format as the current time -************/
	/*********************************************************************************************/	
	
	/**
	 * @return a String with all the info related to Events
	 */
	private String EventsInfo() 
	{
		return "/**ID of events*/";
		
	}

	/*********************************************************************************************/
	/*******************************- Shorten methods will help -*********************************/
	/*********************************************************************************************/
	
	/**
	 * We want to get back the ID/Name of the milestone based on a Position's instance 
	 * @param position : Position 
	 * @return a String : the ID of the position put in argument. The ID, which is also a name, has been initialized in the method <i>ID_pack<i>
	 */
	private String GetPositionName(Position position) 
	{
		//The object might be in the middle of nowhere so we initialized with this possibility
		String returns = "ID_SOMWHERE";
		//Then we open the map that links the name of the milestones to their position 
		for (Map.Entry<String, Position> entryMilestoneTask : this.MilestonesID.entrySet() )
		{
			//We browse the map until we found a milestone registered under that position
			if ( position.equals(entryMilestoneTask.getValue()))
			{
				//If we found a registered information, we return the ID/Name
				returns = entryMilestoneTask.getKey();
			}
		}
		return returns;
	}
	
	
}
