package humanainet.physicsmata.thread;
import humanainet.physicsmata.WeightsNodeTree;

/** Merges the top few layers after they were calculated in different threads.
This is very fast since its very few nodes, so it should be run single threaded.
Refreshes from minHeight up to root.
*/
public class RefreshB implements Runnable{
	
	public final WeightsNodeTree tree;
	
	public final int minHeight;
	
	public RefreshB(WeightsNodeTree tree, int minHeight){
		this.tree = tree;
		this.minHeight = minHeight;
	}
	
	public void run(){
		tree.refreshB(minHeight);
	}

}
