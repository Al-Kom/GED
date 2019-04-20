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
	private GraphEditorGUI motherGUI;
	private GraphEditorGUI.DrawPanel motherPanel;
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
		int addWidth = Math.max(motherPanel.getWidth(), X + 40);
		int addHeight = Math.max(motherPanel.getHeight(), Y + 40);
		if( (addWidth > motherPanel.getWidth()) ||
			(addHeight > motherPanel.getHeight())) {
			Dimension newSize = new Dimension(addWidth,	addHeight);
			motherPanel.setPreferredSize(newSize);
		}
		setBounds(X, Y, 100, 50);
		addMouseListener( this);
		addMouseMotionListener( this);
		setAutoscrolls(true);
	//System.out.println("new NODE at " + X + "," + Y);
	}
	
	public void paintComponent(Graphics g) {
		int addWidth = Math.max(motherPanel.getWidth(), getX() +
				Math.max(getWidth()*2, getID().length()));
		int addHeight = Math.max(motherPanel.getHeight(), getY() +
				getHeight()*2);
		if( (addWidth > motherPanel.getWidth()) ||
			(addHeight > motherPanel.getHeight())) {
			Dimension newSize = new Dimension(addWidth,	addHeight);
			motherPanel.setPreferredSize(newSize);
		}
		setBounds(X, Y, 20, 20);
		
		g.clearRect(0, 0, 20, 20);
		g.setColor(curColor);
		g.fillOval(0, 0, 20, 20);
		
		motherPanel.repaint();
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
			X = Math.max(X + e.getX() - dX, 0);
			Y = Math.max(Y + e.getY() - dY, 0);
	//System.out.println("DRAG to " + X + "." + Y);
			Rectangle r = new Rectangle(e.getX(), e.getY(), getWidth(), getHeight());
			scrollRectToVisible(r);
			repaint();
		}
	}

	public void setX(int x) {
		X = x;
	}
	
	public void setY(int y) {
		Y = y;
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