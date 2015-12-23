package humanainet.physicsmata.thread;
import humanainet.physicsmata.WeightsNodeTree;
import humanainet.physicsmata.CellGrid;

/** TODO get this working before ManyThreadedCellGrid */
public class SingleThreadedCellGrid extends RunningCellGrid{
	
	public SingleThreadedCellGrid(CellGrid cellGrid){
		super(cellGrid);
	}
	
	public CellGrid cellGrid(){ return cellGrid; }
	
	public void refreshCustom(){
		cellGrid.refreshCustom();
	}
	
	public void refreshA(){
		for(WeightsNodeTree t : cellGrid.vectorDims){
			t.refreshAAndBSingleThreaded();
		}
	}
	
	/** Does nothing since refreshC in singlethreaded mode refreshes root and leaves nothing to merge */
	public void refreshB(){}
	
	public void refreshC(){
		cellGrid.refreshCInAllCellsSingleThreaded();
	}
	
	public void refreshD(){
		cellGrid.refreshDInAllCellsSingleThreaded();
	}

}
