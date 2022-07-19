package edu.collaboration.model.queries;

import java.util.List;

import edu.collaboration.model.structure.UPPAgentFleet;
import edu.collaboration.model.structure.UPPAgentVehicle;

public class UPPAgentTaskTimedIteration extends UPPAgentUppaalQuery {
	private UPPAgentVehicle agent;
	private List<UPPAgentVehicle> listAgentID;
	
	public UPPAgentTaskTimedIteration(UPPAgentFleet fleet)
	{
		super();
		this.agent = null;
		this.listAgentID = fleet.agents;
		String sFormula = "E<>  iteration[" + this.listAgentID.get(0).ID + "]>=MaxIteration";
		for(int i = 1; i < this.listAgentID.size(); i++)
		{
			sFormula += " &amp;&amp; iteration[" + this.listAgentID.get(i).ID + "]>=MaxIteration";
		}
		if(this.listAgentID.get(0).missionTimeLimit != 0)
		{
			sFormula += " &amp;&amp; globalTime <= " + this.listAgentID.get(0).missionTimeLimit;
		}
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
