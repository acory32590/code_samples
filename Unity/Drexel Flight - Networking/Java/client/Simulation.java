package drexelride.client;

import drexelride.YappData;

public abstract class Simulation implements Runnable {
	protected double xLinVel = 0.0;
	protected double yLinVel = 0.0;
	protected double zLinVel = 0.0;
	protected double pitch = 0.0;
	protected double roll = 0.0;
	protected boolean finished;
	
	public YappData getYappData() {
		return new YappData(xLinVel, yLinVel, zLinVel, pitch, roll);
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public abstract void run();
}
