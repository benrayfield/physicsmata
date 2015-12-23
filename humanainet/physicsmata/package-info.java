package humanainet.physicsmata;
/** This text copied from BellscalarNode before merging it with WeightsNode.
<br><br>
A bellscalar is a sum of coin flips, each as -1 or 1, and a quantity
of coins flipped, except more generally as floating points (scalars).
As coins it converges to stdDev of sqrt(coinsFlipped).
The space var is quantity of scalars weightedSummed, of the sum var.
As a WeightsNode, each node the sum vars come from is paired with a weight.
<br><br>
TODO move SparseNode and WeightsNode to a more neutral place than bellautomata or blackholecortex.
<br><br>
------------------
<br><br>
This is for bainterfCircleSum. bainterf means Bell Automata Interface aka bellscalarNode.
<br><br>
This is the default way to use bellscalarNodes, but not the only way allowed. They dont have to be used for weightedAve, so their weights dont have to sum to 1, but its often useful to.
<br><br>
b = parent bellScalarNode.
c = each child of b.
<br><br>
Each bellscalarNode has 1 weight per child and 2 vars: weightSum and space.
<br><br>
weightSum always ranges -1 to 1 and is weightedAve of weightedSum of its childs.
<br><br>
Each weight is nonnegative.
<br><br>
Sum of weights is 1.
<br><br>
touchFraction ranges 0 to 1 (as implied by fraction). Its 1 for bellscalarNodes inside
the circle (of bainterfCircleSum) and the fraction of how much the square overlaps the
circle for squares that partially intersect that circle. These squares can be at any
treeHeight, normally all the same lowest treeHeight within the same bainterfCircleSum.
<br><br>
CHOOSE:
* touchFraction will be stored in bellscalarNode because it cant be derived from space vars.
* touchFraction is not stored in a bellscalarNode. Its part of the calculation of weights.
<br><br>
b.space = weightedSum of c.space for all child c, where weight is touchFraction.
<br><br>
IS THIS TRUE? If not, everything which follows it isnt. No, its only true when all
touchFraction are 1.
(
<br><br>
	b's weight of c = touchFraction*c.space/b.space.
<br><br>
	b.space is sum of touchFraction*c.space, for all child c.
<br><br>
	I want to get touchFraction, which is not directly stored, for each child c. Can it be
	derived from the space vars?
<br><br>
	TODO
<br><br>
	(touchFraction of child c) = (b's weight of c)/(c.space/b.space)
	(touchFraction of child c) = (b's weight of c)*b.space/c.space
)
<br><br>

b's weight of c = touchFraction*c.space/(sum of all touchFraction*c.space).
<br><br>
It makes more sense to hold touchFraction=weight.
<br><br>
Space var was originally designed for touchFraction=1, so its use is limited to branches of forest where that is true, which is in cellGrid and 1 level up to node representing circle. What should that node's space var be?
<br><br>
If that node's space var is sum of touchFraction*c.space instead of sum of c.space, would this original equation work?:
b's weight of c = touchFraction*c.space/b.space.
Yes it is, since b.space=(sum of all touchFraction*c.space))
...and if true then...
(touchFraction of child c) = (b's weight of c)/(c.space/b.space)
<br><br>
I'll add a spaceWeighted var, and rename space var to spaceRoundUp. The new equations are:
<br><br>
spaceRoundUp = sum of child.spaceRoundUp. //the whole space used, regardless of only partially using some of it.
<br><br>
spaceWeighted = sum of touchFraction*child.spaceWeighted.
<br><br>
spaceRoundUp=spaceWeighted when all touchFractions of childs and recursively down are 1.
<br><br>
b's weight of c = (b's touchFraction of c)*c.spaceWeighted/b.spaceWeighted
<br><br>
(b's touchFraction of c) = (b's weight of c)/(c.spaceWeighted/b.spaceWeighted)
<br><br>
(b's touchFraction of c) = (b's weight of c)*b.spaceWeighted/c.spaceWeighted.
<br><br>
TODO verify those last few equations.
<br><br><br>
TODO create version of BellscalarNode that uses floats instead of doubles,
andOr add a func to swap indexs so all the nodes with weight 1 are in a block of
the first indexs, and add a var which says how many such indexs,
and loop over that many without reading the array, to improve caching
especially in multithreaded mode.
*/