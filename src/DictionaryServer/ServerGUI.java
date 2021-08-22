package DictionaryServer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerGUI {

	private JFrame frame;
	private JTextField portField;
	private JTextField dictField;
	private JButton btnStart,btnStop;
	private TCPInteractiveServer server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 871, 596);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel portLabel = new JLabel("Port Number");
		portLabel.setBounds(65, 81, 84, 16);
		frame.getContentPane().add(portLabel);
		
		JLabel dictLabel = new JLabel("Dictionary Address");
		dictLabel.setBounds(65, 137, 120, 16);
		frame.getContentPane().add(dictLabel);
		
		portField = new JTextField();
		portField.setBounds(217, 76, 229, 26);
		frame.getContentPane().add(portField);
		portField.setColumns(10);
		
		dictField = new JTextField();
		dictField.setBounds(217, 132, 229, 26);
		frame.getContentPane().add(dictField);
		dictField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Dictionary Server");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(325, 6, 246, 44);
		frame.getContentPane().add(lblNewLabel_1);
		
		JTextArea outputArea = new JTextArea();
		//outputArea.setBounds(6, 344, 859, 224);
		//frame.getContentPane().add(outputArea);
		
		JLabel lblTerminal = new JLabel("Terminal");
		lblTerminal.setHorizontalAlignment(SwingConstants.CENTER);
		lblTerminal.setBounds(425, 301, 61, 16);
		frame.getContentPane().add(lblTerminal);
		
		JScrollPane scrollPane = new JScrollPane(outputArea);
		scrollPane.setVerticalScrollBarPolicy(scrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(6, 344, 859, 224);
		frame.getContentPane().add(scrollPane);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker()
				{
					protected Object doInBackground()throws Exception
					{
						if(portField.getText().isEmpty() | dictField.getText().isEmpty())
						{
							outputArea.append("Required Fields Empty"+"\n");
						}
						else
						{
							try
							{
								int portNumber = Integer.valueOf(portField.getText());
								btnStart.setEnabled(false);
								btnStop.setEnabled(true);
								server = new TCPInteractiveServer(portNumber,dictField.getText(),outputArea);//Creating Interactive Server object
								server.setUp();
								
								
							}
							catch(NumberFormatException e)
							{
								outputArea.append("Port Number should only be numeric"+"\n");
							}
							catch(Exception e)
							{
								outputArea.append("Server couldn't be started-"+e.getMessage()+"\n");
							}
							
						}
						
						return null;
					}
				}.execute();
				
			}
		});
		btnStart.setBounds(253, 244, 117, 29);
		frame.getContentPane().add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker()
				{
					protected Object doInBackground()throws Exception
					{
						server.finalize();
						return null;
					}
				}.execute();
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				
			}
		});
		btnStop.setBounds(546, 244, 117, 29);
		frame.getContentPane().add(btnStop);
	}
}
