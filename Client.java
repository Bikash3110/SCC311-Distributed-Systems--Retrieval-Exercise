import java.io.*;
import java.rmi.*;
import java.util.*;
import javax.crypto.*;


/*
* Clients class sends request to the CW_server  
* get response about the coursework spec
*
*/
public class Client implements Serializable {
	final static String host = "scc311-server.lancs.ac.uk";
    final static String server = "CW_server";
    final static int User_ID = 34363718;
	final static int passcode = 355817534;
	final static String Key = "34363718.key";
	final static String File = "Coursework_spec.doc";
	
	public static void main(String[] args){
	
     Client_request request = new Client_request(User_ID, Nonse());  
	 CW_server_interface serv; 
	 Server_response response;	
	   
	   try{
		 // Inspect the RMI registry in the host 
		 serv = (CW_server_interface) Naming.lookup("rmi://"+ host + "/" + server);     
		 System.out.println("Server Connected \n");
        
		// ENCRYPTION  
	     System.out.println("Encryphering...\n");
	   
	     /*Reads the file and convertsto an object 
		 * Furthermore, converts that boject into secretkey
		 */
		 Object key = readFile(Key);
		 SecretKey skey = (SecretKey) key;
		   
         /* Cipher to data Encryption standard		   
		 * and Initiate Cipher ENCRYPT_MODE, Secret Key
		 */
		 Cipher cipher = Cipher.getInstance("DES");
		 cipher.init(Cipher.ENCRYPT_MODE, skey);
		 
         	 
		 SealedObject obj = new SealedObject(request, cipher);
		 obj = serv.getSpec(User_ID, obj);
		  
         // Get response from server 		  
		 response = (Server_response) obj.getObject(skey);
		 System.out.println("Status: 0. Failed, 1. Password Authentication , 2. key based authentication \n");  
	     System.out.println("Status ---> " + serv.getStatus(34363718) + "\n");  
		 
         // Get the CW from the CW_server in .doc file		 
		 writeFile(File, response);
		 System.out.println("COURSEWORK RECIEVED !!!");
	   }catch(Exception e){
		  System.out.println("Failed Attempt ...");
		  e.printStackTrace();
		  return;
	   }
	   
	}
    
    /* 
	* Generate a random number using random nuumber generator
    */		   
	public static int Nonse(){
		Random rand = new Random(99999);
		return rand.nextInt();
	}
	
	/* This method read file 
	*  @param file - file need to be read
	*/
	public static Object readFile(String file){
		
		try{
			FileInputStream f = new FileInputStream(file);
			ObjectInputStream o = new ObjectInputStream(f);
			Object obj = (Object) o.readObject();
			o.close();
			return obj;
		}catch(Exception e){
			System.out.println("Reading Faliure...");
		}
		return null;
	}
	
	/* This method creates and write file.doc
	*  @param file - New file name that will be created
	*  @param s - Server Response 
	*/
	public static void writeFile(String file, Server_response s){
		File f = new File(file);
		try{
			OutputStream stream = new FileOutputStream(f);
			s.write_to(stream);
			stream.close();
		}catch(Exception e)
		{
			System.out.println("Writing Failed...");
		}
		
	}
}