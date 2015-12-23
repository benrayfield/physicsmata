package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

/** an arbitrary small func to get started testing bellAutomata in CellGrid.
Func always returns between -1 and 1.
*/
public class BlobPathsWithVibratingSidesVecFunc implements NsphereVecFunc{
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		//double a = r[0];
		double a = r[0], b = r[1], c = r[2];
		//double sigmoidParam = b-3*a*c;
		//out[0] = 2*CoreUtil.sigmoid(sigmoidParam)-1;
		//out[0] = out[0]*(1-decay)+decay*(a+.02);
		//double randBifraction = CoreUtil.weakRand.nextDouble()*2-1;
		//out[0] = a*.95 + .05*randBifraction;
		//out[0] = a;
		out[0] = b-a*c;
		//out[0] = CoreUtil.weakRand.nextDouble()*2-1;
	}
	
	public int vectorDims(){ return 1; }
	
	public int minCircles(){ return 3; }
	
	public int maxCircles(){ return minCircles(); }
	
	public double radius(int whichCircle){
		//return 2.5*Math.exp(whichCircle*.3);
		return 1.5*Math.exp(whichCircle*.7);
		//return 1.5*Math.exp(whichCircle*.3);
		//return 7;
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}