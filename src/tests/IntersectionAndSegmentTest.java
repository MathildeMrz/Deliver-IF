package tests;

import model.Intersection;
import model.Segment;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntersectionAndSegmentTest {
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
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIntersectionGetId() {
		assertEquals(2756625, inter1.getId());
	}

	@Test
	public void testIntersectionGetLatitude() {
		assertEquals((float)45.365, inter1.getLatitude(), 0.0002);
	}

	@Test
	public void testIntersectionGetLongitude() {
		assertEquals((float)5.34585, inter1.getLongitude(), 0.0002);
	}
	
	@Test
	public void testIntersectionAddOutSection() {
		inter1.addOutSection(seg2);
		assertEquals(seg2, inter1.getOutSections().get(0));
	}

	@Test
	public void testIntersectionToString() {
		assertEquals("Intersection [id=2756625, latitude=45.365, longitude=5.34585]", inter1.toString());
	}
	
	@Test
	public void testSegmentGetDestination() {
		assertEquals(inter1, seg1.getDestination());
	}

	@Test
	public void testSegmentGetLength() {
		assertEquals((float)27.6, seg1.getLength(), 0.0002);
	}

	@Test
	public void testSegmentGetName() {
		assertEquals("Rue Albert", seg1.getName());
	}
	
	@Test
	public void testSegmentToString() {
		assertEquals("Segment [destination=" + inter1.toString() + ", length=27.6, name=Rue Albert]", seg1.toString());
	}

}
