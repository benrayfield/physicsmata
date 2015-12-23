package humanainet.physicsmata.nspherevecfunc.examples;
import humanainet.physicsmata.NsphereVecFunc;

public abstract class AbstractNsphereVecFunc implements NsphereVecFunc{
	
	protected final double volume[];
	
	public AbstractNsphereVecFunc(int flatDims){
		volume = new double[maxCircles()];
		for(int i=0; i<volume.length; i++){
			if(flatDims != 2) throw new RuntimeException("TODO");
			double radius = radius(i);
			volume[i] = radius*radius*Math.PI;
		}
	}

}
