package tests;

import model.Intersection;
import model.Segment;
import model.Tour;
import model.Map;
import model.Courier;
import model.Delivery;
import algorithm.RunTSP2;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
public class TestTsp {
	protected Map plan;
	protected Intersection inter1;
	protected Intersection inter2;
	protected Intersection inter3;
	protected Intersection inter4;
	protected Intersection inter5;
	protected Intersection inter6;
	protected Intersection inter7;
	protected Intersection inter8;
	protected Segment seg1;
	protected Segment seg2;
	protected Segment seg3;
	protected Segment seg4;
	protected Segment seg5;
	protected Segment seg6;
	protected Courier courier1;
	protected Courier courier2;
	protected Delivery del1;
	protected Delivery del2;
	protected Delivery del3;
	protected Delivery del4;
	protected Tour tour;
	protected Intersection dest;
	
	@Before
	public void setUp() throws Exception {
		inter1 = new Intersection(2756625, (float)45.365, (float)5.34585);
		inter2 = new Intersection(6874512, (float)46.365, (float)4.34585);
		inter2.setOutSections(null);
		inter7 = new Intersection(253422236, (float)45.77888, (float)4.8316236);
		inter8 = new Intersection(130143413, (float)45.778397, (float)4.8322353);
		seg1 = new Segment(inter1, (float)27.6, "Rue Albert");
		seg2 = new Segment(inter2, (float)67.2, "Rue du fromage");
		seg3 = new Segment(inter7, (float)67.2, "Rue du fromage");
		seg4= new Segment(inter8, (float)67.2, "Rue du fromage");
		seg5=new Segment(inter1, (float)67.2, "Rue du fromage");
		ArrayList <Segment> ointer2=new ArrayList<Segment>();
		ointer2.add(seg3);
		ointer2.add(seg4);
		ointer2.add(seg5);
		ointer2.add(seg1);
		
		ArrayList <Segment> ointer1=new ArrayList<Segment>();
		ointer1.add(seg2);
		
		inter2.setOutSections(ointer2);
		inter1.setOutSections(ointer1);
		plan = new Map();
		courier1 = new Courier("Jean");
		plan.addNode(inter1); //warehouse
		plan.addNode(inter2);
		plan.addNode(inter7);
		plan.addNode(inter8);
		this.tour = new Tour();
		this.tour.initArrivals();
		LocalDate date = LocalDate.now();
		this.tour.addDelivery(inter2, date, 8);
	}
	
	@Test
	public void testTSP() {
		List<Intersection>intersection=new ArrayList<Intersection>();
		intersection.add(inter1);
		intersection.add(inter2);
		RunTSP2 tsp=new RunTSP2(inter1,2,intersection,plan,tour) ;
		tsp.start();
		assertEquals(tsp.getSolutionTrouvee(),true);
	}


}

