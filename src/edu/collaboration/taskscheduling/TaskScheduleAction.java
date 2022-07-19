package edu.collaboration.taskscheduling;

public class TaskScheduleAction {
	public static String StrMoveStart = "Move Start";
	public static String StrMoveFinish = "Move Finish";
	public static String StrTaskStart = "Task Start";
	public static String StrTaskFinish = "Task Finish";
	public int agentID;
	public String type;
	public String target;
	public double time;
	
	public TaskScheduleAction()
	{
		this.agentID = -1;
		this.type = "";
		this.target = "";
		this.time = 0.0;
	}
	
	public TaskScheduleAction(int agentID, String type, String target, double time)
	{
		this.agentID = agentID;
		this.type = type;
		this.target = target;
		this.time = time;
	}
	
	public String toString()
	{
		return "(" + this.agentID + ", " + this.type + ", " + this.target + ", " + this.time + ")";
	}
}
