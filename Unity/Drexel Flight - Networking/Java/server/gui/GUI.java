package drexelride.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import drexelride.server.Server;

public class GUI {
	
	private Server server;
	private JFrame mainFrame;
	private JTextField ipBox;
	private JTextField portBox;
	private JCheckBox videoCheckBox;
	private JButton startButton;
	private JButton stopButton;
	private JButton initializeButton;
	
	public GUI() {
		this.mainFrame = buildMainFrame();
		this.mainFrame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {
				if(server != null) {
					server.stop();
				}
			}
			public void windowClosing(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
			
		});
		initializeComponents();
	}
	
	private JFrame buildMainFrame() {
		JFrame frame = new JFrame();
		frame.setTitle("Drexel Ride Passthrough");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void initializeComponents() {
		this.ipBox = new JTextField(15);
		this.portBox = new JTextField(5);
		this.videoCheckBox = new JCheckBox("Video?", true);
		this.initializeButton = new JButton("Initialize");
		this.startButton = new JButton("Start");
		this.stopButton = new JButton("Stop");
		this.initializeButton.setEnabled(true);
		this.startButton.setEnabled(false);
		this.stopButton.setEnabled(false);
		setupListeners();
		JPanel mainPanel = new JPanel();
		mainPanel.add(new JLabel("Passthrough IP: "));
		mainPanel.add(this.ipBox);
		mainPanel.add(new JLabel("Port: "));
		mainPanel.add(this.portBox);
		mainPanel.add(this.videoCheckBox);
		mainPanel.add(this.initializeButton);
		mainPanel.add(this.startButton);
		mainPanel.add(this.stopButton);
		this.mainFrame.setContentPane(mainPanel);
	}
	
	private void setupListeners() {
		this.initializeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String ip = ipBox.getText();
				String port = portBox.getText();
				if(ip.equals("") || port.equals("")) {
					return;					
				} else {
					if(server != null) {
						server.stop();
					}
					server = new Server(ip, Integer.parseInt(port), videoCheckBox.isSelected());
					startButton.setEnabled(true);
				}
			}
		});
		this.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new Thread(server).start();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				initializeButton.setEnabled(false);
			}
		});
		this.stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				server.stop();
				server = null;
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				initializeButton.setEnabled(true);
			}
		});
		this.videoCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fieldChange();
			}
		});
		DocumentListener listener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fieldChange();
			}
			public void removeUpdate(DocumentEvent e) {
				fieldChange();
			}
			public void insertUpdate(DocumentEvent e) {
				fieldChange();
			}
		};
		this.ipBox.getDocument().addDocumentListener(listener);
		this.portBox.getDocument().addDocumentListener(listener);
	}
	
	private void fieldChange() {
		startButton.setEnabled(false);
		initializeButton.setEnabled(true);
	}
	
	public void show() {
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}
	
	public void close() {
		server.stop();
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.show();
	}
	
}
