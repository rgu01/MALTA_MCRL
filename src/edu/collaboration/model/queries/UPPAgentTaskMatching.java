package edu.collaboration.model.queries;

import edu.collaboration.model.structure.*;

public class UPPAgentTaskMatching {
	
	public static void addQueries(UPPAgentMissionPlan plan, UPPAgentUppaalQueries queries, int agentID)
	{
		String sFormula="",sComment="",sMilestones="";
		UPPAgentUppaalFormula formula;
		UPPAgentUppaalComment comment;
		UPPAgentUppaalQuery query;
		
		for(UPPAgentMission m:plan.agent.missions)
		{
			for(int i=0;i<m.getMilestones().size();i++)
			{
				if(i != m.getMilestones().size()-1)
				{
					sMilestones += UPPAgentStaticMap.InstanceName + agentID + ".P" + m.getMilestones().get(i) + "||";
				}
				else
				{
					sMilestones += UPPAgentStaticMap.InstanceName + agentID + ".P" + m.getMilestones().get(i);
				}
			}
			
			sFormula = "E<> (" + sMilestones + ") imply " + UPPAgentMissionPlan.InstanceName + agentID + ".T" + m.ID;
			sComment = "Task Matching for Task" + m.ID;
			formula = new UPPAgentUppaalFormula(sFormula);
			comment = new UPPAgentUppaalComment(sComment);
			query = new UPPAgentUppaalQuery();
			query.setFormula(formula);
			query.setComment(comment);
			queries.addQuery(query);
			
			sFormula = "A[] " + UPPAgentMissionPlan.InstanceName + agentID + ".T" + m.ID + " imply (" + sMilestones + ")";
			sComment = "Task Matching for Task" + m.ID;
			formula = new UPPAgentUppaalFormula(sFormula);
			comment = new UPPAgentUppaalComment(sComment);
			query = new UPPAgentUppaalQuery();
			query.setFormula(formula);
			query.setComment(comment);
			queries.addQuery(query);
			
			sMilestones = "";
		}
	}
}
