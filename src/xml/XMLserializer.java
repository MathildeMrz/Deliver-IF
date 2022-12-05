package xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.stage.Stage;
import model.Intersection;
import model.Segment;
import model.Map;

public class XMLserializer{// Singleton
	
	private Element eltRoot;
	private Document document;
	private static XMLserializer instance = null;
	private XMLserializer(){}
	public static XMLserializer getInstance(){
		if (instance == null)
			instance = new XMLserializer();
		return instance;
	}
 
	/**
	 * Open an XML file and write an XML description of the plan in it 
	 * @param plan the plan to serialise
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws ExceptionXML
	 */
	public void save(Map plan, Stage stage) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML{
		File xml = XMLfileOpener.getInstance().open(false, stage);
  		StreamResult result = new StreamResult(xml);
       	document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
       	document.appendChild(createPlanElt(plan));
        DOMSource source = new DOMSource(document);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.transform(source, result);
	}
	
	public void save(Map plan) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML{
		File xml = XMLfileOpener.getInstance().open(false);
  		StreamResult result = new StreamResult(xml);
       	document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
       	document.appendChild(createPlanElt(plan));
        DOMSource source = new DOMSource(document);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.transform(source, result);
	}

	private Element createPlanElt(Map plan) {
		Element racine = document.createElement("map");
		eltRoot = document.createElement("warehouse");
		createAttribute(eltRoot,"address",Long.toString(plan.getWarehouse().getId()));
		racine.appendChild(eltRoot);
		for(Intersection i : plan.getNodes().values()) {
			eltRoot = document.createElement("intersection");
			createAttribute(eltRoot, "id", Long.toString(i.getId()));
			createAttribute(eltRoot, "latitude", Float.toString(i.getLatitude()));
			createAttribute(eltRoot, "longitude", Float.toString(i.getLongitude()));
			racine.appendChild(eltRoot);
			for(Segment s : i.getOutSections()) {
				eltRoot = document.createElement("segment");
				createAttribute(eltRoot, "destination", Long.toString(s.getDestination().getId()));
				createAttribute(eltRoot, "length", Float.toString(s.getLength()));
				createAttribute(eltRoot, "name", s.getName());
				createAttribute(eltRoot, "origin", Long.toString(i.getId()));
				racine.appendChild(eltRoot);
			}
		}
		return racine;
	}
	
    private void createAttribute(Element root, String name, String value){
    	Attr attribut = document.createAttribute(name);
    	root.setAttributeNode(attribut);
    	attribut.setValue(value);
    }
}
