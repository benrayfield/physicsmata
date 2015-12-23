package humanainet.physicsmata.mathevo;

/** Immutable func, output depends only on input params */
public interface VarargFunc{
	
	public double func(double... in);
	
	public int params();

}
