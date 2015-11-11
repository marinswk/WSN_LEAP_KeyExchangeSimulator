import java.net.*;
import java.util.Random;
import java.io.*;

public class HardwareSensorNode {

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException{
		
		Random randomDistance = new Random();
		SensorNode client = new SensorNode ("10.10.10.1",9997,randomDistance.nextInt(100),"B");
        try {
            //trying to establish connection to the server
            client.Connect();
            client.ReadIndividualKey();
            client.SendPosition();
            while(true)
            	client.Read();
            
            
        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
        
	}
}
