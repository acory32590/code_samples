package drexelride.server;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.LiveGraph.LiveGraph;
import org.LiveGraph.dataFile.write.DataStreamWriter;
import org.LiveGraph.dataFile.write.DataStreamWriterFactory;
import org.LiveGraph.settings.DataFileSettings;
import org.LiveGraph.settings.GraphSettings;

public class Logger {

	private final String dataDir = "data/";
	private final String dataFileSettingsFile = "resources/session.lgdfs";
	private final String graphSettingsFile = "resources/session.lggs";
	private final String TIME = "Time";
	private final String X_LIN_VEL = "X Linear Velocity";
	private final String Y_LIN_VEL = "Y Linear Velocity";
	private final String Z_LIN_VEL = "Z Linear Velocity";
	private final String PITCH_POS = "Pitch Position";
	private final String ROLL_POS = "Roll Position";
	
	private long startTime;
	private DataStreamWriter writer;
	private LiveGraph app;

	public Logger(String outFile) {
		checkDirectory();
		String file = dataDir + outFile + ".dat";
		this.writer = DataStreamWriterFactory.createDataWriter(file, true);
		this.writer.addDataSeries(TIME);
		this.writer.addDataSeries(X_LIN_VEL);
		this.writer.addDataSeries(Y_LIN_VEL);
		this.writer.addDataSeries(Z_LIN_VEL);
		this.writer.addDataSeries(PITCH_POS);
		this.writer.addDataSeries(ROLL_POS);
		this.app = LiveGraph.application();
		startLiveGraph(file);
	}

	private void checkDirectory() {
		new File(dataDir).mkdirs();
	}
	
	private void startLiveGraph(String dataFile) {
		DataFileSettings dataFileSettings = new DataFileSettings();
		dataFileSettings.setDataFile(dataFile);
		dataFileSettings.setUpdateFrequency(500);
		dataFileSettings.save(dataFileSettingsFile);
		
		GraphSettings graphSettings = new GraphSettings();
		graphSettings.setXAxisType(GraphSettings.XAxisType.XAxis_DataValSimple);
		graphSettings.setXAxisSeriesIndex(0);
		graphSettings.save(graphSettingsFile);
		
		String[] args = new String[4];
		args[0] = "-dfs";
		args[1] = dataFileSettingsFile;
		args[2] = "-gs";
		args[3] = graphSettingsFile;
		this.app.exec(args);
	}

	public void log(byte[] datagram) {
		long currentTime = System.currentTimeMillis();
		if (this.startTime == 0) {
			this.startTime = currentTime;
		}
		this.writer.setDataValue(TIME, (currentTime - startTime) / 1000.0);
		this.writer.setDataValue(X_LIN_VEL, toDouble(datagram, 24, 8));
		this.writer.setDataValue(Y_LIN_VEL, toDouble(datagram, 32, 8));
		this.writer.setDataValue(Z_LIN_VEL, toDouble(datagram, 40, 8));
		this.writer.setDataValue(PITCH_POS, toDouble(datagram, 64, 8));
		this.writer.setDataValue(ROLL_POS, toDouble(datagram, 72, 8));
		this.writer.writeDataSet();
	}

	public double toDouble(byte[] buf, int start, int length) {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.put(buf, start, length);
		return b.getDouble(0);
	}

	public double toDouble(byte[] byteArr) {
		return ByteBuffer.wrap(byteArr).getDouble();
	}

	public void close() {
		this.writer.close();
		this.app.disposeGUIAndExit();
	}

}
