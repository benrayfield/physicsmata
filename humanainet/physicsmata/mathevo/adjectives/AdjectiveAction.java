package humanainet.physicsmata.mathevo.adjectives;

import humanainet.physicsmata.mathevo.Adjective;
import humanainet.physicsmata.mathevo.ArrayAction;

public class AdjectiveAction implements ArrayAction{
	
	public final Adjective a;
	
	public final int readIndex, writeIndex;
	
	public AdjectiveAction(Adjective a, int readIndex, int writeIndex){
		this.a = a;
		this.readIndex = readIndex;
		this.writeIndex = writeIndex;
	}

	public void arrayAction(double d[]){
		d[writeIndex] = a.func(d[readIndex]);
	}

	public int[] indexsRead(){
		return new int[]{readIndex};
	}

	public int[] indexsWrite(){
		return new int[]{writeIndex};
	}
	
	public int minArraySize(){
		return Math.max(readIndex, writeIndex)+1;
	}

}
