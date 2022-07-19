package edu.collaboration.model.structure;

import java.util.ArrayList;
import java.util.List;
import org.fmaes.j2uppaal.builders.UppaalXMLSorter;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalAutomaton;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLabel;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLocation;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalTransition;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Path;

public class UPPAgentStaticMap extends UppaalAutomaton {
	public static String SystemName = "Movement";
	public static String InstanceName = "movement";
	public String mDeclaration = "clock t;\r\n";
	/*
	 * public String mDeclaration = "clock t;\r\n" + "\r\n" + "void reset()\r\n" +
	 * "{\r\n" + "	t=0;\r\n" + "}\r\n";
	 */
	public String mParameters = "const AgentScale id";
	private static int x = -400, y = -200;
	public int Size = 50;
	public int map[][];
	private UPPAgentVehicle agent;

	public UPPAgentStaticMap(UPPAgentVehicle agent) {
		super();
		this.agent = agent;
		createMatrix();
		Initialize();
	}

	private void createMatrix() {
		Path p;
		Node[] positionsArray; // milestones + starting positions
		List<Node> positionsList = new ArrayList<Node>();

		positionsList.add(this.agent.getStartNode());
		for (UPPAgentMission mission : this.agent.missions) {
			for (Node milestone : mission.getMilestones()) {
				positionsList.add(milestone);
			}
		}
		Size = positionsList.size();
		map = new int[Size][Size];
		positionsArray = new Node[positionsList.size()];
		for (Node position : positionsList) {
			positionsArray[position.id] = position;
		}
		for (Node start : positionsArray) {
			for (Node end : positionsArray) {
				if (start.equals(end)) {
					map[start.id][end.id] = 0;
				} else {
					p = this.agent.findPath(start, end);
					if(p == null)
					{
						System.out.println("No Path is found for agent " + this.agent.ID + " from " + start.toString() + "to: " + end.toString());
						//return;
					}
					else
					{
						
					}
					/*double path_length = p.length();
					double vehicle_speed = this.agent.vehicle.maxSpeed;
					double traveling_time = path_length / vehicle_speed;
					int estimation = (int) (100 * traveling_time); //scale the number to estimate more accurately*/
					map[start.id][end.id] = this.agent.getTravelingTime(p.length());
				}
			}
		}
	}

	private void Initialize() {
		UppaalLocation p;
		UppaalLabel label;
		UppaalTransition transition;

		UppaalLocation initial = new UppaalLocation();
		initial.setName("initial");
		initial.setId("initial");
		initial.setCommitted();
		setLocationCorrdinates(initial, x - 100, y - 100);

		this.addOrReplaceLocation(initial);
		this.setInitialLocation("initial");

		for (int i = 0; i < Size; i++) {
			p = new UppaalLocation();
			p.setName("P" + i);
			p.setId("P" + i);
			setLocationCorrdinates(p, x + i * 150, y + i * 150);
			this.addOrReplaceLocation(p);

			for (int j = 0; j < Size; j++) {
				if (map[i][j] > 0) {
					p = new UppaalLocation();
					p.setName("F" + i + "T" + j);
					p.setId("F" + i + "T" + j);
					setLocationCorrdinates(p, x + 100 + j * 150, y + 100 + i * 150);
					label = new UppaalLabel();
					label.setKind("invariant");
					label.setValue("t<=" + map[i][j]);
					p.addOrReplaceLabel(label);
					this.addOrReplaceLocation(p);
				}
			}
		}

		/*
		 * for(int i=0;i<Scale;i++) { transition = new UppaalTransition();
		 * transition.setSourceLocationId("P"+i); transition.setTargetLocationId("P"+i);
		 * label = new UppaalLabel(); label.setKind("synchronisation");
		 * label.setValue("done?"); transition.addOrReplaceLabel(label);
		 * this.addOrReplaceTransition(transition); }
		 */

		transition = new UppaalTransition();
		transition.setSourceLocationId("initial");
		transition.setTargetLocationId("P" + 0);
		label = new UppaalLabel();
		label.setKind("assignment");
		// label.setValue("position[id][0]=true,\r\nvisited[id][0]=true");
		label.setValue("position[id][0]=true");
		transition.addOrReplaceLabel(label);
		this.addOrReplaceTransition(transition);

		for (int i = 0; i < Size; i++) {
			for (int j = 0; j < Size; j++) {
				if (map[i][j] > 0) {
					transition = new UppaalTransition();
					transition.setSourceLocationId("P" + i);
					transition.setTargetLocationId("F" + i + "T" + j);
					label = new UppaalLabel();
					label.setKind("assignment");
					label.setValue("t=0,position[id][" + i + "]=false");
					transition.addOrReplaceLabel(label);
					label = new UppaalLabel();
					label.setKind("synchronisation");
					label.setValue("move[id]?");
					transition.addOrReplaceLabel(label);
					this.addOrReplaceTransition(transition);

					transition = new UppaalTransition();
					transition.setSourceLocationId("F" + i + "T" + j);
					transition.setTargetLocationId("P" + j);
					label = new UppaalLabel();
					label.setKind("assignment");
					// label.setValue("t=0,position[id]["+j+"]=true,\r\nvisited[id]["+j+"]=true");
					label.setValue("t=0,position[id][" + j + "]=true");
					transition.addOrReplaceLabel(label);
					label = new UppaalLabel();
					label.setKind("guard");
					label.setValue("t>=" + map[i][j]);
					transition.addOrReplaceLabel(label);
					this.addOrReplaceTransition(transition);
				}
			}
		}

		// transitions back to initial state
		/*
		 * String locationName=""; for(int i=0;i<Scale;i++) { locationName="P"+i;
		 * transition = new UppaalTransition();
		 * transition.setSourceLocationId(locationName);
		 * transition.setTargetLocationId("initial"); label = new UppaalLabel();
		 * label.setKind("assignment"); label.setValue("reset()");
		 * transition.addOrReplaceLabel(label); label = new UppaalLabel();
		 * label.setKind("synchronisation"); label.setValue("initialize?");
		 * transition.addOrReplaceLabel(label); this.addOrReplaceTransition(transition);
		 * 
		 * for(int j=0;j<Scale;j++) { if(map[i][j] > 0) { locationName="T"+i+j;
		 * transition = new UppaalTransition();
		 * transition.setSourceLocationId(locationName);
		 * transition.setTargetLocationId("initial"); label = new UppaalLabel();
		 * label.setKind("assignment"); label.setValue("reset()");
		 * transition.addOrReplaceLabel(label); label = new UppaalLabel();
		 * label.setKind("synchronisation"); label.setValue("initialize?");
		 * transition.addOrReplaceLabel(label); this.addOrReplaceTransition(transition);
		 * } } }
		 */

		childrenUppaalElements.sort(new UppaalXMLSorter());

		this.setName(SystemName + agent.ID);
		this.setParameter(mParameters);
		this.setDeclaration(mDeclaration);
	}

	private void setLocationCorrdinates(UppaalLocation loc, int x, int y) {

		loc.setCoordinate("x", Integer.toString(x));
		loc.setCoordinate("y", Integer.toString(y));
	}
}
