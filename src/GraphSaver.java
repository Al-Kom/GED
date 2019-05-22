import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GraphSaver {
	private File file;
	
	public GraphSaver(File file) {
		this.file = file;
	}
	
	public void saveGraph(ArrayList<GraphNode> nodes, ArrayList<GraphLine> lines) {
		file = new File(file.toString() + ".xml");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			//root
			Element graphEl = doc.createElement("graph");
			doc.appendChild(graphEl);
			
			//nodes
			Element nodesEl = doc.createElement("nodes");
			graphEl.appendChild(nodesEl);
			for(GraphNode node:nodes) {
				Element nodeEl = doc.createElement("node");
				nodeEl.setAttribute("X", String.valueOf(node.getX()));
				nodeEl.setAttribute("Y", String.valueOf(node.getY()));
				nodeEl.setAttribute("ID", node.getID());
				nodesEl.appendChild(nodeEl);
			}
			//lines
			Element linesEl = doc.createElement("lines");
			graphEl.appendChild(linesEl);
			for(GraphLine line:lines) {
				Element lineEl = doc.createElement("line");
				lineEl.setAttribute("X1", String.valueOf(line.first.getX()));
				lineEl.setAttribute("Y1", String.valueOf(line.first.getY()));
				lineEl.setAttribute("X2", String.valueOf(line.second.getX()));
				lineEl.setAttribute("Y2", String.valueOf(line.second.getY()));
				lineEl.setAttribute("ID", String.valueOf(line.getID()));
				linesEl.appendChild(lineEl);
			}
			//saving to XML-file
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			Transformer transformer = transfFactory.newTransformer();
			
			DOMSource source = new DOMSource(doc);
			StreamResult fileStream = new StreamResult(file);
			transformer.transform(source, fileStream);
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Something was wrong\n when saving to file:\n" + file.getPath(),
					"Error!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
