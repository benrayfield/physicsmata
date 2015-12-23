package humanainet.physicsmata.mathevo.verbs;

import humanainet.physicsmata.mathevo.Adjective;
import humanainet.physicsmata.mathevo.ArrayAction;
import humanainet.physicsmata.mathevo.Verb;

public class VerbAction implements ArrayAction{
	
	public final Verb v;
	
	public final int readIndexA, readIndexB, writeIndex;
	
	public VerbAction(Verb v, int readIndexA, int readIndexB, int writeIndex){
		this.v = v;
		this.readIndexA = readIndexA;
		this.readIndexB = readIndexB;
		this.writeIndex = writeIndex;
	}

	public void arrayAction(double d[]){
		d[writeIndex] = v.func(d[readIndexA], d[readIndexB]);
	}

	public int[] indexsRead(){
		return new int[]{readIndexA, readIndexB};
	}

	public int[] indexsWrite(){
		return new int[]{writeIndex};
	}
	
	public int minArraySize(){
		return Math.max(readIndexA, Math.max(readIndexB, writeIndex))+1;
	}

}
