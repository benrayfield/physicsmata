/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL, including WeightsNode */
package humanaicore.weightsnode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import humanaicore.common.MathUtil;
//import humanaicore.common.MathUtil;
import humanaicore.common.Rand;
//import humanainet.blackholecortex.weightsfuncs.BernoulliSumSigmoid;

/** Merging NumNode and SparseNode into this WeightsNode class.
TODO update comments as many comments and code were copied together.
<br><br>
datastruct for sparse edges with scalar weight FROM any nodes.
TODO verify direction: Set a weight FROM x to y to 0 and x is removed from y's list,
but x's list is not changed.
<br><br>
TODO move SparseNode and WeightsNode to a more neutral place than bellautomata or blackholecortex.
<br><br>
datastruct for sparse edges which can be extended with other arrays between nodes
by updating swapIndexs and changeArraySize funcs to call super then also do those.
This more general datastruct doesnt assume any specific kind of node or edge data.
<br><br>
TODO move SparseNode and WeightsNode to a more neutral place than bellautomata or blackholecortex.
*/
public class WeightsNode implements Comparable<WeightsNode>{
	
	/** Attention ranges 0 to 1 and chooses how much influence specific nodes have in learning and predicting.
	<br><br>
	In neural nodes at least, the "scalar" var is multiplied by attention after scalar is calculated from sigmoid of
	weighted sum. Then if this is a bernoulli node (bernoulli distribution describes weighted coin flips) observe
	the bit var that way as usual in a boltzmann machine (which is a bidirectional kind of neuralnet), for example.
	<br><br>
	In the simplest case attention is always 1. It could instead be set by another neuralnet who wants this neuralnet
	to learn or predict based on certain parts of its mind or data.
	Any way of spreading attention across the nodes will technically work,
	but a good spread of contexts is useful for more general learning.
	When not set by another network, this is meant to be used with sortedPointers to choose a percentile graph
	of how much attention should be at various nodes, so it could be controlled that maybe 20% of the nodes have
	most of the attention while the top 40% have a little attention, etc.
	If you try to think about everything at once, you'll end up very confused. Thats what attention is for.
	https://en.wikipedia.org/wiki/Saccade and the scalar confidence in TruthValue in OpenCog are examples of attention.
	*/
	public double attention = 1;
	
	/** This used to be called "scalar" but renamed it to "position" when added "speed" var,
	which makes it compatible with complex numbers as a spring moves by
	continuously subtracting position from speed as in SparseDoppler software.
	<br><br>
	If this is a neural node, then scalar is its "neural activation", range 0 to 1,
	set to attention multiplied by the output of the neural func whose params are weighted sum from other nodes and temperature.
	As usual in boltzmann machines, weights are divided by temperature so as it gets colder, the sum of weights is for each
	node either very negative or very positive so it converges to extreme values near 0 or 1 and is less possible states.
	*/
	public double position;
	
	/** Derivative of position var. Some implementations will use only position and ignore this. */
	public double speed;
	
	/** Only used in some kinds of nodes, like physicsmataV2.0.0's BellscalarNode has a
	weightedSumOrInput var which is a weightedSum of such vars lower in the tree
	of how space is divided into recursive squares to sum brightness in various circles.
	Sum can be used for anything, but I'm mostly including it for compatibility with that. 
	*/
	public double sum = 1;
	
	/** Optional, depending on if you want a continuous/scalar value or weighted coin flips. This would be how the coin lands
	and the weighted sums are multiplied by this bit (add each weight or dont),
	else multiply that weight by the scalar. Its the same as setting the scalar to 0 or 1 as the result of this coin flip
	and is an optimization of that to add instead of multiply. Its also a good logic as normally used in boltzmann machines
	to avoid local minimums.
	*/
	public boolean bit;
	
	/** Some WeightsNodes have parents.
	Example: in PhysicsmataV2.0's BellscalarNode class, which is replaced by WeightsNode in V2.1.
	*/
	public WeightsNode parentOrNull;
	
	public double squaredRadius(){
		return position*position + speed*speed;
	}
	
	public double radius(){
		return Math.sqrt(position*position + speed*speed);
	}
	
	/** set radius of position and speed directly (instead of any normalizing vars) */
	public void multiplyRadius(double mult){
		position *= mult;
		speed *= mult;
	}
	
	public void addToSpeed(double acceleration){
		speed += acceleration;
	}
	
	/** set position and speed directly (instead of any normalizing vars) */
	public void set(double position, double speed){
		this.position = position;
		this.speed = speed;
	}
	
	////////////////////////////////////////////NumNode above, SparseNode below
	
	
	public final long localName;
	protected final int hashCode;
	
	/** Size of nodeFrom[] and weightFrom[], which only contain nonzero weighted nodes */
	public int size;
	
	/** indexs aligned with weightFrom[] */
	public WeightsNode nodeFrom[] = new WeightsNode[1];
	
	/** An optimization for often, but not guaranteed correct so must check it each time,
	finding this node's index in other nodes, of the other nodes in nodeFrom[].
	This array's indexs are aligned to nodeFrom[] indexs.
	This is used when setting weightFrom[] andOr learning[] symmetricly between node pairs.
	<br><br>
	cacheReverseIndex can never contain -1. If not found, set it to 0 (or any nonnegative int),
	and then when looked up it will find thats not correct and will slowly return -1.
	<br><br>
	TODO create functions to use this.
	*/
	protected int cacheReverseIndex[] = new int[1];
	
	/** Null for small nodes. TODO create more efficient map specialized in Object to int. */
	protected Map<WeightsNode,Integer> nodeToIndex = null;
	protected static final int createMapIfBiggerThan = 12;
	
	/** TODO thread locking wont be used since if 2 threads write to NeuralNode,
	they will have calculated the same value except for weightedRandomBit.
	<br><br>
	which is in a WeightsNode.threadLock. This is to prevent deadlocks.
	Many WeightsNodes normally used together (like in the same rbm) should use the
	same threadLock object, a different such object per thread.
	Each thread would run its own rbm or other structure of nodes,
	merging data less often. The WeightsNodes can be in the same FlatXYP and other
	datastructs, meant to be in the same grid of pixels on screen,
	while the user could scroll across the many rbms that are each run
	in a different thread and are allocated and deleted as needed in the
	large scrollable space of pixels, where each magnified pixel is a WeightsNode.
	*
	public volatile Object threadLock = firstThreadLock;
	/** To simplify how WeightsNodes are allocated, their threadLock starts as this value.
	No thread may lock on this object. Its meant to be changed if used multithreaded.
	*
	public static final Object firstThreadLock = new Object();
	*/
	
	/** for security of the hash algorithm being different each run of the program.
	https://en.wikipedia.org/wiki/Universal_hashing a little.
	*/
	private static final int hashMult = 103+Rand.strongRand.nextInt(1 << 12);
	
	/*public SparseNode(long localName){
		//System.out.println("New sparsenode, address="+address);
		this.localName = localName;
		//this.level = level; 
		hashCode = (hashMult*(int)(localName >> 32)) ^ (int)localName;
	}*/
	
	protected void changeArraysSize_sparseNode(int newCapacity){
		if(newCapacity < size) throw new RuntimeException("newCapacity="+newCapacity+" size="+size);
		WeightsNode nodeFrom2[] = new WeightsNode[newCapacity];
		System.arraycopy(nodeFrom, 0, nodeFrom2, 0, size);
		nodeFrom = nodeFrom2;
		int cacheReverseIndex2[] = new int[newCapacity];
		System.arraycopy(cacheReverseIndex, 0, cacheReverseIndex2, 0, size);
		cacheReverseIndex = cacheReverseIndex2;
	}
	
	/** For sorting, maybe in a later version. I'm not sure if its needed.
	Subclass EconbitsNode already extends this with its extra array.
	*/
	protected void swapIndexs_sparseNode(int x, int y){
		WeightsNode tempNode = nodeFrom[x];
		nodeFrom[x] = nodeFrom[y];
		nodeFrom[y] = tempNode;
		int tempCacheReverse = cacheReverseIndex[x];
		cacheReverseIndex[x] = cacheReverseIndex[y];
		cacheReverseIndex[y] = tempCacheReverse;
		if(nodeToIndex != null){
			nodeToIndex.put(nodeFrom[x], x);
			nodeToIndex.put(nodeFrom[y], y);
		}
	}
	
	/** -1 if not found */
	public int indexOf(WeightsNode from){
		if(nodeToIndex == null){
			for(int i=0; i<size; i++) if(nodeFrom[i] == from) return i;
		}else{
			Integer i = nodeToIndex.get(from);
			if(i != null) return i;
		}
		return -1;
	}
	
	/** Cached until this node's position in other node changes after last call of this,
	for efficient updating of nodes in pairs without looking them up using hashCode etc.
	<br><br>
	Returns reverseIndex where nodeFrom[from].nodeFrom[reverseIndex] == this,
	or -1 if nodeFrom[from] has 0 weight back to this WeightsNode.
	Updates the reverseCache if its found.
	<br><br>
	Its best that if either in a node pair has nonzero weight to the other,
	then the other has nonzero weight back, because otherwise they will continue
	to cache miss and slow it down just to return -1 that its not found.
	*/
	public int reverseIndexIn(int from){
		WeightsNode otherNode = nodeFrom[from];
		int cachedRevInd = cacheReverseIndex[from];
		if(cachedRevInd < otherNode.size){
			if(otherNode.nodeFrom[cachedRevInd] == this){
				return cachedRevInd;
			}
		}
		//cache miss. Update cache, unless this not exist in otherNode (which will continue to be slow).
		int correctReverseIndex = otherNode.indexOf(this);
		if(correctReverseIndex == -1){
			cacheReverseIndex[from] = 0; //must always point at positive index, even if will cache miss
			return -1;
		}else{
			return cacheReverseIndex[from] = correctReverseIndex;
		}
	}
	
	public int compareTo(WeightsNode n){
		if(localName < n.localName) return -1;
		if(localName > n.localName) return 1;
		if(n != this) throw new RuntimeException("No 2 nodes can have the same long address: "+localName);
		return 0;
	}
	
	public boolean equals(Object ob){
		//Dont check for duplicate nodes with same long/address here (even though its not allowed)
		//Just do the fast thing, ==
		//return this == ob;
		//Or maybe I want 2 nodes with same long/address to approximate eachother
		//and be merged, but for datastructs they are not equal.
		
		if(ob == this) return true;
		if(!(ob instanceof WeightsNode)) return false;
		if(((WeightsNode)ob).localName == localName) throw new RuntimeException(
			"No 2 nodes can have the same long address: "+localName);
		return false;
	}
	
	public int hashCode(){ return hashCode; }
	
	
	////////////////////////////////////////////SparseNode above, WeightsNode below
	
	//"Lets just merge WeightsNode, SparseNode, and NumNode since the uncertainty of if things have the basic vars and weights is complicating things. Change the array type of nodesFrom to WeightsNode."
	
	//"For compatibility with bainterf, generalize to a function that takes a WeightsNode parameter, reads its nodeFroms and weightFroms, and writes its position, speed, and maybe other local vars. I'd like it to be a function that returns scalar, if possible, but with the addition of the new vars (originally in NumNode), theres more than 1 scalar var to potentially update. As long as it works in any WeightsNode, it will be reproducible to create a new WeightsNode of the same connections (which only reads them not writes anything, by design of WeightsNode) and call it on that, so its still a math function."
	
	/** Should average near 0 per weight, not necessarily the whole array together. */
	public double weightFrom[] = new double[1];
	
	/** This array can be used as derivative of weightFrom array.
	Will be added to weightFrom[] at same indexs,
	after a group of things learned is finished together.
	*/
	public double learning[] = new double[1];
	
	/** For example, FlatXYP.viewNewNodeAt(Rectangle2D.Double rect, int p) only creates
	positive weights of how much of the surface the rectangle intersects at each pixel,
	but the node as viewing that rectangle needs to be off sometimes (at least half the time),
	so that nodes addToWeight would be around negative half the weights to those it views.
	Default addToWeight is 0 since most nodes have positive and negative weights
	between eachother. This is better than having all nodes connect to a node which
	is always on (bit is 1) since that could slow things when many threads need
	to touch the same memory, especially in cell processors.
	*/
	public double addToWeight;
	
	/** If not null, this func is run in refreshScalar. Starts null. */
	//public WeightsFunc func = BernoulliSumSigmoid.instance;
	public WeightsFunc func;
	
	public WeightsNode(long localName){
		//super(localName);
		this.localName = localName;
		hashCode = (hashMult*(int)(localName >> 32)) ^ (int)localName;
	}
	
	public void refresh(Random rand, double temperature){
		refreshScalar(temperature);
		refreshBit(rand);
	}
	
	public void refreshScalar(double temperature){
		final WeightsFunc f = func;
		//if(f != null) position = attention*f.weightsFunc(this, temperature);
		//Moved the multiply by attention into BernoulliSumSigmoid which is a WeightsFunc
		if(f != null) f.weightsFunc(this, temperature);
	}
	
	public void refreshBit(Random rand){
		bit = MathUtil.weightedRandomBit(position, rand);
	}
	
	public double sumWeightsPerOnBit(){
		//return sumWeights(recogOn);
		double sum = 0;
		final int siz = size;
		final WeightsNode n[] = nodeFrom;
		final double w[] = weightFrom;
		for(int i=0; i<siz; i++){
			if(n[i].bit){
				sum += w[i];
			}
		}
		sum += addToWeight;
		return sum;
	}
	
	//TODO setWeightFrom(int) func as optimization to not call indexOf(WeightsNode) if already know it
	//TODO remove(int) func, and call inside setWeightFrom and in BoltzUtil.addToBothWeightsBetween
	//and in BoltzUtil.setWeightBetween
	
	/** Set to weight 0 to remove Node. Set to nonzero to add it. */
	public void setWeightFrom(WeightsNode from, double weight){
		//FIXME this func needs to use swapIndexs(int,int) instead of moving between i and endIndex
		int i = indexOf(from);
		if(weight == 0){ //remove
			if(i != -1){
				int endIndex = size-1;
				nodeFrom[i] = nodeFrom[endIndex]; //may be same index
				weightFrom[i] = weightFrom[endIndex];
				nodeFrom[endIndex] = null; //become garbageCollectable
				size--;
				//TODO if(size <= nodeFrom.length/4){ //shrink arrays to size*2 and HashMap
				//TODO }
			}
		}else{
			if(i == -1){ //add node
				if(nodeFrom.length == size){ //enlarge arrays when not enough room
					changeArraysSize(size*2);
				}
				nodeFrom[i = size++] = from;
				if(nodeToIndex == null){
					if(createMapIfBiggerThan < size){
						nodeToIndex = new HashMap(nodeFrom.length*2, .75f);
						for(int n=0; n<size; n++) nodeToIndex.put(nodeFrom[n],n);
					}
				}else{
					nodeToIndex.put(from, i);
				}
			}
			weightFrom[i] = weight;
		}
	}
	
	public void setLearningFrom(WeightsNode from, double d){
		int i = indexOf(from);
		if(i == -1) throw new RuntimeException("TODO define an edge as deleted when learning and weightFrom are both 0, not just weightFrom");
		learning[i] = d;
	}
	
	protected void changeArraysSize(int newCapacity){
		//super.changeArraysSize(newCapacity);
		changeArraysSize_sparseNode(newCapacity);
		double weightFrom2[] = new double[newCapacity];
		System.arraycopy(weightFrom, 0, weightFrom2, 0, size);
		weightFrom = weightFrom2;
		double learning2[] = new double[newCapacity];
		System.arraycopy(learning, 0, learning2, 0, size);
		learning = learning2;
	}
	
	/** For sorting, maybe in a later version. I'm not sure if its needed.
	Subclass EconbitsNode already extends this with its extra array.
	*/
	protected void swapIndexs(int x, int y){
		//super.swapIndexs(x, y);
		swapIndexs_sparseNode(x,y);
		double tempWeight = weightFrom[x];
		weightFrom[x] = weightFrom[y];
		weightFrom[y] = tempWeight;
		double tempLearn = learning[x];
		learning[x] = learning[y];
		learning[y] = tempLearn;
	}
	
	/** 0 if node not exist here */
	public double weightFrom(WeightsNode from){
		int i = indexOf(from);
		if(i == -1) return 0;
		return weightFrom[i];
	}
	
	public double learningFrom(WeightsNode from){
		int i = indexOf(from);
		if(i == -1) return 0;
		return learning[i];
	}

	/** Empties learning[] into weightsFrom[] */
	public void moveLearningToWeights(){
		for(int i=0; i<size; i++){
			weightFrom[i] += learning[i];
		}
		Arrays.fill(learning, 0, size, 0.);
	}
	
	public double weightsAve(){
		if(size == 0) throw new RuntimeException("empty");
		double sum = 0;
		for(int i=0; i<size; i++) sum += weightFrom[i];
		return sum/size;
	}
	
	public double weightsSumOfSquaresFromAve(double ave){
		if(size == 0) throw new RuntimeException("empty");
		double sum = 0;
		for(int i=0; i<size; i++){
			double diff = weightFrom[i]-ave;
			sum += diff*diff;
		}
		return sum;
	}
	
	public double weightsSumOfSquaresFromAve(){
		return weightsSumOfSquaresFromAve(weightsAve());
	}
	
	/** Returns NaN if size is 0 */
	public double weightsStdDev(){
		return Math.sqrt(weightsSumOfSquaresFromAve()/size);
	}
	
	public double weightsStdDev(double ave){
		return Math.sqrt(weightsSumOfSquaresFromAve(ave)/size);
	}
	
	/** from whatever the ave is */
	public double hypersphereRadiusFromAve(){
		return hypersphereRadiusFromAve(weightsAve());
	}
	
	public double hypersphereRadiusFromAve(double ave){
		if(size == 0) return 0;
		return Math.sqrt(weightsSumOfSquaresFromAve(ave));
	}
	
	//TODO use aod, where Neuron.influence is aod, and dotProd between 2 Neuron is by their weights to same nodes, if ave weight in each node is 0
	
	//TODO to avoid exponential complexity of which combinations of weights are positive vs negative, maybe should require all weights be positive (or all negative), and balance that weith Neuron.addToWeight
	
	/** TODO when 2 adjacent, rbm layers have different sizes,
	adjust hypersphere radius so pushing both directions of weight
	between each pair to equal eachother does not change the learning.
	For example, all weights equal to inverse layer size.
	*/
	public void normWeightsToHypersphere(double newAve, double newRadius){
		if(size == 0) return;
		double observedAve = weightsAve();
		double obsevedRadiusFromAve = hypersphereRadiusFromAve(observedAve);
		if(obsevedRadiusFromAve == 0){
			throw new RuntimeException("TODO add Random param to choose random vector of that radius");
		}
		double radiusMult = newRadius/obsevedRadiusFromAve;
		//double observedDev = weightsStdDev(observedAve);
		if(newAve == 0){
			for(int i=0; i<size; i++){
				weightFrom[i] = (weightFrom[i]-observedAve)*radiusMult;
			}
		}else{
			for(int i=0; i<size; i++){
				weightFrom[i] = newAve + (weightFrom[i]-observedAve)*radiusMult;
			}
		}
	}
	
	public void clear(){
		Arrays.fill(nodeFrom, 0, size, null);
		Arrays.fill(cacheReverseIndex, 0, size, -1);
		Arrays.fill(weightFrom, 0, size, 0);
		Arrays.fill(learning, 0, size, 0);
		size = 0;
		nodeToIndex = null;
		attention = 1;
		position = speed = 0;
		sum = 1;
		bit = false;
		parentOrNull = null;
	}
	
	/*public void normWeightsToCenteredHypersphere(double newRadius){
		normWeightsToHypersphere(0, newRadius);
	}
	
	/** TODO is the same as normWeightsToHypersphere but just different params?
	Theres at least duplicated code between them.
	*
	public void normWeightsToBellCurve(double newAve, double newDev){
		if(size == 0) return;
		double observedAve = weightsAve();
		double observedDev = Math.sqrt(weightsSumOfSquaresFromAve(observedAve)/size);
		if(observedDev == 0){
			throw new RuntimeException("TODO include Random param to handle when all weights equal, how to put them on a specific stdDev");
		}
		for(int i=0; i<size; i++){
			double unitDevZeroAve = (weightFrom[i]-observedAve)/observedDev;
			weightFrom[i] = newAve + unitDevZeroAve*newDev;
		}
	}*/

}