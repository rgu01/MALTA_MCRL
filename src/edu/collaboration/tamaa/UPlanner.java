package edu.collaboration.tamaa;

import org.apache.thrift.transport.*;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;

import com.afarcloud.thrift.*;

import edu.collaboration.taskscheduling.TaskScheduleParser;
import edu.collaboration.taskscheduling.TaskSchedulePlan;

public class UPlanner extends Thread {
	public String configPath = "res/config.txt";
	public String mmtAddress;
	public int mmtPort;
	public int tamaaPort;
	public String uppaalAddress;
	public int uppaalPort;
	public boolean ownModel = false;
	public String ownModelAddress = "";
	TServer server;
	
	public UPlanner(boolean ownModel, String ownModelAddress) {
		this.ownModel = ownModel;
		this.ownModelAddress = ownModelAddress;
	}
	
	public void run()
	{
		this.readConfig();
		this.logtoText();
		System.out.println("============" + new Date().toString() + "============");
		this.StartServer(new PlannerService.Processor<PlannerServiceHandler>(
				new PlannerServiceHandler(this.mmtAddress, this.mmtPort, this.uppaalAddress, this.uppaalPort, this.ownModel, this.ownModelAddress)));
	}
	
	public void exit()
	{
		this.server.stop();
	}

	public void StartServer(PlannerService.Processor<PlannerServiceHandler> processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(this.tamaaPort);
			// Use this for a multi-thread server
//			TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport)
//				    .minWorkerThreads(5)
//				    .maxWorkerThreads(256)
//				    .processor(processor)
//				    .protocolFactory(new TBinaryProtocol.Factory());
//			this.server = new TThreadPoolServer(args);
//			System.out.println("Starting the multi-thread server...");
			
			// Use the following line for a single-thread server
			this.server = new TSimpleServer(new Args(serverTransport).processor(processor));
			System.out.println("Starting the simple server...");

//			System.out.println(UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath());
//			JOptionPane.showMessageDialog(null, UPPAgentGenerator.class.getClassLoader().getResource("empty_template.xml").getPath(), "DEBUG", JOptionPane.PLAIN_MESSAGE);
//			InputStream is = new InputStream("resources/empty_template.xml");
			
			server.serve();
			System.out.println("Server started...");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void logtoText() {
		String dirName = "./results/";
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String fileName = "Log " + format.format(date) + ".txt";
		File file = new File(dirName + fileName);

		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readConfig() {
		try {
			File filename = new File(this.configPath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
			Scanner sc = new Scanner(reader);
			if (sc != null && sc.hasNextLine()) {
				this.mmtAddress = sc.nextLine().replace("MMT Address: ", "");
				this.mmtPort = Integer.parseInt(sc.nextLine().replace("MMT Port: ", ""));
				this.tamaaPort = Integer.parseInt(sc.nextLine().replace("Planner Port: ", ""));
				this.uppaalAddress = sc.nextLine().replace("UPPAAL Address: ", "");
				this.uppaalPort = Integer.parseInt(sc.nextLine().replace("UPPAAL Port: ", ""));
				sc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//
//		
//		UPlanner planner = new UPlanner();
//		planner.readConfig();
//		planner.logtoText();
//		System.out.println("============" + new Date().toString() + "============");
//		planner.StartServer(new PlannerService.Processor<PlannerServiceHandler>(
//				new PlannerServiceHandler(planner.mmtAddress, planner.mmtPort, planner.uppaalAddress, planner.uppaalPort)));
//		//The following is for experiments
//		//planner.StartServer(new PlannerService.Processor<PlannerServiceHandlerTestVersion>(
//		//		new PlannerServiceHandlerTestVersion(planner.mmtAddress, planner.mmtPort, planner.uppaalAddress, planner.uppaalPort)));
// 
//	}
}
