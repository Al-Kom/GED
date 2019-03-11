import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/*
 * TODO 
 * -JToolBar or JMenuBar?
 * 
 * -pictures for buttons
 * -GridLayout for buttons
 * 
 * -ToggleButton for change button
 * 	or just change cartoon
 * 
 * -class GraphNode
 * -class GraphLine
 * -double click to drawPanel ==> event 
 */
public class GraphEditorGUI {
	String curOperation;
	JButton selectButton;
	JButton nodeButton;
	JButton lineButton;
	JPanel leftPanel;
	public void run() {
		JFrame frame = new JFrame("GraphEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu("Edit");
		JMenuItem editIDMenuItem = new JMenuItem("Edit ID");
		editMenu.add(editIDMenuItem);
		menuBar.add(editMenu);
		frame.setJMenuBar(menuBar);
		
		DrawPanel drawPanel = new DrawPanel();
		frame.getContentPane().add(BorderLayout.CENTER, drawPanel);

		leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(Color.DARK_GRAY);
		ImagePanel logoPanel = new ImagePanel("sources/logo.jpg");
		leftPanel.add(logoPanel);	

		GridLayout grid = new GridLayout(3,1);
		grid.setVgap(3);
		JPanel buttonPanel = new JPanel(grid);
		buttonPanel.setBackground(Color.DARK_GRAY);

		ImageIcon iconSelectButton = new ImageIcon("sources/Bselect.jpg");
		selectButton = new JButton(iconSelectButton);
		selectButton.setMaximumSize(new Dimension(75,75));
		buttonPanel.add(selectButton);
		selectButton.addActionListener( new SelectButtonListener());

		ImageIcon iconNodeButton = new ImageIcon("sources/Bnode.jpg");
		nodeButton = new JButton(iconNodeButton);
		nodeButton.setMaximumSize(new Dimension(75,75));
		buttonPanel.add(nodeButton);
		nodeButton.addActionListener( new NodeButtonListener());

		ImageIcon iconLineButton = new ImageIcon("sources/Bline.jpg");
		lineButton = new JButton(iconLineButton);
		lineButton.setMaximumSize(new Dimension(75,75));
		buttonPanel.add(lineButton);
		lineButton.addActionListener( new LineButtonListener());
		leftPanel.add(buttonPanel);
		
		JPanel emptyLeftPanel = new JPanel();
		emptyLeftPanel.setToolTipText("emptyPanel");
		emptyLeftPanel.setBackground(Color.DARK_GRAY);
		leftPanel.add(emptyLeftPanel);
		
		frame.getContentPane().add(BorderLayout.WEST, leftPanel);
		
		frame.setSize(700, 500);
		frame.setVisible(true);
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
	
	public class DrawPanel extends JPanel {
		/*TODO
		 * -list of nodes
		 * -list of lines
		 */
		
		public DrawPanel() {
			setBackground(Color.WHITE);
			setToolTipText("DrawPanel");
			setLayout(null);
		}
		public void paintComponent(Graphics g) {
			/*TODO
			 * -way to draw nodes
			 * -way to draw lines
			 */
		}
	}

	public class SelectButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			curOperation = "select";
			System.out.println(curOperation);
			//System.out.println("width of leftPanel " + leftPanel.getWidth());
			//System.out.println("heigh of leftPanel " + leftPanel.getHeight());
		}
	}
	
	public class NodeButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			curOperation = "node";
			System.out.println(curOperation);
		}
	}
	
	public class LineButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			curOperation = "line";
			System.out.println(curOperation);
		}
	}
}
