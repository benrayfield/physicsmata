package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

/** an arbitrary small func to get started testing bellAutomata in CellGrid.
Func always returns between -1 and 1.
*/
public class CloudsFlowingOverHardBitsVecFunc implements NsphereVecFunc{
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		double a=r[0], b=r[1], c=r[2], d=r[3], e=r[4];
		double sum = 0;
		/*boolean outBit = false;
		sum += b-d+a;
		outBit = sum > 0;
		out[0] = outBit ? 1 : -1;
		*/
		sum += b*e-d*a+a;
		out[0] = CoreUtil.sigmoid(sum);
	}
	
	public int vectorDims(){ return 1; }
	
	public int minCircles(){ return 5; }
	
	public int maxCircles(){ return minCircles(); }
	
	public double radius(int whichCircle){
		//return 3*Math.exp(whichCircle*.7);
		double circleArea = 64*whichCircle;
		return Math.sqrt(circleArea)/Math.PI;
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}