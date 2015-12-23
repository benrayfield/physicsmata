package humanainet.physicsmata.thread;
import humanainet.physicsmata.Cell;

public class RefreshD implements Runnable{
	
	public final Cell cell;
	
	public RefreshD(Cell c){
		cell = c;
	}
	
	public void run(){
		cell.refreshD();
	}

}
