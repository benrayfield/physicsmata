package humanainet.physicsmata.mathevo.adjectives;

import humanaicore.common.CoreUtil;
import humanainet.physicsmata.mathevo.Adjective;

public class Sigmoid implements Adjective/*VarargFunc*/{

	/*public double func(double... in){
		return CoreUtil.sigmoid(in[0]);
	}

	public int params(){ return 1; }
	*/
	
	public double func(double a){
		return CoreUtil.sigmoid(a);
	}

}
