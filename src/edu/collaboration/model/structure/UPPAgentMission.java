package edu.collaboration.model.structure;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Task;
import com.afarcloud.thrift.TaskTemplate;

import edu.collaboration.pathplanning.Node;

public class UPPAgentMission {
	public int ID;
	private Task task;
	private List<Node> milestones = new ArrayList<Node>();
	public boolean regularTask = true;
	// private List<Integer> events_trigger; //The event triggering this task
	private String precondition = "";
	private UPPAgentEventMonitor monitor;

	public UPPAgentMission(int id, Node milestone) {
		this.ID = id;
		this.task = milestone.task;
		this.regularTask = true;
		// events_trigger = new ArrayList<Integer>();
		// test
		/*
		 * if (this.ID != 0) { this.setPrecondition("tf[" + (this.ID - 1) + "]");
		 * this.task.bcet = 1; this.task.wcet = 5; }
		 */
		// test finish
		this.setPrecondition(this.task.precondition);
		this.addMilestone(milestone);
	}

	public int getTaskWCET()
	{
		return this.task.wcet * UPPAgentFleet.Scale;
	}

	public int getTaskBCET()
	{
		return this.task.bcet * UPPAgentFleet.Scale;
	}
	
	public TaskTemplate getTaskTemplate()
	{
		return this.task.taskTemplate;
	}
	
	public void addMilestone(Node milestone) {
		this.milestones.add(milestone);
	}

	public List<Node> getMilestones() {
		return this.milestones;
	}

	public void setPrecondition(String value) {
		// boolean tag = false;
		if (value != null && !value.equals("")) {
			precondition = value.replaceAll("&&", "&amp;&amp;");
			/*
			 * if (regularTask && this.monitor != null) { for (UPPAgentEvent e :
			 * this.monitor.events) { precondition += "&amp;&amp;!ev[id][" + e.id + "]"; } }
			 * else {
			 * 
			 * for(UPPAgentEvent e:monitor.events) { for(int t:events_trigger) { if(t==e.id)
			 * { precondition += "&amp;&amp;ev[id][" + e.id + "]"; tag = true; break; } }
			 * if(!tag) { precondition += "&amp;&amp;!ev[id][" + e.id + "]"; } tag = false;
			 * }
			 * 
			 * }
			 */
		} else {
			/*
			 * if (!regularTask) {
			 * 
			 * precondition = ""; for(int t:events_trigger) { precondition += "ev[id][" + t
			 * + "]"; }
			 * 
			 * } else if (this.monitor != null) { for (UPPAgentEvent e :
			 * this.monitor.events) { precondition += "!ev[id][" + e.id + "]"; } }
			 */
		}
	}

	public String getPrecondition() {
		String milestoneStr = "", result = "";
		Node milestone;

		for (int i = 0; i < this.milestones.size(); i++) {
			milestone = this.milestones.get(i);
			if (i == 0) {
				milestoneStr += "position[id][" + milestone.id + "]";
			} else {
				milestoneStr += "||position[id][" + milestone.id + "]";
			}
		}
		if (precondition.equals("")) {
			result = milestoneStr;
		} else {
			result = "(" + this.precondition + ")&amp;&amp;(" + milestoneStr + ")";
		}

		return result;
	}

	public void addEventsTrigger(int event) {
		if (event != -1) {
			regularTask = false;
			// events_trigger.add(event);
		}
	}
}
