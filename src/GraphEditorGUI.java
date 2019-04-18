import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
/*
 * TODO 
 * -add other tasks
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
	JScrollPane drawPanelScroller;
	//task
	GraphNode taskStartNode;
	GraphNode taskEndNode;
	
	public void run() {
		frame = new JFrame("GraphEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//menu
		JMenuBar menuBar = getStandartMenuBar();
		frame.setJMenuBar(menuBar);
			//central panel for drawing
		drawPanel = new DrawPanel();
		drawPanelScroller = new JScrollPane(drawPanel);
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
		JMenu editMenu = new JMenu("Manage");
		JMenu task4Menu = new JMenu("Tasks");
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem editIDMenuItem = new JMenuItem("Edit ID");
		JMenuItem removeMenuItem = new JMenuItem("Remove");
		JMenuItem findAllWaysMenuItem = new JMenuItem("Run task");
		JMenuItem taskStartNodeMenuItem = new JMenuItem("Set start node");
		JMenuItem taskEndNodeMenuItem = new JMenuItem("Set end node");
		newMenuItem.addActionListener( new NewMenuListener());
		saveMenuItem.addActionListener( new SaveMenuListener());
		openMenuItem.addActionListener( new OpenMenuListener());
		editIDMenuItem.addActionListener( new EditIDMenuListener());
		removeMenuItem.addActionListener( new RemoveMenuListener());
		findAllWaysMenuItem.addActionListener( new FindAllWaysMenuListener());
		taskStartNodeMenuItem.addActionListener( new TasksStartNodeMenuListener());
		taskEndNodeMenuItem.addActionListener( new TasksEndNodeMenuListener());
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(openMenuItem);
		editMenu.add(editIDMenuItem);
		editMenu.add(removeMenuItem);
		task4Menu.add(findAllWaysMenuItem);
		task4Menu.add(taskStartNodeMenuItem);
		task4Menu.add(taskEndNodeMenuItem);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(task4Menu);
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
			writer.write(nodes.size() + " nodes, " + lines.size() + " lines\n");
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

	private class TasksStartNodeMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			taskStartNode = selectedNode;
		}
	}

	private class TasksEndNodeMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			taskEndNode = selectedNode;
		}
	}
	
	private class FindAllWaysMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(taskStartNode == null) {
				JOptionPane.showMessageDialog(frame, "start node is not selected!");
				return;
			}
			if(taskEndNode == null) {
				JOptionPane.showMessageDialog(frame, "end node is not selected!");
				return;
			}
			String start = taskStartNode.getID() + "(" + taskStartNode.getX()
					+ "," + taskStartNode.getY() + ")";
			String end = taskEndNode.getID() + "(" + taskEndNode.getX()
					+ "," + taskEndNode.getY() + ")";
			String task4 = "Найти в графе все возможные пути, не пересекающиеся" +
					" по вершинам, между двумя вершинами:\n" + start +
					" и " + end;
			JOptionPane.showMessageDialog(frame, task4);
			
			taskStartNode.setFirstColor(Color.WHITE);
			taskStartNode.setCurColor(taskStartNode.getFirstColor());
			taskEndNode.setFirstColor(Color.BLACK);
			taskEndNode.setCurColor(taskEndNode.getFirstColor());
			
			sleepAndRepaint(500);

			String taskAccount = "Found ways:";
			ArrayList<GraphNode> notSteped = new ArrayList<GraphNode>(nodes);
			notSteped.remove(taskStartNode);
			ArrayList<GraphNode> way = null;
			ArrayList<GraphNode> prevWay = new ArrayList<GraphNode>();
			while((way = task4(notSteped)) != null) {
				if(prevWay.equals(way)) {
					break;
				}
				prevWay = way;
				
				String sWay = new String();
				for(GraphNode n : way) {
					notSteped.remove(n);
					sWay += n.getID();
				}
				sWay = new StringBuffer(sWay).reverse().toString();
				taskAccount += "\n" + sWay;
				
				for(GraphNode n : notSteped) {
					n.setFirstColor(Color.BLUE);
					n.setCurColor(n.getFirstColor());
				}
				
				notSteped.add(taskEndNode);
				taskEndNode.setFirstColor(Color.BLACK);
				taskEndNode.setCurColor(taskEndNode.getFirstColor());
				sleepAndRepaint(500);
			}
			if(taskAccount.equals("Found ways:")) {
				taskAccount += "\nno ways found";
			}
			JOptionPane.showMessageDialog(frame, taskAccount);
			for(GraphNode n : nodes) {
				n.setFirstColor(Color.BLUE);
				n.setSecondColor(Color.RED);
			}
			for(GraphLine l : lines) {
				l.setFirstColor(Color.BLACK);
				l.setSecondColor(Color.YELLOW);
			}
		}
	}
	
	private void sleepAndRepaint(long time) {
		try {
			drawPanel.paint(drawPanel.getGraphics());
			Thread.sleep(time);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	private ArrayList<GraphNode> task4(ArrayList<GraphNode> nS) {
		ArrayList<GraphNode> notSteped = new ArrayList<GraphNode>(nS);
		ArrayList<GraphNode> resultWay = new ArrayList<GraphNode>();
		
		Map<GraphNode,Integer> stepMap = new HashMap<GraphNode,Integer>();
		for(GraphNode n : nodes) {
			stepMap.put(n, -1);
		}
		stepMap.replace(taskStartNode, 0);
		stepMap.replace(taskEndNode, nodes.size());
		
		ArrayList<GraphNode> newWave = null;
		ArrayList<GraphNode> curWave = new ArrayList<GraphNode>();
		curWave.add(taskStartNode);
		
		int curTime = 0;
		boolean wayFounded = false;
		while(!curWave.isEmpty() && !wayFounded) {
			
			for(GraphNode n : curWave) {
				n.setFirstColor(Color.ORANGE);
				n.setCurColor(n.getFirstColor());
			}
			sleepAndRepaint(500);
			
			newWave = new ArrayList<GraphNode>();
			
			for(GraphNode curNode : curWave) {
				//node selecting
				curNode.setCurColor(curNode.getSecondColor());
				sleepAndRepaint(500);
				
				for(GraphLine l : lines) {
					//line selecting
					l.setCurColor(l.getSecondColor());
					sleepAndRepaint(500);
					
					//find incident nodes
					GraphNode incidentCurNode = null;
					if(l.first.equals(curNode)) {
						incidentCurNode = l.second;
					} else if(l.second.equals(curNode)) {
						incidentCurNode = l.first;						
					}
					//if incident node founded 
					if(incidentCurNode != null) {
						if(notSteped.contains(incidentCurNode)) {
							incidentCurNode.setCurColor(Color.YELLOW);
							sleepAndRepaint(500);

							//add to newWave
							newWave.add(incidentCurNode);
							notSteped.remove(incidentCurNode);

							//if it end node
							if(incidentCurNode.equals(taskEndNode)) {
								wayFounded = true;
								break;
							}							
						}
					}
					//disable line selecting
					l.setCurColor(l.getFirstColor());
					sleepAndRepaint(500);
				}
				//disable node selecting
				curNode.setCurColor(curNode.getFirstColor());
				sleepAndRepaint(500);
				if(wayFounded) {
					break;
				}
			}//for(GraphNode curNode : curWave)
			curTime++;
			//mark wave number for nodes in newWave
			if(!newWave.isEmpty()) {
				for(GraphNode n : newWave) {
					stepMap.replace(n, curTime);
					n.setFirstColor(Color.ORANGE);
					n.setCurColor(n.getFirstColor());
					
				}
			}
			//add newWave->curWave
			curWave = newWave;
			//repaint added to curWave nodes
			sleepAndRepaint(500);
			newWave = null;
		}
		//back away to mark the way
		if(wayFounded) {
			resultWay.add(taskEndNode);
			GraphNode curNode = taskEndNode;
			curNode.setCurColor(Color.GREEN);
			sleepAndRepaint(500);
			while(curNode != taskStartNode) {
				for(GraphLine l : lines) {
					if(l.first.equals(curNode) && (stepMap.get(curNode) ==
											stepMap.get(l.second) + 1)) {
						resultWay.add(l.second);
						curNode = l.second;
						l.setFirstColor(Color.GREEN);
						l.setCurColor(l.getFirstColor());
						curNode.setFirstColor(Color.GREEN);
						curNode.setCurColor(curNode.getFirstColor());
						sleepAndRepaint(500);				
						break;
					} else if(l.second.equals(curNode) &&(stepMap.get(curNode) == 
													stepMap.get(l.first) + 1)) {
						resultWay.add(l.first);
						curNode = l.first;
						l.setFirstColor(Color.GREEN);
						l.setCurColor(l.getFirstColor());
						curNode.setFirstColor(Color.GREEN);
						curNode.setCurColor(curNode.getFirstColor());
						sleepAndRepaint(500);				
						break;
					}
				}
			}//adding nodes to result way
		}
		if(resultWay.isEmpty()) {
			resultWay = null;
		}
		return resultWay;
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
			this.setPreferredSize(new Dimension(500,400));
			nodes = new ArrayList<GraphNode>();
			lines = new ArrayList<GraphLine>();
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.WHITE);
			g.clearRect(0, 0, getWidth(), getHeight());
			
			boolean outOfTheBorders = true;
			while(outOfTheBorders) {
				outOfTheBorders = false;
				for(GraphNode n : nodes) {
					int addX = Math.min(n.getX(), 0);
					int addY = Math.min(n.getY(), 0);
					if( (addX < 0) || (addY < 0) ) {
						this.shiftBorders(-addX, -addY);
						Rectangle viewFocus = new Rectangle(n.getX(), n.getY(),
								n.getWidth(), n.getHeight());
						n.scrollRectToVisible(viewFocus);
					}
					int addWidth = Math.max(this.getWidth(), n.getX() +
							Math.max(n.getWidth()*2, n.getID().length()));
					int addHeight = Math.max(this.getHeight(), n.getY() +
							n.getHeight()*2);
					if( (addWidth > this.getWidth()) ||
						(addHeight > this.getHeight())) {
						Dimension newSize = new Dimension(addWidth,	addHeight);
						this.setPreferredSize(newSize);
					}
				}
			}
			
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
			drawPanelScroller.updateUI();
		}
		
		private void shiftBorders(int dx, int dy) {
			if(dx == 0 && dy == 0)
				return;
			Dimension newSize = new Dimension(dx + this.getWidth(),
					dy + this.getHeight());
			this.setPreferredSize(newSize);
			for(GraphNode n : nodes) {
				n.setX(dx + n.getX());
				n.setY(dy + n.getY());
			}
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
	    			selectedLine = null;
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
