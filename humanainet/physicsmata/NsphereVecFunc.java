package humanainet.physicsmata;

/** A func that takes sum of vectors at each ring
(UPDATE: circles/spheres/hyperspheres),
a range or blur of radius, for some integer number of rings,
and returns a vector as next cellular automata value at current point.
<br><br>
Number of rings is not normally same as dims of vector.
For example, vector may be 2d as complexnum, while it has many rings
in which those complexnums are summed.
Cellular automata of n vectorDims are represented as n BellscalarNode at each point,
which may be stored in a FlatPYX of p=n.
<br><br>
The word "ring" can substitute for "sphere surface" or hypersurface,
since its not specific to any number of dimensions of cellular automata,
while 2d is default.
*/
public interface NsphereVecFunc{
	
	/** nsphereVolume is needed for funcs which subtract between
	nspheres to get a sum of outer ring/nsphereSurface.
	TODO if preferredRadius is required instead of just preferred,
	then nsphereVolume would equal it, so I'll do that instead and
	remove the nsphereVolume param here.
	*/
	public void func(double out[], double in[][]/*, double nsphereVolume[]*/);
	
	/** out.length and in.length, params of func(double[],double[][]) */
	public int vectorDims();
	
	/** Min in[eachInt].length (which are all equal in same call),
	where in[][] is second param of func(double[],double[][])
	*/
	public int minCircles();
	
	/** Max in[eachInt].length (which are all equal in same call),
	where in[][] is second param of func(double[],double[][])
	*/
	public int maxCircles();
	
	/** See comment in volume(int) about why volume cant always be
	known from just the radius. This software can represent curved space.
	*/
	public double radius(int whichCircle);
	
	/** Since BellscalarNode can represent curved space
	as a dimensionless topological manifold, where 2d is the default,
	radius and volume are not always related by circle equation.
	It doesnt even have to be an integer number of dimensions.
	However they connect, and preferably it be a smooth shape.
	*/
	public double volume(int whichCircle);

}
