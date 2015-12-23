package humanainet.physicsmata;
import humanaicore.alloc.RootAllocJ;
import humanaicore.weightsnode.WeightsNode;


/** Uses BellscalarNodes in sum mode instead of ave mode,
which means refresh() divides weightedSumOrInput by spaceWeighted
(those 2 vars in BellscalarNode) instead of using weightedSumOrInput directly.
*/
public class Cell{
	
	//TODO use CellUtil.circleSum (either when receive a RingVecFunc or cached per radius and at runtime) instead of CellUtil.fillRings.

	/** Normally changes many times while the (default dimensions) 2d grid runs,
	potentially many times per second
	*/
	public NsphereVecFunc func;
	
	public final int vectorDims, rings;
	
	/** Indexs aligned to vector[].
	Root node of each ring in forest,
	or call it a network with cycles as connected through these Cell nodes.
	*/
	public final WeightsNode inNodes[][];
	
	/** in[vectorDims][rings].
	Updated from scalars in rings[][] which goes into RingVecFunc which writes to out[]
	*/
	public final double in[][];
	
	/** Normally the constant max dims per bellAutomata across the 2d grid. Different BellAutomata
	may use less, but if more is needed, rebuild all the Cell objects to that new max.
	<br><br>
	If the automata use complexnum, this would be 2 dims for each of those.
	*/
	public final double out[];
	
	public final WeightsNode outNodes[];
	
	/** for refreshB() to do anything, caller must once fill outNodes[] */
	public Cell(WeightsNode inNodes[][], NsphereVecFunc firstFunc){
		this.inNodes = inNodes;
		out = new double[vectorDims = inNodes.length];
		outNodes = new WeightsNode[vectorDims];
		rings = inNodes[0].length;
		in = new double[vectorDims][rings];
		func = firstFunc;
	}
	
	/** Starts with each ring having no childs (empty), to be filled in later */
	public Cell(int vectorDims, int rings, NsphereVecFunc firstFunc){
		this(newEmptyNodes(vectorDims, rings), firstFunc);
	}
	
	public String debugText(){
		String n = "\r\n";
		String s = "START CELL="+this;
		for(int i=0; i<inNodes[0].length; i++){
			WeightsNode w = inNodes[0][i];
			double weightSum = 0;
			for(int j=0; j<w.size; j++){
				weightSum += w.weightFrom[j];
			}
			s += n+"ring "+i+" ("+w.size+" childs) sum weights is "+weightSum;
		}
		s += n+"END CELL.";
		return s;
	}
	
	/** Updates in[][] from scalars in rings[][] then calls RingVecFunc which writes to out[].
	<br><br>
	TODO After calling this, somehow the contents of out[] must be copied
	to BellscalarNode.weightedSumOrInput of all leaf BellscalarNode at this cell,
	but all at once as the CellGrid updates leaf nodes in sync.
	Should that be done here or caller does it?
	<br><br>
	TODO update comments because this refresh() func was renamed to run() as in Runnable.
	<br><br>
	All relevant cells refreshA() should be called, then those same cells refreshB(),
	then all trees refreshC to copy up to larger squares.
	RefreshA calculates scalars, and RefreshB copies those to leaf nodes.
	TODO This should be multithreaded, while all threads must finish each step
	before any move on to the next.
	*/
	public void refreshC(){
		for(int v=0; v<vectorDims; v++){
			final WeightsNode nodesInForThatV[] = inNodes[v];
			final double scalarsInForThatV[] = in[v];
			for(int r=0; r<rings; r++){
				WeightsNode b = nodesInForThatV[r];
				//b.refreshWeightedSum();
				CellUtil.refreshWeightedSum(b);
				scalarsInForThatV[r] = b.position/b.sum;
			}
		}
		func.func(out, in);
	}
	
	/** All relevant cells refreshA() should be called, then those same cells refreshB(),
	then all trees refreshC to copy up to larger squares.
	RefreshA calculates scalars, and RefreshB copies those to leaf nodes.
	TODO This should be multithreaded, while all threads must finish each step
	before any move on to the next.
	<br><br>
	Does nothing if caller has not once filled at least 1 node (or normally all) in outNodes[].
	*/
	public void refreshD(){
		for(int v=0; v<vectorDims; v++){
			if(outNodes[v] != null){
				outNodes[v].position = out[v];
			}
		}
	}
	
	protected static WeightsNode[][] newEmptyNodes(int vectorDims, int rings){
		WeightsNode nodes[][] = new WeightsNode[vectorDims][rings];
		for(int v=0; v<vectorDims; v++){
			for(int r=0; r<rings; r++){
				nodes[v][r] = new WeightsNode(RootAllocJ.newJ());
			}
		}
		return nodes;
	}

}
