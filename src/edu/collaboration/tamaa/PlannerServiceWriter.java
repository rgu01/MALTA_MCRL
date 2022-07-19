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
 * The class exists in order to write and save info for experiment purpose. Will also allow us to send the plan to MMT. 
 * @author claire
 */
public class PlannerServiceWriter {
	public static String timeLogFile = "./results/time.log";
	public static String logFileDali = "./results/dali.log";
	public static String logFileDaliStar = "./results/dalistar.log";
	private int NTasks = 0;
	
	/**
	 * Constructor by default
	 */
	public PlannerServiceWriter(){
	}
	
	/**
	 * Constructor 
	 * @param NTasks
	 */
	public PlannerServiceWriter(int NTasks) {
		this.NTasks = NTasks;
	}
	
	/**
	 * This method will automatically:  1. Write and save the logs 2.Send the plan to MMT.
	 * @param success
	 * @param passAnomalyPathsSize
	 * @param startTime
	 * @param algo
	 * @param plan
	 * @param nArea
	 * @param client
	 * @param requestId
	 */
	public void Write(boolean success, int passAnomalyPathsSize, long startTime, Algo algo, Mission plan, NavigationArea nArea, MmtService.Client client, int requestId) {
		try{
			// Start to write logs. This is only for experiments
			if (success && passAnomalyPathsSize == 0) {
				long stopTime = System.nanoTime();
		
				try {
					FileWriter fw = new FileWriter(timeLogFile, true);
					String log = String.valueOf((stopTime - startTime) / 1000000) + ' ' + algo.toString()
							+ " Threshold " + String.valueOf(NavigationArea.threshold) + " Tasks "
							+ String.valueOf(NTasks) + " Forbidden " + String.valueOf(plan.getForbiddenArea().size())
							+ "\n";
					fw.write(log);
					// for (Long l : execTimes) {
					// fw.write(String.valueOf(l / 1000000) + '\n');
					// }
					fw.close();
				} catch (Exception e) {
				}
		
				String commandsLog = "";
				Position ppp1 = new Position(), ppp2 = new Position();
				Node s1, s2;
				for (int i = 0; i < plan.getCommands().size() - 1; i++) {
					ppp1.latitude = plan.getCommands().get(i).params.get(4);
					ppp1.longitude = plan.getCommands().get(i).params.get(5);
					ppp2.latitude = plan.getCommands().get(i + 1).params.get(4);
					ppp2.longitude = plan.getCommands().get(i + 1).params.get(5);
					s1 = new Node(ppp1);
					s2 = new Node(ppp2);
					if (i == 58) {
						i = i + 0;
					}
					if (nArea.collide(s1, s2)) {
						i = i + 0;
					}
					commandsLog += plan.getCommands().get(i).endTime + ": " + new Node(ppp1).toString() + "\r\n";
					// commandsLog += plan.getCommands().get(i+1).endTime + ": " + new
					// Node(ppp2).toString() + "\r\n";
				}
				System.out.println(commandsLog);
				//End of writing logs. This is only for experiments
		
				// Send the mission plan to MMT
				client.sendPlan(requestId, plan);
			} else {
				String show = "No mission plan is found! Recomputation limit is reached";
				JOptionPane.showMessageDialog(null, show, "Warning: Dissatisfied", JOptionPane.PLAIN_MESSAGE);
			}
		}
		catch (TException x) {
			System.out.println("final xerror");
			System.out.println(x.getMessage());
			x.printStackTrace();
		}
	}
	
}
