import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class GraphNode extends JComponent implements MouseInputListener {
	public int X;
	public int Y;
	public Color nodeColor;
	JTextField ID;
	GraphEditorGUI motherGUI;
	GraphEditorGUI.DrawPanel motherPanel;
	private boolean isDragged;
	private int dX;
	private int dY;
	
	public GraphNode(int x, int y, GraphEditorGUI gui) {
		motherGUI = gui;
		motherPanel = gui.drawPanel;
		X = x;
		Y = y;
		setBounds(X, Y, 100, 50);
		this.addMouseListener( this);
		this.addMouseMotionListener( this);
		
		ID = new JTextField("" + X + "." + Y);
		ID.setEditable(false);
		motherPanel.add(ID);
		nodeColor = Color.BLUE;
		//System.out.println("new NODE at " + X + "," + Y);
	}
	
	public void paintComponent(Graphics g) {
		setBounds(X, Y, 100, 50);
		
		g.clearRect(0, 0, 20, 20);
		g.setColor(nodeColor);
		g.fillOval(0, 0, 20, 20);
		
		ID.setBounds(X+20, Y+20, ID.getText().length()*8, 20);
		//change borders of motherPanel if need
		if((this.getX() + 20 + ID.getWidth()) > motherPanel.getWidth()) {
			motherPanel.setPreferredSize( new Dimension(motherPanel.getWidth() + 20,
					motherPanel.getHeight()));
		}
		if((this.getY() + 20 + ID.getHeight()) > motherPanel.getHeight()) {
			motherPanel.setPreferredSize( new Dimension(motherPanel.getWidth(), 
					motherPanel.getHeight() + 20));
		}
		motherPanel.repaint();
	}
	
	public void mouseClicked(MouseEvent e) {
		GraphNode clickedNode = (GraphNode) e.getSource();
		if(motherGUI.curOperation.equals("line")) {
			//if line
			if(motherPanel.firstLineNode==null) {
				motherPanel.firstLineNode = clickedNode;
				//System.out.println(">>new LINE from " + clickedNode.X + "." + clickedNode.Y);
			} else {
				GraphLine l = new GraphLine(motherPanel.firstLineNode, clickedNode);
				motherPanel.lines.add(l);
				//System.out.println("<<new LINE to " + clickedNode.X + "." + clickedNode.Y);
				motherPanel.repaint();
				motherPanel.firstLineNode = null;
			}
		} else if(motherGUI.curOperation.equals("select")) {
			//if select
			if(motherPanel.selectedNode==null) {
				//motherPanel.selectedNode-->clickedNode
				clickedNode.nodeColor = Color.YELLOW;
				clickedNode.repaint();
				motherPanel.selectedNode = clickedNode;
			} else if(motherPanel.selectedNode.equals(clickedNode)) {
				//make nothing selected
				motherPanel.selectedNode.nodeColor = Color.BLUE;
				motherPanel.selectedNode.repaint();
				motherPanel.selectedNode = null;
			} else {
				//motherPanel.selectedNode <--> clickedNode
				motherPanel.selectedNode.nodeColor = Color.BLUE;
				motherPanel.selectedNode.repaint();
				clickedNode.nodeColor = Color.YELLOW;
				clickedNode.repaint();
				motherPanel.selectedNode = clickedNode;
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if(motherGUI.curOperation.equals("select")) {
			isDragged = true;
			dX = e.getX();
			dY = e.getY();
			//System.out.println("--move from " + X + "." + Y);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(motherGUI.curOperation.equals("select")) {
			isDragged = false;
			//System.out.println("-->to " + X + "." + Y);
		}
		
	}

	public void mouseDragged(MouseEvent e) {
		if(isDragged) {
			X = X + e.getX() - dX;
			Y = Y + e.getY() - dY;
			//System.out.println("DRAG to " + X + "." + Y);
			motherPanel.repaint();
		}
	}
	
	public void setID(String id) {
		ID.setText(id);
	}
	
	public String getID() {
		return ID.getText();
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {		
	}

	public void mouseMoved(MouseEvent e) {
	}
	
}