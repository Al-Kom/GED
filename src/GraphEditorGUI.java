import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
/*
 * TODO
 * -add task: output adjacency matrix, planarity
 * -add task: searching Euler cycles
 * -add task: making graph planar
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
	double speedCoef = 1;
	String[] nodesIDes;
	
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
		JMenu fileMenu = new JMenu("Граф");
		JMenu editMenu = new JMenu("Управление");
		JMenu taskMenu = new JMenu("Запрос");
		JMenuItem newMenuItem = new JMenuItem("Создать новый");
		JMenuItem saveMenuItem = new JMenuItem("Сохранить");
		JMenuItem openMenuItem = new JMenuItem("Открыть");
		JMenuItem infoMenuItem = new JMenuItem("Информация о графе");
		JMenuItem editIDMenuItem = new JMenuItem("Изменить имя (вес)");
		JMenuItem removeMenuItem = new JMenuItem("Удалить");
		JMenuItem taskStartNodeMenuItem = new JMenuItem("Установить вершину "
				+ "как начальную");
		JMenuItem taskEndNodeMenuItem = new JMenuItem("Установить вершину "
				+ "как конечную");
		JMenuItem findAllWaysMenuItem = 
				new JMenuItem("Найти все пути между начальной и конечной вершинами");
		JMenuItem getDistanceMenuItem =
				new JMenuItem("Вычислить расстояние между начальной"
						+ " и конечной вершинами");
		JMenuItem changeSpeedCoefMenuItem =
				new JMenuItem("Изменить скорость отображения");
		newMenuItem.addActionListener( new NewMenuListener());
		saveMenuItem.addActionListener( new SaveMenuListener());
		openMenuItem.addActionListener( new OpenMenuListener());
		infoMenuItem.addActionListener( new InfoMenuListener());
		editIDMenuItem.addActionListener( new EditIDMenuListener());
		removeMenuItem.addActionListener( new RemoveMenuListener());
		taskStartNodeMenuItem.addActionListener( new TasksStartNodeMenuListener());
		taskEndNodeMenuItem.addActionListener( new TasksEndNodeMenuListener());
		findAllWaysMenuItem.addActionListener( new FindAllWaysMenuListener());
		getDistanceMenuItem.addActionListener( new TaskGetDistanceMenuListener());
		changeSpeedCoefMenuItem.addActionListener( new ChangeSpeedCoefMenuListener());
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(infoMenuItem);
		editMenu.add(editIDMenuItem);
		editMenu.add(removeMenuItem);
		taskMenu.add(changeSpeedCoefMenuItem);
		taskMenu.add(taskStartNodeMenuItem);
		taskMenu.add(taskEndNodeMenuItem);
		taskMenu.add(findAllWaysMenuItem);
		taskMenu.add(getDistanceMenuItem);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(taskMenu);
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
					l.setID(Integer.parseInt(lineParams[4]));
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
			System.out.println("Error when parsing coordinates or weight. " + ex.getLocalizedMessage());
			return;
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
	}
	
	private class InfoMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(nodes.size() == 0) {
				JOptionPane.showMessageDialog(frame, "Ничего нет",
						"Информация о графе",JOptionPane.PLAIN_MESSAGE);
			} else {
				//create and fill matrix by zero
				int len = nodes.size();
				char[] adjacencyMatrix = new char[(len)*(len+1)];
				for(int i = 0; i < len*(len+1); i++)
					adjacencyMatrix[i] = '0';
				for(int i = 1; i < len+1; i++)
					adjacencyMatrix[i*(len+1)-1] = '\n';
				//create IDs
				String nodesIDs = "";
				for(int i = 0; i < len; i++) {
					nodesIDs += nodes.get(i).getID();
				}
				//fill adjacency in matrix
				for(GraphLine l:lines) {
					int i = nodes.indexOf(l.first);
					int j = nodes.indexOf(l.second);
					adjacencyMatrix[i*(len+1) + j] = '1';
					adjacencyMatrix[j*(len+1) + i] = '1';
				}
				//create output matrix
				String outputMatrix = String.valueOf(adjacencyMatrix);
				//output matrix
				JOptionPane.showMessageDialog(
						frame, 
						nodesIDs + "\n" + outputMatrix,
						"Информация о графе",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
	private class EditIDMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//open a dialog window for selected node
			if(selectedNode != null) {
				String selectedNodeID = JOptionPane.showInputDialog(
						frame,
						"Введите новое имя для вершины",
						"Вершина",
						JOptionPane.PLAIN_MESSAGE);
				if(selectedNodeID != null) {
					selectedNode.setID(selectedNodeID);
				}
			}
			//and a dialog window for selected line
			try {
				if(selectedLine != null) {
					String selectedLineID = JOptionPane.showInputDialog(
							frame,
							"Введите новое имя для ребра",
							"Ребро",
							JOptionPane.PLAIN_MESSAGE);
					if(selectedLineID != null) {
						selectedLine.setID(Integer.parseInt(selectedLineID));
					}
				}
			} catch (NumberFormatException ex) {
				System.out.println("Bad number for line ID");
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

	private class ChangeSpeedCoefMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				String enteredCoef = JOptionPane.showInputDialog("Увеличить скорость в "
						+ "[x] раз");
				if(enteredCoef != null)
					speedCoef *= Double.parseDouble(enteredCoef);
			} catch(NumberFormatException ex) {
				System.out.println("Bad entered coefficient!");
				ex.printStackTrace();
			}
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
			selectedNode = null;
			if(taskStartNode == null) {
				JOptionPane.showMessageDialog(frame, 
						"Начальная вершина не обозначена",
						"Недостаточно данных",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(taskEndNode == null) {
				JOptionPane.showMessageDialog(frame, 
						"Конечная вершина не обозначена",
						"Недостаточно данных",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			//show task dialog
			String start = taskStartNode.getID() + "(" + taskStartNode.getX()
					+ "," + taskStartNode.getY() + ")";
			String end = taskEndNode.getID() + "(" + taskEndNode.getX()
					+ "," + taskEndNode.getY() + ")";
			String taskName =
					"Найти в графе все возможные пути между двумя вершинами:\n"
					+ start + " и " + end;
			JOptionPane.showMessageDialog(frame, 
					taskName,
					"Запрос",
					JOptionPane.PLAIN_MESSAGE);
			//create way
			String taskAccount = "Найденные пути:";
			ArrayList<String> foundedWays = findAllWays();
			for(String s:foundedWays) {
				taskAccount += "\n" + s;
			}
			if(foundedWays.isEmpty()) {
				taskAccount += "\nни одного пути не найдено";
			}
			//show answer dialog
			JOptionPane.showMessageDialog(frame, 
					taskAccount,
					"Ответ",
					JOptionPane.PLAIN_MESSAGE);
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

	private ArrayList<String> findAllWays() {
		//create result ways
		ArrayList<String> ways = new ArrayList<String>();
		//prepare graph and store IDs
		nodesIDes = prepareGraphToTaskAndStoreNodesIDs();
		//store lines
		ArrayList<GraphLine> linesRecover = new ArrayList<GraphLine>(lines);
		//enter algorithm
		Map<GraphNode,ArrayList<GraphLine>> stepMap;
		while((stepMap = minimizeNeighborDistance(new ArrayList<GraphNode>(nodes)))
																		!= null) {
			//create string way
			String sWay = nodesIDes[nodes.indexOf(taskStartNode)];
			//extract way
			ArrayList<GraphLine> curWay = stepMap.get(taskEndNode);
			//extract nodes from way
			GraphNode prevNode = taskStartNode;
			GraphNode postNode = null;
			for(GraphLine line:curWay) {
				if(line.first.equals(prevNode))
					postNode = line.second;
				else if(line.second.equals(prevNode))
					postNode = line.first;
				if(postNode != null) {
					sWay += "-" + nodesIDes[nodes.indexOf(postNode)];
					lines.remove(line);
					if(postNode.equals(taskEndNode))
						break;
					else prevNode = postNode;
					postNode = null;
				}
			}
System.out.println("++sWay: " + sWay);			
			ways.add(sWay);
		}
		restoreGraph(nodesIDes, linesRecover);
		//answer
		return ways;
	}
	
	private class TaskGetDistanceMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			selectedNode = null;
			if(taskStartNode == null) {
				JOptionPane.showMessageDialog(frame, "start node is not selected!");
				return;
			}
			if(taskEndNode == null) {
				JOptionPane.showMessageDialog(frame, "end node is not selected!");
				return;
			}
			String startNodeName = taskStartNode.getID() + "(" + 
					taskStartNode.getX() + "," +
					taskStartNode.getY() + ")";
			String endNodeName = taskEndNode.getID() + "(" + taskEndNode.getX()
					+ "," + taskEndNode.getY() + ")";
			String taskName = "Определить расстояние между:\n" + startNodeName +
					" и " + endNodeName;
			JOptionPane.showMessageDialog(frame, taskName);

			//find distance
			int dist = findDistanceToAllNodes();
			//dialog with answer
			String taskAccount = "Расстояние между " + startNodeName +
					" и " + endNodeName + ":\n";
			if(dist != Integer.MAX_VALUE) {
				taskAccount += String.valueOf(dist);
			} else {
				taskAccount += "между вершинами нет пути";
			}
			JOptionPane.showMessageDialog(frame, taskAccount);
		}
	}

	private int findDistanceToAllNodes() {
		//prepare graph and store IDs
		String[] nodesIDs = prepareGraphToTaskAndStoreNodesIDs();
		//enter algorithm
		minimizeNeighborDistance(new ArrayList<GraphNode>(nodes));
		//extract distance to end
		int dist = Integer.MAX_VALUE;
		try {
			dist = Integer.parseInt(taskEndNode.getID());
		} catch(NumberFormatException ex) {
			System.out.println("Strange distance");
		}
		//return graph
		restoreGraph(nodesIDs, lines);
		//answer
		return dist;
	}

	private String[] prepareGraphToTaskAndStoreNodesIDs() {
		//color mark for start and end nodes
		taskStartNode.setCurColor(Color.WHITE);
		taskEndNode.setCurColor(Color.BLACK);
		sleepAndRepaint(500);
		//store nodes
		String nodesIDs[] = new String[nodes.size()];
		for(int i = 0; i < nodes.size(); i++) {
			nodesIDs[i] = nodes.get(i).getID();
		}
		return nodesIDs;
	}
	
	private void restoreGraph(String[] nodesIDs, ArrayList<GraphLine> linesRecover) {
		//return color marks 
		for(GraphNode n : nodes) {
			n.setCurColor(Color.BLUE);
		}
		for(GraphLine l : lines) {
			l.setCurColor(l.getFirstColor());
		}
		//return IDs
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setID(nodesIDs[i]);
		}
		//recover lines
		lines = linesRecover;
		sleepAndRepaint(300);
	}
	
	private Map<GraphNode,ArrayList<GraphLine>> minimizeNeighborDistance(
			ArrayList<GraphNode> notStepped) {
		//initializing algorithm
			//set distance marks
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setID(String.valueOf(Integer.MAX_VALUE));
		}
		taskStartNode.setID("0");
			//create and fill map of ways
		Map<GraphNode,ArrayList<GraphLine>> stepMap =
				new HashMap<GraphNode,ArrayList<GraphLine>>();
		for(GraphNode n : notStepped) {
			stepMap.put(n, new ArrayList<GraphLine>());
		}
		//find ways for all nodes
		try {
			while(!notStepped.isEmpty()) {
				//find node with minimal distance
				GraphNode curNode = null;
				int curNodeDist = Integer.MAX_VALUE;
				for(GraphNode n:notStepped) {
					if(curNode == null) {
						curNode = n;
						curNodeDist = Integer.parseInt(curNode.getID());
					} else {
						int nDist = Integer.parseInt(n.getID());
						curNodeDist = Integer.parseInt(curNode.getID());
						if(nDist < curNodeDist) {
							curNode = n;
							curNodeDist = Integer.parseInt(curNode.getID());			
						}
					}
				}
				//coloring current node
//System.out.println("curNode: " + curNode.getID());
				curNode.setCurColor(curNode.getSecondColor());
				sleepAndRepaint(300);
				//check distances of neighbors of current node
				GraphNode nbor = null;
				for(GraphLine line:lines) {
					if(line.first.equals(curNode) && notStepped.contains(line.second)) {
						nbor = line.second;
					} else if(line.second.equals(curNode) && notStepped.contains(line.first)) {
						nbor = line.first;
					}
					//if found neighbor
					if(nbor != null) {
						//coloring current line
//System.out.println("--neighbor: " + nbor.getID());
						line.setCurColor(line.getSecondColor());
						sleepAndRepaint(300);
						//check distance
						int curNborDist = Integer.parseInt(nbor.getID());
						if(curNodeDist + line.getID() < curNborDist) {
							nbor.setID(String.valueOf(curNodeDist + line.getID()));
//System.out.println("--his new ID: " + nbor.getID());
							stepMap.replace(nbor,
									new ArrayList<GraphLine>(stepMap.get(curNode)));
							stepMap.get(nbor).add(line);
System.out.println("stepMap of " + nodesIDes[nodes.indexOf(nbor)]);
for(GraphLine l:stepMap.get(nbor)) System.out.println("	-" + l.getID());
							sleepAndRepaint(300);
						}
						nbor = null;
						line.setCurColor(line.getFirstColor());
						sleepAndRepaint(300);
					}
				}
				//check connectedness
					//check if not-checked-distance-nodes exist
				String maxValue = String.valueOf(Integer.MAX_VALUE);
				boolean goOn = false;
				for(GraphNode n:notStepped) {
					if(!n.getID().equals(maxValue) && !n.equals(curNode))
						goOn = true;
				}
				if(!goOn)
					break;
				//mark (+coloring) current node as stepped
//System.out.println("--curNode: " + curNode.getID());
				notStepped.remove(curNode);
				curNode.setCurColor(Color.MAGENTA);
				sleepAndRepaint(500);
			}
		} catch(NumberFormatException ex) {
			ex.printStackTrace();
		}
System.out.println("stepMap of taskEndNode " + nodesIDes[nodes.indexOf(taskEndNode)]);
for(GraphLine l:stepMap.get(taskEndNode)) System.out.println("	-" + l.getID());
		if(taskEndNode.getID().equals(String.valueOf(Integer.MAX_VALUE)))
			return null;
		return stepMap;
	}
	
	private void sleepAndRepaint(long time) {
		try {
			drawPanel.paint(drawPanel.getGraphics());
			Thread.sleep((long)(time/speedCoef));
		} catch (InterruptedException ie) {
			ie.printStackTrace();
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
			this.setPreferredSize(new Dimension(500,400));
			nodes = new ArrayList<GraphNode>();
			lines = new ArrayList<GraphLine>();
		}

		public void paintComponent(Graphics g) {
			g.setColor(Color.WHITE);
			g.clearRect(0, 0, getWidth(), getHeight());
			
			for(GraphLine l : lines) {
				g.setColor(l.getCurColor());
				g.drawString(String.valueOf(l.getID()), l.getIDx(), l.getIDy());
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
			
	    public void mouseClicked(MouseEvent e) {
			//System.out.println("click on " + e.getX() + "," + e.getY());
	    	if(curOperation.equals("node")) {
	    		GraphNode n = new GraphNode(e.getX(),e.getY(), GraphEditorGUI.this);
				nodes.add(n);
				add(n);
				repaint();
	    	} else if(curOperation.equals("select")) {
	    		//select-off selected node
	    		if(selectedNode != null) {
		    		selectedNode.setCurColor(selectedNode.getFirstColor());
					selectedNode = null;
	    		}
	    		//select-off selected line
    			if(selectedLine!=null) {
	    			selectedLine.setCurColor(selectedLine.getFirstColor());
	    			selectedLine = null;
    			}
    			//check if click on line
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
