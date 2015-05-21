package drexelride.recorder;

import java.awt.Dimension;

import com.github.sarxos.webcam.Webcam;

public class Recorder implements Runnable {

	private Webcam cam;
	private Writer writer;
	private boolean shouldStop = false;
	private int frameRate;

	public Recorder(String outFile) {
		this(outFile, 30);
	}
	
	public Recorder(String outFile, int frameRate) {
		this.frameRate = frameRate;
		this.cam = initializeWebcam();
		this.writer = new Writer(outFile, frameRate);
	}

	private Webcam initializeWebcam() {
		Webcam cam = Webcam.getDefault();
		System.out.print("Initializing webcam...");
		cam.setViewSize(new Dimension(640, 480));
		// CONFIGURE WEBCAM BEFORE CALLING .OPEN!!!
		cam.open();
		/* unstable but graceful exit. comment
		 this line out if experiencing
		 problems */
		Webcam.setHandleTermSignal(true);
		System.out.println("...done");
		return cam;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		double timePerFrame = 1.0 / (double)frameRate * 1000;
		System.out.println("Recording!");
		Thread writerThread = new Thread(this.writer);
		writerThread.start();
		long frameTimer = startTime;
		while (!shouldStop) {
			long current = System.currentTimeMillis();
			if(current - frameTimer >= timePerFrame) {
				this.writer.addFrame(cam.getImage());
				frameTimer = current;
			}
		}
		this.writer.stop();
		System.out.println("Recording stopped.");
		double duration = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.println("Video duration: " + duration);
		try {
			writerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished writing video file.");
	}

	public void close() {
		System.out.println("Closing camera");
		this.cam.close();
	}

	public void stop() {
		this.shouldStop = true;
	}
	
	public static void main(String[] args) throws InterruptedException {
		Recorder r = new Recorder("test", 15);
		Thread t = new Thread(r);
		t.start();
		Thread.sleep(30000);
		r.stop();
		t.join();
	}
}