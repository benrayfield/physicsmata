package humanainet.physicsmata.thread;

import humanainet.physicsmata.CellGrid;

/** TODO get this working before ManyThreadedCellGrid.
<br><br>
TODO While multithreaded, the threads should be permanently sleeping
until interrupted and start them doing work, and when finished they go back to sleep.  
*/
public class ManyThreadedCellGrid extends RunningCellGrid{
	
	/** TODO more params for how many threads, what height to split trees, etc */
	public ManyThreadedCellGrid(CellGrid cellGrid){
		super(cellGrid);
	}
	
	public CellGrid cellGrid(){ return cellGrid; }
	
	/** singlethreaded */
	public void refreshCustom(){
		cellGrid.refreshCustom();
	}
	
	public void refreshA(){
		throw new RuntimeException("TODO");
	}
	
	public void refreshB(){
		throw new RuntimeException("TODO");
	}
	
	public void refreshC(){
		throw new RuntimeException("TODO");
	}
	
	public void refreshD(){
		throw new RuntimeException("TODO");
	}

}
