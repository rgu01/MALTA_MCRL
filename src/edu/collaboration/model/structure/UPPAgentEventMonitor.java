package edu.collaboration.model.structure;


import java.util.ArrayList;
import java.util.List;
import org.fmaes.j2uppaal.builders.UppaalXMLSorter;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalAutomaton;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLabel;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLocation;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalTransition;

public class UPPAgentEventMonitor extends UppaalAutomaton {
	public static String SystemName="Monitor";
	public static String InstanceName="monitor";
	public static String mDeclaration = "clock x;\r\nconst int T[AgentNum][EventNum]={";
	public static String mParameters = "const AgentScale id, const int event";
	private static int x = -400, y = -200;
	public List<UPPAgentEvent> events;
	//private UPPAgentVehicle agent;
	
	public UPPAgentEventMonitor(UPPAgentVehicle agent)
	{
		super();
		//this.agent = agent;
		createMonitor();
	}
	
	private void createMonitor()
	{
		events = new ArrayList<UPPAgentEvent>();	
	}
	
	public void setDeclaration()
	{
		int threshold = 0;
		int eventNum = this.events.size();
		for(int i = 0; i < eventNum; i++)
		{
			if(i < events.size())
			{
				UPPAgentEvent e = events.get(i);
				threshold = e.threshold;
			}
			if(i==0)
			{
				mDeclaration += "{";
			}
			mDeclaration += threshold;
			if(i==eventNum-1)
			{
				mDeclaration += "},";
			}
			else
			{
				mDeclaration += ",";
			}
			i++;
		}
	}
	
	public void initialize()
	{
		UppaalLocation p;
		UppaalLabel label;
		UppaalTransition transition;	
		UppaalLocation initial = new UppaalLocation();
		
		initial.setName("initial");
		initial.setId("initial");
		setLocationCorrdinates(initial, x-200, y);
		label = new UppaalLabel();
		label.setKind("invariant");
		label.setValue("x<=" + "T[id][event]");
		initial.addOrReplaceLabel(label);

		p = new UppaalLocation();
		p.setName("M");
		p.setId("M");
		setLocationCorrdinates(p, x+200, y);
		
		this.addOrReplaceLocation(initial);	
		this.addOrReplaceLocation(p);	
		this.setInitialLocation("initial");
		
		transition = new UppaalTransition();
		transition.setSourceLocationId("initial");
		transition.setTargetLocationId("M");
		label = new UppaalLabel();
		label.setKind("guard");
		label.setValue("x>=T[id][event]");
		label.setCoordinate("x", x-80+"");
		label.setCoordinate("y", y+80+"");
		transition.addOrReplaceLabel(label);
		label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("ev[id][event]:=true");
		label.setCoordinate("x", x+80+"");
		label.setCoordinate("y", y+80+"");
		transition.addOrReplaceLabel(label);
		transition.addNail(x+"", y+100+"");
		this.addOrReplaceTransition(transition);
		
		transition = new UppaalTransition();
		transition.setSourceLocationId("M");
		transition.setTargetLocationId("initial");
		label = new UppaalLabel();
		label.setKind("synchronisation");
		label.setValue("done[id][event]?");
		label.setCoordinate("x", x-80+"");
		label.setCoordinate("y", y-80+"");
		transition.addOrReplaceLabel(label);
		label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("x:=0,ev[id][event]:=false");
		label.setCoordinate("x", x+80+"");
		label.setCoordinate("y", y-80+"");
		transition.addOrReplaceLabel(label);
		transition.addNail(x+"", y-100+"");
		this.addOrReplaceTransition(transition);
		
		childrenUppaalElements.sort(new UppaalXMLSorter()); 
		
		mDeclaration = mDeclaration.substring(0, mDeclaration.length()-1) + "};";

		this.setName(SystemName);
		this.setParameter(mParameters);
		this.setDeclaration(mDeclaration);
	}
	
	private void setLocationCorrdinates(UppaalLocation loc, int x, int y) {
		loc.setCoordinate("x", Integer.toString(x));
		loc.setCoordinate("y", Integer.toString(y));
	}

}
