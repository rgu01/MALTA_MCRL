package edu.collaboration.taskscheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.collaboration.model.structure.UPPAgentFleet;

public class TaskScheduleParser
{
	public static String planPath = "./results/plan.xml";

	private static void generateState(Element node, AgentState state)
	{
		String name = node.getName();
		String text = node.getText();
		
		if(name.equals("Milestone"))
		{
			state.currentPosition = text;
		}
		else if(name.equals("Task"))
		{
			state.currentTask = text;
		}
		else if(name.equals("Finish"))
		{
			for (Iterator<Element> itTask = node.elementIterator(); itTask.hasNext(); )
			{
				Element taskNode = (Element) itTask.next();
				name = taskNode.getName();
				text = taskNode.getText();
				state.finishedTasks.add(text);
			}
		}
		else if(name.equals("Iteration"))
		{
			state.iteration = Integer.parseInt(text);
		}
	}
	
	private static void generateAction(Element node, TaskScheduleAction action)
	{
		String name = node.getName();
		String text = node.getText();
		
		if(name.equals("Type"))
		{
			action.type = text;
		}
		else if(name.equals("Target"))
		{
			action.target = text;
		}
		else if(name.equals("Time"))
		{
			double time = Double.parseDouble(text);
			action.time = time / UPPAgentFleet.Scale;
		}
	}
	
	public static TaskSchedulePlan parse()
	{
		String satisfied;
        File inputXml = new File(planPath);
        SAXReader saxReader = new SAXReader();
        TaskSchedulePlan result = new TaskSchedulePlan();
        try {
            Document document = saxReader.read(inputXml);
            Element traceNode = document.getRootElement(), newNode, agentNode, stateNode, actionNode;
            satisfied = traceNode.attribute("result").getText();
            result.satisfied = satisfied.equals("1");
            for (Iterator<Element> itNew = traceNode.elementIterator(); itNew.hasNext(); ) 
            {
            	newNode = (Element) itNew.next();
            	if(newNode.getName().equals("State"))
            	{
                    TaskScheduleState tss = new TaskScheduleState();
                	for (Iterator<Element> itState = newNode.elementIterator(); itState.hasNext(); ) 
                	{
                		agentNode = itState.next();
                		AgentState as = new AgentState();
                		as.agentID = Integer.parseInt(agentNode.attribute("id").getText());
                		for (Iterator<Element> itStateAgent = agentNode.elementIterator(); itStateAgent.hasNext(); ) 
                    	{
                    		stateNode = itStateAgent.next();
                    		generateState(stateNode, as);
                    		//TaskScheduleState state = new TaskScheduleState()
                    		/*for (Iterator<Element> j = stateNode.elementIterator(); j.hasNext(); ) 
                    		{
                    			Element node = (Element) j.next();
                    			System.out.println(node.getName() + ":" + node.getText());
                    		}*/
                    	}
                		tss.agentStates.add(as);
                	}
                	result.states.add(tss);
            	}
            	else if(newNode.getName().equals("Action"))
            	{
            		TaskScheduleAction tsa = new TaskScheduleAction();
            		for (Iterator<Element> itAgent = newNode.elementIterator(); itAgent.hasNext(); ) 
            		{
            			agentNode = itAgent.next();
                		tsa.agentID = Integer.parseInt(agentNode.attribute("id").getText());
                		for (Iterator<Element> itAction = agentNode.elementIterator(); itAction.hasNext(); )
                		{
                			actionNode = itAction.next();
                			generateAction(actionNode, tsa);
                		}
            		}
            		result.actions.add(tsa);
            	}
            }
        } 
        catch (DocumentException e) 
        {
            System.out.println(e.getMessage());
        }
        finally
        {
        	System.out.println("dom4j parserXml");
        }
    	return result;
    }
}
