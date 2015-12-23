package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

/** an arbitrary small func to get started testing bellAutomata in CellGrid.
Func always returns between -1 and 1.
*/
public class SimpleCurvedWaveAlternatingStabilizerVecFunc implements NsphereVecFunc{
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		double a = r[0], b = r[1], c = r[2];
		boolean outBit = false;
		double sum = 0;
		sum += .4*a-c;
		outBit = sum < 0;
		out[0] = outBit ? 1 : -1;
	}
	
	public int vectorDims(){ return 1; }
	
	public int minCircles(){ return 3; }
	
	public int maxCircles(){ return minCircles(); }
	
	public double radius(int whichCircle){
		//return 2.5*Math.exp(whichCircle*.3);
		return 5*Math.exp(whichCircle*.7);
		//return 1.5*Math.exp(whichCircle*.3);
		//return 7;
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}