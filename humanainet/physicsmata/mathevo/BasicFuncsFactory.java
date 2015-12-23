package humanainet.physicsmata.mathevo;

import humanainet.physicsmata.mathevo.adjectives.DoubleIt;
import humanainet.physicsmata.mathevo.adjectives.Exp;
import humanainet.physicsmata.mathevo.adjectives.HalfIt;
import humanainet.physicsmata.mathevo.adjectives.InverseSigmoid;
import humanainet.physicsmata.mathevo.adjectives.InverseSine;
import humanainet.physicsmata.mathevo.adjectives.Log;
import humanainet.physicsmata.mathevo.adjectives.Neg;
import humanainet.physicsmata.mathevo.adjectives.OneDividedBy;
import humanainet.physicsmata.mathevo.adjectives.Sigmoid;
import humanainet.physicsmata.mathevo.adjectives.Sine;
import humanainet.physicsmata.mathevo.nouns.Const;
import humanainet.physicsmata.mathevo.verbs.Mult;
import humanainet.physicsmata.mathevo.verbs.Plus;

public class BasicFuncsFactory{
	private BasicFuncsFactory(){}
	
	public static ArrayActionFactory factory = new ArrayActionFactory(new ScalarFunc[]{
		Const.one,
		Const.zero, //derive zero from one plus negOne?
		Const.negOne, //derive negOne from neg one? Its useful to have these at top of screen in each column
		new HalfIt(),
		new DoubleIt(),
		new Neg(),
		new Plus(),
		new Mult(),
		new OneDividedBy(),
		new Log(),
		new Exp(),
		//new Pow(),
		new Sigmoid(),
		new InverseSigmoid(),
		new Sine(),
		new InverseSine()
	});

}
