import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
/*
 * TODO 
 * 
 * -change button: just change cartoon
 * 
 * -JComponent GraphNode
 * https://stackoverflow.com/questions/423950/rounded-swing-jbutton-using-java
 * -JComponent GraphLine
 */
public class GraphEditorGUI {
	String curOperation = "";
	ArrayList<JButton> leftButtons;
	DrawPanel drawPanel;
	public void run() {
		JFrame frame = new JFrame("GraphEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//menu
		JMenuBar menuBar = getStandartMenuBar();
		frame.setJMenuBar(menuBar);
			//central panel for drawing
		drawPanel = new DrawPanel();
		frame.getContentPane().add(BorderLayout.CENTER, drawPanel);
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
		leftButtons = new ArrayList<JButton>();
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
			drawPanel.moveNode = null;
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
		GraphNode moveNode;
		public DrawPanel() {
			addMouseListener(this);
			setBackground(Color.WHITE);
			setLayout(null);
			setToolTipText("DrawPanel");
			
			nodes = new ArrayList<GraphNode>();
			lines = new ArrayList<GraphLine>();
		}

		public void paintComponent(Graphics g) {
			g.clearRect(0, 0, getWidth(), getHeight());
			
			g.setColor(Color.BLACK);
			for(GraphLine l : lines) {
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
	    	}
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
