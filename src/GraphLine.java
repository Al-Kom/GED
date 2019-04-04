import java.awt.Color;

public class GraphLine {

	public GraphNode first;
	public GraphNode second;
	public Color myColor;	
	public String ID;
		
	public GraphLine(GraphNode f, GraphNode s) {
			first = f;
			second = s;
			myColor = Color.BLACK;
			ID = "";
	}
	
	public void setColor(Color clr) {
		myColor = clr;
	}

	public int getIDx() {
		return (first.X + second.X)/2;
	}
	
	public int getIDy() {
		return (first.Y + second.Y)/2;
	}
}
