package edu.collaboration.taskscheduling;

import java.util.ArrayList;
import java.util.List;

public class TaskSchedulePlan {
	public boolean satisfied;
	public List<TaskScheduleState> states = new ArrayList<TaskScheduleState>();
	public List<TaskScheduleAction> actions = new ArrayList<TaskScheduleAction>();
	
	public int length()
	{
		return states.size();
	}
}
