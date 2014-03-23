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
	protected static boolean debug=false;

	protected MJpegRenderer viewer;
	protected String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream inputStream;

	protected URL url;
	protected byte[] curFrame;
	// count each frame
	protected int frameCount;
	// count frames in last second for frame per second calculation
	private int fpscountIn;
	private int fpscountOut;
	// nano time of last frame
	private long fpsFrameNanoTime;
	// nano time of the first frame
	private long firstFrameNanoTime;
	// nano time of last second
	private long fpssecond;
	private Thread streamReader;
	protected URLConnection conn;
	// constants
	public DebugMode debugMode = DebugMode.None;
	private int rotation = 0;
	private int readTimeOut;
	public List<ImageListener> imageListeners = new ArrayList<ImageListener>();

	// current frame per second
	private int fpsIn;

	// how many milliseconds to wait for next frame to limit fps
	private int fpsLimitMillis=1;
	private int fpsOut;
	protected boolean connected = false;

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @return the readTimeOut
	 */
	public int getReadTimeOut() {
		return readTimeOut;
	}

	/**
	 * @param readTimeOut the readTimeOut to set
	 */
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

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

	// read time out
	public static int INPUT_BUFFER_SIZE = 8192*2;
	
	
	/**
	 * show a debug Trace
	 * @param msg
	 */
	public void debugTrace(String msg) {
		try {
			throw new Exception("force stack trace");
		} catch (Exception e) {
			System.err.println("debug trace for "+this+":"+msg);
			e.printStackTrace();
		}		
	}
	
	/**
	 * limit the number of frames per second
	 * @param fpsLimit e.g. 10 for one frame each 100 millisecs, 0.5 for one frame each 2000 millisecs
	 */
	public void setFPSLimit(double fpsLimit) {
		 fpsLimitMillis = (int) (1000/fpsLimit);
	}

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
			conn.setReadTimeout(this.getReadTimeOut());
			conn.connect();
			result = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		} catch (MalformedURLException e) {
			handle("Invalid URL", e);
		} catch (IOException ioe) {
			handle("Unable to connect: ", ioe);
		}
		connected=true;
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
		fpscountIn=0;
		fpscountOut=0;
	}
	
	/**
	 * wait until I am finished
	 */
	public void join() throws InterruptedException {
		if (streamReader!=null) {
			streamReader.join();
		}
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
	 * get the total elapsedTime
	 * @return
	 */
	public long elapsedTimeMillisecs() {
		long elapsed = this.fpsFrameNanoTime-this.firstFrameNanoTime;
		long result = TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
		return result;
	}
	
	/**
	 * read
	 */
	public void read() {
		try {
			BufferedImage bufImg = MJpegHelper.getImage(curFrame);
			if (frameCount==0) {
				this.firstFrameNanoTime=System.nanoTime();
				this.fpsFrameNanoTime=firstFrameNanoTime;
				this.fpssecond=fpsFrameNanoTime;
			}
			frameCount++;
			fpscountIn++;
			frameAvailable = false;
			
			// uncomment next line for debug image
			// image= viewer.getBufferedImage("/images/start.png");
      // viewer.repaint();
			// Frame per second calculation
			long now = System.nanoTime(); 
			// how many nanosecs since last frame?
			long elapsedFrameTime = now - fpsFrameNanoTime;
			// how many nanosecs since last second timestamp
			long elapsedSecondTime = now -fpssecond;
			long framemillisecs = TimeUnit.MILLISECONDS.convert(elapsedFrameTime, TimeUnit.NANOSECONDS);
			long secmillisecs = TimeUnit.MILLISECONDS.convert(elapsedSecondTime, TimeUnit.NANOSECONDS);
			// is a second over?
			if (secmillisecs>1000) {
				fpsIn=fpscountIn;
				fpsOut=fpscountOut;
				fpscountOut=0;
				fpscountIn=0;
				fpssecond=now;
			}
			// do not render images that are "too quick/too early"
			if (framemillisecs>=this.fpsLimitMillis) {
				for (ImageListener listener : this.imageListeners) {
					listener.onRead(this, bufImg);
				}
				BufferedImage rotatedImage = this.getRotatedImage(bufImg, rotation);
				viewer.renderNextImage(rotatedImage);
				fpsFrameNanoTime=now;
				fpscountOut++;
			}

			switch (debugMode) {
			case Verbose:
				LOGGER.log(Level.INFO, "frame=" + frameCount+" after "+framemillisecs+" msecs - total "+this.elapsedTimeMillisecs()+" msecs");
				break;
			case FPS:
				if (fpssecond==now)
					LOGGER.log(Level.INFO, "frame=" + frameCount+" ("+framemillisecs+" msecs) "+fpsIn+"/"+fpsOut+" Frames per second in/out - total "+this.elapsedTimeMillisecs()+" msecs");
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
		stop("disposing "+this);
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
