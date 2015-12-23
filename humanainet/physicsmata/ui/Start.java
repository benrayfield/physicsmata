/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package humanainet.physicsmata.ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import humanaicore.common.CoreUtil;
import humanaicore.realtimeschedulerTodoThreadpool.RealtimeScheduler;
import humanainet.physicsmata.CellGrid;
import humanainet.physicsmata.CellUtil;
import humanainet.physicsmata.NsphereVecFunc;
import humanainet.physicsmata.mathevo.ArrayAction;
import humanainet.physicsmata.mathevo.BasicFuncsFactory;
import humanainet.physicsmata.mathevo.ui.MathevoEditor;
import humanainet.physicsmata.nspherevecfunc.MathevoVecFunc;
import humanainet.physicsmata.nspherevecfunc.examples.BlurBalance;
import humanainet.physicsmata.nspherevecfunc.examples.PhysicsmataV0Point9CircleVecFunc;
import humanainet.physicsmata.nspherevecfunc.examples.TestRingVecFunc;

public class Start{
	
	static MathevoEditor mathevoEditor = null;
	
	public static void main(String args[]){
		JFrame window = new JFrame("Physicsmata 2.1.0 (opensource GNU GPL 2+ unzip this jar file to get source code)");
		System.out.println("TODO use WeightsNode.weightsFunc to call nspherevecfunc");
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		int treeHeight = 6;
		//int treeHeight = 7;
		//int treeHeight = 8;
		int vectorDims = 1;
		//int rings = 8;
		//int rings = 5;
		//int rings = 12;
		//int rings = 1;
		//int rings = 3;
		//CircleVecFunc func = new PhysicsmataV0Point9CircleVecFunc();
		NsphereVecFunc func = new TestRingVecFunc();
		
		
		
		//CircleVecFunc func = new BlurBalance();
		int circles = func.minCircles();
		final CellGrid cellGrid = new CellGrid(treeHeight, vectorDims, circles, func);
		
		cellGrid.normBySortedPointers = true;
		
		//CellUtil.fillRings(cellGrid, false, 3, 5, 9, 15, 20, 24, 27, 29);
		//CellUtil.fillRingsWithCircleSums(cellGrid, false, 2, 5, 9, 15, 24);
		boolean wrapY = true, wrapX = true;
		//boolean wrapY = false, wrapX = false;
		//boolean wrapY = true, wrapX = false;
		//boolean wrapY = false, wrapX = true;
		boolean sumInsteadOfAve = true;
		boolean includePartialTouches = true;
		final double radius[] = new double[circles];
		//leave ringSizes[0] as 0 so its the leaf itself
		/*for(int i=1; i<radius.length; i++){
			//ringSizes[i] = 2.5*Math.exp(i*.3);
			radius[i] = Math.exp(i*.3);
		}*/
		for(int c=0; c<circles; c++) radius[c] = func.radius(c);
		CellUtil.fillWithCircleSums(sumInsteadOfAve, includePartialTouches, cellGrid,
			wrapY, wrapX, radius);
			//1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
			//3.7, 6, 9, 12);
		//int magnify = 3;
		int magnify = 8;
		//int magnify = 5;
		//int magnify = 25;
		//int magnify = 4;
		//int magnify = 3;
		CellGridTabs tabs = new CellGridTabs(cellGrid, magnify);
		
		Runnable onFuncChange = new Runnable(){
			public void run(){
				ArrayAction func = mathevoEditor.func();
				cellGrid.setAllCellsFuncTo(new MathevoVecFunc(radius, func));
			}
		};
		
		mathevoEditor = new MathevoEditor(BasicFuncsFactory.factory, 14, 18, radius.length, onFuncChange);
		
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(tabs, BorderLayout.CENTER);
		JTextArea text = new JTextArea("Everything you do here is to flow information from the left columns to the right column, so you'll see it on screen in patterns of waves you build. Left columns are average brightness at different size circles around each point in the cellular automata (seen on the left). Right column is the end of the math you're building. It decides the next brightness for each point. In the middle is a network of numbers you can use for anything. The top red squares choose which kind of math, like plus, multiply, sine, sigmoid, neg, half, double, exp, etc. Each of those kinds of math looks at 0, 1, or 2 numbers in the columns to its left. You choose which columns by clicking in the green and blue squares which activate the columns that can be reached from the output column on the right.");
		text.setWrapStyleWord(true);
		text.setEditable(false);
		text.setLineWrap(true);
		panel.add(text, BorderLayout.NORTH);
		panel.add(mathevoEditor, BorderLayout.EAST);
		window.add(panel);
		window.pack();
		System.out.println("window size "+window.getSize());
		CoreUtil.moveToScreenCenter(window);
		window.setVisible(true);
		cellGrid.randomizeCellOutScalars(CoreUtil.strongRand);
		RealtimeScheduler.start(tabs);
	}
}