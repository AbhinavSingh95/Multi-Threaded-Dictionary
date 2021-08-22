package DictionaryServer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.Button;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.net.*;
import java.io.*;

public class clientGUI {

	private JFrame frame;
	private final JPanel panel = new JPanel();
	private JTextField wordField;
	private String[] operations = new String[] {"Search", "Add", "Delete"};
	private JTextArea outputArea;
	private JComboBox<String> functionBox;
	private JTextArea meaningText;
	private JButton btnGo;
	private TCPInteractiveClient client = null;
	private static String serverAddress;
	private static int portNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		if(args.length!=2)
		{
			JOptionPane.showMessageDialog(null, "Invalid Input Parameters","Error!!",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		else
		{
			serverAddress=args[0];
			portNumber=Integer.valueOf(args[1]);
		}
		
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() {
				try 
				{
					clientGUI window = new clientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
			
	}

	/**
	 * Create the application.
	 */
	public clientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		client = new TCPInteractiveClient(portNumber,serverAddress);
		String connectionStatus=client.setUp();
		//frame.setTitle("Connected to server......");
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		panel.setBounds(0, 0, 381, 594);
		frame.getContentPane().add(panel);
		
		JLabel imageLbl = new JLabel("");
		ImageIcon imageIcon = new ImageIcon(clientGUI.class.getResource("/DictionaryServer/Screen Shot 2019-08-20 at 7.29.21 PM.png"));
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(381, 594, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);
		imageLbl.setIcon(imageIcon);
		panel.add(imageLbl);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(813, 33, 43, 16);
		frame.getContentPane().add(lblStatus);
		
		JLabel lblStatusField = new JLabel("Status:");
		lblStatusField.setBounds(858, 33, 94, 16);
		frame.getContentPane().add(lblStatusField);
		lblStatusField.setText(connectionStatus.equals("Connected to server......")?"Connected":"Not Connected");
		
		
		JLabel lblWord = new JLabel("Word");
		lblWord.setBounds(393, 33, 32, 16);
		frame.getContentPane().add(lblWord);
		
		wordField = new JTextField();
		wordField.setBounds(393, 61, 381, 26);
		frame.getContentPane().add(wordField);
		wordField.setColumns(10);
		
		functionBox = new JComboBox();
		functionBox.setModel(new DefaultComboBoxModel(operations));
		functionBox.setBounds(393, 127, 381, 26);
		frame.getContentPane().add(functionBox);
		
		JLabel lblAction = new JLabel("Action");
		lblAction.setBounds(393, 99, 41, 16);
		frame.getContentPane().add(lblAction);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(393, 105, 240, 0);
		frame.getContentPane().add(separator);
		
		JLabel lblMeaning = new JLabel("Meaning(; Separator)");
		lblMeaning.setBounds(393, 165, 189, 16);
		frame.getContentPane().add(lblMeaning);
		
		JPanel meaningPanel = new JPanel();
		meaningPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		meaningPanel.setBounds(394, 186, 381, 127);
		frame.getContentPane().add(meaningPanel);
		meaningPanel.setLayout(null);
		
		meaningText = new JTextArea();
		meaningText.setLineWrap(true);
		meaningText.setBounds(6, 6, 369, 115);
		meaningPanel.add(meaningText);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(393, 323, 61, 16);
		frame.getContentPane().add(lblOutput);
		
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(null);
		outputPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		outputPanel.setBounds(392, 347, 486, 205);
		frame.getContentPane().add(outputPanel);
		
		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		outputArea.setBounds(6, 6, 474, 193);
		outputPanel.add(outputArea);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(381, 560, 506, 0);
		frame.getContentPane().add(separator_1);
		
		
		
		
		//Button GO!! Syntax
		btnGo = new JButton("GO!!");
		btnGo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				outputArea.selectAll();
				outputArea.replaceSelection("");//Clearing output pane
				if(connectionStatus.equals("Connected to server......"))
				{
					String function = String.valueOf(functionBox.getSelectedItem());
					if(function.equals(operations[0]))//Operation Search
					{
						if(client.isAlpha(wordField.getText()))
						{
							if(!(wordField.getText().isEmpty()))
							{
								new SwingWorker()
								{
									protected Object doInBackground()throws Exception
									{
										String input=(operations[0]+";"+wordField.getText().toLowerCase());
										String response = client.searchWord(input);
										lblStatusField.setText(response.equals("Operation Failed due to I/O exception")?"Error":"Connected");
										outputArea.append(response);
										return null;
									}
								}.execute();
								
							}
							else
							{
								outputArea.append("Word Field Empty");
							}
						}
						else
						{
							outputArea.append("Word Syntax not supported");
						}
						
						//String response;
							
					}//End of Search block
					if(function.equals(operations[1]))//Operation Add Word
					{
						if(client.isAlpha(wordField.getText()))
						{
							if(!(wordField.getText().isEmpty() | meaningText.getText().isEmpty()))
							{
								new SwingWorker()
								{
									protected Object doInBackground()throws Exception
									{
										String input=(operations[1]+";"+wordField.getText().toLowerCase()+";"+meaningText.getText());
										String response = client.addWord(input);
										meaningText.selectAll();
										meaningText.replaceSelection("");//Clearing meaning text box
										lblStatusField.setText(response.equals("Operation Failed due to I/O exception")?"Error":"Connected");
										outputArea.append(response);
										return null;
									}
								}.execute();	
							}//End of Add Word block
							else
							{
								outputArea.append("Required Fields Empty");
							}
						}
						else
						{
							outputArea.append("Word Syntax not supported");
						}
						
					}//End of add word block
						if(function.equals(operations[2]))//Operation Delete Word
						{
							if(client.isAlpha(wordField.getText()))
							{
								if(!(wordField.getText().isEmpty()))
								{
									new SwingWorker()
									{
										protected Object doInBackground()throws Exception
										{
											String input=(operations[2]+";"+wordField.getText().toLowerCase());
											String response = client.deleteWord(input);
											lblStatusField.setText(response.equals("Operation Failed due to I/O exception")?"Error":"Connected");
											outputArea.append(response);
											return null;
										}
									}.execute();
									
								}
								else
								{
									outputArea.append("World Field empty");
								}
							}
							else
							{
								outputArea.append("Word Syntax not supported");
							}
							
								
						}//End of Delete Word block
							
				}//End of connected block
				else
				{
					outputArea.append(connectionStatus);
				}
				
				
			}//End of actionPerformed method
		});
		btnGo.setBackground(new Color(255, 255, 255));
		btnGo.setOpaque(true);
		btnGo.setContentAreaFilled(true);
		btnGo.setBounds(465, 564, 117, 26);
		frame.getContentPane().add(btnGo);
		
		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String response = client.disconnect("Disconnect;None");
				lblStatusField.setText(response.equals("Success")?"Disconnected":"Disconnect Failed");
				
				
			}
		});
		btnDisconnect.setBounds(640, 564, 117, 29);
		frame.getContentPane().add(btnDisconnect);
		
		JLabel lblStatus_1 = new JLabel("Status:");
		lblStatus_1.setBounds(813, 33, 43, 16);
		frame.getContentPane().add(lblStatus_1);
		
		
		frame.setBackground(Color.WHITE);
		frame.setBounds(100, 100, 967, 616);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}
