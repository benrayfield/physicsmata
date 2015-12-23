package humanainet.physicsmata.mathevo.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import humanainet.physicsmata.mathevo.ArrayAction;
import humanainet.physicsmata.mathevo.ArrayActionFactory;
import humanainet.physicsmata.mathevo.BasicFuncsFactory;
import humanainet.physicsmata.mathevo.ScalarFunc;

public class MathevoColumnEditor extends JPanel implements MouseListener/*, MouseMotionListener*/{
	
	public ArrayAction func;
	
	public final int column, columns;
	
	public int funcIndex, readIndexA, readIndexB;
	
	public final int magnify;
	
	public final ArrayActionFactory funcs;
	
	public final Runnable onFuncChange;
	
	public final boolean isParamColumn;
	
	public volatile boolean isReachableFromLastColumn = false;
	
	public MathevoColumnEditor(int column, int columns, Runnable onFuncChange, Color backgroundColor, boolean isParamColumn){
		this(BasicFuncsFactory.factory, 8, column, columns, onFuncChange, backgroundColor, isParamColumn);
	}
	
	public MathevoColumnEditor(ArrayActionFactory funcs, int magnify, int column, int columns, Runnable onFuncChange, Color backgroundColor, boolean isParamColumn){
		this.func = funcs.get(0, 0, 0, column);
		this.funcs = funcs;
		this.column = column;
		this.columns = columns;
		this.magnify = magnify;
		Dimension d = new Dimension(magnify, magnify*(columns+funcs.howManyFuncs));
		setMinimumSize(d);
		setPreferredSize(d);
		addMouseListener(this);
		//addMouseMotionListener(this);
		this.onFuncChange = onFuncChange;
		setBackground(backgroundColor);
		this.isParamColumn = isParamColumn;
		isReachableFromLastColumn = column==columns-1;
	}
	
	public void paint(Graphics g){
		//TODO only paint visible part
		//int magnify = 8;
		g.setColor(getBackground());
		g.fillRect(0, 0, magnify, magnify*(columns+funcs.howManyFuncs));
		if(isReachableFromLastColumn){
			boolean hasFunc = !isParamColumn; //TODO not for params in lowest indexs
			if(hasFunc){
				g.setColor(Color.red);
				g.fillRect(0, funcIndex*magnify, magnify, magnify);
				int params = func.indexsRead().length;
				if(params > 0){
					g.setColor(Color.green);
					g.fillRect(0, (readIndexA+funcs.howManyFuncs)*magnify, magnify, magnify);
				}
				if(params > 1){
					if(readIndexB != readIndexA){
						g.setColor(Color.blue);
						g.fillRect(0, (readIndexB+funcs.howManyFuncs)*magnify, magnify, magnify);
					}
				}
			}
			g.setColor(Color.white);
			g.fillRect(0, (column+funcs.howManyFuncs)*magnify, magnify, magnify);
		}
	}

	public void mouseClicked(MouseEvent e){}

	public void mousePressed(MouseEvent e){
		if(isParamColumn) return;
		int row = e.getY()/magnify;
		System.out.println("row="+row);
		if(row < funcs.howManyFuncs){ //choose func type
			funcIndex = row;
		}else{ //choose 1 of the 2 param indexs, which are earlier columns
			int otherColumn = row-funcs.howManyFuncs;
			if(otherColumn < column){
				if(readIndexA == otherColumn){ //swap
					int temp = readIndexA;
					readIndexA = readIndexB;
					readIndexB = temp;
				}else{ //second last click is paramB. Last click is paramA
					readIndexB = readIndexA;
					readIndexA = otherColumn;
					//System.out.println("a="+readIndexA+" b="+readIndexB);
				}
			}
		}
		func = funcs.get(funcIndex, readIndexA, readIndexB, column);
		onFuncChange.run();
		//repaint();
	}

	public void mouseReleased(MouseEvent e){}

	public void mouseEntered(MouseEvent e){}

	public void mouseExited(MouseEvent e){}
	
	/*public void mouseDragged(MouseEvent e){
		mousePressed(e);
	}
	
	public void mouseMoved(MouseEvent e){}
	*/

}
