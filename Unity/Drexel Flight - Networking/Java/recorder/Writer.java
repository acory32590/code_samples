package drexelride.recorder;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_MJPG;
import static org.monte.media.VideoFormatKeys.HeightKey;
import static org.monte.media.VideoFormatKeys.QualityKey;
import static org.monte.media.VideoFormatKeys.WidthKey;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.avi.AVIWriter;
import org.monte.media.math.Rational;

public class Writer implements Runnable{
	
	private Format format;
	private AVIWriter writer;
	private ConcurrentLinkedQueue<BufferedImage> frames;
	private final String videoDir = "video/";
	private boolean done = false;
	private int index = 0;
	
	public Writer(String outFile, int frameRate) {
		checkDirectory();
		this.writer = createOutputStream(outFile, frameRate);
		this.frames = new ConcurrentLinkedQueue<BufferedImage>();
	}
	
	private AVIWriter createOutputStream(String outFile, int frameRate) {
		AVIWriter out = null;
		try {
			out = new AVIWriter(new File(videoDir + outFile + ".avi"));
			this.format = new Format(MediaTypeKey, MediaType.VIDEO,
					WidthKey, 640,
					HeightKey, 480, EncodingKey, ENCODING_AVI_MJPG, DepthKey, 24, QualityKey, 1f,
					FrameRateKey, new Rational(frameRate, 1));
			out.addTrack(this.format);
		} catch (IOException e) {
			System.out.println("Failed to create video output stream. Aborting.");
			e.printStackTrace();
			System.exit(1);
		}
		return out;
	}
	
	private void checkDirectory() {
		new File(videoDir).mkdirs();
	}
	
	public void run() {
		try {
			while(!done || this.frames.size() > 0) {
				BufferedImage frame = this.frames.poll();
				if(frame != null) {
					writeFrame(frame);
				}
			}
		} finally {
			close();
		}
	}
	
	public void addFrame(BufferedImage image) {
		this.frames.add(image);
	}
	
	public void stop() {
		done = true;
	}
	
	public void close() {
		try {
			this.writer.close();
		} catch(IOException e) {
			System.out.println("Failure...couldn't close the video writer.");
		}
	}
	
	private boolean writeFrame(BufferedImage image) {
		try {
			this.writer.write(0, image, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.index++;
		return true;
	}
	
	public boolean isDataLimitReached() {
		return this.writer.isDataLimitReached();
	}
}
