package tests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import deliverif.DeliverIf;
import model.Intersection;
import model.Segment;

import static org.junit.Assert.assertEquals;

public class InitialiseWindowTest {


	@Before
	public void setUp() throws Exception {
		/*inter1 = new Intersection(2756625, (float)45.365, (float)5.34585);
		inter2 = new Intersection(6874512, (float)46.365, (float)4.34585);
		seg1 = new Segment(inter1, (float)27.6, "Rue Albert");
		seg2 = new Segment(inter2, (float)67.2, "Rue du fromage");*/
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitialisation() {
		DeliverIf deliverif= new DeliverIf();
		//assertEquals(2756625, inter1.getId());
	}
}
