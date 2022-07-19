package edu.collaboration.PathPlanningFactory;

import java.util.ArrayList;
import java.util.List;
import edu.collaboration.pathplanning.*;
import edu.collaboration.pathplanning.dali.*;
import edu.collaboration.pathplanning.xstar.*;

/**
 * The goal is to create an easy access to our different path planning algorithm constructors
 * @author claire
 */

public class PathPlannningFactory {
	/**
	 * With this function you put all the parameters that you need to call the constructor of the path planning algorithm you are interested in. 
	 * @param Info which is an Object[]. Be careful there is an order !<p> First variable is a String with the name of the algorithm. The second is a NavigationArea variable. The third one is a List<FDaliRegionConstraint> variable.
	 * @return a PathPlanningAlgorithm variable : AStar, Dali, DaliStar or Astar2 ( algorithms at the moment of the creation )
	 */
	public PathPlanningAlgorithm getAlgo(Object[] Info){
		List<DaliRegionConstraint> regionPreferences = new ArrayList<DaliRegionConstraint>();
		NavigationArea nArea = null;
		
		switch( (String) Info[0] )
		{
			case "AStar":
				nArea = (NavigationArea) Info[1];
				return new AStar(nArea);
			
			case "Dali":
				nArea = (NavigationArea) Info[1];
				regionPreferences = (List<DaliRegionConstraint>) Info[2];
				return new Dali( nArea, regionPreferences);
			
			case "DaliStar":
				nArea = (NavigationArea) Info[1];
				regionPreferences = (List<DaliRegionConstraint>) Info[2];
				return new DaliStar(nArea, regionPreferences);
			
			case "AStar2":
				nArea = (NavigationArea) Info[1];
				return new AStar2(nArea);
		}	  
		return null;
	}	
}
