package humanainet.physicsmata.thread;
import humanainet.physicsmata.WeightsNodeTree;

public class RefreshA implements Runnable{
	
	public final WeightsNodeTree tree;
	
	public final int height, yAtHeight, xAtHeight;
	
	/** height is array index in tree of branch as if it was a root in this thread */
	public RefreshA(WeightsNodeTree tree, int height, int yAtHeight, int xAtHeight){
		this.tree = tree;
		this.height = height;
		this.yAtHeight = yAtHeight;
		this.xAtHeight = xAtHeight;
	}
	
	public void run(){
		tree.refreshABranch(height, yAtHeight, xAtHeight);
	}

}