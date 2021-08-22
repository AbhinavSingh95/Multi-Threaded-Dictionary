package DictionaryServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.sql.Timestamp;
import javax.swing.JTextArea;

public class TCPInteractiveServer
{
	private int PORT_NUMBER;
    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Thread thread;
    private String dictionaryAddress;
    private String logAddress;
    private MessageList messageList;
    private ArrayList<Socket> clientList;
    private PrintWriter pw;
    private JTextArea outputArea;
    public TCPInteractiveServer(int portNumber,String dictionaryAddress,JTextArea outputArea)
    {
    	this.PORT_NUMBER = portNumber;
    	this.dictionaryAddress = dictionaryAddress;
    	this.logAddress = "serverLog.json";
    	this.outputArea = outputArea;
    	
    }
  
    	public void setUp()
        {
    		try
    		{
	        	messageList = new MessageList(dictionaryAddress);
	        	messageList.loadList();
	        	outputArea.append("/Dictionary Successfully Loaded....../"+"\n");
	        	writeLog();
	        	serverSocket = new ServerSocket(PORT_NUMBER);
	        	serverSocket.setReuseAddress(true);
	        	outputArea.append("/Server Started..../"+"\n");
	        	clientList = new ArrayList<Socket>();
	        	outputArea.append("/Awaiting connection....../"+"\n");
	            while (true) 
	            {
	            	Socket client = serverSocket.accept();
	            	Timestamp upTime = new Timestamp(System.currentTimeMillis());
	            	clientList.add(client);
	            	outputArea.append("New Client Added..."+ client.getRemoteSocketAddress()+"\n");
	                outputArea.append("Total Active Users:"+clientList.size()+"\n");
	                clientHandler = new ClientHandler(client,messageList,clientList,upTime,pw);
	                thread = new Thread(clientHandler);
	                thread.start();
	            }
        	
    		}
    		catch(Exception e)
    		{
    			outputArea.append("Server Down"+"\n");
    			outputArea.append(e.getStackTrace()+"\n");
    			finalize();
    			
    		}
    		
        }
   

    protected void finalize() //Closes server gracefully
    {
    	try
    	{
    		messageList.writeList();
            serverSocket.close();
            System.exit(0);
    	}
    	catch(FileNotFoundException e)
    	{
    		outputArea.append("File Write Failed:"+e.getMessage()+"\n");
    	}
        catch(IOException e)
    	{
        	outputArea.append("Unable to close server socket:"+e.getMessage()+"\n");
    	}
    }
    public void writeLog()
    {
    	FileOutputStream fos = null;
    	try
    	{
    		fos = new FileOutputStream(logAddress,true);
    	}
    	catch(FileNotFoundException e)
    	{
    		outputArea.append("Could not find Log File"+"\n");
    	}
		pw = new PrintWriter(fos);
    }
}
