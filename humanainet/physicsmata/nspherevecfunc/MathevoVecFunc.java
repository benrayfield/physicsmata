package humanainet.physicsmata.nspherevecfunc;
import humanainet.physicsmata.NsphereVecFunc;
import humanainet.physicsmata.mathevo.ArrayAction;

public class MathevoVecFunc implements NsphereVecFunc{
	
	protected final double radius[];
	
	public final ArrayAction mathevoFunc;
	
	public MathevoVecFunc(double radius[], ArrayAction mathevoFunc){
		this.radius = radius.clone();
		this.mathevoFunc = mathevoFunc;
	}

	public void func(double out[], double in[][]){
		double r[] = in[0];
		double stack[] = new double[mathevoFunc.minArraySize()];
		//copy params into first indexs
		System.arraycopy(r, 0, stack, 0, r.length);
		//Run func which reads and writes in the array,
		//normally low index to high sequentially
		mathevoFunc.arrayAction(stack);
		//get return from last index
		out[0] = stack[stack.length-1];
		//out[0] = stack[1];
	}

	public int vectorDims(){ return 1; }

	public int minCircles(){ return radius.length; }

	public int maxCircles(){ return minCircles(); }

	public double radius(int whichCircle){ return radius[whichCircle]; }
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}
