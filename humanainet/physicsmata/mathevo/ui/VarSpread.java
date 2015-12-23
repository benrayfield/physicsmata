package humanainet.physicsmata.mathevo.ui;
import java.awt.Graphics;
import java.util.Arrays;

import javax.swing.JPanel;

import humanainet.physicsmata.mathevo.Adjective;
import humanainet.physicsmata.mathevo.adjectives.Sigmoid;

import java.awt.Color;

/** A column of possible values of the same var.
Example: 256 rows, 1 pixel high each, where brightness is
proportional to how often the var is in that small range
of values.
*/
public class VarSpread extends JPanel{
	
	/** always sums to 1 */
	public final double howOftenItsEachValue[];
	
	/** If it returns outside fraction range, uses 0 or 1 */
	public final Adjective valueToFraction;
	
	public VarSpread(){
		this(256);
	}
	
	public VarSpread(int possibleValues){
		this(possibleValues, new Sigmoid());
	}
	
	public VarSpread(int possibleValues, Adjective valueToFraction){
		howOftenItsEachValue = new double[possibleValues];
		this.valueToFraction = valueToFraction;
		Arrays.fill(howOftenItsEachValue, 1/howOftenItsEachValue.length);
	}
	
	/** literalValue can be any finite value */
	public void observe(double literalValue, double decay){
		double fraction = valueToFraction.func(literalValue);
		int index;
		if(fraction <= 0) index = 0;
		else if(fraction >= 1) index = howOftenItsEachValue.length;
		else index = (int)(fraction*howOftenItsEachValue.length);
		observe(index, decay);
	}
	
	public void observe(int indexOfValue, double decay){
		decay = Math.max(0, Math.min(decay, 1));
		double sum = 0;
		for(double d : howOftenItsEachValue){
			sum += d;
		}
		double targetSum = 1-decay;
		double mult = targetSum/sum;
		for(int i=0; i<howOftenItsEachValue.length; i++){
			howOftenItsEachValue[i] *= mult;
		}
		howOftenItsEachValue[indexOfValue] += decay;
	}
	
	public void paint(Graphics g){
		int w = getWidth();
		double max = 0; //draw most frequent value as white
		for(double d : howOftenItsEachValue){
			max = Math.max(max, d);
		}
		for(int i=0; i<howOftenItsEachValue.length; i++){
			float bright = (float)(howOftenItsEachValue[i]/max);
			g.setColor(new Color(bright, bright, bright));
			g.drawLine(0, i, w, i);
		}
	}

}
