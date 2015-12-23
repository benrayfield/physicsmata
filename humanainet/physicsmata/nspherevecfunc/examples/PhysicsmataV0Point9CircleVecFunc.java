package humanainet.physicsmata.nspherevecfunc.examples;
import humanaicore.common.CoreUtil;
import humanainet.physicsmata.NsphereVecFunc;

public class PhysicsmataV0Point9CircleVecFunc implements NsphereVecFunc{
	
	//FIXME Without CellGrid.normBySortedPointers, this makes all the pixels converge toward 0 (medium brightness),
	//but thats not what happens in physicsmataV0.9
	
	public void func(double out[], double in[][]){
		double r[] = in[0]; //vectorDims is 1
		//double centerValue = r[0];
		double c=r[0], d=r[1], e=r[2], f=r[3], g=r[4];
		double target =
			c*(1-d)*e*f*(1-g)*.5
			+ (1-c)*(1-d)*(1-e)*f*(1-g)*.2
			+ .3*(1-c)*d*e*(1-f)*(1-g);
		//double target = c;
		//out[0] = centerValue*(1-pixelBrightnessDecay) + pixelBrightnessDecay*target;
		out[0] = target; //because in Physicsmata0.9 pixelBrightnessDecay is 1.
	}
	
	public int vectorDims(){ return 1; }
	
	public int minCircles(){ return 5; }
	
	public int maxCircles(){ return minCircles(); }
	
	public double radius(int whichCircle){
		switch(whichCircle){
		//case 0: return 0;
		case 0: return 3;
		case 1: return 5;
		case 2: return 7;
		case 3: return 9;
		case 4: return 10;
		default: throw new IndexOutOfBoundsException(""+whichCircle);
		}
	}
	
	public double volume(int whichCircle){
		throw new RuntimeException("TODO should NsphereVecFunc have to know the number of dimensions or curve of space?");
	}

}