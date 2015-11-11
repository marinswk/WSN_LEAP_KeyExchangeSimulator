import java.io.*;
import java.net.*;


public class Controller {

	private String masterKey;
	
	private int posizione;
	private ServerSocket server=null;
	private Socket clientSocket=null;
	private int id=0;
	private int counter;
	
	private SocketClientHandler[] clientList= new SocketClientHandler[100]; 
	
	//costruttore del controller
	public Controller(int pos,String key){
		this.posizione=pos;
		this.masterKey=key;
	};
	
	//metodo per far partire il controllore
	public void StartServer() throws InterruptedException{
		try{
			//apre un socket server
			System.out.println("-Starting the socket server at port: 9997");
			server=new ServerSocket(9997);
			
			while(true){
				
				System.out.println("Waiting for clients...");
				clientSocket=server.accept(); //creo un oggetto socket per ascoltare e accettare le richieste
				
				//un client si connette
				//viene creato un nuovo thread per la gestione del client
				System.out.println("-The following client has connected:"+clientSocket.getInetAddress().getCanonicalHostName()+"\n");
				clientList[id]=new SocketClientHandler(clientSocket,id,masterKey);
				Thread thread = new Thread(clientList[id]);
	            thread.start();
	            
	            
	            try {
	                Thread.sleep(2000);                 //1000 milliseconds is one second.
	            } catch(InterruptedException ex) {
	                Thread.currentThread().interrupt();
	            }
	            
	            //aggiorno la lista dei vettori delle distanze di tutti i client
	            
	            for(counter=0;counter<100;counter++){
	            	if(clientList[counter]!=null){
	            		clientList[counter].SetPositionVector(CalcolaDistanza(counter));
	            		clientList[counter].SendPairwiseKey();
	            		Thread.sleep(2000);
	            		for(int i=0;i<100;i++){
	            				clientList[counter].SendClusterKey(i); 
	            		}
	            	}
	            	
	            }
	            
	            id++;
	            
			}
		}catch(IOException e){
			System.out.println(e);
		}
		
	}
	
	//metodo per il calcolo del vettore delle distanze di ogni nodo da ogni altro nodo della rete
	private int[] CalcolaDistanza(int id){
		int[] vettoreDis= new int[100];
	    int c;
	    
		for(c=0;c<100;c++){
			if(c==id||clientList[c]==null){
				vettoreDis[c]=0;
			}
			else {vettoreDis[c]=Math.abs(clientList[id].GetPosizione()-clientList[c].GetPosizione());
			System.out.println("-Sensor node: "+id+", distance from sensor node "+c+" is: "+vettoreDis[c]);}
		}
		return vettoreDis;
		}
	
	
		
}


