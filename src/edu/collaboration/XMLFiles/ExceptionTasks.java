package edu.collaboration.XMLFiles;

/**
 * A Exception class for things related to the format of our tasks
 * @author claire
 */
public class ExceptionTasks extends Exception{
	
	/**
	 * Basic Constructor
	 */
	public ExceptionTasks() {
		super();
	}
	
	/**
	 * Constructor 
	 * @param s, a String, the message
	 */
	public ExceptionTasks(String s) {
		super(s);
	}
}
