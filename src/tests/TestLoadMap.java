package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import javafx.stage.Stage;
import model.Map;
import model.Segment;
import xml.ExceptionXML;
import xml.XMLdeserializer;
import xml.XMLserializer;

public class TestLoadMap {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ExceptionXML, TransformerFactoryConfigurationError, TransformerException {
		Map plan = new Map();
		Stage stage = new Stage();
		XMLdeserializer.load(plan, stage);
		System.out.println(plan);
		System.out.println("Warehouse = " + plan.getWarehouse());
		System.out.println("Number of intersections : " + plan.getNodes().size());
	}
}
