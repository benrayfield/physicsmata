package humanainet.physicsmata.nspherevecfunc;

import humanainet.physicsmata.nspherevecfunc.examples.AbstractNsphereVecFunc;

/** An NsphereVecFunc of 1 vectorDim and any small number of circles/nspheres
which are named by letters a, b, c, and so on. Rings are named by
capital letters and are the outer circle minus the inner circle
multiplied to adjust for the difference in circle sizes.
 * @author ben
 *
 */
public class EquationVecFunc extends AbstractNsphereVecFunc{
	
	//TODO this is being replaced by mathevo, but maybe I want equations in written form too
	
	/** sumEqualsWhat is something like B*E-D*a+a
	<br><br>
	Or this way, not an equation, but reuses vars:
	a=ring(0,3); b=ring(3,5); c=ring(5,9); x=a*b; sum=x-c;
	*/
	public EquationVecFunc(String sumEqualsWhat){
		super(2);
	}

	public void func(double[] out, double[][] in){
		// TODO Auto-generated method stub
		
	}

	public int vectorDims(){
		// TODO Auto-generated method stub
		return 0;
	}

	public int minCircles(){
		// TODO Auto-generated method stub
		return 0;
	}

	public int maxCircles(){
		// TODO Auto-generated method stub
		return 0;
	}

	public double radius(int whichCircle){
		// TODO Auto-generated method stub
		return 0;
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}
