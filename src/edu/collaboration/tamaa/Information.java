package edu.collaboration.tamaa;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class Information {

	private JFrame frmMiddlewareOfMalta;
	UPlanner planner;
	private String title = "Middleware of MALTA";
	private JTextField txtmodelspecialUseCase;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Information window = new Information();
					window.frmMiddlewareOfMalta.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Information() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMiddlewareOfMalta = new JFrame();
		frmMiddlewareOfMalta.setResizable(false);
		frmMiddlewareOfMalta.setTitle("Middleware of MALTA");
		frmMiddlewareOfMalta.setBounds(100, 100, 442, 283);
		frmMiddlewareOfMalta.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMiddlewareOfMalta.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("The middleware of MALTA has ...");
		lblNewLabel.setBackground(new Color(240, 240, 240));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 10, 350, 50);
		frmMiddlewareOfMalta.getContentPane().add(lblNewLabel);
		
		JLabel lblStatus = new JLabel("stopped! ");
		lblStatus.setForeground(Color.RED);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblStatus.setBounds(10, 60, 205, 50);
		frmMiddlewareOfMalta.getContentPane().add(lblStatus);
		
		JButton btnStart = new JButton("Start");
		JButton btnStop = new JButton("Stop");
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Use you own model?");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNewCheckBox.isSelected()) {
					btnStop.doClick();
					btnStart.doClick();
					txtmodelspecialUseCase.setEnabled(true);
				}
				else {
					btnStop.doClick();
					btnStart.doClick();
					txtmodelspecialUseCase.setEnabled(false);
				}
			}
		});
		chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxNewCheckBox.setBounds(10, 113, 295, 21);
		frmMiddlewareOfMalta.getContentPane().add(chckbxNewCheckBox);
		btnStop.setEnabled(false);
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(planner != null) {
					planner.exit();
				}
				System.exit(0);
			}
		});
		btnStart.setBounds(162, 208, 85, 21);
		btnStop.setBounds(35, 208, 85, 21);
		frmMiddlewareOfMalta.getContentPane().add(btnStop);
		btnExit.setBounds(293, 208, 85, 21);
		frmMiddlewareOfMalta.getContentPane().add(btnExit);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(true);
				frmMiddlewareOfMalta.setTitle(title + " - started....");
				lblStatus.setText("started!");
				lblStatus.setForeground(Color.green);
				btnStart.setEnabled(false);
				planner = new UPlanner(chckbxNewCheckBox.isSelected(), txtmodelspecialUseCase.getText());
				planner.start();
			}
		});
		frmMiddlewareOfMalta.getContentPane().add(btnStart);
		
		txtmodelspecialUseCase = new JTextField();
		txtmodelspecialUseCase.setEnabled(false);
		txtmodelspecialUseCase.setText("./model/special use case - no monitors.xml");
		txtmodelspecialUseCase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtmodelspecialUseCase.setBounds(83, 152, 295, 19);
		frmMiddlewareOfMalta.getContentPane().add(txtmodelspecialUseCase);
		txtmodelspecialUseCase.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Directory: ");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(10, 155, 79, 13);
		frmMiddlewareOfMalta.getContentPane().add(lblNewLabel_1);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmMiddlewareOfMalta.setTitle(title + " - stopped....");
				lblStatus.setText("stopped!");
				lblStatus.setForeground(Color.red);
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				planner.exit();
			}
		});
		
	}
}
