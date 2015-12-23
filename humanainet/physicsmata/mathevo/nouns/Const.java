package humanainet.physicsmata.mathevo.nouns;
import humanainet.physicsmata.mathevo.Noun;

public class Const extends Number implements Noun{
	
	public final double value;
	
	public static final Const negTwo = new Const(-2);
	
	public static final Const negOne = new Const(-1);
	
	public static final Const negHalf = new Const(-.5);
	
	public static final Const zero = new Const(0);
	
	public static final Const half = new Const(.5);
	
	public static final Const one = new Const(1);
	
	public static final Const two = new Const(2);
	
	public static final Const pi = new Const(Math.PI);
	
	public static final Const e = new Const(Math.E);
	
	public static final Const inverseE = new Const(1/Math.E);
	
	public Const(double value){
		this.value = value;
	}
	
	public double func(){
		return value;
	}

	public int params(){ return 0; }

	public int intValue(){
		return (int)value;
	}

	public long longValue(){
		return (long)value;
	}

	public float floatValue(){
		return (float)value;
	}

	public double doubleValue(){
		return value;
	}

}
