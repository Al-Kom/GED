import java.awt.Color;

public class GraphLine {

	public GraphNode first;
	public GraphNode second;
	private Color curColor;
	private Color firstColor;
	private Color secondColor;
	private int ID;
		
	public GraphLine(GraphNode f, GraphNode s) {
			first = f;
			second = s;
			firstColor = Color.BLACK;
			secondColor = Color.ORANGE;
			curColor = firstColor;
			ID = 0;
	//System.out.println("new LINE");
	}

	public void setCurColor(Color clr) {
		curColor = clr;
	}
	
	public Color getCurColor() {
		return curColor;
	}

	public void setFirstColor(Color clr) {
		firstColor = clr;
	}
	
	public Color getFirstColor() {
		return firstColor;
	}

	public void setSecondColor(Color clr) {
		secondColor = clr;
	}
	
	public Color getSecondColor() {
		return secondColor;
	}
	
	public void setID(int id) {
		ID = id;
	}
	
	public int getID() {
		return ID;
	}

	public int getIDx() {
		return (first.getX() + second.getX())/2 + 5;
	}
	
	public int getIDy() {
		return (first.getY() + second.getY())/2 + 5;
	}
}
