package edu.collaboration.taskscheduling;

import java.util.ArrayList;
import java.util.List;

public class AgentState {
	public int agentID;
	public String currentPosition;
	public String currentTask;
	public List<String> finishedTasks;
	public int iteration;

	public AgentState()
	{
		this.agentID = -1;
		this.currentPosition = "";
		this.currentTask = "";
		this.finishedTasks = new ArrayList<String>();
		this.iteration = 0;
	}
	
	public AgentState(int agentID, String currentPosition, String currentTask, List<String> finishedTasks, int iteration)
	{
		this.agentID = agentID;
		this.currentPosition = currentPosition;
		this.currentTask = currentTask;
		this.finishedTasks = finishedTasks;
		this.iteration = iteration;
	}
}
