package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Courier {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private String name;
	private double speed;
	
	public Courier(String name, double speed)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.name = name;
		this.speed = speed;
	}
	
	@Override
    public String toString() {
        return id + " " + name + " " +speed;
    }
}
