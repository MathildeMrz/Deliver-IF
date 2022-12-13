package tests;

import model.Intersection;
import model.Segment;
import model.Map;
import model.Courier;
import model.Delivery;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
public class MapTest {
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
	protected Courier courier1;
	protected Courier courier2;
	protected Delivery del1;
	protected Delivery del2;
	protected Delivery del3;
	protected Delivery del4;
	
	@Before
	public void setUp() throws Exception {
		inter1 = new Intersection(2756625, (float)45.365, (float)5.34585);
		inter2 = new Intersection(6874512, (float)46.365, (float)4.34585);
		inter7 = new Intersection(253422236, (float)45.77888, (float)4.8316236);
		inter8 = new Intersection(130143413, (float)45.778397, (float)4.8322353);
		seg1 = new Segment(inter1, (float)27.6, "Rue Albert");
		seg2 = new Segment(inter2, (float)67.2, "Rue du fromage");
		plan = new Map();
		courier1 = new Courier("Jean");
		
	}
	
	@Test
	public void testAddAndGetNode() {
		plan.addNode(inter1);
		assertEquals(inter1, plan.getNodes().get(new Long(2756625)));
	}

	@Test
	public void testAddAndGetWarehouse() {
		plan.addNode(inter1);
		plan.addWarehouse(Long.parseLong("2756625"));
		assertEquals(inter1, plan.getWarehouse());
	}
	
	@Test
	public void testgetClosestIntersection() {
		plan.addNode(inter1);
		plan.addNode(inter2);
		Intersection inter3 = plan.getClosestIntersection((float)45.365, (float)5.2, -1);
		assertEquals(inter1, inter3);
	}
	
	@Test
	public void testgetClosestIntersectionLeftCorner() {
		plan.addNode(inter7);
		plan.addNode(inter8);
		Intersection inter3 = plan.getClosestIntersection((float)45.77888, (float)4.8316236, 253422236);
		assertEquals(inter8, inter3);
	}

}
