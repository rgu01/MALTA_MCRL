package edu.collaboration.model.queries;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.model.structure.UPPAgentFleet;
import edu.collaboration.model.structure.UPPAgentVehicle;

public class UPPAgentTaskCoverage extends UPPAgentUppaalQuery {
	private UPPAgentVehicle agent;
	private List<UPPAgentVehicle> listAgentID;
	
	public UPPAgentTaskCoverage(UPPAgentVehicle agent)
	{
		super();
		this.agent = agent;
		this.listAgentID = new ArrayList<UPPAgentVehicle>();
		this.listAgentID.add(agent);
		//String sFormula = "E<> forall(i:int[0," + plan.regularTasksNum + "]) TF[i]";
		//String sFormula = "E<> forall(int id:AgentScale) iteration[id]>=1";
		String sFormula = "E<>  iteration[" + agent.ID + "]>=MaxIteration";
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
	
	public UPPAgentTaskCoverage(UPPAgentFleet fleet)
	{
		super();
		this.agent = null;
		this.listAgentID = fleet.agents;
		String sFormula = "E<>  iteration[" + this.listAgentID.get(0).ID + "]>=MaxIteration";
		for(int i = 1; i < this.listAgentID.size(); i++)
		{
			sFormula += "&amp;&amp; iteration[" + this.listAgentID.get(i).ID + "]>=MaxIteration";
		}
		UPPAgentUppaalFormula formula = new UPPAgentUppaalFormula(sFormula);
		String sComment = "Task Coverage";
		UPPAgentUppaalComment comment = new UPPAgentUppaalComment(sComment);
		super.setFormula(formula);
		super.setComment(comment);
	}
}
