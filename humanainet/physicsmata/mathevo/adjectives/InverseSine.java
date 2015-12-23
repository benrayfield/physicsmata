package humanainet.physicsmata.mathevo.adjectives;
import humanainet.physicsmata.mathevo.Adjective;

public class InverseSine implements Adjective{	
	
	public double func(double a){
		if(a < -1 || a > 1) return a; //TODO what to return here?
		return Math.asin(a);
	}

}
