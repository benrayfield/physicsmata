package humanainet.physicsmata.ui;
import humanaicore.common.CoreUtil;
import humanaicore.realtimeschedulerTodoThreadpool.Task;
import humanaicore.realtimeschedulerTodoThreadpool.TimedEvent;
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

/** A game panel for mouse (or TODO multitouchscreen) to see, edit, and run a CellGrid.
For now, it only supports CellGrid of 1 vectorDim and 1 color.
<br><br>
Its expected this would be in a tab parallel to RingTestDisplay and other views of the CellGrid,
but after the game is more finished maybe only this view would be wanted in most cases.
<br><br>
In future versions its expected this would accept dragAndDrop from mindmap,
which would contain code or pointers to RingVecFunc which could be painted into each Cell
so different cells have different cellular automata functions.
*/
public class CellGridDisplay extends JPanel implements MouseMotionListener, Task{
	
	public final CellGrid cellGrid;
	
	public double lastTimeMouseMoved = CoreUtil.time();
	
	public int magnify;
	
	public CellGridDisplay(CellGrid cellGrid, int magnify){
		this.cellGrid = cellGrid;
		this.magnify = magnify;
		addMouseMotionListener(this);
		setBackground(Color.black);
		int side = cellGrid.squareSide*magnify;
		Dimension d = new Dimension(side, side);
		setMinimumSize(d);
		setPreferredSize(d);
	}

	public void mouseDragged(MouseEvent e){
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e){
		lastTimeMouseMoved = CoreUtil.time();
	}
	
	protected boolean displayFrames = true;
	protected long frames;
	
	public void paint(Graphics g){
		Rectangle visible = getVisibleRect();
		final int magnify = this.magnify;
		//TODO Do these end grid vars extend a little past end of window?
		int startGridY = visible.y/magnify; //inclusive
		int endGridY = (visible.y+visible.height+magnify-1)/magnify; //exclusive
		endGridY = CoreUtil.min(endGridY, cellGrid.squareSide);
		int startGridX = visible.x/magnify; //inclusive
		int endGridX = (visible.x+visible.width+magnify-1)/magnify; //exclusive
		endGridX = CoreUtil.min(endGridX, cellGrid.squareSide);
		
		int dataEndXAndY = cellGrid.squareSide*magnify;
		g.setColor(getBackground());
		//g.setColor(new Color(0xff000000 | CoreUtil.weakRand.nextInt(0x1000000)));
		int fromY = Math.max(dataEndXAndY,visible.y);
		int toY = visible.height+visible.y;
		if(fromY < toY){ //fill background below grid and into the bottomright corner
			int fromX = visible.x;
			int toX = visible.width;
			g.fillRect(fromX, fromY, toX-fromX, toY-fromY);
		}
		//g.setColor(new Color(0xff000000 | CoreUtil.weakRand.nextInt(0x1000000)));
		int fromX = dataEndXAndY;
		int toX = visible.x+visible.width;
		fromY = visible.y;
		toY = Math.min(dataEndXAndY, visible.y+visible.height);
		if(fromX < toX && fromY < toY){ //fill background right of grid
			g.fillRect(fromX, fromY, toX-fromX, toY-fromY);
		}
		
		final int vectorDim = 0;
		final WeightsNode leafs[][] = cellGrid.vectorDims[vectorDim].nodes[0];
		for(int gridY=startGridY; gridY<endGridY; gridY++){
			final WeightsNode leafsY[] = leafs[gridY];
			for(int gridX=startGridX; gridX<endGridX; gridX++){
				final WeightsNode leafYX = leafsY[gridX];
				double bifraction = leafYX.position; //should be range -1 to 1 (at least in the lowest layer)
				//if(gridY==17 && gridX==17) System.out.println("bifraction="+bifraction);
				//double bifraction = CoreUtil.weakRand.nextDouble()*2-1;
				//if(gridX==3 && gridY==3){
				//	System.out.println("leafYX.weightedSumOrInput at (3,3) is "+leafYX.weightedSumOrInput);
				//}
				float fraction = (float)CoreUtil.holdInRange(0, .5+.5*bifraction, 1);
				//Color c = new Color(0xff000000 | CoreUtil.weakRand.nextInt(0x1000000)); //FIXME
				Color c = new Color(fraction, fraction, fraction);
				g.setColor(c);
				g.fillRect(gridX*magnify, gridY*magnify, magnify, magnify);
			}
		}
		frames++;
		g.setColor(new Color(0, 1, 1));
		if(displayFrames){
			g.drawString("Frames: "+frames, 20, 20);
		}
	}
	
	long countEvents;
	
	static boolean printedMouseMoveNotPaintMessage;
	
	public void event(Object context){
		if(context instanceof TimedEvent){
			//TODO try to keep the events timed evenly since automata rules arent all continuous in time,
			//so theres no consistent way for all possible automata rules to run them with a time param
			//for when more or less time passes between each call, except to skip some runs if
			//the timer is called too often.
			
			double now = ((TimedEvent)context).time;
			double timeSinceMouseMove = now-lastTimeMouseMoved;
			//TODO? if(timeSinceMouseMove < 3){
			int limitSeconds = 60;
			if(timeSinceMouseMove < limitSeconds){ //pause after 60 seconds of no mouse movement in this cellGrid
				printedMouseMoveNotPaintMessage = false;
				//System.out.println("Events: "+(countEvents++));
				cellGrid.run();
				repaint();
			}else{
				if(!printedMouseMoveNotPaintMessage){
					System.out.println("To save computing resources, you havent moved mouse in "+limitSeconds+" seconds, so pausing simulation until move mouse over the cell grid.");
					printedMouseMoveNotPaintMessage = true;
				}
			}
		}
	}
	
	public double preferredInterval(){
		return .01;
		//return .05; //TODO faster
		//return .5;
	}
	

}
