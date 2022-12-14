package algorithm;

import model.Path;

public interface Graph {
	/**
	 * @return the number of vertices in <code>this</code>
	 */
	public abstract int getNbVertices();

	/**
	 * @param i 
	 * @param j 
	 * @return the cost of arc (i,j) if (i,j) is an arc; -1 otherwise
	 */
	public abstract double getCost(int i, int j);

	/**
	 * @param i 
	 * @param j 
	 * @return true if <code>(i,j)</code> is an arc of <code>this</code>
	 */
	public abstract boolean isArc(int i, int j);
	
	/**
	 * @param i 
	 * @param j 
	 * @return the path of arc (i,j) if (i,j) is an arc; -1 otherwise
	 */
	public abstract Path getPath(int i, int j);
	
	/** 
	 * @return the table of costs
	 */
	public abstract double [][] getTableCost();
	
	/** 
	 * @return the table of Laps of start times
	 */
	public int [] getTimeLapsStart();
	
	/** 
	 * @return the table of Laps of end times
	 */
	public int [] getTimeLapsEnd() ;

}
