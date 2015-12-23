package humanainet.physicsmata.mathevo.adjectives;

import humanaicore.common.CoreUtil;
import humanainet.physicsmata.mathevo.Adjective;

public class InverseSigmoid implements Adjective{
	
	public double func(double a){
		if(a <= 0 || 1 <= a) return a; //TODO what to return here?
		return CoreUtil.inverseSigmoid(a);
	}

}
