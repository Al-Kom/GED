import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class GraphNode extends JComponent implements MouseInputListener {
	private int X;
	private int Y;
	private Color curColor;
	private Color firstColor;
	private Color secondColor;
	private String ID;
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
		ID = "";
		firstColor = Color.BLUE;
		secondColor = Color.RED;
		curColor = firstColor;
		setBounds(X, Y, 100, 50);
		this.addMouseListener( this);
		this.addMouseMotionListener( this);
	//System.out.println("new NODE at " + X + "," + Y);
	}
	
	public void paintComponent(Graphics g) {
		setBounds(X, Y, 20, 20);
		
		g.clearRect(0, 0, 20, 20);
		g.setColor(curColor);
		g.fillOval(0, 0, 20, 20);
		//change borders of motherPanel if need
		checkBorders();
		motherPanel.repaint();
		
	}
	
	private void checkBorders( ) {
		int rightBorder = Math.max(getX() + ID.length(), getX() + 20);
		int leftBorder = getX();
		int upBorder = getY() - 20;
		int downBorder = getY() + 20;
		if(rightBorder + 20 > motherPanel.getWidth()){
			motherPanel.setPreferredSize( new Dimension((int)rightBorder + 20,
													motherPanel.getHeight()));
		}
		if(leftBorder < 0) {
			motherPanel.setBounds(-20, 0, motherPanel.getWidth(), motherPanel.getHeight());
		}
		if(upBorder < 0) {
			motherPanel.setBounds(0, -20, motherPanel.getWidth(), motherPanel.getHeight());
		}
		if(downBorder + 20 > motherPanel.getHeight()){
			motherPanel.setPreferredSize( new Dimension(motherPanel.getWidth(),
														(int)downBorder + 20));
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		GraphNode clickedNode = (GraphNode) e.getSource();
		if(motherGUI.curOperation.equals("line")) {
			//if line
			if(motherGUI.firstLineNode==null) {
				motherGUI.firstLineNode = clickedNode;
	//System.out.println(">>new LINE from " + clickedNode.X + "." +clickedNode.Y);
			} else {
				GraphLine l = new GraphLine(motherGUI.firstLineNode, clickedNode);
				motherGUI.lines.add(l);
	//System.out.println("<<new LINE to " + clickedNode.X + "." + clickedNode.Y);
				motherPanel.repaint();
				motherGUI.firstLineNode = null;
			}
		} else if(motherGUI.curOperation.equals("select")) {
			//if select
			if(motherGUI.selectedNode==null) {
				//motherGUI.selectedNode-->clickedNode
				clickedNode.curColor = secondColor;
				clickedNode.repaint();
				motherGUI.selectedNode = clickedNode;
			} else if(motherGUI.selectedNode.equals(clickedNode)) {
				//make nothing selected
				motherGUI.selectedNode.curColor = firstColor;
				motherGUI.selectedNode.repaint();
				motherGUI.selectedNode = null;
			} else {
				//motherGUI.selectedNode <--> clickedNode
				motherGUI.selectedNode.curColor = firstColor;
				motherGUI.selectedNode.repaint();
				clickedNode.curColor = secondColor;
				clickedNode.repaint();
				motherGUI.selectedNode = clickedNode;
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
		ID = id;
	}
	
	public String getID() {
		return ID;
	}

	public void setCurColor(Color cl) {
		curColor = cl;
	}
	
	public Color getCurColor() {
		return curColor;
	}
	
	public void setFirstColor(Color cl) {
		firstColor = cl;
	}
	
	public Color getFirstColor() {
		return firstColor;
	}
	
	public void setSecondColor(Color cl) {
		secondColor = cl;
	}
	
	public Color getSecondColor() {
		return secondColor;
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {		
	}

	public void mouseMoved(MouseEvent e) {
	}
	
}