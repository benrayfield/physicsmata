package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

/** an arbitrary small func to get started testing bellAutomata in CellGrid.
Func always returns between -1 and 1.
*/
public class CloudsFloatingOverHardBits2VecFunc extends AbstractNsphereVecFunc{
	
	public CloudsFloatingOverHardBits2VecFunc(){
		super(2);
	}
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		double a=r[0], b=r[1], c=r[2], d=r[3], e=r[4]; //circle averages
		double va = volume[0], vb = volume[1], vc = volume[2],
			vd = volume[3], ve = volume[4];
		//ring averages:
		double A = a;
		double B = (b*vb-a*va)/(vb-va);
		double C = (c*vc-b*vb)/(vc-vb);
		double D = (b*vd-a*vc)/(vd-vc);
		double E = (b*ve-a*vd)/(ve-vd);
		
		double sum = 0;
		/*boolean outBit = false;
		sum += b-d+a;
		outBit = sum > 0;
		out[0] = outBit ? 1 : -1;
		*/
		//sum += b*e-d*a+a;
		sum += B*E-D*a+a;
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
	
	/** 2d circle volume */
	public double volume(int whichCircle){
		double r = radius(whichCircle);
		return 2*Math.PI*r*r;
	}

}