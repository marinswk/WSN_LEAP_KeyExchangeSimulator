import java.io.*;
import java.net.*;
import java.util.Random;

public class SensorNode {

	private String individualKey;
	private String groupKey;
	private int individualHalfPairwiseKey='P';
	private String[] pairwiseKey=new String[100];
	private int[] clusterKey=new int[100];
	

	private Random randomKey = new Random();
	private int randomClusterKey=randomKey.nextInt(10000);
	
	private int[] vettoreDis= new int[100];
	
	private String tmp;
	private int integerPairKey;
	private int IntegerIndividualHalfPairwiseKey;
	private int id;
	private int posizione;
	BufferedWriter writer = null;
	Socket clientSocket=null;
	BufferedWriter out=null;
	BufferedReader in=null;
	private String hostname;
	private int port;
	
	public SensorNode(String hostname,int port,int posizione,String groupKey){
		this.hostname=hostname;
		this.port=port;	
		this.posizione=posizione;
		this.groupKey=groupKey;
	}
	
	//metodo usato dal client per la connessione al server
	public void Connect() throws UnknownHostException, IOException{
		System.out.println("-Attempting to connect to "+hostname+":"+port);
        clientSocket = new Socket(hostname,port);
        System.out.println("-Connection Established\n");
        System.out.println("-The group key of the WSN is : "+groupKey);
        writer= new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	}
	
	//il client appena connesso attende l'invio della Individual Key da parte della base station
	public void ReadIndividualKey() throws IOException{
		in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		
		while (!(tmp = in.readLine()).equals("finish")) {
			   individualKey=tmp;	
	           System.out.println("-The Individual Key shared with the Sink node is:" +individualKey);   
	       }
	}
	
	//appena il client riceve l'individual Key dal server invia la propria posizione iniziando il 
	//processo di discovery dei nodi vicini
	public void SendPosition() throws IOException{
		 
		writer.write("HELLO\n");
		writer.flush();
		writer.write(posizione+"\n");
		System.out.println("-Position of the current sensor : "+posizione);
		writer.flush();
	}
	
	//il client entra in un ciclo infinito di lettura dallo stream
	public void Read() throws IOException, ClassNotFoundException, InterruptedException{
		String tmp1;
		int pairId;

		in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		
		
		while (true){
			int c;
			tmp1 = in.readLine();
			
			if(tmp1.equals("ACK")){
				
				id=Integer.parseInt(in.readLine());
				integerPairKey=Integer.parseInt(in.readLine());
				pairId=Integer.parseInt(in.readLine());
				pairwiseKey[pairId]=(id+integerPairKey+IntegerIndividualHalfPairwiseKey)*3478+"";
				System.out.println("-Pairwise Key shared with node "+pairId+" : "+pairwiseKey[pairId]);
				
				for(c=0;c<100;c++){
					vettoreDis[c]=Integer.parseInt(in.readLine());
					}
				
				for(c=0;c<100;c++){
					if(vettoreDis[c]!=0){
						System.out.println("-Distance from sensor node "+c+" : "+vettoreDis[c]);
						if(pairwiseKey[c]!=null){
							SendClusterKey(pairId);
						}
					}
				}
			}
			
			if(tmp1.equals("CLUSTERINGREAD")){
				c=Integer.parseInt(in.readLine());
				pairId=Integer.parseInt(in.readLine());
				
				clusterKey[pairId]= c - (Integer.parseInt(pairwiseKey[pairId]));
				System.out.println("-Cluster Key shared with node "+pairId+" :"+clusterKey[pairId]);
				break;
				
			}
		}
			
	}
	
	
	private void SendClusterKey(int c) throws IOException, InterruptedException{
		int tmp;
		
		//if(clusterKey[c]==0){
			tmp=randomClusterKey+Integer.parseInt(pairwiseKey[c]);
			writer.write("CLUSTERING\n");
			writer.flush();
			writer.write(tmp+"\n");
			writer.flush();
			writer.write(c+"\n");
			writer.flush();
		//}
		}
	}

