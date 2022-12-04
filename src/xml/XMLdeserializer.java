package xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.stage.Stage;
import model.Intersection;
import model.Map;
import model.Segment;


public class XMLdeserializer {
	/**
	 * Open an XML file and create plan from this file
	 * @param plan the plan to create from the file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionXML
	 */
	public static void load(Map plan, Stage stage) throws ParserConfigurationException, SAXException, IOException, ExceptionXML{
		File xml = XMLfileOpener.getInstance().open(true, stage);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();	
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("map")) {
        	long start = System.currentTimeMillis();
        	buildFromDOMXML(root, plan);
        	System.out.println("Temps d'exécution (ms) : "+ (System.currentTimeMillis()-start));
        }
        else
        	throw new ExceptionXML("Wrong format");
	}

    private static void buildFromDOMXML(Element noeudDOMRacine, Map plan) throws ExceptionXML, NumberFormatException{
       	NodeList intersectionList = noeudDOMRacine.getElementsByTagName("intersection");
       	for (int i = 0; i < intersectionList.getLength(); i++) {
        	plan.addNode(createIntersection((Element) intersectionList.item(i), plan));
       	}
       	NodeList warehouse = noeudDOMRacine.getElementsByTagName("warehouse");
       	for (int i = 0; i < warehouse.getLength(); i++) {
        	plan.addWarehouse(Long.parseLong(( (Element)warehouse.item(i) ).getAttribute("address")));
       	}
       	NodeList segmentList = noeudDOMRacine.getElementsByTagName("segment");
       	for (int i = 0; i < segmentList.getLength(); i++) {
          	createSegment((Element) segmentList.item(i), plan);
       	}
    }
    
    private static Intersection createIntersection(Element elt, Map plan) throws ExceptionXML{
    	long id = Long.parseLong(elt.getAttribute("id"));
   		float latitude = Float.parseFloat(elt.getAttribute("latitude"));
   		float longitude = Float.parseFloat(elt.getAttribute("longitude"));
   		if (plan.getLatitudeMin() > latitude)
   		{
   			plan.setLatitudeMin(latitude);
   		}
   		if (plan.getLatitudeMax() < latitude)
   		{
   			plan.setLatitudeMax(latitude);
   		}
   		if (plan.getLongitudeMin() > longitude)
   		{
   			plan.setLongitudeMin(longitude);
   		}
   		if (plan.getLongitudeMax() < longitude)
   		{
   			plan.setLongitudeMax(longitude);
   		}
   		Intersection i = new Intersection(id, latitude, longitude);
   		return i;
    }
    
    private static void createSegment(Element elt, Map plan) throws ExceptionXML{
   		long origin = Long.parseLong(elt.getAttribute("origin"));
   		long dest = Long.parseLong(elt.getAttribute("destination"));
   		float length = Float.parseFloat(elt.getAttribute("length"));
   		String name = elt.getAttribute("name");
   		Intersection originInter = plan.getNodes().get(origin);
   		Intersection destInter = plan.getNodes().get(dest);
		if(originInter == null) {
			throw new ExceptionXML("Error : could not find the origin of the segment");
		}
		if(destInter == null) {
			throw new ExceptionXML("Error : could not find the destination of the segment [destination=" + dest +" length="+length+" name="+name+" origin="+origin+"]");
		}
   		Segment s = new Segment(destInter, length, name);
   		originInter.addOutSection(s);
    }
 
}
