package humanainet.physicsmata.mathevo;

/** a scalar array which starts with n indexs filled,
normally from NsphereVecFunc's parameters,
and from n+1 and up, runs 1 func to get scalar written
in each next index of the array. Params of each func
come from specific indexs lower in the array so its acyclic.
*/
public class FuncSequence implements ArrayAction{
	
	//TODO fill first n indexs with GetParam.
	
	//public final int startIndex;
	
	protected final ArrayAction funcs[];
	
	public final int minArraySize;
	
	public FuncSequence(/*int startIndex,*/ ArrayAction... funcs){
		//this.startIndex = startIndex;
		this.funcs = funcs;
		int minArraySize = 0;
		for(ArrayAction a : funcs){
			minArraySize = Math.max(a.minArraySize(), minArraySize);
		}
		this.minArraySize = minArraySize;
	}
	
	/** from 0 to startIndex-1 has been filled in already.
	Next, fill in the remaining indexs using func for each
	and which lower indexs it points at.
	*/
	public void arrayAction(double d[]){
		for(ArrayAction a : funcs){
			a.arrayAction(d);
		}
	}

	public int[] indexsRead(){
		throw new RuntimeException("TODO");
	}

	public int[] indexsWrite(){
		throw new RuntimeException("TODO");
	}
	
	public int minArraySize(){ return minArraySize; }

}
