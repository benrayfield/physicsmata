package humanainet.physicsmata;

import java.util.Random;

import humanaicore.common.CoreUtil;
import humanaicore.common.NumberUtil;
import humanaicore.weightsnode.WeightsNode;
import humanainet.physicsmata.thread.ManyThreadedCellGrid;
import humanainet.physicsmata.thread.RunningCellGrid;
import humanainet.physicsmata.thread.SingleThreadedCellGrid;

/** Similar to FlatPYX, except layers align in a partially incompatible way.
They can be merged only at visibleNodes/leaf layer of BellscalarNode which can
fit in a FlatPYX if FlatPYX is changed to more generally
hold WeightsNode or SparseNode instead of specificly Neuron.
<br><br>
Each gridSquare has a vector which is updated each cycle based on rings
of summed vectors in area of ring of various radius range around each point.
A Cell holds that vector and func which receives double[][] array of summed
vectors at each ring.
<br><br>
CellGrid holds those Cells which are visibleNodes and a BellscalarTree
for each dim in those vectors. For example, if the vectors are complexnum,
that is 2 dim, and there would be 2 BellscalarTree which sum the
real and imaginary parts of those complexnum in those rings.
<br><br>
Normally its 1 dim (which is fastest and still very flexible
to create custom wave shapes), but its there for expansion ability.
<br><br>
As a Runnable, CellGrid uses its RunningCellGrid, which may have many threads
which sync on each step which is parallelizable within the step,
therefore as a Runnable this should be run in a high priority thread,
while the threads it calls would be normal priority or anything less than this.
<br><br>
Starts as singlethreaded, until you call a startUsing*ThreadsWhenRun func,
and after that every run() uses that many threads.
*/
public class CellGrid implements Runnable{
	
	public final int treeHeight, rings;
	
	public final Cell cell[][];
	
	/** cells wide in x and y, the cell[][] array */
	public final int squareSide;
	
	/** 1 for each vectorDim */
	public final WeightsNodeTree vectorDims[];
	
	protected RunningCellGrid runningCellGrid;
	
	/** Starts false. If true, in refreshCustom, runs normBySortedPointers(each vectorDim) */
	public boolean normBySortedPointers;
	
	/** Number of visibleNode cells are 4^treeHeight, a square of 2^treeHeight per side.
	<br><br>
	Creates Cells with empty rings (no child pointers), to be added later.
	<br><br>
	Theres many possible ways to create rings, balancing between size, quantity, accuracy,
	and rings of constant density vs interpolation of slightly different density.
	*/
	public CellGrid(int treeHeight, int vectorDims, int rings, NsphereVecFunc defaultFunc){
		this.treeHeight = treeHeight;
		this.rings = rings;
		squareSide = 1<<treeHeight;
		cell = new Cell[squareSide][squareSide];
		for(int y=0; y<squareSide; y++){
			for(int x=0; x<squareSide; x++){
				cell[y][x] = new Cell(vectorDims, rings, defaultFunc);
			}
		}
		this.vectorDims = new WeightsNodeTree[vectorDims];
		for(int v=0; v<vectorDims; v++){
			this.vectorDims[v] = new WeightsNodeTree(treeHeight);
		}
		//connect each Cell outNodes[v in vectorDims] to a tree leaf
		for(int y=0; y<squareSide; y++){
			for(int x=0; x<squareSide; x++){
				Cell c = cell[y][x];
				for(int v=0; v<vectorDims; v++){
					c.outNodes[v] = this.vectorDims[v].getLeaf(y,x);
				}
			}
		}
		startBeingSingleThreadedWhenRun();
	}
	
	public void refreshCustom(){
		if(normBySortedPointers){
			//TODO 1 thread per vectorDim, but usually its only 1 vectorDim, and grid is small enough
			//that this will return very fast
			for(int v=0; v<vectorDims.length; v++){
				normBySortedPointers(v);
			}
		}
	}
	
	public RunningCellGrid runningCellGrid(){ return runningCellGrid; }
	
	/** runningThis must be a RunningCellGrid whose cellGrid() returns this.
	Starts using the new RunningCellGrid */
	public void setRunningCellGrid(RunningCellGrid runningThis){ runningCellGrid = runningThis; }
	
	public void refreshCInAllCellsSingleThreaded(){
		//for(Cell cc[] : cell){
		//	for(Cell c : cc){
		for(int y=0; y<squareSide; y++){
			for(int x=0; x<squareSide; x++){
				Cell c = cell[y][x];
				c.refreshC();
			}
		}
	}
	
	public void refreshDInAllCellsSingleThreaded(){
		for(Cell cc[] : cell){
			for(Cell c : cc){
				c.refreshD();
			}
		}
	}
	
	public void run(){
		runningCellGrid.run();
	}
	
	/** Unlike startUsing4ThreadsWhenRun and startUsing64ThreadsWhenRun, uses a SingleThreadedCellGrid */
	public void startBeingSingleThreadedWhenRun(){
		setRunningCellGrid(new SingleThreadedCellGrid(this));
	}
	
	/** Like startUsing16ThreadsWhenRun except branches 1 level higher. 16 threads should be default. */
	public void startUsing4ThreadsWhenRun(){
		throw new RuntimeException("Use "+ManyThreadedCellGrid.class+" when its working");
	}
	
	/** Branches BellscalarTree at third highest level. Each next level lower level
	has 4 times as many branches which can have their own thread. 16 threads should be default.
	*/
	public void startUsing16ThreadsWhenRun(){
		throw new RuntimeException("Use "+ManyThreadedCellGrid.class+" when its working");
	}
	
	/** Like startUsing16ThreadsWhenRun except branches 1 level lower. 16 threads should be default. */
	public void startUsing64ThreadsWhenRun(){
		throw new RuntimeException("Use "+ManyThreadedCellGrid.class+" when its working");
	}
	
	/** TODO gpus, apus, stream processors, grid processors, etc? */
	public void startUsing256ThreadsWhenRun(){
		throw new RuntimeException("Use "+ManyThreadedCellGrid.class+" when its working");
	}
	
	/** The BellscalarNode.weightedSumOrInput vars are each randomized between -1 and 1 */
	public void randomizeCellOutScalars(Random rand){
		for(WeightsNodeTree tree : vectorDims){
			for(int y=0; y<squareSide; y++){
				for(int x=0; x<squareSide; x++){
					tree.getLeaf(y,x).position = rand.nextDouble()*2-1;
				}
			}
		}
		/* THIS IS WRONG BECAUSE LEAFS ARE IN TREES, NOT RINGS.
		for(Cell cc[] : cell){
			for(Cell c : cc){
				for(int v=0; v<vectorDims.length; v++){
					c.out[v] = rand.nextDouble()*2-1;
				}
				c.refreshB(); //sets c.outNodes[v in vectorDims.length].weightedSumOrInput
			}
		}*/
	}
	
	/** Sorts all BellscalarNode.weightedSumOrInput at that vectorDim then normalizes them by percentile into range -1 to 1 */
	public void normBySortedPointers(int vectorDim){
		WeightsNodeTree tree = vectorDims[vectorDim];
		//TODO reuse same array?
		double d[] = new double[squareSide*squareSide];
		int offset = 0;
		for(int y=0; y<squareSide; y++){
			final WeightsNode nodesHY[] = tree.nodes[0][y];
			for(int x=0; x<squareSide; x++){
				d[offset++] = nodesHY[x].position;
			}
		}
		NumberUtil.normBySortedPointers(-1, 1, d);
		offset = 0;
		for(int y=0; y<squareSide; y++){
			final WeightsNode nodesHY[] = tree.nodes[0][y];
			for(int x=0; x<squareSide; x++){
				nodesHY[x].position = d[offset++];
				//nodesHY[x].weightedSumOrInput = 1;//CoreUtil.weakRand.nextDouble();
			}
		}
	}
	
	/** more advanced uses will set different cells funcs separately,
	maybe even have different size circles/nspheres per cell
	which the datastructs already efficiently support.
	*/
	public void setAllCellsFuncTo(NsphereVecFunc func){
		for(int y=0; y<squareSide; y++){
			for(int x=0; x<squareSide; x++){
				Cell c = cell[y][x];
				c.func = func;
			}
		}
	}

}