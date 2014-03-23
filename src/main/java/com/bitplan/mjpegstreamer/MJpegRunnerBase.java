package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


// JDK 8
// import java.util.Base64;
import org.apache.commons.codec.binary.Base64;

/**
 * base class for MJPegRunners
 * 
 * @author muf
 * 
 */
public abstract class MJpegRunnerBase implements MJpegReaderRunner {
	protected static Logger LOGGER = Logger
			.getLogger("com.bitplan.mjpegstreamer");

	protected MJpegRenderer viewer;
	protected String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream inputStream;

	protected URL url;
	protected byte[] curFrame;
	// count each frame
	protected int frameCount;
	// count frames in last second for frame per second calculation
	private int fpscount;
	// nano time of last frame
	private long fpstime;
	// nano time of last second
	private long fpssecond;
	private Thread streamReader;
	protected URLConnection conn;
	// constants
	public DebugMode debugMode = DebugMode.None;
	private int rotation = 0;
	public List<ImageListener> imageListeners = new ArrayList<ImageListener>();

	private int fps;


	/**
	 * @return the rotation
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * @param rotation
	 *          the rotation to set
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	@Override
	public void setDebugMode(DebugMode debugMode) {
		this.debugMode = debugMode;
	}

	public static int READ_TIME_OUT = 4000;
	public static int INPUT_BUFFER_SIZE = 8192;

	/**
	 * get a Base64 Encoder
	 * 
	 * @return
	 */
	public Base64 getEncoder() {
		// JDK 8
		// Base64.Encoder base64 = Base64.getEncoder();
		// Apache Commons codec
		Base64 base64 = new Base64();
		return base64;
	}

	/**
	 * open the connection
	 * 
	 * @return
	 */
	public BufferedInputStream openConnection() {
		BufferedInputStream result = null;
		try {
			url = new URL(urlString);
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64 base64 = getEncoder();
				byte[] encoded_credentials = base64.encode(credentials.getBytes());
				conn.setRequestProperty("Authorization", "Basic " + encoded_credentials);
			}
			// change the timeout to taste, I like 1 second
			conn.setReadTimeout(READ_TIME_OUT);
			conn.connect();
			result = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		} catch (MalformedURLException e) {
			handle("Invalid URL", e);
		} catch (IOException ioe) {
			handle("Unable to connect: ", ioe);
		}
		return result;
	}

	/**
	 * connect
	 */
	public void connect() {
		// if inputStream has been set - keep it!
		if (inputStream != null)
			return;
		if ("-".equals(urlString))
			inputStream = new BufferedInputStream(System.in, INPUT_BUFFER_SIZE);
		else
			inputStream = openConnection();
	}

	/**
	 * start reading
	 */
	public void start() {
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
		viewer.init();
		fpscount=0;
		this.fpstime=System.nanoTime();
		this.fpssecond=fpstime;
	}

	/**
	 * is there a new frame?
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		return frameAvailable;
	}

	/**
	 * handle the given exception with the given title
	 * 
	 * @param title
	 * @param e
	 */
	public void handle(String title, Exception e) {
		String msg = title + e.getMessage();
		switch (debugMode) {
		case FPS:
		case Verbose:
			LOGGER.log(Level.WARNING, msg);
			break;
		default:
		}
		viewer.showMessage(msg);
	}

	@Override
	public BufferedImage getRotatedImage(BufferedImage inputImage, int rotation) {
		BufferedImage result = MJpegHelper.getRotatedImage(inputImage, rotation);
		return result;
	}

	/**
	 * read
	 */
	public void read() {
		try {
			BufferedImage bufImg = MJpegHelper.getImage(curFrame);
			for (ImageListener listener : this.imageListeners) {
				listener.onRead(this, bufImg);
			}
			frameCount++;
			frameAvailable = false;
			// debug repaint
			// image= viewer.getBufferedImage("/images/start.png");
			BufferedImage rotatedImage = this.getRotatedImage(bufImg, rotation);
			viewer.renderNextImage(rotatedImage);
      // viewer.repaint();
			// Frame per second calculation
			long now = System.nanoTime(); 
			// how many nanosecs since last frame?
			long elapsedFrameTime = now - fpstime;
			// how many nanosecs since last second timestamp
			long elapsedSecondTime = now -fpssecond;
			long framemillisecs = TimeUnit.MILLISECONDS.convert(elapsedFrameTime, TimeUnit.NANOSECONDS);
			long secmillisecs = TimeUnit.MILLISECONDS.convert(elapsedSecondTime, TimeUnit.NANOSECONDS);
			// is a second over?
			if (secmillisecs>1000) {
				fps=frameCount-fpscount;
			  fpssecond=now;
				fpscount=this.frameCount;
			}
			fpstime=now;

			switch (debugMode) {
			case Verbose:
				LOGGER.log(Level.INFO, "frame=" + frameCount+" after "+framemillisecs+" msecs");
				break;
			case FPS:
				if (fpssecond==now)
					LOGGER.log(Level.INFO, "frame=" + frameCount+" ("+framemillisecs+" msecs) "+fps+" Frames per second");
				break;
			case None:
				break;
			}
		} catch (IOException e) {
			handle("Error acquiring the frame: ", e);
		}
	}

	/**
	 * when disposing stop
	 */
	public void dispose() {
		stop();
	}

	/**
	 * add an imageListener
	 * 
	 * @param listener
	 */
	public void addImageListener(ImageListener listener) {
		this.imageListeners.add(listener);
	}
}
