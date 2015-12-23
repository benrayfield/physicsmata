package humanainet.physicsmata;
import java.util.HashSet;
import java.util.Set;
import java.awt.Rectangle;

import humanaicore.alloc.RootAllocJ;
import humanaicore.common.CoreUtil;
import humanaicore.weightsnode.WeightsNode;

public class CellUtil{
	
	//TODO include manually built circle types based on pascalstri, only for smallest circles,
	//at least the first one being exactly the current cell. The second one may be latticeBoltzmann 3x3 grid,
	//or maybe it would have more sticking out its sides since all paths of length 2-3 are more than that.
	
	/** TODO IMPORTANT if first radius is 0 or very near it, use previous value of same cell */
	public static void fillWithCircleSums(
			boolean sumInsteadOfAve, boolean includePartialTouches,
			CellGrid cellGrid, boolean wrapY, boolean wrapX,
			double... radius){
		for(int y=0; y<cellGrid.squareSide; y++){
			System.out.println("fillRingsWithCircleSums y="+y+" up to "+cellGrid.squareSide);
			for(int x=0; x<cellGrid.squareSide; x++){
				Cell cell = cellGrid.cell[y][x];
				for(int v=0; v<cellGrid.vectorDims.length; v++){
					//TODO remove all childs from each BellscalarNode then copy child pointers and weights,
					//instead of creating new node that way, in case there are pointers into CellGrid to them.
					if(cell.rings != radius.length) throw new RuntimeException(
						cell.rings+" == cell.rings != radius.length == "+radius.length);
					for(int r=0; r<radius.length; r++){
						WeightsNodeTree tree = cellGrid.vectorDims[v];
						//int minHeight = 0; //TODO bigger minHeight for larger rings, since they need less accuracy
						//TODO for bigger rings int minHeight = 1; //TODO bigger minHeight for larger rings,
						//ince they need less accuracy
						
						
						
						//int minHeight = (int)CoreUtil.max(0, .7*Math.log(radius[r]));
						int minHeight = (int)CoreUtil.max(0, .5*Math.log(radius[r]));
						
						
						
						WeightsNode circleRoot = cell.inNodes[v][r];
						if(radius[r] < .5){
							//thisSquare should be leaf, since radius is so small.
							WeightsNode thisSquare = tree.getByLeafYX(minHeight, y, x);
							circleRoot.clear();
							circleRoot.setWeightFrom(thisSquare, 1.);
							//circleRoot.spaceWeighted = thisSquare.spaceWeighted;
							circleRoot.sum = thisSquare.sum;
							//Removing spaceRoundUp circleRoot.spaceRoundUp = thisSquare.spaceRoundUp;
						}else{
							WeightsNode circleSum = circleSum(tree, sumInsteadOfAve,
								minHeight, y, x, wrapY, wrapX, radius[r], includePartialTouches);
							copy(circleSum, circleRoot);
						}
					}
				}
			}
		}
	}
	
	protected static boolean printRects;
	
	/** The returned BellscalarNode continues to work as long as the BellscalarTree
	is updated before it each time, else it will return same value of that.
	<br><br>
	The returned BellscalarNode.space is area of the circle.
	<br><br>
	This func is to replace fillRings func, and RingVecFunc need redesigning,
	maybe merging it with bellautomata.continuous package andOr caching of BellscalarNode
	for each radius asked about from each Cell, either when its RingVecFunc is changed
	or at runtime when each new radius is asked for, reusing that BellscalarNode if its
	already been built and asking about the same radius at the same place as earlier.
	<br><br>
	bainterf means BellAutomata interface, what I've named BellscalarNode in this software.
	bainterf will be based on sums of vectors (normally 1d, same as scalar) at circles
	of any chosen radius, and it will be very precise and cost only the perimeter of the
	circle in units of the smallest blocks used, because most of the area will be in large
	blocks in bainterfTree. The bainterfNode which calculates each specific sum of circle
	of specific radius should be created at time a bainterfNode receives a bainterfFunc
	(which from then on calculates its vector at that point). Each bainterfFunc will tell
	which  list of radius (sorted ascending) it wants calculated each run of that func,
	or another way to design it would be to cache a bainterfNode for each radius
	(and bainterfCell and vectorDim) asked about at runtime which would be almost as
	fast except the lookup time in such a cache. In this paradigm, the scalar in any
	donut shape is calculated as the outer radius's bainterfNode minus the inner radius's
	bainterfNode, which costs only the perimeter of the 2 circles instead of the volume
	between them. That cost is true even if a new branch of the acyclicNet has to be
	created for a unique radius each call, but we save a constant multiple of time
	faster by having it already exist and reusing it. For extra accuracy, as weightsNode,
	these bainterfNode will use fractional weight on the smallest blocks they include to
	describe how much of those blocks is inside the circle they represent. It will be very
	accurate and fast, with option to trade accuracy for speed by using small number of
	circles andOr not recursing to the smallest blocks in the larger rings.
	<br><br>
	x and y must be at most 2^14 different than each node's Rectangle's corners,
	and if this isnt true then create a version of this func which uses double positions
	instead of ints which is an optimization. This would happen in a tree that
	takes gigabytes of memory since it would exceed a 2^14 x 2^14 node tree on the bottom,
	but there may be other useful ways of organizing nodes where they're farther away. 
	*/
	public static WeightsNode circleSum(
			WeightsNodeTree tree, boolean sumInsteadOfAve, int minHeight,
			int y, int x, boolean wrapY, boolean wrapX, double radius,
			boolean includePartialTouches){
		WeightsNode weightedSet = new WeightsNode(RootAllocJ.newJ());
		//System.out.println("\r\n\r\n\r\n\r\nx="+x+" y="+y+" radius="+radius);
		//if(y==63 && (x==34 || x==48)){
		//	printRects = true;
		//}
		splitRecursivelyForCircleSumOrAve(tree, sumInsteadOfAve, minHeight,
				weightedSet, tree.getRoot(), tree.treeHeight,
				y, x, wrapY, wrapX, radius, includePartialTouches);
		//printRects = false;
		//Removing spaceRoundUp weightedSet.refreshSpaceRUpSum(); //should be very close to area of circle of given radius
		if(!sumInsteadOfAve) throw new RuntimeException(
			"Code for ave not finished. Use sum instead, at least for now.");
		//weightedSet.normWeights();
		double sumWeightTimesChildSpaceWeighted = 0;
		for(int i=0; i<weightedSet.size; i++){
			WeightsNode n = weightedSet.nodeFrom[i];
			if(n instanceof WeightsNode){
				sumWeightTimesChildSpaceWeighted +=
					weightedSet.weightFrom[i]*((WeightsNode)n).sum;
			}
		}
		//In sum mode, weight of child is touchFraction of that child.
		weightedSet.sum = sumWeightTimesChildSpaceWeighted;
		return weightedSet;
	}
	
	protected static final double sqrt2 = Math.sqrt(2), halfSqrt2 = sqrt2/2;
	
	
	
	
	
	
	/** Fills BellscalarNode weightedSet.
	<br><br>
	TODO this is approximate. See comment in code of this func and partially finished code.
	<br><br>
	x and y must be at most 2^14 different than each node's Rectangle's corners,
	and if this isnt true then create a version of this func which uses double positions
	instead of ints which is an optimization. This would happen in a tree that
	takes gigabytes of memory since it would exceed a 2^14 x 2^14 node tree on the bottom,
	but there may be other useful ways of organizing nodes where they're farther away.
	<br><br>
	Caller must call weightedSet.refreshSpaceSum() after this.
	UPDATE: This may not be enough as design is changing about sumInsteadOfAve.
	<br><br>
	circleYi and circleXi must be inside BellscalarTree/CellGrid range.
	*/
	protected static void splitRecursivelyForCircleSumOrAve(
			WeightsNodeTree tree, boolean sumInsteadOfAve, int minHeight,
			WeightsNode weightedSet, WeightsNode currentNode,
			int currentLayer, int circleYi, int circleXi, boolean wrapY, boolean wrapX,
			double radius, boolean includePartialTouches){
		//System.out.println("splitRecursivelyForCircleSum minHeight="+minHeight
		//	+" weightedSet.size="+weightedSet.size+" x="+x+" y="+y+" radius="+radius);
		Rectangle r = tree.nodeToRect(currentNode);
		//if(printRects){
		//	System.out.println("rect area="+r.width*r.height+" "+r);
		//}
		if(r.width != r.height) throw new RuntimeException(
			"BellscalarTree only uses squares (of sides a power of 2): "+r);
		int cellGridSide = tree.xAndYSize;
		if(cellGridSide < 2) throw new RuntimeException(tree.xAndYSize+" == cellGridSide < 2");
		int halfCellGridSide = cellGridSide/2;
		final double radiusSq = radius*radius;
		
		double halfSmallestSquareSide = .5*(1<<minHeight);
		
		//This way, plus .5, circle is at center of leaf cell:
		double circleX = circleXi+.5, circleY = circleYi+.5;
		//OLD: double circleX = circleXi+halfSmallestSquareSide, circleY = circleYi+halfSmallestSquareSide;
				
		//first check if square is completely inside circle		
		//A B
		//C D
		//Optimization: Use each multiply distance calculations of 2 corners
		
		//These 4 vars are each true if it wraps in x or y
		boolean wrapCornerA = false;
		boolean wrapCornerB = false;
		boolean wrapCornerC = false;
		boolean wrapCornerD = false;
		
		double cornersAAndBDy = r.y-circleY;
		double cornersCAndDDy = cornersAAndBDy+r.height;
		//
		double cornersAAndCDx = r.x-circleX;
		double cornersBAndDDx = cornersAAndCDx+r.width;
		//
		//If wrap, use min manhattanDistance, before squaring,
		//by adding or subtracting cellGrid.squareSide from each dx and dy.
		if(wrapY){
			if(cornersAAndBDy < -halfCellGridSide){
				cornersAAndBDy += cellGridSide;
				//y -= cellGridSide;
				wrapCornerA = true;
				wrapCornerB = true;
			}else if(halfCellGridSide < cornersAAndBDy){
				cornersAAndBDy -= cellGridSide;
				//y += cellGridSide;
				wrapCornerA = true;
				wrapCornerB = true;
			}
			
			if(cornersCAndDDy < -halfCellGridSide){
				cornersCAndDDy += cellGridSide;
				//y -= cellGridSide;
				wrapCornerC = true;
				wrapCornerD = true;
			}else if(halfCellGridSide < cornersCAndDDy){
				cornersCAndDDy -= cellGridSide;
				//y += cellGridSide;
				wrapCornerC = true;
				wrapCornerD = true;
			}
		}
		if(wrapX){
			if(cornersAAndCDx < -halfCellGridSide){
				cornersAAndCDx += cellGridSide;
				//x -= cellGridSide;
				wrapCornerA = true;
				wrapCornerC = true;
			}else if(halfCellGridSide < cornersAAndCDx){
				cornersAAndCDx -= cellGridSide;
				//x += cellGridSide;
				wrapCornerA = true;
				wrapCornerC = true;
			}
			
			if(cornersBAndDDx < -halfCellGridSide){
				cornersBAndDDx += cellGridSide;
				//x -= cellGridSide;
				wrapCornerB = true;
				wrapCornerD = true;
			}else if(halfCellGridSide < cornersBAndDDx){
				cornersBAndDDx -= cellGridSide;
				//x += cellGridSide;
				wrapCornerB = true;
				wrapCornerD = true;
			}
		}
		int cornersWrapped = 0;
		if(wrapCornerA) cornersWrapped++;
		if(wrapCornerB) cornersWrapped++;
		if(wrapCornerC) cornersWrapped++;
		if(wrapCornerD) cornersWrapped++;
		//
		double cornersAAndBDySq = cornersAAndBDy*cornersAAndBDy;
		double cornersCAndDDySq = cornersCAndDDy*cornersCAndDDy;
		//
		double cornersAAndCDxSq = cornersAAndCDx*cornersAAndCDx;
		double cornersBAndDDxSq = cornersBAndDDx*cornersBAndDDx;
		//
		double cornerADistanceSq = cornersAAndBDySq + cornersAAndCDxSq;
		double cornerBDistanceSq = cornersAAndBDySq + cornersBAndDDxSq;
		double cornerCDistanceSq = cornersCAndDDySq + cornersAAndCDxSq;
		double cornerDDistanceSq = cornersCAndDDySq + cornersBAndDDxSq;
		
		/*
		int cornersAAndBDy = r.y-y;
		int cornersCAndDDy = cornersAAndBDy+r.height;
		//
		int cornersAAndCDx = r.x-x;
		int cornersBAndDDx = cornersAAndCDx+r.width;
		//		
		int cornersAAndBDySq = cornersAAndBDy*cornersAAndBDy;
		int cornersCAndDDySq = cornersCAndDDy*cornersCAndDDy;
		//
		int cornersAAndCDxSq = cornersAAndCDx*cornersAAndCDx;
		int cornersBAndDDxSq = cornersBAndDDx*cornersBAndDDx;
		//
		int cornerADistanceSq = cornersAAndBDySq + cornersAAndCDxSq;
		int cornerBDistanceSq = cornersAAndBDySq + cornersBAndDDxSq;
		int cornerCDistanceSq = cornersCAndDDySq + cornersAAndCDxSq;
		int cornerDDistanceSq = cornersCAndDDySq + cornersBAndDDxSq;
		*/
		
		boolean cornerAIn = cornerADistanceSq <= radiusSq;
		boolean cornerBIn = cornerBDistanceSq <= radiusSq;
		boolean cornerCIn = cornerCDistanceSq <= radiusSq;
		boolean cornerDIn = cornerDDistanceSq <= radiusSq;
		
		boolean rectCompletelyInCircle = cornerAIn & cornerBIn & cornerCIn & cornerDIn
			& (cornersWrapped==0 || cornersWrapped==4 /*|| cornersWrapped == 2*/);
		
		if(rectCompletelyInCircle){
			weightedSet.setWeightFrom(currentNode, 1.);
			//weightedSet.setTouchFractionFrom(currentNode, 1.);
			return;
		}else{
			//rect is either completely out or partially in. We already know its not completely in.
			//If completely out, end recursion. If its partially in, include it with fraction weight.

			if(cornerAIn || cornerBIn || cornerCIn || cornerDIn){
				//Rect is partially in. This is not the only way that can happen.
				//If it wraps, depending on how it wraps, it may be partially in this way.
				//TODO go over this logic again. Something isnt working when wrap.
			}else{
				
				//No corner of rect is in the circle.
				
				if(wrapX || wrapY){
					
					//Check if circle, and wrapped around x andOr y, is outside rect.
					//First check uses smallest square that the circle would fit in.
					
					//TODO optimize by checking wrapX and wrapY before some of this code parts
					
					
					/*double circleYWrapped = circleY;
					if(circleYWrapped < -halfCellGridSide) circleYWrapped += cellGridSide;
					else if(halfCellGridSide < circleYWrapped) circleYWrapped -= cellGridSide;
					*/
					double circleYWrapped = circleY;
					if(circleYWrapped < halfCellGridSide) circleYWrapped += cellGridSide;
					else circleYWrapped -= cellGridSide;
					
					if(
						(
							circleY+radius <= r.y //nonwrapped circle is above rect
							|| r.y+r.height <= circleY-radius //nonwrapped circle is below rect
						) & (
							circleYWrapped+radius <= r.y //y wrapped circle is above rect
							|| r.y+r.height <= circleYWrapped-radius //y wrapped circle is below rect
						)
					){
						//smallest square that contains the circle, wrapped and nonwrapped,
						//are both outside current rect in y.
						return;
					}
					
					/*double circleXWrapped = circleX;
					if(circleXWrapped < -halfCellGridSide) circleXWrapped += cellGridSide;
					else if(halfCellGridSide < circleXWrapped) circleXWrapped -= cellGridSide;
					*/
					double circleXWrapped = circleX;
					if(circleXWrapped < halfCellGridSide) circleXWrapped += cellGridSide;
					else circleXWrapped -= cellGridSide;
					
					if(
						(
							circleX+radius <= r.x //nonwrapped circle is left of rect
							|| r.x+r.width <= circleX-radius //nonwrapped circle is right of rect
						) & (
							circleXWrapped+radius <= r.x //x wrapped circle is left of rect
							|| r.x+r.width <= circleXWrapped-radius //x wrapped circle is right of rect
						)
					){
						//smallest square that contains the circle, wrapped and nonwrapped,
						//are both outside current rect in x.
						return;
					}
					
					boolean allCornersInSameQuarterOfNonwrappedCircle =
						((r.x+r.width <= circleX) || (circleX <= r.x)) //leftHalf or rightHalf
						&& ((r.y+r.height <= circleY) || (circleY <= r.y)); //upHalf or downHalf
					
					//Parallel to this code above: circleX = circleXi+halfSmallestSquareSide, same for y.
					//double circleXWrapped = circleXiWrapped+halfSmallestSquareSide;
					//double circleYWrapped = circleYiWrapped+halfSmallestSquareSide;
					
					boolean allCornersInSameQuarterOfWrappedCircle =
						((r.x+r.width <= circleXWrapped) || (circleXWrapped <= r.x)) //leftHalf or rightHalf
						&& ((r.y+r.height <= circleYWrapped) || (circleYWrapped <= r.y)); //upHalf or downHalf
						
					//if(!allCornersInSameQuarterOfNonwrappedCircle) foundIntersectionYet = true;
					
					//TODO theres probably duplicate math between these 2 circles, 1 wrapped,
					//because they have same x andOr y
					
					boolean allCornersSameQuarterForBothCircles =
						allCornersInSameQuarterOfNonwrappedCircle & allCornersInSameQuarterOfWrappedCircle;
					
					if(allCornersSameQuarterForBothCircles){
						//Since no corner of rect is in circle (wrapped or nonwrapped),
						//and rect intersects smallest square that contains the circle,
						//and all corners of rect are in same quarter of smallest square that contains circle,
						//the rect may touch the border of circle but other than that is outside it.
						return;
					}
					
				}else{
					
					//This code was written before considered wrapping.
					//TODO can it be efficiently and simply merged with some of the wrapping code
					
					if(
						//x increases to right on screen
						//y increases down on screen
						//FIXME TODO circleY and circleX, which have .5 added?
						circleX+radius <= r.x //nonwrapped circle is left of rect
						|| r.x+r.width <= circleX-radius //nonwrapped circle is right of rect
						|| circleY+radius <= r.y //nonwrapped circle is above rect
						|| r.y+r.height <= circleY-radius //nonwrapped circle is below rect
					){
						//smallest square that contains the circle is outside current rect
						return;
					}
					
					//Rect intersects smallest square that contains the circle.
					
					if( //all corners of rect are in same quarter of smallest square that contains circle
						((r.x+r.width <= circleX) || (circleX <= r.x)) //leftHalf or rightHalf
						&& ((r.y+r.height <= circleY) || (circleY <= r.y)) //upHalf or downHalf
					){
						//Since no corner of rect is in circle,
						//and rect intersects smallest square that contains the circle,
						//and all corners of rect are in same quarter of smallest square that contains circle,
						//the rect may touch the border of circle but other than that is outside it.
						return;
					}
					
				}

			}
			
			//Rect partially intersects circle, so recurse if can, or create leaf.
			//Would have returned if not.
			boolean canRecurse = minHeight < currentLayer;
			if(canRecurse){
				//Recurse into child rects (probably squares) because partial overlap
				if(currentNode.size != 4) throw new RuntimeException(
					"Not a 4way branch, size is "+currentNode.size+" in "+currentNode);
				for(int c=0; c<currentNode.size; c++){
					WeightsNode child = currentNode.nodeFrom[c];
					if(!(child instanceof WeightsNode)) throw new RuntimeException(
						"Not a "+WeightsNode.class.getName()+": "+child);
					splitRecursivelyForCircleSumOrAve(tree, sumInsteadOfAve, minHeight,
						weightedSet, (WeightsNode)child, currentLayer-1,
						circleYi, circleXi, wrapY, wrapX, radius, includePartialTouches);
				}
			}else{ //create leaf with fractional weight
				if(includePartialTouches){
					//FIXME create leaf with exact fraction weight, instead of this "approximate" func
					//FIXME the adding of .5 would change if treeLevel not 0.
					if(wrapY){
						double rectCenterY = r.y+.5*r.height;
						double dy = circleY-rectCenterY;
						if(dy < -halfCellGridSide) circleY += cellGridSide;
						else if(halfCellGridSide < dy) circleY -= cellGridSide;
					}
					if(wrapX){
						double rectCenterX = r.x+.5*r.width;
						double dx = circleX-rectCenterX;
						if(dx < -halfCellGridSide) circleX += cellGridSide;
						else if(halfCellGridSide < dx) circleX -= cellGridSide;
					}
					double approxTouchFraction = approxTouchFraction(
						r.x+halfSmallestSquareSide, r.y+halfSmallestSquareSide, r.width, circleX, circleY, radius);
					//int totalSquareVolume = r.width*r.width;
					//double touchFraction = approxVolumeIntersect/totalSquareVolume;
					if(!sumInsteadOfAve) throw new RuntimeException(
						"Code for ave not finished. Use sum instead, at least for now.");
					//double touchFraction = .5;
					weightedSet.setWeightFrom(currentNode, approxTouchFraction);
					//weightedSet.setTouchFractionFrom(currentNode, fractionAsWeight);
				}
				return;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO public static boolean rectIntersectCircle(Rectangle rect, int x, int y, double radius){
	//}
	
	/** FIXME this will be first approximated using repeatable pseudorandom points in the square,
	and in later versions it will become more precise.
	touchFraction*squareSide*squareSide is intersection volume.
	*/
	public static double approxTouchFraction(
			double squareX, double squareY, double squareSide, double circleX, double circleY, double radius){
		//return .5*squareSide*squareSide; //FIXME
		
		double dx = squareX-circleX;
		double dy = squareY-circleY;
		double centerToCenterDistanceSq = dx*dx + dy*dy;
		double centerToCenterDistance = Math.sqrt(centerToCenterDistanceSq);
		double approxSquareRadius = 1.15*squareSide; //TODO should this be 1?
		//maxRadius+minRadius double approxRadiusSum = approxSquareRadius+radius;
		double minRadius = CoreUtil.min(approxSquareRadius, radius); //normally approxSquareRadius
		double maxRadius = CoreUtil.max(approxSquareRadius, radius); //normally radius
		//If maxRadius+minRadius == centerToCenterDistance, theres almost no overlap.
		//If maxRadius-minRadius == centerToCenterDistance, most of square overlaps.
		if(minRadius == 0) return 0;
		double sigmoidParam = -3*(centerToCenterDistance-maxRadius)/minRadius; 
		return CoreUtil.sigmoid(sigmoidParam);
		//TODO more accurate
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*TODO?
	public static double approxVolumeOfSquareCircleIntersection(
			double squareX, double squareY, double squareSide, double circleX, double circleY, double radius){
		throw new RuntimeException("TODO");
	}*/
	
	/** maxRadius is to center of squares, which are larger the farther out they go.
	If wrap, then near edges (or if rings are large) rings wrap around cellGrid.
	<br><br>
	TODO I want radius to expand exponentially, but its not easy to choose which squares
	and at what tree level (higher is exponentially bigger squares) are in each ring,
	so I'm making ringRadiusEnd[] a parameter and caller can choose.
	*/
	public static void fillRings(CellGrid cellGrid, boolean wrap, double... ringRadiusEnd){
		if(wrap) throw new RuntimeException("TODO wrap");
		//TODO I want radius to expand exponentially, but its not easy to choose which squares
		//and at what tree level (higher is exponentially bigger squares) are in each ring.
		/*for(int r=0; r<cellGrid.rings; r++){
			double minRadius = r==0 ? 0 : ringRadiusEnd[r-1];
			double maxRadius = ringRadiusEnd[r];
			//TODO Or would it be better to start with 1 big square and divide it into 4 recursively,
			//dividing deeper the closer to center it gets?
			TODO
		}
		TODO
		*/
		double ringRadiusEndSquared[] = new double[ringRadiusEnd.length];
		for(int r=0; r<ringRadiusEnd.length; r++){
			ringRadiusEndSquared[r] = ringRadiusEnd[r]*ringRadiusEnd[r];
		}
		for(int v=0; v<cellGrid.vectorDims.length; v++){
			WeightsNodeTree tree = cellGrid.vectorDims[v];
			for(int y=0; y<cellGrid.squareSide; y++){
				for(int x=0; x<cellGrid.squareSide; x++){
					Cell cell = cellGrid.cell[y][x];
					double maxRadius = ringRadiusEnd[ringRadiusEnd.length-1];
					Set<WeightsNode> set = splitRecursivelyMoreNearCenter(
						tree, wrap, y, x, maxRadius);
					for(WeightsNode nodeInSomeRing : set){
						Rectangle rect = tree.nodeToRect(nodeInSomeRing);
						double nodeXCenter = rect.x+.5*rect.width;
						double nodeYCenter = rect.y+.5*rect.height;
						double dy = nodeYCenter-y;
						double dx = nodeXCenter-x;
						double distanceToNodeCenterSquared = dx*dx+dy*dy;
						for(int r=0; r<ringRadiusEnd.length; r++){ //each square is in exactly 1 ring
							if(distanceToNodeCenterSquared < ringRadiusEndSquared[r]){
								WeightsNode ringNode = cell.inNodes[v][r];
								//CellGrid already has right BellscalarNode.space as sum of 4 child space vars.
								//Should the BellscalarNode.weightedSumOrInput var be the sum of all leaf nodes
								//weightedSumOrInput vars? If so, simply add nodeInSomeRing with same weight,
								//which would be weight 1 here then normalize it later when know how much space in each ring
								//because ringNode's scalar should be the average per space.
								ringNode.setWeightFrom(nodeInSomeRing, 1.);
								break;
							}
						}
					}
				}
			}
		}
		//All nodeInSomeRing were added to ringNode with weight 1.
		//Divide all those by total BellscalarNode.space in each ring
		//so each ringNode's scalar is average per space.
		for(int v=0; v<cellGrid.vectorDims.length; v++){
			for(int y=0; y<cellGrid.squareSide; y++){
				for(int x=0; x<cellGrid.squareSide; x++){
					Cell cell = cellGrid.cell[y][x];
					for(int r=0; r<ringRadiusEnd.length; r++){
						WeightsNode ringNode = cell.inNodes[v][r];
						double spaceSum = 0;
						for(int n=0; n<ringNode.size; n++){
							WeightsNode child = ringNode.nodeFrom[n];
							if(!(child instanceof WeightsNode)) throw new RuntimeException(
								"Not a "+WeightsNode.class.getName()+": "+child);
							//FIXME This code was written before space var was divided into 2 vars:
							//spaceWeighted and spaceRoundUp, and I didnt think much about
							//which should be here:
							//Removing spaceRoundUp spaceSum += ((WeightsNode)child).spaceRoundUp;
							spaceSum += child.sum;
						}
						for(int n=0; n<ringNode.size; n++){
							//FIXME? Why divide by the spaceRoundUp here? Why not spaceWeighted? If it needs to be spaceRoundUp, then WeightsNode needs both sum and sumRoundUp vars.
							ringNode.weightFrom[n] /= spaceSum;
						}
					}
				}
			}
		}
	}
	
	/** Returns a Set of BellscalarNode which mostly over, a little lacking near edges,
	and a little sticking out past edges, the circle. Larger squares are used near edges,
	trading accuracy for speed since those larger squares are reused many times by other cells.
	This allows rings to extend outward practically to any distance,
	with exponentially increasing ring sizes. Recommended exponents are in range 1.1 to 1.5.
	<br><br>
	TODO make sure to do this deterministicly (not relying on Set order at all)
	so if there are multiple vectorDims, they get the same Rectangles of each size and position.
	*/
	public static Set<WeightsNode> splitRecursivelyMoreNearCenter(
			WeightsNodeTree tree, boolean wrap, int y, int x, double maxRadius){
		if(wrap) throw new RuntimeException("TODO wrap");
		Set<WeightsNode> set = new HashSet();
		splitIntoSet(tree, set, tree.getRoot(), y, x, maxRadius);
		return set;
	}
	
	protected static void splitIntoSet(WeightsNodeTree tree, Set<WeightsNode> set, WeightsNode node, int y, int x, double maxRadius){
		//TODO how to choose when to leave square out of circle? It center could be outside, while some corner(s) are inside and its a very large square and should be split and those parts included
		//TODO how to choose when square is small enough to include without further recursing?
		//TODO recursive function on certain square which it calls recursively 0-4 times and puts into same Set
		Rectangle nodeRect = tree.nodeToRect(node);
		double nodeXCenter = nodeRect.x+.5*nodeRect.width;
		double nodeYCenter = nodeRect.y+.5*nodeRect.height;
		double dy = nodeYCenter-y;
		double dx = nodeXCenter-x;
		double distanceToNodeCenter = Math.sqrt(dx*dx+dy*dy);
		double rectCornerToCorner = Math.sqrt(nodeRect.width*nodeRect.width + nodeRect.height*nodeRect.height);
		//maxPossibleDist may be farther than actual farthest corner depending on angles
		//double maxPossibleDist = distanceToNodeCenter+rectCornerToCorner/2;
		//double minPossibleDist = Math.max(0,distanceToNodeCenter-rectCornerToCorner/2);
		
		//3 choices:
		//* Exclude this Rectangle completely (because its mostly or completely outside maxRadius)
		//* Include this rectangle as it is without recursing
		//* Recurse into all 4 child Rectangles, and have these same 3 choices about each.
		
		//TODO what size should be at what radius? Then compare it to that size limit. How to do this as power of 2 xAndYSize?
		boolean canSplit = node.size != 0;
		//boolean preferSplit = distanceToNodeCenter < rectCornerToCorner*3;
		boolean preferSplit = distanceToNodeCenter < rectCornerToCorner*1.5;
		//boolean preferSplit = distanceToNodeCenter < rectCornerToCorner*.6;
		boolean split = canSplit & preferSplit;
		if(split){
			if(node.size != 4) throw new RuntimeException(
				"Different number of child nodes than 0 or 4: "+node.size+" in "+node);
			for(int i=0; i<node.size; i++){
				WeightsNode child = node.nodeFrom[i];
				if(child instanceof WeightsNode){
					splitIntoSet(tree, set, (WeightsNode)child, y, x, maxRadius);
				}
			}
		}else{
			if(distanceToNodeCenter < maxRadius){ //include
				set.add(node);
			}
		}
	}
	
	/** removes all childs from "BellscalarNode to" and copies them from "BellscalarNode from",
	including the spaceWeighted, spaceRoundUp, and weightedSumOrInput vars.
	*/
	public static void copy(WeightsNode from, WeightsNode to){
		to.clear();
		for(int i=0; i<from.size; i++){
			to.setWeightFrom(from.nodeFrom[i], from.weightFrom[i]);
		}
		to.sum = from.sum;
		//Removing spaceRoundUp to.spaceRoundUp = from.spaceRoundUp;
		to.position = from.position;
	}
	
	public static void refreshWeightedSum(WeightsNode w){
		double s = 0;
		for(int i=0; i<w.size; i++){
			WeightsNode n = w.nodeFrom[i];
			s += w.weightFrom[i]*n.position;
		}
		w.position = s;
	}

}