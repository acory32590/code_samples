package drexelride.client;

public class DefaultSimulation extends Simulation {

	private double acceleration = 0.5;
	private double maxVelocity = 25.0;
	private int sleep = 33;

	public void run() {
		try {
			int counter = 0;
			while(counter < 5) {
				while(xLinVel <= maxVelocity) {
					xLinVel += acceleration;
					Thread.sleep(sleep);
				}
				while(xLinVel >= -maxVelocity) {
					xLinVel -= acceleration;
					Thread.sleep(sleep);
				}
				counter++;
			}
			counter=0;
			while(counter < 5) {
				while(yLinVel <= maxVelocity) {
					yLinVel += acceleration;
					Thread.sleep(sleep);
				}
				while(yLinVel >= -maxVelocity) {
					yLinVel -= acceleration;
					Thread.sleep(sleep);
				}
				counter++;
			}
			counter=0;
			while(counter < 5) {
				while(zLinVel <= maxVelocity) {
					zLinVel += acceleration;
					Thread.sleep(sleep);
				}
				while(xLinVel >= -maxVelocity) {
					zLinVel -= acceleration;
					Thread.sleep(sleep);
				}
				counter++;
			}
			counter=0;
			while(counter < 5) {
				while(pitch <= maxVelocity) {
					pitch += acceleration;
					Thread.sleep(sleep);
				}
				while(pitch >= -maxVelocity) {
					pitch -= acceleration;
					Thread.sleep(sleep);
				}
				counter++;
			}
			counter=0;
			while(counter < 5) {
				while(roll <= maxVelocity) {
					roll += acceleration;
					Thread.sleep(sleep);
				}
				while(roll >= -maxVelocity) {
					roll -= acceleration;
					Thread.sleep(sleep);
				}
				counter++;
			}
			counter=0;
		} catch(InterruptedException e) {
			System.out.println("barf");
		}
		finished = true;
	}
}