package humanainet.physicsmata.mathevo.verbs;

import humanainet.physicsmata.mathevo.Verb;

public class Pow implements Verb{

	public double func(double a, double b){
		return Math.pow(a,b);
	}

}
