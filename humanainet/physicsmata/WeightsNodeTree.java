package humanainet.physicsmata;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import humanaicore.alloc.RootAllocJ;
import humanaicore.weightsnode.WeightsNode;

/** A 2d tree of cells, each having either 4 square branches or 0 as leaf */
public class WeightsNodeTree{
	
	public final int xAndYSize, treeHeight;
	
	/** nodes[0].length is biggest, and each higher index gets half as big
	in both remaining dims until nodes[nodes.length-1].length is 1.
	The root node is nodes[nodes.length-1][0][0].
	*/
	public WeightsNode nodes[][][];
	//TODO make this var protected
	
	/** Each BellscalarNode is a certain x, y, and square size relative to
	units of leaf nodes and the top left corner being (0,0).
	<br><br>
	TODO? This is a useful datastruct, but its duplicated vectorDims number of times
	when there are that many parallel BellscalarTree. The alternative would be
	some system of mapping those multiple different nodes of the same size and position,
	in different BellscalarTrees, to the same Rectangle. I dont think its a high enough
	cost of memory to justify that complexity. Keep it separate in each BellscalarTree.
	Since theres normally 1 or only a few vectorDims, like 2 for complexnum,
	its a small cost. 
	*/
	protected Map<WeightsNode,Rectangle> nodeToRect = new HashMap();
	
	/** xAndYSize is 2^treeHeight for a total of 4^treeHeight nodes.
	First layer is 0, and last layer is treeHeight, so there are treeHeight+1 layers.
	*/
	public WeightsNodeTree(int treeHeight){
		//TODO merge duplicate code between constructors
		xAndYSize = 1 << treeHeight;
		this.treeHeight = treeHeight;
		nodes = new WeightsNode[treeHeight+1][][];
		for(int t=0; t<=treeHeight; t++){
			int localXAndYSize = 1 << (treeHeight-t);
			nodes[t] = new WeightsNode[localXAndYSize][localXAndYSize];
			for(int y=0; y<localXAndYSize; y++){
				for(int x=0; x<localXAndYSize; x++){
					WeightsNode b = new WeightsNode(RootAllocJ.newJ());
					//4^treeHeight, squares branching 4 ways
					//Removing spaceRoundUp b.spaceRoundUp = b.spaceWeighted = 1 << t*2;
					int blockSide = 1<<t;
					Rectangle r = new Rectangle(x*blockSide, y*blockSide, blockSide, blockSide);
					nodeToRect.put(b, r);
					nodes[t][y][x] = b;
				}
			}
		}
		for(int t=0; t<treeHeight; t++){ //until second last layer
			int localXAndYSize = 1 << (treeHeight-t); //TODO? 1 << (treeHeight-1-t) 
			for(int y=0; y<localXAndYSize; y++){
				for(int x=0; x<localXAndYSize; x++){
					WeightsNode child = nodes[t][y][x];
					WeightsNode parent = nodes[t+1][y>>1][x>>1];
					parent.setWeightFrom(child, 1.); //sum instead of ave
					//parent.setWeightFrom(child, child.space); //norm it later so weights sum to 1
					//Optimization: all .25 so not have to call BellscalarNode.normWeights()
					//parent.setWeightFrom(child, .25);
					child.parentOrNull = parent;
				}
			}
		}
	}
	
	
	/** Views all layers same size, so theres duplication except at layer 0 */
	public WeightsNode getByLeafYX(int height, int leafY, int leafX){
		return nodes[height][leafY>>height][leafX>>height];
	}

	/** Height ranges 0 to treeHeight-1. Each higher height has half as many x and y indexs. */
	public WeightsNode getByLayerYX(int height, int yAtHeight, int xAtHeight){
		return nodes[height][yAtHeight][xAtHeight];
	}
	
	/** Same as get(y,x,0) */
	public WeightsNode getLeaf(int y, int x){
		return nodes[0][y][x];
	}

	/** Same as get(0, 0, treeHeight-1) */
	public WeightsNode getRoot(){
		return nodes[nodes.length-1][0][0];
	}
	
	/** Returns null if its not 1 of the BellscalarNode in this tree */
	public Rectangle nodeToRect(WeightsNode node){
		return nodeToRect.get(node);
	}
	
	/** To be called after all nodes refreshB together which is after all nodes refreshA together.
	Updates squares from leafs up to larger and finally the root square covering the whole area.
	<br><br>
	This is the single threaded version that does it all.
	TODO this should be multithreaded. For example, could use 16 threads each starting from 1 of 16
	nodes in the third highest level. The highest level has 1 node, and each lower has 4 times more.
	Then in 1 thread run the top 2 layers which will finish near instantly since its so small.
	*/
	public void refreshAAndBSingleThreaded(){
		//Start at height 1 because not update leafs, which were set by refreshD andOr other input.
		//Leafs are brightness on screen at each pixel.
		for(int height=1; height<=treeHeight; height++){
			int localXAndYSize = 1 << (treeHeight-height);
			final WeightsNode nodesT[][] = nodes[height];
			for(int yAtHeight=0; yAtHeight<localXAndYSize; yAtHeight++){
				final WeightsNode nodesTY[] = nodesT[yAtHeight];
				for(int xAtHeight=0; xAtHeight<localXAndYSize; xAtHeight++){
					WeightsNode nodeTYX = nodesTY[xAtHeight];
					//nodeTYX.refreshWeightedSum();
					CellUtil.refreshWeightedSum(nodeTYX);
				}
			}
		}
	}
	
	/** This is for each thread to call.
	height is array index in tree of branch as if it was a root in this thread.
	*/
	public void refreshABranch(int height, int yAtHeight, int xAtHeight){
		throw new RuntimeException("TODO");
	}
	
	/** Refreshes from minHeight up to root, after refreshC is run (TODO change its params)
	with each branch in different thread.
	*/
	public void refreshB(int minHeight){
		for(int height=minHeight; height<=treeHeight; height++){
			int localXAndYSize = 1 << (treeHeight-height);
			final WeightsNode nodesT[][] = nodes[height];
			for(int y=0; y<localXAndYSize; y++){
				final WeightsNode nodesTY[] = nodesT[y];
				for(int x=0; x<localXAndYSize; x++){
					WeightsNode nodeTYX = nodesTY[x];
					//nodeTYX.refreshWeightedSum();
					CellUtil.refreshWeightedSum(nodeTYX);
				}
			}
		}
	}

}
