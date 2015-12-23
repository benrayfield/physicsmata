package humanainet.physicsmata.mathevo.verbs;

import humanainet.physicsmata.mathevo.Verb;

public class Mult implements Verb/*VarargFunc*/{

	/*public double func(double... in){
		return in[0]*in[1];
	}

	public int params(){ return 2; }
	*/
	
	public double func(double a, double b){
		return a*b;
	}

}
