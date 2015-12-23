package humanainet.physicsmata.ui;
import humanaicore.common.CoreUtil;
import humanaicore.weightsnode.WeightsNode;
import humanainet.physicsmata.WeightsNodeTree;
import humanainet.physicsmata.Cell;
import humanainet.physicsmata.CellGrid;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

/** Allows a person to see each ring from each point.
<br><br>
Displays in reverse direction info normally flows in nodes,
which is from node to nodes in its nodeFrom[] array.
The only use I know for this so far is BellscalarRingsTester,
and I am putting in logic specificly for that,
so its not a generally useful class.
<br><br>
TODO include benfrayfieldResearch.bainterfBlurAfterRing at this level?
*/
public class RingTestDisplay extends JPanel implements MouseMotionListener, MouseWheelListener{
	
	public int displayLeafY, displayLeafX, displayWhichRing;
	
	public boolean chooseSelectedCellByMouseMove = true;
	
	public boolean chooseRingWithMouseWheel = true;
	
	public final int magnify;
	
	public final CellGrid cellGrid;
	
	protected double mouseWheelSum;

	/** For now, can only display if cellGrid.vectorDims==1 */
	public RingTestDisplay(CellGrid cellGrid, int magnify){
		if(cellGrid.vectorDims.length != 1) throw new RuntimeException(
			"TODO display up to 3 vectorDims as red, green, and blue (with blue first,"
			+" green second, and red third) or have multiple rectangles in each square"
			+" for the multiple vectorDims. But for now, vectorDims must be 1 but is "
			+cellGrid.vectorDims.length);
		this.cellGrid = cellGrid;
		this.magnify = magnify;
		int pix = cellGrid.squareSide*magnify;
		Dimension d = new Dimension(pix, pix);
		System.out.println("RingTestDisplay Dimension "+d);
		setMinimumSize(d);
		setPreferredSize(d);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		//setBackground(new Color(.5f, .5f, .5f));
		//setBackground(Color.black);
		setBackground(new Color(0, 0, .7f));
	}
	
	public void paint(Graphics g){
		final int vectorDim = 0; //See throw message in constructor
		int w = getWidth(), h = getHeight();
		Color bg = getBackground();
		g.setColor(bg);
		g.fillRect(0, 0, w, h);
		Cell selectedCell = cellGrid.cell[displayLeafY][displayLeafX];
		System.out.println("selectedCell..debugText()="+selectedCell.debugText());
		WeightsNode ring = selectedCell.inNodes[vectorDim][displayWhichRing];
		WeightsNodeTree treeForThatDim = cellGrid.vectorDims[vectorDim];
		for(int i=0; i<ring.size; i++){
			WeightsNode child = ring.nodeFrom[i];
			Rectangle rect = treeForThatDim.nodeToRect(child);
			/*if(CoreUtil.weakRand.nextBoolean()){ //draw random rectangle in the BellscalarTree instead
				//int randH = CoreUtil.weakRand.nextInt(treeForThatDim.treeHeight);
				//int randH = 5;
				int randH = Math.min(displayWhichRing, cellGrid.rings-1);
				//int squareSideAtThatH = 1<<(treeForThatDim.treeHeight-randH);
				int squareSide = 1<<treeForThatDim.treeHeight;
				int randY = CoreUtil.weakRand.nextInt(squareSide);
				int randX = CoreUtil.weakRand.nextInt(squareSide);
				BellscalarNode randNode = treeForThatDim.get(randY, randX, randH);
				rect = treeForThatDim.nodeToRect(randNode);
			}*/
			//
			g.setColor(bg);
			g.fillRect(rect.x*magnify, rect.y*magnify, rect.width*magnify, rect.height*magnify);
			if(rect.width*magnify > 2 && rect.height*magnify > 2){ //fill black rectangle inside
				//TODO run nodeFrom in reverse to trace BellscalarNode ring back to everything that would normally flow into it
				//Color c = Color.black;
				Color c;
				//if(child instanceof BellscalarNode){
					WeightsNode b = (WeightsNode)child;
					//a bifraction is a number in range -1 to 1
					//double bifraction = b.weightedSumOrInput/b.space;
					//float bright = .5f+.5f*(float)bifraction;
					//multiply by space because total weights should be 1
					
					//double childWeight = ring.weightFrom[i];
					//double childSpaceFraction = b.space/ring.space;
					
					//double fraction = ring.weightFrom[i]*ring.space;
					
					//double fraction = ring.touchFraction[i];
					
					double fraction = ring.weightFrom[i]; 
					
					float bright = CoreUtil.holdInRange(0f, (float)fraction, 1f);
					//System.out.println("i="+i+" fraction="+fraction+" childSpace="+b.space+" ringSpace="+ring.space+" bright="+bright);
					float red = bright;
					float green = bright;
					float blue = bright;
					c = new Color(red, green, blue);
				//}
				g.setColor(c);
				//g.setColor(Color.black);
				g.fillRect(rect.x*magnify+1, rect.y*magnify+1, rect.width*magnify-2, rect.height*magnify-2);
			}
		}
		g.setColor(Color.red);
		g.fillRect(displayLeafX*magnify, displayLeafY*magnify, magnify, magnify);
		g.setColor(Color.green);
		int firstLevelSize = treeForThatDim.nodes[0].length;
		int lastLevelSize = treeForThatDim.nodes[treeForThatDim.nodes.length-1].length;
		g.drawString("displaying size or ring "+displayWhichRing+" firstLevelSize="+firstLevelSize+" lastLevelSize="+lastLevelSize+" squaresInCircle="+ring.size, 20, 20);
	}
	
	public void mouseDragged(MouseEvent e){
		mouseMoved(e);
	}
	
	public void mouseMoved(MouseEvent e){
		if(chooseSelectedCellByMouseMove){
			int newLeafX = e.getX()/magnify;
			int newLeafY = e.getY()/magnify;
			//System.out.println("mouse x "+e.getX()+" y "+e.getY()+" nodeX "+newLeafX+" nodeY "+newLeafY+" cellGrid.squareSide "+cellGrid.squareSide);
			if(0 <= newLeafX && newLeafX < cellGrid.squareSide && 0 <= newLeafY && newLeafY < cellGrid.squareSide){
				displayLeafX = newLeafX;
				displayLeafY = newLeafY;
			}
		}
		repaint();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e){
		if(chooseRingWithMouseWheel){
			mouseWheelSum += e.getPreciseWheelRotation();
			if(mouseWheelSum >= 1){
				displayWhichRing = Math.max(displayWhichRing-1, 0);
				mouseWheelSum--;
			}else if(mouseWheelSum <= -1){
				displayWhichRing = Math.min(displayWhichRing+1, cellGrid.rings-1);
				mouseWheelSum++;
			}
		}
		repaint();
	}

}
