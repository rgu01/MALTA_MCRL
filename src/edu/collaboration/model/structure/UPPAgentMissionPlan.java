package edu.collaboration.model.structure;

import org.fmaes.j2uppaal.builders.UppaalXMLSorter;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalAutomaton;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLabel;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalLocation;
import org.fmaes.j2uppaal.datastructures.uppaalstructures.UppaalTransition;

public class UPPAgentMissionPlan extends UppaalAutomaton {
	public static String SystemName="TaskExecution";
	public static String InstanceName="taskExe";
	private static int x = -400, y = -200;
	public UPPAgentVehicle agent;

	public UPPAgentMissionPlan(UPPAgentVehicle agent, UPPAgentEventMonitor ev)
	{
		super();
		this.agent = agent;
		initialize();
	}
	
	private void initialize()
	{
		UppaalLocation m,source,target;
		UppaalLabel label;
		UppaalTransition transition;
		//UPPAgentEventMonitor empty = new UPPAgentEventMonitor(agent);
		int tx=0,ty=0,sx=0,sy=0,nx=0,ny=0,k=0,j=0;
		String mParameters = "";
		String mDeclaration = "";
		
		m = new UppaalLocation();
		m.setName("T0");
		m.setId("T0");
		setLocationCorrdinates(m, x, y);
		
		this.addOrReplaceLocation(m);	
		this.setInitialLocation("T0");
		
		for(UPPAgentMission um:this.agent.missions)
		{
			k=um.ID%4;
			switch (k)
			{
			case 0:
				tx = x-300;
				ty = y-j*200;
				break;
			case 1:
				tx = x+j*200;
				ty = y-300;
				break;
			case 2:
				tx = x+300;
				ty = y+j*200;
				break;
			case 3:
				tx = x+j*200;
				ty = y+300;
				j++;
				break;
			}
			
			m = new UppaalLocation();
			m.setName("T"+um.ID);
			m.setId("T"+um.ID);
			setLocationCorrdinates(m, tx, ty);
			label = new UppaalLabel();
			label.setKind("invariant");
			//label.setValue("t<=" + "WCET[id]["+id+"]");
			label.setValue("t<=" + um.getTaskWCET());
			m.addOrReplaceLabel(label);
			this.addOrReplaceLocation(m);
		}
		
		transition = new UppaalTransition();
		transition.setSourceLocationId("T0");
		transition.setTargetLocationId("T0");
		label = new UppaalLabel();
		label.setKind("synchronisation");
		label.setValue("move[id]!");
		transition.addOrReplaceLabel(label);
		//reinitialize
		/*label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("steps++,t=0");
		transition.addOrReplaceLabel(label);*/
		label = new UppaalLabel();
		label.setKind("assignment");
		label.setValue("t=0");
		transition.addOrReplaceLabel(label);
		this.addOrReplaceTransition(transition);
		
		String guardOfPositions = "";
		for(UPPAgentMission um:this.agent.missions)
		{
			transition = new UppaalTransition();
			transition.setSourceLocationId("T0");
			transition.setTargetLocationId("T"+um.ID);
			label = new UppaalLabel();
			label.setKind("assignment");
			//for(j=0; j<um.getEventsToTrigger().size();j++)
			//{
			//	assignmentOfEvents += ",E[" + um.getEventsToTrigger().get(j) + "]=true";
			//}
			//label.setValue("t=0,MS["+id+"]=true,MF["+id+"]=false" + assignmentOfEvents);
			label.setValue("t=0,ts[id]["+um.ID+"]=true,tf[id]["+um.ID+"]=false");
			transition.addOrReplaceLabel(label);
			label = new UppaalLabel();
			label.setKind("guard");
			label.setValue(um.getPrecondition());
			/*for(j=0; j<um.getMilestones().size();j++)
			{
				if(j != um.getMilestones().size()-1)
				{
					guardOfPositions += "position[id][" + um.getMilestones().get(j) + "]||";
				}
				else
				{
					guardOfPositions += "position[id][" + um.getMilestones().get(j) + "]";
				}
			}
			if(um.getPrecondition().equals(""))
			{
				label.setValue(guardOfPositions);
			}
			else
			{
				label.setValue(guardOfPositions + "&amp;&amp;(" + um.getPrecondition()+")");
			}*/
			transition.addOrReplaceLabel(label);
			this.addOrReplaceTransition(transition);
			
			guardOfPositions = "";
			//assignmentOfEvents = "";
			
			transition = new UppaalTransition();
			transition.setSourceLocationId("T"+um.ID);
			transition.setTargetLocationId("T0");
			
			source = (UppaalLocation) this.getLocationById("T"+um.ID);
			target = (UppaalLocation) this.getLocationById("T0");
			sx=Integer.parseInt(source.getCoordinate("x"));
			sy=Integer.parseInt(source.getCoordinate("y"));
			tx=Integer.parseInt(target.getCoordinate("x"));
			ty=Integer.parseInt(target.getCoordinate("y"));
			if(sx==tx && sy==ty)
			{
				nx = sx - 100;
			}
			else if(sx!=tx)
			{
				nx = sx + (tx - sx) / 2;
			}
			else if(sy!=ty)
			{
				ny = sy + (ty - sy) / 2;
			}
			
			label = new UppaalLabel();
			label.setKind("assignment");
			//for(j=0; j<um.getEventsToReset().size();j++)
			//{
			//	assignmentOfEvents += ",E[" + um.getEventsToReset().get(j) + "]=false";
			//}
			//label.setValue("t=0,MS["+id+"]=false,MF["+id+"]=true" + assignmentOfEvents);
			label.setValue("t=0,ts[id]["+um.ID+"]=false,tf[id]["+um.ID+"]=true,\r\nupdateIteration()");
			label.setCoordinate("x", nx+30+"");
			label.setCoordinate("y", ny+30+"");
			transition.addOrReplaceLabel(label);
			label = new UppaalLabel();
			label.setKind("guard");
			//label.setValue("t>=" + "BCET[id]["+id+"]");
			label.setValue("t>=" + um.getTaskBCET());
			label.setCoordinate("x", nx+"");
			label.setCoordinate("y", ny+"");
			transition.addOrReplaceLabel(label);
			if(!um.regularTask)
			{
				/*label = new UppaalLabel();
				label.setKind("synchronisation");
				label.setValue("done[id][" + um.getEventsTrigger().get(0) + "]!");
				label.setCoordinate("x", nx-30+"");
				label.setCoordinate("y", ny-30+"");
				transition.addOrReplaceLabel(label);*/
			}
			transition.addNail(nx+"", ny+"");
			this.addOrReplaceTransition(transition);
			
			guardOfPositions = "";
			//assignmentOfEvents = "";
		}
		
		
		childrenUppaalElements.sort(new UppaalXMLSorter()); 

		this.setName(SystemName+agent.ID);
		mParameters = "const AgentScale id";
		/*String tfString = "";
		String tsString = "";
		for(int i = 0; i < agent.missions.size(); i++)
		{
			if(i==0)
			{
				tfString += "bool tf[TaskNum]={true,";
				tsString += "bool ts[TaskNum]={true,";
			}
			tfString += "false";
			tsString += "false";
			if(i!=agent.missions.size()-1)
			{
				tfString += ",";
				tsString += ",";
			}
			else
			{
				tfString += "};\r\n";
				tsString += "};\r\n";
			}
			
		}*/
		mDeclaration += "clock t;\r\n" + 
				//tfString + tsString + 
				"\r\n" + 
				"void updateIteration()\r\n" +
				"{\r\n" +
				"	int i=0;\r\n" +
				"	bool finish = true;\r\n" +
				"	for(i=1;i<=RegularTaskNum[id];i++)\r\n" +
				"	{\r\n" +
				"		finish &amp;= tf[id][i];\r\n" +
    			"	}\r\n" +
    			"	if(finish)\r\n" +
    			"	{\r\n" +
    			"		for(i=1;i<=RegularTaskNum[id];i++)\r\n" +
    			"		{\r\n" +
    			"			tf[id][i]=false;\r\n" +
        		"		}\r\n" +
    			"		\r\n" +
    			"		iteration[id]+=1;\r\n" +
    			"		if(iteration[id]>=MaxIteration)\r\n" +
    			"		{\r\n" +
    			"			iteration[id]=MaxIteration;\r\n" +
        		"		}\r\n" +
    			"	}\r\n" + 
    			"	else\r\n" +
    			"	{\r\n" +
    			"    	finish = true;\r\n" +
    			"	}\r\n" + 
    			"}\r\n\r\n";
    			/*"void reset()\r\n" +
    			"{\r\n" +
    			"   t=0;\r\n" +
    			"   gReset();\r\n" +
    			"}\r\n";*/
		this.setParameter(mParameters);
		this.setDeclaration(mDeclaration);
	}

	private void setLocationCorrdinates(UppaalLocation loc, int x, int y) 
	{
		loc.setCoordinate("x", Integer.toString(x));
		loc.setCoordinate("y", Integer.toString(y));
	}
}
