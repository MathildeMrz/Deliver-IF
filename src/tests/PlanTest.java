package tests;

import model.Intersection;
import model.Segment;
import model.Map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
public class PlanTest {
	protected Map plan;
	protected Intersection inter1;
	protected Intersection inter2;
	protected Segment seg1;
	protected Segment seg2;

	@Before
	public void setUp() throws Exception {
		inter1 = new Intersection(2756625, (float)45.365, (float)5.34585);
		inter2 = new Intersection(6874512, (float)46.365, (float)4.34585);
		seg1 = new Segment(inter1, (float)27.6, "Rue Albert");
		seg2 = new Segment(inter2, (float)67.2, "Rue du fromage");
		plan = new Map();
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

}
