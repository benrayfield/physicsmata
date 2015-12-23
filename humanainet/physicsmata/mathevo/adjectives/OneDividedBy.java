package humanainet.physicsmata.mathevo.adjectives;

import humanainet.physicsmata.mathevo.Adjective;

public class OneDividedBy implements Adjective/*VarargFunc*/{

	/*public double func(double... in){
		return 1/in[0];
	}

	public int params(){ return 1; }
	*/
	
	public double func(double a){
		return 1/a;
	}

}
