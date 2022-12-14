package tests;

import static org.junit.Assert.*;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import model.Tour;
import model.Intersection;

public class TourTest {
	Tour tour;
	Intersection dest;

	@Before
	public void setUp() throws Exception {
		this.tour = new Tour();
		this.tour.initArrivals();
		this.dest = new Intersection(2129259178, (float)45.750404, (float)4.8744674);
	}

	@Test
	public void testAddDelivery() {
		LocalDate date = LocalDate.now();
		this.tour.addDelivery(this.dest, date, 8);
	}
	
	@Test
	public void testSetArrival() {
		LocalDate date = LocalDate.now();
		this.tour.addDelivery(this.dest, date, 8);
		this.tour.setArrival(this.dest, 1, 25);
		assertTrue(this.tour.getTourTimes()[1].compareTo(this.tour.getTourTimes()[0])>0);
	}

}
