package humanainet.physicsmata.ui;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import humanaicore.common.CoreUtil;
import humanainet.physicsmata.CellGrid;
import humanainet.physicsmata.CellUtil;
import humanainet.physicsmata.NsphereVecFunc;
import humanainet.physicsmata.nspherevecfunc.examples.PhysicsmataV0Point9CircleVecFunc;
import humanainet.physicsmata.nspherevecfunc.examples.TestRingVecFunc;

public class TestCellGridOnly{
	public static void main(String args[]){
		JFrame window = new JFrame("Test rings");
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		int treeHeight = 6;
		//int treeHeight = 7;
		int vectorDims = 1;
		//int rings = 8;
		//int rings = 5;
		int rings = 12;
		//RingVecFunc func = new PhysicsmataV0Point9RingVecFunc();
		NsphereVecFunc func = new TestRingVecFunc();
		CellGrid cellGrid = new CellGrid(treeHeight, vectorDims, rings, func);
		//CellUtil.fillRings(cellGrid, false, 3, 5, 9, 15, 20, 24, 27, 29);
		//CellUtil.fillRingsWithCircleSums(cellGrid, false, 2, 5, 9, 15, 24);
		//boolean wrapY = false, wrapX = false;
		boolean wrapY = true, wrapX = true;
		boolean sumInsteadOfAve = true;
		boolean includePartialTouches = true;
		double ringSizes[] = new double[rings];
		for(int i=0; i<ringSizes.length; i++){
			ringSizes[i] = Math.exp(i*.3);
		}
		CellUtil.fillWithCircleSums(sumInsteadOfAve, includePartialTouches, cellGrid,
			wrapY, wrapX, ringSizes);
			//1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
			//3.7, 6, 9, 12);
		//int magnify = 3;
		int magnify = 8;
		RingTestDisplay display = new RingTestDisplay(cellGrid, magnify);
		window.add(display);
		window.pack();
		System.out.println("window size "+window.getSize());
		CoreUtil.moveToScreenCenter(window);
		window.setVisible(true);
	}
}