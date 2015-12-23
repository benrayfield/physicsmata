package humanainet.physicsmata.mathevo.adjectives;

import humanainet.physicsmata.mathevo.Adjective;

public class Exp implements Adjective/*VarargFunc*/{

	/*public double func(double... in){
		return Math.exp(in[0]);
	}

	public int params(){ return 1; }
	*/
	
	public double func(double a){
		return Math.exp(a);
	}

}
