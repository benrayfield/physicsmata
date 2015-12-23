package humanainet.physicsmata.mathevo.adjectives;

import humanainet.physicsmata.mathevo.Adjective;

public class Log implements Adjective{

	public double func(double a){
		return Math.log(Math.abs(a));
	}

}
