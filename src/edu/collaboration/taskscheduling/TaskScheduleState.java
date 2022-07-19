package edu.collaboration.taskscheduling;

import java.util.ArrayList;
import java.util.List;

public class TaskScheduleState {
	public List<AgentState> agentStates = new ArrayList<AgentState>();
	
	public AgentState getAgentState(int id)
	{
		for(AgentState state:this.agentStates)
		{
			if(state.agentID == id)
			{
				return state;
			}
		}
		
		return null;
	}
}
