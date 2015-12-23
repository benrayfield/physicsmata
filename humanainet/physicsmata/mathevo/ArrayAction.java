package humanainet.physicsmata.mathevo;

public interface ArrayAction{
	
	public void arrayAction(double d[]);
	
	/** Dont include any indexs that are never read
	before this ArrayAction writes them
	(and reads by later child funcs). Those are temp vars.
	*/
	public int[] indexsRead();
	
	/** normally 1 index which is above all read indexs */
	public int[] indexsWrite();
	
	/** Max of ints in indexsRead() and indexsWrite(), plus one */
	public int minArraySize();

}
