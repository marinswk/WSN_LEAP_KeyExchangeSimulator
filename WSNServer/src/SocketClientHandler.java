import java.io.*;
import java.net.*;

public class SocketClientHandler implements Runnable {

	private String masterKey;
	private String individualKey;
	private int individualHalfPairwiseKey='P';
	private Socket clientSocket;
	private int id;
	private int posizione=0;
	private int[] vettoreDis= new int[100];
	private int tmp;
	private int[] clusterKey=new int[100];
	
	
	public SocketClientHandler(Socket client, int id,String Mkey) {
		this.clientSocket = client;
		this.masterKey=Mkey;
		this.id=id;
	  }
	  
	  
	@Override
	public void run() {
		try {
			
			System.out.println("-Thread started with name: WSN Sensor "+id);
			SendIndividualKey();
			//aggiorna il vettore delle chiavi ai nodi non appena connessi che si trovano entro una certa distanza dal nodo  
			
			
		}catch (InterruptedException e) {
		         e.printStackTrace();
		       } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//metodo per l'invio della individual key, chiave unica tra base station e nodo 
	private void SendIndividualKey() throws IOException, InterruptedException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		individualKey=masterKey+(Integer.toString(id));
		System.out.println("-Individual Key of the Base Station shared with node "+id+" is : "+individualKey);
		writer.write(individualKey+"\n");
		writer.write("finish\n");
		writer.flush();
		
		//viene richiamato questo metodo per mettersi in attesa di risposta dal nodo
		//la risposta sarà la posizione corrente del nodo
		ReadResponse();
	}
	
	//metodo per la lettura della posizione del nodo 
	private void ReadResponse() throws IOException, InterruptedException {
		String clientInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		int clusterId;
		int tmpKey;
		
		
		while (true) {
			
			clientInput = stdIn.readLine();
			//legge la comunicazione della posizione
			if(clientInput.equals("HELLO")){
				clientInput=stdIn.readLine();  
				posizione=Integer.parseInt(clientInput);
				System.out.println("-Position of the sensor node "+id+": "+posizione);
				break;
			}
			
			if(clientInput.equals("CLUSTERING")){
				tmpKey=Integer.parseInt(stdIn.readLine());
				clusterId=Integer.parseInt(stdIn.readLine());
				clusterKey[clusterId]=tmpKey;
				System.out.println("-Sensor "+id+" encrypted Cluster Key, forwarding to sensor "+clusterId+" : "+tmpKey);
				break;
			}
		}
		
		}
	
	//metodo che permette al server (che simula la connessione wireless dei sensori) di prendere la pozione del nodo
	//collegato a questo thread
	public int GetPosizione(){
		return posizione;
	}
	
	//metodo che permette al server di aggiornare il vettore delle distanze di questo thread a tutti i nodi della rete
	public void SetPositionVector(int[] vettorePos){
		this.vettoreDis=vettorePos;
	}
	
	//metodo per l'invio della chiave condivisa nel caso in cui due nodi siano entro un certo range 
	//crea una pairwise key e invia al nodo associato a questo thread la chiave del nodo con cui deve associarsi
	public void SendPairwiseKey() throws IOException, InterruptedException{
		int c,i;
		
		for(c=0;c<100;c++){
			if(vettoreDis[c]!=0 && vettoreDis[c]<200){
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				
				
				tmp=individualHalfPairwiseKey+c;
				
				//mandare ACK nel writer per capire nel nodo che sta arrivando una pairwise key
				writer.write("ACK\n");
				writer.flush();
				writer.write(id+"\n");
				writer.flush();
				writer.write(tmp+"\n");
				writer.flush();
				writer.write(c+"\n");
				writer.flush();
		
				for(i=0;i<100;i++){
					writer.write(vettoreDis[i]+"\n");
					writer.flush();
				}
				
				ReadResponse();
				
			}
			
		}
		
	}
	
	public void SendClusterKey(int nodeId) throws IOException, InterruptedException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		
		
			if(clusterKey[nodeId]!=0){
		
					writer.write("CLUSTERINGREAD\n");
					writer.flush();
					writer.write(clusterKey[nodeId]+"\n");
					writer.flush();
					writer.write(nodeId+"\n");
					writer.flush();


					Thread.sleep(2000);
					
			}
			
		
	}
	

}
