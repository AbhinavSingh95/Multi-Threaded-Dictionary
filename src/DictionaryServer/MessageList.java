package DictionaryServer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageList
{
	private String dictionaryAddress;
	private Map<String, JSONArray> messageList=null;
	public MessageList(String dictionaryAddress)
	{
		 messageList = new HashMap<String, JSONArray>();
		 this.dictionaryAddress = dictionaryAddress;
	}
	public void loadList()
	{
		// This will reference one line at a time
	    String line = null;
	    JSONObject obj;
	 // FileReader reads text files in the default encoding.
        FileReader fileReader = null;
        try
        {
        	fileReader = new FileReader(dictionaryAddress);
        }
        catch(FileNotFoundException e)
        {
        	JOptionPane.showMessageDialog(null, "Dictionary Not Found\nCreating a new dictionary with same name","Error!!",JOptionPane.ERROR_MESSAGE);
        	try
        	{
        		PrintWriter pw = new PrintWriter(dictionaryAddress);
        		fileReader = new FileReader(dictionaryAddress);
        	}
        	catch(Exception ex)
        	{
        		JOptionPane.showMessageDialog(null, "New Dictionary couldn't be created","Error!!",JOptionPane.ERROR_MESSAGE);
        		System.exit(0);
        	}
        	
        	
        }

	    try {
	        

	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);

	        while((line = bufferedReader.readLine()) != null) 
	        {
	            obj = (JSONObject) new JSONParser().parse(line);
	            Object[] array=(obj.keySet()).toArray();//Word array
	            Object[] value=(obj.values()).toArray();// meaning array
	            messageList.put((String)array[0], (JSONArray)value[0]);
	        }
	        // Always close files.
	        bufferedReader.close();
	        fileReader.close();
	        PrintWriter writer = new PrintWriter(dictionaryAddress);
	        writer.print("");//Clearing the contents of the file
	        writer.close();//Closing the print writer
	        
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println("Unable to open file '" + dictionaryAddress + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println("Error reading file '" + dictionaryAddress + "'");                  
	        // Or we could just do this: 
	        // ex.printStackTrace();
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}// End of loadList method
	public void writeList()throws FileNotFoundException//Writes the content of the hash map onto the dictionary file
	{
		FileOutputStream fos = new FileOutputStream(dictionaryAddress,true);
		PrintWriter pw = new PrintWriter(fos);
		for(Map.Entry<String,JSONArray> entry : messageList.entrySet())
		{
			JSONObject jo = new JSONObject();
			jo.put(entry.getKey(),entry.getValue());
			pw.write(jo.toJSONString()+"\n"); 
	        pw.flush();
		}
		pw.close();
	}
	
	public synchronized JSONArray search(String word)
	{
		return(messageList.get(word));//Return null when mapped key is not found and return JSON array when found
	}
	public synchronized String add(String word,JSONArray meaning)
	{
		System.out.print(word+meaning.toString());
		if(messageList.get(word)!=null)
		{
			return("Word already exists in the Dictionary");
		}
		else
		{
			messageList.put(word, meaning);
			System.out.println(messageList.get(word));
			return("Word Successfully Added");
		}
		
	}
	public synchronized String delete(String word)
	{
		if(messageList.remove(word)!=null)
		{
			return("Successfully Deleted");
		}
		else
		{
			return("Deletion Failed- Given Word doesnt exist in the dictionary");
		}
	}
	

}
