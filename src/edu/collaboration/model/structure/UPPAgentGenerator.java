package edu.collaboration.model.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import edu.collaboration.model.queries.*;
import edu.collaboration.pathplanning.Node;
import edu.mdh.se.*;
import edu.mdh.se.parse.TAMAAParser;

public class UPPAgentGenerator {
	public static String outputXML = "./model/tamaa.xml";
	public static String outputMCRL = "./model/mcrl";
	private static String templateXML = "empty_template.xml"; // under the bin folder
	private static String gResetString = "int steps = 0;\r\nvoid gReset()\r\n" + "{\r\n" + "   int i,j,k;\r\n"
			+ "   for(i=0;i<AgentNum;i++)\r\n" + "   {\r\n" + "       tf[i][0]=true;\r\n" + "       ts[i][0]=true;\r\n"
			+ "       for(j=1;j<TaskNum;j++)\r\n" + "        {\r\n" + "           ts[i][j]=false;\r\n"
			+ "           tf[i][j]=false;\r\n" + "        }\r\n" + "       for(k=0;k<MilestoneNum;k++)\r\n"
			+ "       {\r\n" + "           position[i][k]=false;\r\n" + "           visited[i][k]=false;\r\n"
			+ "       }\r\n" + "       iteration[i]=0;\r\n" + "    }\r\n" + "    steps=0;\r\n" + "    globalTime=0;\r\n"
			+ "} \r\n";
	private static String updateIterationString = "\r\n";

	public static void run(List<UPPAgentVehicle> agents) {
		String show = "";
		String tempatePath = "";
		UppaalDocument doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			tempatePath = new File(".").getCanonicalPath() + File.separator + "res" + File.separator + templateXML;
			DocumentBuilder builder = factory.newDocumentBuilder();
			// InputStream is =
			// UPPAgentGenerator.class.getClassLoader().getResourceAsStream(tempatePath);
			InputStream is = new FileInputStream(new File(tempatePath));
			if (is != null) {
				Document templatedoc = builder.parse(is);
				templatedoc.getDocumentElement().normalize();

				Element root = templatedoc.getDocumentElement();
				doc = new UppaalDocument(root);
			}
		} catch (ParserConfigurationException ex) {

			show = ex.getMessage();
			JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		} catch (SAXException ex) {

			show = ex.getMessage();
			JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		} catch (IOException ex) {

			show = ex.getMessage();
			JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		}

		int mapSize = 0, agentNum = 0;
		int eventNum = 0, missionNum = 0;
		UPPAgentFleet fleet = new UPPAgentFleet(agents);
		UPPAgentMission m = null;
		Map<Node, UPPAgentMission> milestonesList = new HashMap<Node, UPPAgentMission>();
		UPPAgentUppaalQueries queries = new UPPAgentUppaalQueries();
		String system_declaration = "";
		String global_declaration = "// Place global declarations here.\r\n";
		// String constBcet = "const int BCET[AgentNum][TaskNum]={";
		// String constWcet = "const int WCET[AgentNum][TaskNum]={";
		String constRegularTask = "const int RegularTaskNum[AgentNum] = {";
		String iterationString = "int iteration[AgentNum] = {";
		String channelString = "chan move[AgentNum], initialize;\r\n";
		String rulesString = "const int rules[MilestoneNum]={";
		UPPAgentEventMonitor monitor = null;
		// UPPAgentTaskCoverage tcq = new UPPAgentTaskCoverage(fleet);
		UPPAgentTaskTimedIteration tcq = new UPPAgentTaskTimedIteration(fleet);
		queries.addQuery(tcq);
		agentNum = fleet.agents.size();

		for (UPPAgentMission mission : fleet.agents.get(0).missions) {
			for (Node milestone : mission.getMilestones()) {
				milestonesList.put(milestone, mission);
			}
		}
		int[] rulesInt = new int[milestonesList.size() + 1];
		for (Map.Entry<Node, UPPAgentMission> entry : milestonesList.entrySet()) {
			rulesInt[entry.getKey().id] = entry.getValue().ID;
		}
		for (int i = 0; i < rulesInt.length; i++) {
			rulesString += rulesInt[i] + ",";
		}
		rulesString = rulesString.substring(0, rulesString.length() - 1) + "};\n";

		for (UPPAgentVehicle agent : fleet.agents) {
			// Movement UTA and the lengths of paths are in this function
			UPPAgentStaticMap map = new UPPAgentStaticMap(agent);
			monitor = new UPPAgentEventMonitor(agent);
			UPPAgentMissionPlan missionPlan = new UPPAgentMissionPlan(agent, monitor);

			monitor.setDeclaration();
			mapSize = map.Size;

			/*
			 * for (int i = 0; i < agent.missions.size(); i++) { m = agent.missions.get(i);
			 * if (i == 0) { constBcet += "{0,"; constWcet += "{0,"; } if (i <
			 * agent.missions.size()) { constBcet += m.task.bcet; constWcet += m.task.wcet;
			 * } else { constBcet += 0; constWcet += 0; } if (i != agent.missions.size() -
			 * 1) { constBcet += ","; constWcet += ","; } else { constBcet += "}"; constWcet
			 * += "}"; } }
			 */

			constRegularTask += agent.missions.size();
			iterationString += 0;
			if (agent.ID != fleet.agents.size() - 1) {
				constRegularTask += ",";
				iterationString += ",";
				/*
				 * constBcet += ","; constWcet += ",";
				 */

			} else {
				constRegularTask += "};\r\n";
				iterationString += "};\r\n";
				/*
				 * constBcet += "};\r\n"; constWcet += "};\r\n";
				 */
			}

			for (UPPAgentEvent e : monitor.events) {
				system_declaration += UPPAgentEventMonitor.InstanceName + agent.ID + e.id + " = "
						+ UPPAgentEventMonitor.SystemName + "(" + agent.ID + "," + e.id + ");\r\n";
			}
			system_declaration += UPPAgentStaticMap.InstanceName + agent.ID + " = " + UPPAgentStaticMap.SystemName
					+ agent.ID + "(" + agent.ID + ");\r\n";
			system_declaration += UPPAgentMissionPlan.InstanceName + agent.ID + " = " + UPPAgentMissionPlan.SystemName
					+ agent.ID + "(" + agent.ID + ");\r\n";

			if (missionNum < agent.missions.size()) {
				missionNum = agent.missions.size();
			}

			doc.addAutomaton(map);
			doc.addAutomaton(missionPlan);
		}

		if (monitor != null) {
			monitor.initialize();
			if (eventNum != 0) {
				doc.addAutomaton(monitor);
				channelString += "chan done[AgentNum][EventNum];\r\n";
			}
		}
		system_declaration += "\r\nsystem ";
		for (UPPAgentVehicle agent : fleet.agents) {
			monitor = new UPPAgentEventMonitor(agent);
			system_declaration += UPPAgentStaticMap.InstanceName + agent.ID + ", " + UPPAgentMissionPlan.InstanceName
					+ agent.ID + ", ";

			for (UPPAgentEvent e : monitor.events) {
				system_declaration += UPPAgentEventMonitor.InstanceName + agent.ID + e.id + ", ";
			}
		}
		system_declaration = system_declaration.substring(0, system_declaration.lastIndexOf(",")) + ";";
		try {
			gResetString = "";
			global_declaration += addDeclaration(mapSize, missionNum, eventNum, agentNum) + rulesString /*
																											 * +
																											 * constBcet
																											 * +
																											 * constWcet
																											 */
					+ constRegularTask + channelString + "\r\n" + iterationString + "\r\n" + "clock globalTime;\r\n"
					+ gResetString + updateIterationString;
		} catch (Exception ex) {
			show = ex.getMessage();
			JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		}
		doc.setDeclaration(global_declaration);
		doc.setSystem(system_declaration);
		doc.addOrReplaceChildElement(queries);
		doc.saveToFile(outputXML);

		show = "Model for " + fleet.agents.size() + " agents has built! MapScale: " + mapSize + ", taskNum: "
				+ missionNum + ", eventNum: " + eventNum + ".";

		// MCRL
		try {
			int maxTime = agents.get(0).missionTimeLimit > 0 ? agents.get(0).missionTimeLimit * 5 : 5000;
			String mcrlFile = outputMCRL + "_" + agents.size() + "vehicle.xml";
			TAMAAParser parse = new TAMAAParser(agents.size(), maxTime, (missionNum + 1), outputXML, mcrlFile);
			parse.create(parse.parse());
		} catch (Exception ex) {
			show = ex.getMessage();
			JOptionPane.showMessageDialog(null, show, "Error", JOptionPane.PLAIN_MESSAGE);
		}

		// JOptionPane.showMessageDialog(null, show, "Done", JOptionPane.PLAIN_MESSAGE);
	}

	private static String addDeclaration(int mapScale, int taskNum, int eventNum, int agentNum) throws Exception {

		String constAgentsNum = "const int AgentNum = " + agentNum + ";\r\ntypedef int[0,AgentNum-1] AgentScale;\r\n";
		String constMilestone = "const int MilestoneNum = " + mapScale + ";\r\n";
		String constNum = "const int TaskNum = " + (taskNum + 1) + ";\r\n" + "const int EventNum = " + eventNum
				+ ";\r\n";
		String constMax = "const int MaxIteration = 1;";
		String isBusy = "\r\n" + "\r\n" + "bool isBusy(int taskID)\r\n" + "{\r\n" + "    int id = 0;\r\n"
				+ "    bool busy = false;\r\n" + "\r\n" + "    for(id = 0; id < AgentNum; id++)\r\n" + "    {\r\n"
				+ "        if(ts[id][taskID])\r\n" + "        {\r\n" + "            busy = true;\r\n"
				+ "            return busy;\r\n" + "        }\r\n" + "    }\r\n" + "\r\n" + "    return busy;\r\n"
				+ "}\r\n";

		String finish = "", start = "", visit = "";
		String events = "", position = "", declaration = "";

		if (agentNum != 0) {
			if (taskNum != 0) {
				finish = "bool tf[AgentNum][TaskNum]={";
				start = "bool ts[AgentNum][TaskNum]={";
			} else {
				throw new Exception("Agents number must be larger than 0!");
			}
			if (eventNum != 0) {
				events = "bool ev[AgentNum][EventNum]={";
			}
			if (mapScale != 0) {
				position = "bool position[AgentNum][MilestoneNum]={";
				// visit = "bool visited[AgentNum][MilestoneNum]={";
			} else {
				throw new Exception("Map size must be larger than 0!");
			}
		} else {
			throw new Exception("At least one agent is required!");
		}

		for (int i = 0; i < agentNum; i++) {
			for (int m = 0; m < mapScale; m++) {
				if (m == 0) {
					position += "{";
					// visit += "{";
				}
				position += "false";
				// visit += "false";
				if (m != mapScale - 1) {
					position += ",";
					// visit += ",";
				} else {
					position += "}";
					// visit += "}";
				}
			}
			for (int j = 0; j < taskNum; j++) {
				if (j == 0) {
					finish += "{true,";
					start += "{true,";
				}
				finish += "false";
				start += "false";
				if (j != taskNum - 1) {
					finish += ",";
					start += ",";
				} else {
					finish += "}";
					start += "}";
				}
			}
			for (int k = 0; k < eventNum; k++) {
				if (k == 0) {
					events += "{";
				}
				events += "false";
				if (k != eventNum - 1) {
					events += ",";
				} else {
					events += "}";
				}
			}
			if (i == agentNum - 1) {
				position += "};\r\n";
				// visit += "};\r\n";
				finish += "};\r\n";
				start += "};\r\n";
				if (events != "") {
					events += "};\r\n";
				}
			} else {
				position += ",";
				// visit += ",";
				finish += ",";
				start += ",";
				if (events != "") {
					events += ",";
				}
			}
		}

		declaration = constAgentsNum + constNum + constMilestone + constMax + "\r\n" + "\r\n" + events + position
				+ finish + start + isBusy + "\r\n";

		return declaration;
	}
}
