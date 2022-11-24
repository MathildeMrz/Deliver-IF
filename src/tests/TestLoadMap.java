package tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import model.Plan;
import model.Segment;
import xml.ExceptionXML;
import xml.XMLdeserializer;
import xml.XMLserializer;

public class TestLoadMap {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ExceptionXML, TransformerFactoryConfigurationError, TransformerException {
		Plan plan = new Plan();
		XMLdeserializer.load(plan);
		System.out.println(plan);
		for(Segment s : plan.getNodes().get(10).getOutSections()) {
			System.out.println(s);
		}
		System.out.println("Number of intersections : " + plan.getNodes().size());
		XMLserializer.getInstance().save(plan);
	}
}
