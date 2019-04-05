import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
/*
 * TODO 
 * 
 * -change button: just change cartoon
 * -repair scroll for drawPanel
 */
public class GraphEditorGUI {
	String curOperation = "";
	DrawPanel drawPanel;
	public void run() {
		JFrame frame = new JFrame("GraphEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//menu
		JMenuBar menuBar = getStandartMenuBar();
		frame.setJMenuBar(menuBar);
			//central panel for drawing
		drawPanel = new DrawPanel();
		JScrollPane drawPanelScroller = new JScrollPane(drawPanel);
		drawPanelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		drawPanelScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(BorderLayout.CENTER, drawPanelScroller);
			//left panel for buttons and logo
		JPanel leftPanel = getLeftPanel();
		frame.getContentPane().add(BorderLayout.WEST, leftPanel);
		
		frame.setSize(700, 500);
		frame.setVisible(true);
	}
	
	private JMenuBar getStandartMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu("Manage");
		JMenuItem editIDMenuItem = new JMenuItem("Edit ID");
		JMenuItem removeIDMenuItem = new JMenuItem("Remove");
		editMenu.add(editIDMenuItem);
		editMenu.add(removeIDMenuItem);
		menuBar.add(editMenu);
		return menuBar;
	}
	
	private JPanel getLeftPanel() {
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(Color.DARK_GRAY);
			//sources
		String[] buttonImagesSource = {"sources/Bselect.jpg",
				"sources/Bnode.jpg", "sources/Bline.jpg"};
		String[] operationNames = {"select", "node", "line"};
			//logo
		leftPanel.add( new ImagePanel("sources/logo.jpg"));	
			//left buttons
		JPanel buttonPanel = new JPanel( new GridLayout(3,1));
		ArrayList<JButton> leftButtons = new ArrayList<JButton>();
		for(int i=0;i<3;i++) {
			leftButtons.add( new JButton( new ImageIcon(buttonImagesSource[i])));
			leftButtons.get(i).setName(operationNames[i]);
			leftButtons.get(i).addActionListener( new LeftButtonsListener());
			buttonPanel.add(leftButtons.get(i));
		}
		leftPanel.add(buttonPanel);
			//filler below left buttons
		JPanel filler = new JPanel();
		filler.setBackground(Color.DARK_GRAY);
		filler.setToolTipText("filler");
		leftPanel.add(filler);
		
		curOperation = operationNames[0];
		
		return leftPanel;
	}
	
	public class LeftButtonsListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JButton cur = (JButton) ev.getSource();
			curOperation = cur.getName();
			System.out.println(curOperation);
			drawPanel.firstLineNode = null;
		}
	}
	
	public class ImagePanel extends JPanel {
		private String imageName;
		
		public ImagePanel(String im) {
			imageName = im;
		}
		
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(imageName).getImage();
			g.drawImage(image, 0, 0, this);
		}
	}
	
	public class DrawPanel extends JPanel implements MouseListener{
		ArrayList<GraphLine> lines;
		ArrayList<GraphNode> nodes;
		GraphNode firstLineNode;
		GraphNode selectedNode;
		GraphLine selectedLine;
		
		public DrawPanel() {
			addMouseListener(this);
			setBackground(Color.WHITE);
			setLayout(null);
			setToolTipText("DrawPanel");
			this.setPreferredSize(new Dimension(650,500));
			nodes = new ArrayList<GraphNode>();
			lines = new ArrayList<GraphLine>();
		}

		public void paintComponent(Graphics g) {
			g.clearRect(0, 0, getWidth(), getHeight());
			for(GraphLine l : lines) {
				g.drawString(l.getID(), l.getIDx(), l.getIDy());
				g.setColor(l.getColor());
				g.drawLine(l.first.X+10, l.first.Y+10, l.second.X+10, l.second.Y+10);
			}
		}
		
	    public void mouseClicked(MouseEvent e) {
			//System.out.println("click on " + e.getX() + "," + e.getY());
	    	if(curOperation.equals("node")) {
	    		GraphNode b = new GraphNode(e.getX(),e.getY(), GraphEditorGUI.this);
				nodes.add(b);
				add(b);
				repaint();
	    	} else if(curOperation.equals("select")) {
    			if(selectedLine!=null) {
	    			selectedLine.setColor(Color.BLACK);	    				
    			}
	    		Point clickedPoint = new Point(e.getX(),e.getY());
	    		GraphLine newSelectedLine = findLineForPoint(clickedPoint);
	    		if(newSelectedLine != null) {
	    			System.out.println("select line");
	    			selectedLine = newSelectedLine;
	    			selectedLine.setColor(Color.ORANGE);
	    		}
				repaint();
	    	}
	    }
	    
	    private GraphLine findLineForPoint(Point p) {
	    	GraphLine foundLine = null;
	    	int sl = 10;		//shifting line from borders to center of node
			int delta = 5;		//maximal distance between click-point and line
			
	    	for(GraphLine l : lines) {
	    		Rectangle r = new Rectangle(p.x - delta, p.y - delta,
	    									delta*2, delta*2);
	    		if(r.intersectsLine(l.first.X + sl, l.first.Y + sl,
	    					l.second.X + sl, l.second.Y + sl)) {
					foundLine = l;
					break;
	    		}	    		
	    	}

	    	return foundLine;
	    }
	    
		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}//DrawPanel
}
