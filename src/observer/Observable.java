package observer;

import java.util.ArrayList;
import java.util.Collection;

public class Observable {
	private Collection<Observer> obs;
	
	/**
	 * Initialises the arrayList of Observer
	 * */	
	public Observable(){
		obs = new ArrayList<Observer>();
	}
	
	/**
	 * Adds an observer to the set of observers for this object, provided that it is not the same as some
	 * observer already in the set.
	 * @param o : observer to add
	 * */	
	public void addObserver(Observer o){
		if (!obs.contains(o)) obs.add(o);
	}
	
	/**
	 * After an observable instance changes, an application calling the Observable's notifyObservers method
	 * causes all of its observers to be notified of the change by a call to their update method.
	 * @param arg : any object
	 * */	
	public void notifyObservers(Object arg){
		for (Observer o : obs) 
			o.update(this, arg);
	}
	
	/**
	 * After an observable instance changes, an application calling the Observable's notifyObservers method
	 * causes all of its observers to be notified of the change by a call to their update method.
	 * */	
	public void notifyObservers(){
		notifyObservers(null);
	}
}