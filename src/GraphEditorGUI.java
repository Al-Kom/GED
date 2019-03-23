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
		ArrayList<Line> lines;
		ArrayList<Node> nodes;
		Node firstLineNode;
		Node selectedNode;
		Node moveNode;
		public DrawPanel() {
			addMouseListener(this);
			setBackground(Color.WHITE);
			setLayout(null);
			setToolTipText("DrawPanel");
			
			nodes = new ArrayList<Node>();
			lines = new ArrayList<Line>();
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(Color.BLACK);
			for(Line l : lines) {
				g.drawLine(l.first.X+10, l.first.Y+10, l.second.X+10, l.second.Y+10);
			}
		}

		public class Line {
			public Node first;
			public Node second;
			
			public Line(Node f, Node s) {
				first = f;
				second = s;
			}
		}
		
		public class Node extends JComponent {
			public int X;
			public int Y;
			public Color nodeColor;
			JTextField ID;
			public Node(int x, int y) {
				X = x;
				Y = y;
				setBounds(X, Y, 100, 50);
				addMouseListener( new NodeListener());
				
				ID = new JTextField("" + X + "." + Y);
				ID.setEditable(false);
				DrawPanel.this.add(ID);
				System.out.println("new node at " + X + "," + Y);
				
				nodeColor = Color.BLUE;
			}
			
			public void paintComponent(Graphics g) {
				setBounds(X, Y, 100, 50);
				
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 20, 20);
				g.setColor(nodeColor);
				g.fillOval(0, 0, 20, 20);
				
				ID.setBounds(X+20, Y+20, ID.getText().length()*8, 20);
				DrawPanel.this.repaint();
			}
			
			public class NodeListener implements MouseListener {

				public void mouseClicked(MouseEvent e) {
					Node clickedNode = (Node) e.getSource();
					if(curOperation.equals("line")) {
						//if line
						if(firstLineNode==null) {
							firstLineNode = clickedNode;
							System.out.println(">>from " + clickedNode.X + "." + clickedNode.Y);
						} else {
							Line l = new Line(firstLineNode, clickedNode);
							lines.add(l);
							System.out.println("<<to " + clickedNode.X + "." + clickedNode.Y);
							DrawPanel.this.repaint();
							firstLineNode = null;
						}
					} else if(curOperation.equals("select")) {
						//if select
						if(selectedNode==null) {
							//selectedNode-->clickedNode
							clickedNode.nodeColor = Color.YELLOW;
							clickedNode.repaint();
							selectedNode = clickedNode;
							System.out.println("YEL");
						} else if(selectedNode.equals(clickedNode)) {
							//make nothing selected
							selectedNode.nodeColor = Color.BLUE;
							selectedNode.repaint();
							selectedNode = null;
							
							System.out.println("==");
						} else {
							//selectedNode <--> clickedNode
							selectedNode.nodeColor = Color.BLUE;
							selectedNode.repaint();

							System.out.println("<-->");
							
							clickedNode.nodeColor = Color.YELLOW;
							clickedNode.repaint();

							selectedNode = clickedNode;
						}
					}
				}

				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					if(curOperation.equals("select")) {
						moveNode = (Node) e.getSource();
						System.out.println("--move from " + moveNode.X + "." + moveNode.Y);
					}
				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					if(curOperation.equals("select")) {
						double delta = e.getLocationOnScreen().distance(moveNode.getLocationOnScreen());
						if(delta>20) {
							moveNode.X = moveNode.X + e.getX();
							moveNode.Y = moveNode.Y + e.getY();
							System.out.println("-->to " + moveNode.X + "." + moveNode.Y);
							DrawPanel.this.repaint();
						}
					}
					
				}

				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			}
		}
		
	    public void mouseClicked(MouseEvent e) {
			System.out.println("click on " + e.getX() + "," + e.getY());
	    	if(curOperation.equals("node")) {
	    		Node b = new Node(e.getX(),e.getY());
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
