package DictionaryServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class TCPInteractiveClient {
	private Socket socket = null;
	private BufferedReader in=null;
	private BufferedWriter out=null;
	private String serverAddress;
	//TCPInteractiveClient client=null;
	int portNumber;
	public TCPInteractiveClient(int portNumber,String serverAddress)
	{
		this.portNumber = portNumber;
		this.serverAddress = serverAddress;
	}
	public String setUp()
	{
		try
		{
			// Create a stream socket bounded to any port and connect it to the
			// socket bound to localhost on port 4444
			socket = new Socket(serverAddress, portNumber);//Local host of connecting to server locally
			// Get the input/output streams for reading/writing data from/to the socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			return("Connected to server......");
		}
		
		catch (UnknownHostException e)
		{
			return("Unknown Host Exception:"+e.getMessage());
		}
		catch (IOException e)
		{
			return("Server down:"+e.getMessage());
		}
		
	}
	
	public String searchWord(String word)
	{
		String messageReceived=null;
		
		try
		{
			out.write(word+"\n");
			out.flush();
			messageReceived = in.readLine();
		}
		catch(IOException e)
		{
			return("Operation Failed due to I/O exception");
		}
		return(messageReceived);
		
	}
	public String addWord(String word)
	{
		String messageReceived=null;
		try
		{
			out.write(word+"\n");
			out.flush();
			messageReceived = in.readLine();
		}
		catch(IOException e)
		{
			return("Operation Failed due to I/O exception");
		}
		return(messageReceived);
	}
	public String deleteWord(String word)
	{
		String messageReceived=null;
		try
		{
			out.write(word+"\n");
			out.flush();
			messageReceived = in.readLine();
		}
		catch(IOException e)
		{
			return("Operation Failed due to I/O exception");
		}
		return(messageReceived);
	}
	public String disconnect(String input)
	{
		try
		{
			out.write(input+"\n");
			out.flush();
		}
		catch(IOException e)
		{
			return("Disconnection Failed;Exit");
		}
		return("Success");
	}
	public boolean isAlpha(String word)
	{
		return word!=null && word.chars().allMatch(Character::isLetter);
	}
	

}//End of class


