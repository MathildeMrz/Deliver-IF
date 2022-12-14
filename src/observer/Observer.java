package observer;

public interface Observer {
	/**
	 * Adds an observer to the set of observers for this object, provided that it is not the same as some
	 * observer already in the set.
	 * @param observed : the observable object
	 * @param arg : an argument passed to the notifyObservers method
	 * */
	public void update(Observable observed, Object arg);
}