package humanainet.physicsmata.thread;

import humanainet.physicsmata.WeightsNodeTree;
import humanainet.physicsmata.CellGrid;

/** TODO contains and syncs threads running RefreshA, RefreshB, RefreshC, and RefreshD,
for a CellGrid. There can be many of each of those, except RefreshD which finishes
near instantly and doesnt need to be multithreaded.
*/
public abstract class RunningCellGrid implements Runnable{
	
	public final CellGrid cellGrid;
	
	public RunningCellGrid(CellGrid cellGrid){
		this.cellGrid = cellGrid;
	}
	
	/** Runs 1 cycle of RefreshA, RefreshB, RefreshC, and RefreshD */
	public void run(){
		refreshA();
		refreshB();
		refreshC();
		refreshD();
		refreshCustom();
	}
	
	
	/** Anything done to the leafs at the start or end (TODO which?) of each cycle, especially normalizing.
	May be single or multithreaded but if you dont know what its doing it would have to be single,
	as is a cost of custom code that may be anything in subclasses of CellGrid,
	but the common uses of normBySortedPointers could be multithreaded since its main cost is sorting. 
	*/
	public abstract void refreshCustom();
	
	/** refreshA sums BellscalarNode.weightedSumOrInput starting from leafs up to some branch.
	Starts all RefreshA threads, or gets from pool, and waits for them all to finish.
	*/
	public abstract void refreshA();
	
	/** refreshB sums from where refreshA threads ended up to tree root.
	Runs the RefreshB and waits for it to finish, which is near instantly so single threaded.
	*/
	public abstract void refreshB();
	
	/** refreshC updates BellscalarNode.weightedSumOrInput in rings and stores it in Cell.out[].
	Starts all RefreshC threads, or gets from pool, and waits for them all to finish.
	*/
	public abstract void refreshC();
	
	/** refreshD copies scalars from Cell.out[] to leaf nodes BellscalarNode.weightedSumOrInput,
	which is used in the next refreshA as larger squares higher in the tree are updated.
	Starts all RefreshD threads, or gets from pool, and waits for them all to finish.
	*/	
	public abstract void refreshD();
	
}
