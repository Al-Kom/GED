import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.event.*;
import java.io.*;
/*
 * TODO 
 * -task
 */
public class GraphEditorGUI {
	GraphNode firstLineNode;
	private Point curLineEndPos;
	GraphNode selectedNode;
	GraphLine selectedLine;
	JFrame frame;
	ArrayList<GraphLine> lines;
	ArrayList<GraphNode> nodes;
	String curOperation = "";
	DrawPanel drawPanel;
	
	public void run() {
		frame = new JFrame("GraphEditor");
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
		JMenuItem openMenuItem = new JMenuItem("Open");
		newMenuItem.addActionListener( new NewMenuListener());
		saveMenuItem.addActionListener( new SaveMenuListener());
		openMenuItem.addActionListener( new OpenMenuListener());
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(openMenuItem);
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu("Manage");
		JMenuItem editIDMenuItem = new JMenuItem("Edit ID");
		JMenuItem removeMenuItem = new JMenuItem("Remove");
		editIDMenuItem.addActionListener( new EditIDMenuListener());
		removeMenuItem.addActionListener( new RemoveMenuListener());
		editMenu.add(editIDMenuItem);
		editMenu.add(removeMenuItem);
		menuBar.add(editMenu);
		return menuBar;
	}
	
	private class NewMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearGraphMemory();
		}
	}
	
	private void clearGraphMemory() {
		lines.clear();
		for(GraphNode n :nodes) {
			drawPanel.remove(n);
		}
		nodes.clear();
		selectedLine = null;
		selectedNode = null;
		firstLineNode = null;
		curLineEndPos = null;
	}
	
	private class SaveMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileSaveDialog = new JFileChooser();
			fileSaveDialog.showSaveDialog(frame);
			saveGraphToFile(fileSaveDialog.getSelectedFile());
		}
	}
	
	private void saveGraphToFile(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file + ".txt"));
			writer.write(nodes.size() + "nodes, " + lines.size() + "lines\n");
			writer.write("nodes:\n");
			for(GraphNode n : nodes) {
				writer.write(n.getX() + "," + n.getY() + "," + n.getID() + "\n");
			}
			writer.write("lines:\n");
			for(GraphLine l : lines) {
				writer.write(l.first.getX() + "," + l.first.getY() + ",");
				writer.write(l.second.getX() + "," + l.second.getY() + ",");
				writer.write(l.getID() + "\n");
			}
			writer.close();
		} catch (IOException ex) {
			System.out.println("can't save to file: " + file.getPath());
			ex.printStackTrace();
		}
	}
	
	private class OpenMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileOpenDialog = new JFileChooser();
			fileOpenDialog.showOpenDialog(frame);
			openGraphFromFile(fileOpenDialog.getSelectedFile());
		}
	}
	
	private void openGraphFromFile(File file) {
		clearGraphMemory();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(file));
			String line=null;
			while(!(line = reader.readLine()).equals("nodes:"));
			while(!(line = reader.readLine()).equals("lines:")) {
				String[] nodeParams = line.split(",");
				GraphNode b = new GraphNode(Integer.parseInt(nodeParams[0]),
											Integer.parseInt(nodeParams[1]),
											GraphEditorGUI.this);
				if(nodeParams.length>2) {
					b.setID(nodeParams[2]);
				}
				nodes.add(b);
				drawPanel.add(b);
			}
			while((line = reader.readLine()) != null) {
				String[] lineParams = line.split(",");
				int coords[] = new int[5];
				for(int i = 0; i < 4; i++) {
					coords[i] = Integer.parseInt(lineParams[i]);
				}
				GraphNode n1 = null;
				GraphNode n2 = null;
				for(GraphNode n : nodes) {
					if((n.getX() ==  coords[0]) && (n.getY() == coords[1])) {
						n1 = n;
					}
					if((n.getX() == coords[2]) && (n.getY() == coords[3])) {
						n2 = n;
					}
				}
				if(n1 == null) {
					throw new Exception("Bad coordinates for first node of line: "
													+ coords[0] + "," + coords[1]);
				}
				if(n2 == null) {
					throw new Exception("Bad coordinates for first node of line: "
													+ coords[2] + "," + coords[3]);
				}
				GraphLine l = new GraphLine(n1, n2);
				if(lineParams.length>4) {
					l.setID(lineParams[4]);
				}
				lines.add(l);
			}
			reader.close();
			drawPanel.repaint();
			for(GraphNode n : nodes) {
				n.repaint();
			}
		} catch (IOException ex) {
			System.out.println("can't load from file: " + file.getPath());
			ex.printStackTrace();			
		} catch (NumberFormatException ex) {
			System.out.println("Error when parsing coordinate. " + ex.getLocalizedMessage());
			return;
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
	}
	
	private class EditIDMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//open a dialog window for selected node
			if(selectedNode != null) {
				String selectedNodeID = JOptionPane.showInputDialog("Input "
						+ "ID for the node");
				if(selectedNodeID != null) {
					selectedNode.setID(selectedNodeID);
				}
			}
			//and a dialog window for selected line
			if(selectedLine != null) {
				String selectedLineID = JOptionPane.showInputDialog("Input "
						+ "ID for the line");
				if(selectedLineID != null) {
					selectedLine.setID(selectedLineID);
				}
			}
		}
	}
	
	private class RemoveMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//remove selected line
			if(selectedLine != null) {
				lines.remove(selectedLine);	
				selectedLine = null;
			}
			//remove selected node
			if(selectedNode != null) {
				//remove lines that contains selected node
				ArrayList<GraphLine> toRemove = new ArrayList<GraphLine>();
				for(GraphLine l : lines) {
					if(l.first.equals(selectedNode) ||
							l.second.equals(selectedNode)) {
						toRemove.add(l);
					}
				}
				for(GraphLine l : toRemove) {
					lines.remove(l);
				}
				
				//remove node
				nodes.remove(selectedNode);
				drawPanel.remove(selectedNode);
				selectedNode = null;
			}
			drawPanel.repaint();
		}
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
			firstLineNode = null;
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
	
	public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener{
		
		public DrawPanel() {
			addMouseListener(this);
			addMouseMotionListener(this);
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
				g.setColor(l.getCurColor());
				g.drawString(l.getID(), l.getIDx(), l.getIDy());
				g.drawLine(l.first.getX()+10, l.first.getY()+10,
						l.second.getX()+10, l.second.getY()+10);
			}
			for(GraphNode n : nodes) {
				g.setColor(n.getCurColor());
				g.drawString(n.getID(), n.getX(), n.getY());
			}
			if(firstLineNode != null && curLineEndPos != null) {
				g.setColor(Color.MAGENTA);
				g.drawLine(firstLineNode.getX() + 10, firstLineNode.getY() + 10,
									curLineEndPos.x, curLineEndPos.y);
			} else curLineEndPos = null;
		}
		
	    public void mouseClicked(MouseEvent e) {
			//System.out.println("click on " + e.getX() + "," + e.getY());
	    	if(curOperation.equals("node")) {
	    		GraphNode n = new GraphNode(e.getX(),e.getY(), GraphEditorGUI.this);
				nodes.add(n);
				add(n);
				repaint();
	    	} else if(curOperation.equals("select")) {
    			if(selectedLine!=null) {
	    			selectedLine.setCurColor(selectedLine.getFirstColor());	    				
    			}
	    		Point clickedPoint = new Point(e.getX(),e.getY());
	    		GraphLine newSelectedLine = findLineForPoint(clickedPoint);
	    		if(newSelectedLine != null) {
	    			System.out.println("select line");
	    			selectedLine = newSelectedLine;
	    			selectedLine.setCurColor(selectedLine.getSecondColor());
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
	    		if(r.intersectsLine(l.first.getX() + sl, l.first.getY() + sl,
	    					l.second.getX() + sl, l.second.getY() + sl)) {
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

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			if(firstLineNode != null) {
				curLineEndPos = new Point(e.getPoint());
				repaint();
			}
		}
	}//DrawPanel
}
