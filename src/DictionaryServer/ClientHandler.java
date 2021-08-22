package DictionaryServer;
import java.lang.Runnable;
import java.util.ArrayList;
import java.net.Socket;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.Timestamp;

public class ClientHandler implements Runnable
{
	  private Socket clientSocket;
	  private MessageList messageList;
	  private BufferedReader in;
	  private BufferedWriter out;
	  private ArrayList<Socket> clientList;
	  private Timestamp upTime;
	  private PrintWriter pw;
	  
	  ClientHandler(Socket clientSocket,MessageList messageList,ArrayList<Socket> clientList,Timestamp upTime,PrintWriter pw)
	  {
	    this.clientSocket = clientSocket;
	    this.messageList = messageList;
	    this.clientList = clientList;
	    this.upTime = upTime;
	    this.pw = pw;
	  }
	  public void run()
	  {
		  synchronized(pw)
		  {
			  pw.write(clientSocket.getRemoteSocketAddress().toString()+"/Started/Up Time:"+upTime+"\n");
			  pw.flush(); 
		  }
		  
		  String clientMsg;
			  try
			  {
				  in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				  out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
				  while((clientMsg = in.readLine()) != null) 
					{
					  	JSONObject transactionLog = new JSONObject();
					  	StringTokenizer st = new StringTokenizer(clientMsg,";");
					  	String functionName = st.nextToken();
					  	if(functionName.equals("Search"))
					  	{
					  		JSONArray meaning;
					  		String word;
					  		String response;
					  		synchronized(messageList)
					  		{
					  			word = st.nextToken();
					  			meaning = messageList.search(word);
					  		}
					  		if(meaning != null)
					  		{
					  			out.write(meaning.toString()+"\n");
					  			response = meaning.toString();
						  		out.flush();
					  		}
					  		else
					  		{
					  			response = "No word found";
					  			out.write("No such word found in the dictionary"+"\n");
						  		out.flush();
					  		}
					  		updateLog(functionName,word,response,transactionLog);
					  		synchronized(pw)
					  		{
					  			pw.write(clientSocket.getRemoteSocketAddress().toString()+"/"+transactionLog.toString()+"\n");
						  		pw.flush();
					  		}
					  	}
					  	if(functionName.equals("Add"))
					  	{
					  		System.out.println("In Add fucntion");
					  		String word = st.nextToken();
					  		JSONArray meaning = new JSONArray();
					  		String response;
					  		while(st.hasMoreTokens())
					  		{
					  			meaning.add(st.nextToken());
					  		}
					  		synchronized(messageList)
					  		{
					  			response = messageList.add(word,meaning);
					  		}
					  		out.write(response + "\n");
					  		out.flush();
					  		updateLog(functionName,word,response,transactionLog);
					  		synchronized(pw)
					  		{
					  			pw.write(clientSocket.getRemoteSocketAddress().toString()+"/"+transactionLog.toString()+"\n");
						  		pw.flush();
					  		}
					  		
					  		
					  	}
					  	if(functionName.equals("Delete"))
					  	{
					  		String word;
					  		String response;
					  		synchronized(messageList)
					  		{
					  			word = st.nextToken();
					  			response = messageList.delete(word);
					  			out.write(response+"\n");
					  		}
					  		out.flush();
					  		updateLog(functionName,word,response,transactionLog);
					  		System.out.println(clientSocket.getRemoteSocketAddress().toString()+"/"+transactionLog.toString());
					  	}
					  	if(functionName.equals("Disconnect"))
					  	{
					  		updateLog("Disconnect","Disconnect client","Success",transactionLog);
					  		synchronized(pw)
					  		{
					  			pw.write(clientSocket.getRemoteSocketAddress().toString()+"/"+transactionLog.toString()+"\n");
						  		pw.flush();
					  		}
					  		break;
					  	}
					}//End of while loop
				  clientSocket.close();
				  Timestamp downTime = new Timestamp(System.currentTimeMillis());
				  synchronized(pw)
				  {
					  pw.write(clientSocket.getRemoteSocketAddress().toString()+"/Closed/"+"DownTime:"+downTime+"\n");
					  pw.flush(); 
				  }
				 
				  synchronized(clientList)
				  {
					  clientList.remove(clientSocket);  
				  }
				  
			  }// End of try block
			  catch(IOException e)
			  {
				  System.out.println("Error in client:" +clientSocket.getRemoteSocketAddress());
			  }
		  }//End of Run method
	  public synchronized void updateLog(String functionName,String request,String response,JSONObject obj) 
	  {
		  String line = "Function Name:"+functionName+" Request:"+request+" Response:"+response;
		  obj.put("Transaction",line);
		  
	  }
}
