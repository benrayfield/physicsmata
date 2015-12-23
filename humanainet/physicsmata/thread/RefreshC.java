package humanainet.physicsmata.thread;
import humanainet.physicsmata.Cell;

/** refresh a specific branch, from leafs up to the specific height and y and x at that height.
Array indexs are half as many each next height up.
*/
public class RefreshC implements Runnable{
	
	public final Cell cell;
	
	public RefreshC(Cell c){
		cell = c;
	}
	
	public void run(){
		cell.refreshC();
	}

}