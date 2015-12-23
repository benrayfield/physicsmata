package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

/** an arbitrary small func to get started testing bellAutomata in CellGrid.
Func always returns between -1 and 1.
*/
public class BlurBalance implements NsphereVecFunc{
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		double a = r[0], b = r[1];
		out[0] = CoreUtil.sigmoid(.4*b-a*1.2)*2-1;
	}
	
	public int vectorDims(){ return 1; }
	
	public int minCircles(){ return 2; }
	
	public int maxCircles(){ return minCircles(); }
	
	public double radius(int whichCircle){
		switch(whichCircle){
		case 0: return 3;
		case 1: return 7;
		default: return 20;
		}
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}