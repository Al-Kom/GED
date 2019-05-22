import java.io.File;

import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GraphLoader {
	private File file;

	public GraphLoader(File file) {
		this.file = file;
	}
	
	public void loadGraph(GraphEditorGUI gui) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, new GraphXMLHandler(gui));
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Something was wrong\n when saving to file:\n" + file.getPath(),
					"Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private class GraphXMLHandler extends DefaultHandler {
		private GraphEditorGUI gui;
		
		public GraphXMLHandler(GraphEditorGUI gui) {
			this.gui = gui;
		}

		@Override
		public void startElement(String uri, String localName,
								 String qName, Attributes attributes)
			throws SAXException
		{
			if(qName.equals("node")) {
				try {
					int x = Integer.parseInt(attributes.getValue("X"));
					int y = Integer.parseInt(attributes.getValue("Y"));
					String id = attributes.getValue("ID");
					GraphNode newNode = new GraphNode(x, y, gui);
					newNode.setID(id);
					gui.nodes.add(newNode);					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null,
							"Bad input values for node:\n" +
							" " + attributes.getValue("ID") +
							"[" + attributes.getValue("X") +
							"."	+ attributes.getValue("Y") + "]",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
				}
			} else if(qName.equals("line")) {
				try {
					int x1 = Integer.parseInt(attributes.getValue("X1"));
					int y1 = Integer.parseInt(attributes.getValue("Y1"));
					int x2 = Integer.parseInt(attributes.getValue("X2"));
					int y2 = Integer.parseInt(attributes.getValue("Y2"));
					int id = Integer.parseInt(attributes.getValue("ID"));
					GraphNode fir = null;
					GraphNode sec = null;
					for(GraphNode node:gui.nodes) {
						if(node.getX() == x1 && node.getY() == y1) {
							fir = node;
						}
						if(node.getX() == x2 && node.getY() == y2) {
							sec = node;
						}
					}
					if(fir != null && sec != null) {
						GraphLine newLine = new GraphLine(fir, sec);
						newLine.setID(id);
						gui.lines.add(newLine);
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null,
							"Bad input values for line:\n" +
							" " + attributes.getValue("ID") +
							"[" + attributes.getValue("X1") +
							"."	+ attributes.getValue("Y1") + "]" +
							"[" + attributes.getValue("X2") +
							"." + attributes.getValue("Y2") + "]",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
