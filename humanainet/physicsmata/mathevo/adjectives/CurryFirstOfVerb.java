package humanainet.physicsmata.mathevo.adjectives;
import humanainet.physicsmata.mathevo.Adjective;
import humanainet.physicsmata.mathevo.Verb;

public class CurryFirstOfVerb implements Adjective{
	
	public final double a;
	
	public final Verb v;
	
	public CurryFirstOfVerb(double a, Verb v){
		this.a = a;
		this.v = v;
	}
	
	public double func(double b){
		return v.func(a,b);
	}

}