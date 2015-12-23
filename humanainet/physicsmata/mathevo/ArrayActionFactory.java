package humanainet.physicsmata.mathevo;

import humanainet.physicsmata.mathevo.adjectives.AdjectiveAction;
import humanainet.physicsmata.mathevo.nouns.NounAction;
import humanainet.physicsmata.mathevo.verbs.VerbAction;

public class ArrayActionFactory{
	
	protected final ScalarFunc funcs[];
	public final int howManyFuncs;
	public ScalarFunc getFunc(int i){ return funcs[i]; }
	
	public ArrayActionFactory(ScalarFunc funcs[]){
		this.funcs = funcs;
		howManyFuncs = funcs.length;
	}
	
	/** If func has 0 or 1 params, ignores both or just paramB,
	which aligns to how its displayed as always 2 selections per column.
	*/
	public ArrayAction get(ScalarFunc f, int paramA, int paramB, int writeIndex){
		if(f instanceof Adjective){ //most common
			return new AdjectiveAction((Adjective)f, paramA, writeIndex);
		}
		if(f instanceof Verb){
			return new VerbAction((Verb)f, paramA, paramB, writeIndex);
		}
		if(f instanceof Noun){
			return new NounAction((Noun)f, writeIndex);
		}
		throw new RuntimeException("Unknown func type: "+f.getClass());
	}
	
	public ArrayAction get(int whichFunc, int paramA, int paramB, int writeIndex){
		return get(funcs[whichFunc], paramA, paramB, writeIndex);
	}

}
