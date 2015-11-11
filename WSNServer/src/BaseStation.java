
public class BaseStation {

	public static void main(String[] args) throws InterruptedException{
		Controller c1;
		
		c1=new Controller(1,"A");
		
		c1.StartServer();
	}
}
