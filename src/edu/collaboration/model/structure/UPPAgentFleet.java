package edu.collaboration.model.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UPPAgentFleet {
	public static int Scale = 1; //a constant number for scaling the integer traveling times 
	public List<UPPAgentVehicle> agents;
	
	public UPPAgentFleet(List<UPPAgentVehicle> agents)
	{
		this.agents = agents;
	}
}
