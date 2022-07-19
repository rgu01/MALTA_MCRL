package edu.collaboration.tamaa;

import MercatoerProjection.SphericalMercator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import com.afarcloud.thrift.*;

import java.io.File;
import edu.collaboration.XMLFiles.WritingProcess;

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
import edu.collaboration.tamaa.PlannerServiceHandlerTestVersion.Algo;
import edu.collaboration.taskscheduling.*;


public class PlannerServiceHandler implements PlannerService.Iface {
	// Mission corePlan = null;
	// public static final String SERVER_IP = "192.168.0.109";
	// public static final String SERVER_IP = "192.168.56.1";
	// public static final String SERVER_IP = "127.0.0.1";
	// public static final int SERVER_PORT = 9779;
	//public String mmtAddress = "192.168.1.2";
	public String mmtAddress = "127.0.0.1";
	public int mmtPort = 9096;
	private String uppaalAddress;
	private int uppaalPort;
	private NavigationArea nArea = null;
	private List<UPPAgentVehicle> agents = new ArrayList<UPPAgentVehicle>();

	public static String timeLogFile = "./results/time.log";
	public static String logFileDali = "./results/dali.log";
	public static String logFileDaliStar = "./results/dalistar.log";
	private boolean UseMultiTargetPathPlanning = false;
	private boolean ownModel;
	private String ownModelAddress;

	public enum Algo {
		AStar, Dali, DaliStar, AStar2
	}

	public Algo algo = Algo.DaliStar;
	//public Algo algo = Algo.AStar;
	public String algoString = "DaliStar";

	public PlannerServiceHandler() {
	}

	public PlannerServiceHandler(String mmtAddress, int mmtPort, String uppaalAddress, int uppaalPort, boolean ownModel, String ownModelAddress) {
		this.mmtAddress = mmtAddress;
		this.mmtPort = mmtPort;
		this.uppaalAddress = uppaalAddress;
		this.uppaalPort = uppaalPort;
		this.ownModel = ownModel;
		this.ownModelAddress = ownModelAddress;
	}

	
	@Override
	public void computePlan(int requestId, Mission plan) throws TException {
		// Communication with MMT
		TTransport transport = null;
		TProtocol protocol = null;
		MmtService.Client client = null;
		PathPlanningAlgorithm as = null;
		
		try {
			//We initialize the connection to MMT in this class because we need it for sending back information
			transport = new TSocket(this.mmtAddress, this.mmtPort);
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new MmtService.Client(protocol);
			System.out.println("msg from MMT: " + client.ping());
			// The information from MMT is stored in "plan".
			
			// Get the number of tasks => we will need it later on 
			int NTasks = plan.getTasksSize();
			
			//We call an instance of the reading class
			PlannerServiceReader MMTInfo = new PlannerServiceReader(algoString);
			//then call the function that will fill nArea and as 
			Object[] info = MMTInfo.read(plan);
			nArea = (NavigationArea) info[0];
			as = (PathPlanningAlgorithm) info[1];
			
			// The the start time of the mission
			long startTime = System.nanoTime();
			
			//Now we can start the computing part, the first one for TAMAA-DALi
			PlannerServiceComputerForTAMAADALi ComputeLinux = new PlannerServiceComputerForTAMAADALi(this.UseMultiTargetPathPlanning, this.algo);
			//This function will add all the info about our agents in the List<UPPAgentVehicle> in the ComputeLinux.agents 
			ComputeLinux.computePaths(plan, as, this.nArea);
			//So we need to get them:
			this.agents = ComputeLinux.getAgentsComputed();
			
			
			/**********************************************************************/
			/**Test for template**/
			/**********************************************************************/			
			WritingProcess write = new WritingProcess();
			write.ModifyTemplate(plan, this.agents);
			/**********************************************************************/
			
			
			/*****************************************************************
			 * If no server is running, and only path planning is needed, 
			 * please comment the code below
			 *****************************************************************/
			//Then the second computer part, for MMT this time 
			PlannerServiceComputerForMMT ComputeMMT = new PlannerServiceComputerForMMT(this.uppaalAddress, this.uppaalPort, this.ownModel, this.ownModelAddress, this.algo, this.agents);
			//This function will allow us to add all the info about our agents
			boolean success = ComputeMMT.SuccessfullyGenerated(plan, as, this.nArea);
			
			//Then in order to write the file for the experiment tests and to send the file :
			PlannerServiceWriter backToMMT = new PlannerServiceWriter(NTasks);
			backToMMT.Write(success, ComputeMMT.getpassAnomalyPathsSize(), startTime, algo, plan, nArea, client, requestId);
			
		} catch (TTransportException e) {
			System.out.println("final error");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (TException x) {
			System.out.println("final xerror");
			System.out.println(x.getMessage());
			x.printStackTrace();
		} finally {
			transport.close();
			this.agents.clear();
			//exit:
			//System.exit(0);
		}
	}
	
	@Override
	public String ping() throws TException {
		// TODO Auto-generated method stub
		return "SOME TEST";
	}
	
}
