package drexelride.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import drexelride.YappData;

public class Client implements Runnable {
	private DatagramSocket sock;
	private String ip = "10.10.101.90";
	private int port;
	public byte[] packet;
	public boolean stop = false;
	
	public Client(int port, String ip) {
		createDatagramSocket();
		this.ip = ip;
		this.port = port;
		this.packet = new YappData().asByteArray();
	}
	
	public Client() {
		this(5001, "10.10.101.90");
	}
	
	private void createDatagramSocket() {
		try {
			this.sock = new DatagramSocket(5001);
		} catch(IOException e) {
			this.sock = null;
			System.err.println("ERROR: Unable to create client socket.");
		}
	}

	public void sendPacket() throws IOException {
		DatagramPacket packet = new DatagramPacket(this.packet, this.packet.length, InetAddress.getByName(ip), this.port);
		sock.send(packet);
	}
	
	public void run() {
		try {
			while(!stop) {
				this.sendPacket();
			}
		} catch(IOException e) {
			System.err.println("ERROR SENDING PACKETS");
		} finally {
			this.sock.close();
		}
	}
}
