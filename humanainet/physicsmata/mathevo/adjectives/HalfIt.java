package humanainet.physicsmata.mathevo.adjectives;

import humanainet.physicsmata.mathevo.Adjective;

public class HalfIt implements Adjective/*VarargFunc*/{

	/*public double func(double... in){
		return in[0]*.5;
	}

	public int params(){ return 1; }
	*/
	
	public double func(double a){
		return a*.5;
	}

}
