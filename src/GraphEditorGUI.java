import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
/*
 * TODO
 * -add task: output planarity -- how?
 * -add task: making graph planar -- how?
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
		JMenuItem findEilerCycleMenuItem = new JMenuItem("Найти эйлеров цикл");
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
		findEilerCycleMenuItem.addActionListener( new FindEilerCycleMenuListener());
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
		taskMenu.add(findEilerCycleMenuItem);
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
			File file = fileSaveDialog.getSelectedFile();
			if(file != null) {
				GraphSaver saver = new GraphSaver(file);
				saver.saveGraph(nodes, lines);
			}
		}
	}
	
	private class OpenMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileOpenDialog = new JFileChooser();
			fileOpenDialog.showOpenDialog(frame);
			File file = fileOpenDialog.getSelectedFile();
			if(file != null) {
				clearGraphMemory();
				GraphLoader loader = new GraphLoader(file);
				loader.loadGraph(GraphEditorGUI.this);
				for(GraphNode node:nodes) {
					drawPanel.add(node);
				}
				drawPanel.paint(drawPanel.getGraphics());
			}
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
						"Матрица смежности:\n" + nodesIDs + "\n" + outputMatrix,
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
				JOptionPane.showMessageDialog(null,
						"Bad number for line ID",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
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
				for(int i = 0; i< lines.size(); i++) {
					if(lines.get(i).first.equals(selectedNode)
						|| lines.get(i).second.equals(selectedNode)) {
						lines.remove(i);
						//go a step back after removing the element we stay on 
						i--;
					}
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
				JOptionPane.showMessageDialog(null,
						"Bad entered coefficient",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
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
		ArrayList<GraphLine> linesRecover = (ArrayList<GraphLine>)lines.clone();
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
			JOptionPane.showMessageDialog(null,
					"Bad distance of [" + taskEndNode.getID() + "] node",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
		}
		//return graph
		restoreGraph(nodesIDs, lines);
		//answer
		return dist;
	}

	private class FindEilerCycleMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String answer = "";
			ArrayList<GraphNode> badGuys = findNonEvenNodes();
			if(!badGuys.isEmpty()) {
				answer += "Эйлеров цикл не найден из-за следующих\n"
						+ "(нечетных или изолированных) вершин:";
				for(GraphNode node:badGuys) {
					answer += "\n        " + node.getID();
				}
			} else {
				ArrayList<GraphNode> eCycle = findEilerCycle();
				answer += "Найденный цикл:\n" + eCycle.get(0).getID();
				for(int i = 1; i < eCycle.size(); i++) {
					answer += "-" + eCycle.get(i).getID();
				}
			}
			JOptionPane.showMessageDialog(null,
					answer,
					"Нахождение эйлерового цикла",
					JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	private ArrayList<GraphNode> findEilerCycle() {
		ArrayList<GraphNode> answerList = new ArrayList<GraphNode>();
		//store lines
		ArrayList<GraphLine> linesStore = new ArrayList<GraphLine>(lines);
		//init algo
		answerList.add(nodes.get(0));
		findAllCycles(nodes.get(0), answerList);
		//restore lines
		lines = linesStore;
		sleepAndRepaint(0);
		return answerList;
	}

	private void findAllCycles(GraphNode node, ArrayList<GraphNode> answerList) {
		ArrayList<GraphNode> cycle = findCycle(node);
		if(cycle.size() == 0) {
			return;
		} else {
			answerList.addAll(answerList.indexOf(node), cycle);
			cycle.remove(node);
			for(GraphNode n:cycle) {
				findAllCycles(n, answerList);
			}
		}
	}
	
	private ArrayList<GraphNode> findCycle(GraphNode node) {
		ArrayList<GraphNode> cycle = new ArrayList<GraphNode>();
		ArrayList<GraphNode> notSteped = new ArrayList<GraphNode>(nodes);
		//DFS (cycle is smth like 'ABCD' (and 'D' has edge with 'A'))
		cycleDFS(node, notSteped, cycle);
		
		return cycle;
	}

	private void cycleDFS(GraphNode node, ArrayList<GraphNode> notSteped,
			ArrayList<GraphNode> cycle) {
		notSteped.remove(node);
		cycle.add(node);
		for(int i = 0; i < lines.size(); i++) {
			if(lines.get(i).first.equals(node)) {
				//if neighbor is cycle-starter
				if(lines.get(i).second.equals(cycle.get(0))) {
					lines.remove(lines.get(i)); //finish a parallel plan to remove cycle
					return;
				} else if(notSteped.contains(lines.get(i).second)) {
					//if we haven't stepped yet on neighbor
					GraphLine lineGoTo = lines.get(i);
					lines.remove(lineGoTo); //no step back!
					cycleDFS(lineGoTo.second, notSteped, cycle);
					//chain of 'returns' move us back to return the founded cycle
					return;
				}
			} else if(lines.get(i).second.equals(node)) {
				//if neighbor is cycle-starter
				if(lines.get(i).first.equals(cycle.get(0))) {
					lines.remove(lines.get(i)); //finish a parallel plan to remove cycle
					return;
				} else if(notSteped.contains(lines.get(i).first)) {
					//if we haven't stepped yet on neighbor
					GraphLine lineGoTo = lines.get(i);
					lines.remove(lineGoTo); //no step back!
					cycleDFS(lineGoTo.first, notSteped, cycle);
					//chain of 'returns' move us back to return the founded cycle
					return;
				}
			}
		}
	}
	
	private ArrayList<GraphNode> findNonEvenNodes() {
		ArrayList<GraphNode> badGuys = new ArrayList<GraphNode>();
		for(GraphNode node:nodes) {
			if(!isEvenOrAlone(node)) {
				badGuys.add(node);
			}
		}
		return badGuys;
	}
	
	private boolean isEvenOrAlone(GraphNode node) {
		int edgeNumber = 0;
		for(GraphLine line:lines) {
			if(line.first.equals(node) || line.second.equals(node))
				edgeNumber++;
		}
		if(edgeNumber%2 == 0 && edgeNumber != 0)
			return true;
		else
			return false;
	}

	private void cutNodeWithEdges(GraphNode node) {
		for(int i = 0; i < lines.size(); i++) {
			if(lines.get(i).first.equals(node) || lines.get(i).second.equals(node)) {
				lines.remove(lines.get(i));
				i--;	//go a step back after removing the element we stay on 
			}
		}
		nodes.remove(node);
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
		for(GraphLine l : linesRecover) {
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
						line.setCurColor(line.getSecondColor());
						sleepAndRepaint(300);
						//check distance
						int curNborDist = Integer.parseInt(nbor.getID());
						if(curNodeDist + line.getID() < curNborDist) {
							nbor.setID(String.valueOf(curNodeDist + line.getID()));
							stepMap.replace(nbor,
									new ArrayList<GraphLine>(stepMap.get(curNode)));
							stepMap.get(nbor).add(line);
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
				notStepped.remove(curNode);
				curNode.setCurColor(Color.MAGENTA);
				sleepAndRepaint(500);
			}
		} catch(NumberFormatException ex) {
			JOptionPane.showMessageDialog(null,
					"Bad distance of nodes",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
		}
		if(taskEndNode.getID().equals(String.valueOf(Integer.MAX_VALUE)))
			return null;
		return stepMap;
	}
	
	private void sleepAndRepaint(long time) {
		try {
			drawPanel.paint(drawPanel.getGraphics());
			Thread.sleep((long)(time/speedCoef));
		} catch (InterruptedException ie) {
			JOptionPane.showMessageDialog(null,
					"Bad time pause",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
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
