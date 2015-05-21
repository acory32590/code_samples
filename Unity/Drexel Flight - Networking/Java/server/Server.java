package drexelride.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import drexelride.YappData;
import drexelride.client.Client;
import drexelride.recorder.Recorder;

public class Server implements Runnable {

	private DatagramSocket ds;
	private int port;
	private Logger logger;
	private Recorder recorder;
	private boolean videoStart = false;
	private boolean stop = false;
	private boolean isRunning = false;
	
	private Client client;

	public Server(String ip, int port, boolean video) {
		this.port = port;
		String timestamp = getTimestamp();
		this.logger = new Logger(timestamp);
		this.recorder = video ? new Recorder(timestamp) : null;
		this.ds = createDatagramSocket();
		this.client = new Client(port, ip);
	}

	private String getTimestamp() {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
		return format.format(new Date());
	}

	private DatagramSocket createDatagramSocket() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(this.port);
			ds.setSoTimeout(1000);
		} catch (SocketException e) {
			System.out.println("Error creating socket. Aborting.");
			e.printStackTrace();
			System.exit(1);
		}
		return ds;
	}

	public void run() {
		Thread clientThread = new Thread(this.client);
		clientThread.start();
		isRunning = true;
		boolean timeout = false;
		while (!stop) {
			byte[] buf = new byte[YappData.SIZE];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			try {
				ds.receive(p);
				if(timeout) {
					System.out.println("Timeout resolved");
					timeout = false;
				}
				if (!videoStart && recorder != null) {
					new Thread(recorder, "Video Recorder").start();
					videoStart = true;
				}
			} catch (SocketTimeoutException e) {
				if(!timeout) {
					System.out.println("Socket timeout");
					timeout = true;
				}
			} catch (IOException e) {
				System.out.println("Unexpected error with socket. Aborting.");
				e.printStackTrace();
				System.exit(1);
			}
			logger.log(buf);
			if(!timeout) {
				this.client.packet = buf;
			}
		}
		close();
		isRunning = false;
	}
	
	public void stop() {
		this.stop = true;
		if(!isRunning) {
			close();
		}
	}
	
	private void close() {
		if(this.recorder != null) {
			this.recorder.stop();
		}
		if(this.client != null) {	
			this.client.stop = true;
		}
		this.logger.close();
		this.ds.close();
	}
}
