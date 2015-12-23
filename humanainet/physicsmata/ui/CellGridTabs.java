package humanainet.physicsmata.ui;
import humanainet.physicsmata.CellGrid;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import humanaicore.realtimeschedulerTodoThreadpool.Task;

public class CellGridTabs extends JTabbedPane implements Task{
	
	public final CellGrid cellGrid;
	
	public final CellGridDisplay cellGridDisplay;
	
	public final RingTestDisplay ringTestDisplay;
	
	public CellGridTabs(CellGrid cellGrid, int magnify){
		this.cellGrid = cellGrid;
		cellGridDisplay = new CellGridDisplay(cellGrid, magnify);
		ringTestDisplay = new RingTestDisplay(cellGrid, magnify);
		JPanel ringPanel = new JPanel(new BorderLayout());
		ringPanel.add(new JLabel("Mouse wheel changes ring size. Each next higher tree layer has squares 2x2 as big."), BorderLayout.NORTH);
		ringPanel.add(ringTestDisplay, BorderLayout.CENTER);
		add(cellGridDisplay, "Game");
		add(ringPanel, "Test circles tree");
	}
	
	public void event(Object context){
		cellGridDisplay.event(context);
	}
	
	public double preferredInterval(){
		return cellGridDisplay.preferredInterval();
	}

}
