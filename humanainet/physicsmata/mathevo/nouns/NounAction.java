package humanainet.physicsmata.mathevo.nouns;

import humanainet.physicsmata.mathevo.Adjective;
import humanainet.physicsmata.mathevo.ArrayAction;
import humanainet.physicsmata.mathevo.Noun;

public class NounAction implements ArrayAction{
	
	public final Noun n;
	
	public final int writeIndex;
	
	public NounAction(Noun n, int writeIndex){
		this.n = n;
		this.writeIndex = writeIndex;
	}

	public void arrayAction(double d[]){
		d[writeIndex] = n.func();
	}

	public int[] indexsRead(){
		return new int[0];
	}

	public int[] indexsWrite(){
		return new int[]{writeIndex};
	}
	
	public int minArraySize(){
		return writeIndex+1;
	}

}
