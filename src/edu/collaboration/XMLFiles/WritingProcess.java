package edu.collaboration.XMLFiles;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.afarcloud.thrift.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.collaboration.model.structure.UPPAgentVehicle;

/**
 * This class will allow us to generate the model in a XML file.
 * @author claire
 */
public class WritingProcess{
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
	 * the number of all devices ( used ?)
	 */
	private int numberOfDevices = -1;
	/**
	 * the number of all milestones from MMT
	 */
	private int numberOfMilestones = -1;
	/**
	 * the number of all milestones in total
	 */
	private int numberOfMilestonesTotal = -1;
	/**
	 * the number of events
	 */
	private int numberOfEvents = -1;
	/**
	 * the number of maximum precondition
	 */
	private int numberOfPreconditions=-1; 
	
	/**
	 * precondition_t's ID/Name for task without preconditions
	 */
	private String PreconditionNameForEmptyOnes = "";

	/**
	 * The maximum different locations a task can be related to 
	 */
	int maxDiffLoc =0;	

	/**
	 * HashMap<Integer, ArrayList<Integer> := key : task's id && value = ArrayList of the equipment id 
	 */
	private HashMap<Integer, ArrayList<Integer>> TaskEquipment = new HashMap<Integer, ArrayList<Integer>>();
	
	/**
	 * HashMap<String, ArrayList<Integer>> := key : agent's ID/name && value : id of equipments
	 */
	private HashMap<String, ArrayList<Integer>> AgentEquipment = new HashMap<String, ArrayList<Integer>>();
	
	/**
	 * We need to get all the id of the devices used easily : ArrayList<Integer>
	 */
	private ArrayList<Integer> AllDevicesUsed = new ArrayList<Integer>();
	
	/**
	 * HashMap<String, Double> := key : agent's ID/name && value : the max speed
	 **/
	private HashMap<String, Double> AgentsMaxSpeed = new HashMap<String, Double >();
	
	/**
	 * HashMap<String, Position> := key : milestone's ID/name && value : milestone Position
	 */
	private HashMap<String, Position> MilestonesID = new HashMap<String, Position>();
	
	/**
	 * HashMap of (String, Position) := key : milestone's ID/name && value : milestone Position
	 */
	private HashMap<String, Position> AgentIniPosition = new HashMap<String, Position>();
	
	/**
	 * HashMap<Integer, Integer> := key : old device id && value : new device id
	 */
	private HashMap<Integer, Integer> DevicesID_Old_and_New = new HashMap<Integer, Integer>();
	
	/**
	 * HashMap<String, HashMap<String,Integer>>  := key : taskid_t’s ID/name && value : HashMap<String,Integer>
	 * HashMap<String,Integer>  := key : agent's ID/Name && value : task id												
	 */
	private HashMap<String, HashMap<String,Integer>> TasksID = new HashMap<String, HashMap<String,Integer>>();
	
	/**
	 * HashMap<Integer, String> := key : task's id && value : taskid_t required as a precondition
	 */
	private HashMap<Integer, String> TaskAndTaskPrecondition = new HashMap<Integer, String>();
	
	/**
	 * HashMap<Integer, String> := key : device's id (old) && value : device's ID/name
	 */
	private HashMap<Integer, String> IdEquipmentString = new HashMap<Integer, String>();
	
	/**
	 * HashMap<String, Integer> := key : milestone's ID/name && value : task's id
	 */
	private HashMap<String, Integer> MilestoneTask = new HashMap<String, Integer> ();
	
	/**
	 * HashMap<String, String> := key : agent's ID/name && value : ID/Name of it's Location 
	 */
	private HashMap<String, String> AgentsLocation = new HashMap<String, String>();
	
	/**
	 * HashMap<String, String> := key : Taskid_t' ID/name && value : Task_t ID/Name 
	 */
	private HashMap<String, String> Taskid_tLinkedTask_t = new HashMap<String, String>();
	
	/**
	 * HashMap of (Integer, String) := key : task's id && value : precondition's ID/Name
	 */
	private HashMap<Integer, String> TaskAndPreconditionName = new HashMap<Integer, String>();
	
	
	/*********************************************************************************************/
	/********************- Main METHODS on the writing process of the file-***********************/
	/*********************************************************************************************/
	
	/**
	 * Modify the template to fill the info into the system section and declaration ( quantity ) 
	 * @param Plan : the Mission variable send by MMT
	 * @param agents : List<UPPAgentVehicle> variable 
	 */
	public void ModifyTemplate(Mission Plan, List<UPPAgentVehicle> agents)
	{
		try {
			//We get our template that we will fill with info 
		    File inputFile = new File("res\\final_example.xml");
		    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    Document doc = docBuilder.parse(inputFile);
		    
		    //Get the node of our root element
		    Node nta = doc.getFirstChild();
		    //And get the list of it's children
		    NodeList ntaChildNodesList = nta.getChildNodes();		    
		    
		    //We need some info before starting:
		    getSizes(Plan,agents);
		    this.numberOfPreconditions = this.numberOfAgents * this.numberOfTask;
		    
		    //Parsing the info will change some parameters. So we need to get back everything before any replacement in the file.
    		//We have 3 classes to fill the info of the 3 parts of our model: 
    		//	 - 1: initialization of all IDs
    		//	 - 2: initialization of relationships between variables
    		//	 - 3: finally the initialization of functions
    		
    		// 1 : ID info
    		//Call an instance of the class IDPack
    		IDPack IdInfos = new IDPack(this.numberOfTask, this.numberOfAgents,this.numberOfMilestones);
    		//Get all the info into a String format from the class's function ID_pack
	    	String InfosID = IdInfos.ID_pack(Plan,agents);
    		
    		//We also need to retrieve the info on the different maps
	    	this.numberOfMilestonesTotal = IdInfos.getNumberOfMilestones();
    		this.TaskEquipment = IdInfos.Get_TaskEquipment();
    		this.AgentEquipment =  IdInfos.Get_AgentEquipment();
    		this.AgentsMaxSpeed = IdInfos.Get_AgentsMaxSpeed();
    		this.DevicesID_Old_and_New = IdInfos.Get_DevicesID_Old_and_New();
    		this.MilestonesID = IdInfos.Get_MilestonesID();		    		
    		this.TasksID = IdInfos.Get_TasksID();		    		
    		this.IdEquipmentString = IdInfos.Get_IdEquipmentString();		    		
    		this.MilestoneTask = IdInfos.Get_MilestoneTask();		    		
    		this.AgentsLocation = IdInfos.Get_AgentsLocation();
    		this.AgentIniPosition = IdInfos.Get_AgentIniPosition();
    		
    		// 2 : Relation between variables 
    		//Call an instance of the class RelationPack
    		RelationPack RelationInfos = new RelationPack(numberOfTask,this.numberOfAgents,this.maxDiffLoc,this.SizeOfGoals, this.numberOfPreconditions, this.TaskEquipment, this.DevicesID_Old_and_New, this.TasksID, this.MilestoneTask);
    		//Get all the info into a String from the class's function relation_pack
    		String InfosRelations = RelationInfos.relation_pack(Plan, agents, AgentEquipment, AllDevicesUsed, IdEquipmentString, AgentsLocation);
    		
    		//We also need to retrieve some info into the different maps 
    		this.TaskAndTaskPrecondition = RelationInfos.GetTaskAndTaskPrecondition();		    		
    		this.Taskid_tLinkedTask_t =  RelationInfos.GetTaskid_tLinkedTask_t();
    		this.TaskAndPreconditionName = RelationInfos.GetTaskAndPreconditionName();
    		this.PreconditionNameForEmptyOnes = RelationInfos.Get_PreconditionNameForEmptyOnes();
    		
    		// 3 : Initialization of functions 
    		//Call an instance of the class Agents
    		Agents AgentsInfos = new Agents(this.numberOfTask,this.maxDiffLoc, this.TasksID, this.PreconditionNameForEmptyOnes);
    		//Get all the info into a String from the class's function FunctionsIni
    		String InfosAgents = AgentsInfos.FunctionsIni(Plan, agents, this.TaskEquipment, this.AgentEquipment, this.AgentsMaxSpeed, this.MilestonesID, this.TaskAndTaskPrecondition, this.MilestoneTask, this.Taskid_tLinkedTask_t, this.TaskAndPreconditionName, this.AgentIniPosition);
    		
    		//We browse the list and when we found the section in the XML file, we change their content
		    for ( int i=0; i<ntaChildNodesList.getLength(); i++)
		    {
		    	Node node = ntaChildNodesList.item(i);
		    	
		    	if ("declaration".equals(node.getNodeName()))
		    	{
		    		//We get the text ( a.k.a String ) that is already present in our template.xml
		    		String declaration = node.getTextContent();
		    		//Then we split this String each time we found a ";". It allows us to retrieve all the info per line.
		    		String[] tokens = declaration.split(";");
		    		
		    		//The 6 first info are the declaration section we need to change for each plan.
		    		int length =0;
		    		//So we get the length of those 6 first sentences
		    		for (int j=0; j<7; j++)
		    			length += tokens[j].length()+1;
		    		
		    		//Then we keep everything from the original text under this limit 
		    		String keep = declaration.substring(length);
		    		//And we will add them back with the correct info
		    		String toAdd ="";
		    		//We retrieve some explanation 
		    		String explanation = tokens[1].substring(0,18);
		    		
		    		//The process is the same: we only want to change the number associated, which is at the end. So we only change that.
		    		//So we keep t
		    		// 0 : GOALRANGE
		    		String GOALRANGE = tokens[0].substring(0,12 + "GOALTRANGE".length());
		    		String GOALRANGESub = String.valueOf(this.SizeOfGoals);
		    		toAdd += GOALRANGE + GOALRANGESub +";";
		    		
		    		//We add some explanation
		    		toAdd += explanation;
		    		
		    		// 1 : NOAGENTS
		    		String NOAGENTS = tokens[1].substring(18,33 + "NOAGENTS".length());
		    		String NOAGENTSSub = String.valueOf(this.numberOfAgents);
		    		toAdd += NOAGENTS + NOAGENTSSub +";";
		    		
		    		// 2 : NOMILESTONES
		    		String NOMILESTONES = tokens[2].substring(0,14 + "NOMILESTONES".length());
		    		String nbMilestones = String.valueOf(this.numberOfMilestonesTotal);
		    		toAdd += NOMILESTONES + nbMilestones +";";
		    		
		    		// 3 : NOTASKS
		    		String NOTASKS = tokens[3].substring(0,14 + "NOTASKS".length());
		    		String nbNoTasks = String.valueOf(this.numberOfTask);
		    		toAdd += NOTASKS + nbNoTasks + ";";
		    		
		    		// 4 : NODEVICES
		    		String NODEVICES = tokens[4].substring(0,14 + "NODEVICES".length());
		    		String nbDevices = String.valueOf(this.numberOfDevices);
		    		toAdd += NODEVICES + nbDevices +";" ;
		    		
		    		// 5 : NOEVENTS
		    		String NOEVENTS = tokens[5].substring(0,14 + "NOEVENTS".length());
		    		String nbevents = String.valueOf(this.numberOfEvents);
		    		toAdd += NOEVENTS + nbevents +";";
		    		
		    		// 5 : NOPRECONS
		    		String NOPRECONS = tokens[6].substring(0,14 + "NOPRECONS".length());
		    		String nbprecons = "NOAGENTS * NOTASKS";
		    		toAdd += NOPRECONS + nbprecons +";";
		    		
		    		//Then we change the rest of the info
		    		tokens = (toAdd + keep).split(";"); 
		    		String Task_IDLE = tokens[58], Task_MOVING = tokens[60];
		    		
		    		Task_IDLE = Task_IDLE.substring(0, Task_IDLE.indexOf("UNKOWNDEVICE"));
		    		Task_IDLE+= "UNKOWNDEVICE, {";
		    		for (int it=0; it<this.AllDevicesUsed.size()-1 ; it++)
		    		{
		    			Task_IDLE+= " UNKOWNDEVICE,";
		    		}
		    		Task_IDLE+= " UNKOWNDEVICE }}";
		    		
		    		Task_MOVING = Task_MOVING.substring(0,Task_MOVING.indexOf("UNKOWNDEVICE"));
		    		Task_MOVING += "UNKOWNDEVICE, {";
		    		for (int it=0; it<this.AllDevicesUsed.size()-1 ; it++)
		    		{
		    			Task_MOVING+= " UNKOWNDEVICE,";
		    		}
		    		Task_MOVING+= " UNKOWNDEVICE }}";
		    		
		    		//We reset the text associate to the node "declaration" 
		    		node.setTextContent("");
		    		//Then add the changes and what we keep
		    		for (int j=0; j<tokens.length-1 ; j++)
		    		{
		    			if ( j == 58 )
		    			{
		    				node.appendChild(doc.createTextNode(Task_IDLE+";")); 
		    			}
		    			else if ( j == 60)
		    			{
		    				node.appendChild(doc.createTextNode(Task_MOVING+";")); 
		    			}
		    			else 
		    			{
		    				node.appendChild(doc.createTextNode(tokens[j]+";"));
		    			}
		    		}
		    		node.appendChild(doc.createTextNode(tokens[tokens.length-1]));
		    	}
		    	
		    	if ("system".equals(node.getNodeName()))
		    	{
		    		//We reset the text associate to the node "system" because we don't want any information from the previous plan left. 
		    		node.setTextContent("");
		    		
		    		//Now we class 3 classes to fill the info of the 3 parts of our model: 
		    		//	 - 1: initialization of all IDs
		    		//	 - 2: initialization of relationships between variables
		    		//	 - 3: finally the initialization of functions
		    		
		    		// 1 : ID info
		    		//Call an instance of the class IDPack
 		    		//Then add the info to the text of the node
		    		node.appendChild(doc.createTextNode(InfosID));
		    		
		    		// 2 : Relation between variables //Then we add the info to the text of the node
		    		node.appendChild(doc.createTextNode(InfosRelations));
		    		
		    		// 3 : Initialization of functions 
		    		//Then we add the info to the text of the node
		    		node.appendChild(doc.createTextNode(InfosAgents));
		      	}
		    }
		    
		    //Then we write the new content 
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(doc);
	        System.out.println("-----------Modified File-----------");
	        StreamResult consoleResult = new StreamResult("res\\final_example.xml");
	        transformer.transform(source, consoleResult);
		    
		}
		catch (Exception e)
		{
			System.out.println("File not opened");
			e.printStackTrace();
		}
	}
	
	/**
	 * This function initialize the different attribute relative to the number of elements in our plan 
	 * @param plan : the Mission variable
	 * @param agents : the List<UPPAgentVehicle> variable
	 */
	private void getSizes(Mission plan, List<UPPAgentVehicle> agents) throws ExceptionTasks
	{
		List<Task> listTask = plan.getTasks();
		//We need to verify the format of the tasks 
		this.numberOfTask = 0;
		for ( Task it : listTask) 
		{
			int typeOfTask = it.taskTemplate.taskType.getValue();
			String Type = (it.taskTemplate.taskType.findByValue(typeOfTask)).toString();
			
			if ( !Type.equals("INSPECT") ) // Or just TRANSFER ? 
			{
				throw new ExceptionTasks("The model has incorrect tasks' type.");
			}
			else 
			{
				this.numberOfTask ++;
			}
		}
		
		this.numberOfAgents = agents.size();
		//Will need to be changed
		this.numberOfEvents = 1;
		
		List<EquipmentType> listEquipmentType = null;
		int equipmentid,k=0;
		String toAdd, noDoubles = "" ;
		
		for (int i=0; i<this.numberOfTask; i++){
			listEquipmentType = listTask.get(i).taskTemplate.getRequiredTypes();
			for (int j=0; j<listEquipmentType.size(); j++){
				equipmentid = listEquipmentType.get(j).getValue();
				toAdd = (listEquipmentType.get(j).findByValue(equipmentid)).toString();
				if ( noDoubles.indexOf(toAdd) == -1){
					noDoubles += toAdd;
					k+=1;	
				}
			} 
		}
		this.numberOfDevices = k;
		
		k=0;
		for (int i=0; i<this.numberOfTask; i++){
			//For each task we want to know the localization 
			List<Position> area = listTask.get(i).area.area;
			for (int j=0; j< area.size() ; j++)
			{
				k+=1;
			}
		}
		this.numberOfMilestones = k;
	}

}



