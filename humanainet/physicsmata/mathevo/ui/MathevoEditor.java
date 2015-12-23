package humanainet.physicsmata.mathevo.ui;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import humanainet.physicsmata.mathevo.ArrayAction;
import humanainet.physicsmata.mathevo.ArrayActionFactory;
import humanainet.physicsmata.mathevo.FuncSequence;

public class MathevoEditor extends JPanel{
	
	protected final MathevoColumnEditor cols[];
	
	public final int paramsOfFuncBeingEdited;
	
	/** Copies the on screen contents of the editor to a function object,
	to be used, for example, in a bellautomata.nspherevecfunc.MathevoVecFunc
	which is a cellular automata func and changes how each pixel reacts
	when it sees the average brightness of different size circles around it.
	Or it could be used like Audivolv, to read in microphone amplitudes
	and a small piece of the array for remembering numbers,
	and writes at the end the new values of numbers to remember
	and 2 more indexs for speaker amplitudes, 44100 times per second.
	*/
	public ArrayAction func(){
		ArrayAction a[] = new ArrayAction[cols.length-paramsOfFuncBeingEdited];
		for(int c=paramsOfFuncBeingEdited; c<cols.length; c++){
			a[c-paramsOfFuncBeingEdited] = cols[c].func;
		}
		return new FuncSequence(a);
	}
	
	public MathevoColumnEditor column(int c){
		return cols[c];
	}
	
	public volatile boolean runningOnFuncChange = false;
	
	public MathevoEditor(ArrayActionFactory funcs, int magnify, int columns, int paramsOfFuncBeingEdited, final Runnable onFuncChange){
		this.paramsOfFuncBeingEdited = paramsOfFuncBeingEdited;
		setLayout(new GridLayout(1, 0));
		cols = new MathevoColumnEditor[columns];
		final MathevoEditor editor = this;
		for(int c=0; c<columns; c++){
			boolean isParamColumn = c<paramsOfFuncBeingEdited;
			Color backgroundColor = isParamColumn ? new Color(.2f, .2f, .2f) : new Color(.4f, .4f, .4f);
			if(c == columns-1) backgroundColor = new Color(.7f, .7f, .7f);
			final int cCopy = c;
			final boolean isLastColumn = c==columns-1; //output of func being edited
			Runnable onFuncChange2 = new Runnable(){
				public void run(){
					if(editor.runningOnFuncChange) return;
					editor.runningOnFuncChange = true;
					/*if(isLastColumn){ //turn off isReachableFromLastColumn for all below, then turn them on						
					}
					MathevoColumnEditor thisColEditor = editor.cols[cCopy];
					for(int readsFromIndex : thisColEditor.func.indexsRead()){
						MathevoColumnEditor observed = editor.cols[readsFromIndex];
						observed.isReachableFromLastColumn = true;
					}
					*/
					for(MathevoColumnEditor e : editor.cols){
						e.isReachableFromLastColumn = false;
						//e.repaint();
					}
					MathevoColumnEditor outputColumn = editor.cols[editor.cols.length-1];
					outputColumn.isReachableFromLastColumn = true; //reachable from itself
					for(int i=editor.cols.length-1; i>=0; i--){
						MathevoColumnEditor e = editor.cols[i];
						if(e.isReachableFromLastColumn){
							for(int readsFromIndex : e.func.indexsRead()){
								MathevoColumnEditor observed = editor.cols[readsFromIndex];
								observed.isReachableFromLastColumn = true;
							}
						}
						//e.repaint();
					}
					editor.repaint();
					onFuncChange.run();
					editor.runningOnFuncChange = false;
				}
			};
			cols[c] = new MathevoColumnEditor(funcs, magnify, c, columns, onFuncChange2, backgroundColor, isParamColumn);
			add(cols[c]);
		}
	}

}
