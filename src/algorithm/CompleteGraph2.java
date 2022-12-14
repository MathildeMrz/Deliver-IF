package algorithm;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Path;
import model.Tour;

public class CompleteGraph2 implements Graph {
	int nbVertices;
	double [][] cost;
	Path [][] path;
	private static List <Intersection> Intersection;
	private static List <Delivery> deliveries;
	private int [] timeLapsStart;
	private int [] timeLapsEnd;
	
	/**
	 * Create a complete directed graph such that each edge has a weight that corresponds to the time spent by the courier between the two nodes in seconds (knowing that his speed is 15 km/h).
	 * @param warehouse : Intersection corresponding to the warehouse
	 * @param nbVertices : number of nodes that should the graph have
	 * @param tour : the tour corresponding to one courier and one day
	 * @param lePlan : the map corresponding to the tour
	 * */
	public CompleteGraph2(Intersection warehouse, int nbVertices,Tour tour, Map lePlan){
		this.nbVertices = nbVertices;
		this.Intersection= new ArrayList<Intersection>();
		this.deliveries=new ArrayList<Delivery>();
		Intersection.add(warehouse);
		for (Delivery d: tour.getDeliveries()) {
			deliveries.add(d);
			Intersection.add(d.getDestination());
		}

		cost = new double[nbVertices][nbVertices];
		path= new Path[nbVertices][nbVertices];
		
		//NEW :TIME LAPS
		int[] timeLapsStart = new int[Intersection.size()];
		int[] timeLapsEnd= new int[Intersection.size()];
		//FIN NEW
		for (int i=0; i<nbVertices; i++){
		    for (int j=0; j<nbVertices; j++){
		        if (i == j) cost[i][j] = -1;
		        else {
		        	Dijkstra djikstra= new Dijkstra(lePlan,this.Intersection.get(i)) ;
		        	djikstra.run();
		        	
		        	cost[i][j] = djikstra.getCoutIntersection(this.Intersection.get(j).getId())*3.6/15;//pour avoir les secondes
		        	
		        	/*save the itineraries*/
		        	path[i][j]=djikstra.getItinerary(this.Intersection.get(j).getId());
		        	
		        	//NEW : TIME LAPS
		        	if(i == 0)
					{
						timeLapsStart[0] = 0;
						timeLapsEnd[0] = Integer.MAX_VALUE;
					}
					else {
						
						int debut = tour.getStartDate().getHour();
						if(debut== 8 || debut== 9 || debut== 10 || debut== 11  ) {
							int deliveryStartLaps=deliveries.get(i-1).getStartTime();
							String deliveryStartLapsString=Integer.toString(deliveryStartLaps);
							String finalHourDeliveryStart="0";
							if(deliveryStartLaps== 8 || deliveryStartLaps==9)
							{
								finalHourDeliveryStart= "0"+deliveryStartLapsString+":00:00";
							}
							else
							{
								finalHourDeliveryStart= deliveryStartLapsString+":00:00";
							}
							String finalHourTourStart="0";
							if(tour.getStartDate().getHour()==8 || tour.getStartDate().getHour()==9)
							{
								finalHourTourStart="0"+Integer.toString(tour.getStartDate().getHour())+":00:00";
							}
							else
							{
								finalHourTourStart=Integer.toString(tour.getStartDate().getHour())+":00:00";
							}
							LocalTime timeOfDeliveryStart = LocalTime.parse(finalHourDeliveryStart, DateTimeFormatter.ISO_TIME);
							LocalTime timeOfTourStart = LocalTime.parse(finalHourTourStart, DateTimeFormatter.ISO_TIME);
							timeLapsStart[i] = (int) ChronoUnit.SECONDS.between(timeOfTourStart/*de la plage horaire de la tournée*/, timeOfDeliveryStart/*debut de la plage horaire du delivery */);
							timeLapsEnd[i] = (int) ChronoUnit.SECONDS.between(timeOfTourStart/*de la plage horaire de la tournée*/, timeOfDeliveryStart.plusHours(1)/*fin de la plage horaire du delivery */);
		
							
						}
						else {
							timeLapsStart[i] = 0;
							timeLapsEnd[i] = Integer.MAX_VALUE;
						}
						
					}
		        	//END NEW
		        }
		    }
		}
		//NEW
		this.timeLapsStart=timeLapsStart;
		this.timeLapsEnd=timeLapsEnd;
		//END NEW
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public double getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1.0;
		return cost[i][j];
	}
	
	@Override
	public double [][] getTableCost() {
		return cost;
	}

	@Override
	public Path getPath(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return null;
		return path[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}
	
	@Override
	public int [] getTimeLapsStart() {
		return timeLapsStart;
	}
	
	@Override
	public int [] getTimeLapsEnd() {
		return timeLapsEnd;
	}
}

