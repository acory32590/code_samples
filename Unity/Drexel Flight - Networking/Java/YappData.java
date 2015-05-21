package drexelride;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class YappData {
	public static final int SIZE = 96;
	private byte[] header = { 0x00, 0x02, 0x60, 0x00, 0x00, 0x00, 0x00, 0x00 }; //unused
	private byte[] fsTimer = { 0x00, 0x00, 0x00, (byte)0xC0, 0x47, 0x61, 0x24, 0x40 }; //unused
	private byte[] plotTimeTick = { 0x00, 0x00, 0x00, 0x00 }; //unused
	private byte[] tmp = {0x00, 0x00, 0x00, 0x00}; //unused
	private byte[] xLinVel = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private byte[] yLinVel = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private byte[] zLinVel = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private byte[] pitchAngVel = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }; //unused
	private byte[] rollAngVel = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }; //unused
	private byte[] pitchPos = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private byte[] rollPos = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	private byte[] altitude = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }; //unused
	private byte[] garbage = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }; //unused

	public YappData() {
		
	}
	
	public YappData(double xLinVel, double yLinVel, double zLinVel, double pitch, double roll) {
		setXLinVel(xLinVel);
		setYLinVel(yLinVel);
		setZLinVel(zLinVel);
		setPitchPos(pitch);
		setRollPos(roll);
	}

	public byte[] asByteArray() {
		ByteBuffer result = ByteBuffer.allocate(SIZE);
		result.put(header);
		result.put(fsTimer);
		result.put(plotTimeTick);
		result.put(tmp);
		result.put(xLinVel);
		result.put(yLinVel);
		result.put(zLinVel);
		result.put(pitchAngVel);
		result.put(rollAngVel);
		result.put(pitchPos);
		result.put(rollPos);
		result.put(altitude);
		result.put(garbage);
		return result.array();
	}

	public void setXLinVel(double velocity) {
		xLinVel = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(velocity).array();
	}

	public void setYLinVel(double velocity) {
		yLinVel = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(velocity).array();
	}

	public void setZLinVel(double velocity) {
		zLinVel = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(velocity).array();
	}

	public void setPitchPos(double p) {
		pitchPos = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(p).array();
	}

	public void setRollPos(double r) {
		rollPos = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(r).array();
	}
}
