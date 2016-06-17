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
import org.apache.commons.lang.exception.ExceptionUtils;

import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

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
	protected int framesReadCount;
	
	protected int framesRenderedCount;
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
	public List<ImageListener> imageListeners = new ArrayList<ImageListener>();

	// current frame per second
	private int fpsIn;

	// how many milliseconds to wait for next frame to limit fps
	private int fpsLimitMillis=0;
	private int fpsOut;
	protected boolean connected = false;

	private long now;

	/**
	 * @return the viewer
	 */
	public MJpegRenderer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(MJpegRenderer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return the urlString
	 */
	public String getUrlString() {
		return urlString;
	}
	
	@Override
	public int getFramesRead() {
		return this.framesReadCount;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	// input buffers size (14 msecs at 568 x 768)
	public static int INPUT_BUFFER_SIZE = 8192*2;
	
	/**
	 * show a debug Trace
	 * @param msg
	 * @param source -the source Object
	 */
	public static void debugTrace(String msg, Object source) {
		try {
			throw new Exception("force stack trace");
		} catch (Exception e) {
			System.err.println("debug trace for "+source+":"+msg);
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
	 * @return the base 64 encoder
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
	 * @return the connection stream
	 */
	public BufferedInputStream openConnection() {
		BufferedInputStream result = null;
		try {
			ViewerSetting s = viewer.getViewerSetting();
			url = new URL(urlString);
			if (s.debugMode!=DebugMode.None)
				LOGGER.log(Level.INFO,"url: "+urlString+" readTimeOut: "+s.readTimeOut+" msecs");
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64 base64 = getEncoder();
				byte[] encoded_credentials = base64.encode(credentials.getBytes());
				String authStringEnc = new String(encoded_credentials);
				// System.out.println("Base64 encoded auth string: " + authStringEnc);
				conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			}
			// change the timeout to taste, I like 1 second
			conn.setReadTimeout(s.readTimeOut);
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
		connected=true;
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
	public synchronized void start() {
		framesReadCount=0;
		framesRenderedCount=0;
		fpscountIn=0;
		fpscountOut=0;
		if ((viewer.getViewerSetting().imageListener)!=null)
			this.addImageListener(viewer.getViewerSetting().imageListener);
		viewer.init();
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
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
	 * @return if a new frame is available
	 */
	public boolean isAvailable() {
		return frameAvailable;
	}

	/**
	 * handle the given exception with the given title
	 * 
	 * @param title
	 * @param th
	 */
	public void handle(String title, Throwable th) {
		String msg = title + th.getMessage();
		DebugMode debugMode = viewer.getViewerSetting().debugMode;
		switch (debugMode) {
		case FPS:
		case Verbose:
			LOGGER.log(Level.WARNING, msg);
			LOGGER.log(Level.WARNING,ExceptionUtils.getStackTrace(th));
			if (debugMode==DebugMode.Verbose)
				debugTrace(msg,this);
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
	 * @return the total elapsed time in milliseconds
	 */
	public long elapsedTimeMillisecs() {
		long elapsed = this.now-this.firstFrameNanoTime;
		long result = TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
		return result;
	}
	
	/**
	 * get a debug message with current time
	 * @return a debug message containing the number of frames read with the elapsed time
	 */
	public String getTimeMsg(String msg) {
		String streamName="?";
		if (inputStream!=null)
			streamName=inputStream.getClass().getSimpleName();
		String timeMsg=streamName+" at frame "+framesReadCount+"->"+framesRenderedCount+"/"+viewer.getViewerSetting().pictureCount+msg+" total="+this.elapsedTimeMillisecs()+" msecs "+this;
		return timeMsg;
	}
	
	/**
	 * get a time debugging message
	 * @return a debug message with the given time and no title
	 */
	public String getTimeMsg() {
		return getTimeMsg("");
	}

	
	/**
	 * read
	 */
	public boolean read() {
		try {
		  BufferedImage bufImg = MJpegHelper.getImage(curFrame);
  		if (bufImg==null) {
				throw new IOException("image is null");
			}
			if (framesReadCount==0) {
				this.firstFrameNanoTime=System.nanoTime();
				this.fpsFrameNanoTime=firstFrameNanoTime;
				this.fpssecond=fpsFrameNanoTime;
			}
			framesReadCount++;
			fpscountIn++;
			frameAvailable = false;
			
			// uncomment next line for debug image
			// image= viewer.getBufferedImage("/images/start.png");
      // viewer.repaint();
			// Frame per second calculation
			now = System.nanoTime(); 
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
					if (!listener.isPostListener())
						listener.onRead(this, bufImg);
				}
				BufferedImage rotatedImage = this.getRotatedImage(bufImg, viewer.getViewerSetting().rotation);
				viewer.renderNextImage(rotatedImage);
				for (ImageListener listener : this.imageListeners) {
					if (listener.isPostListener())
						listener.onRead(this, rotatedImage);
				}
				// how many frames we acutally displayed
				framesRenderedCount++;
				fpsFrameNanoTime=now;
				fpscountOut++;
			}

			switch (viewer.getViewerSetting().debugMode) {
			case Verbose:
				LOGGER.log(Level.INFO, this.getTimeMsg(" after "+framemillisecs+" msecs"));
				break;
			case FPS:
				if (fpssecond==now)
					LOGGER.log(Level.INFO, this.getTimeMsg(" after "+framemillisecs+" msecs "+fpsIn+"/"+fpsOut+" Frames per second in/out "));
				break;
			case None:
				break;
			}
		} catch (Throwable th) {
			handle("Error acquiring the frame: ", th);
		}
		return framesRenderedCount<viewer.getViewerSetting().pictureCount;
	}

	/**
	 * when disposing stop
	 */
	public void dispose() {
		stop("disposing "+this);
	}
	
	/**
	 * Stop the loop, and allow it to clean up
	 */
	public synchronized void stop(String msg) {
		connected = false;
		if (viewer!=null)
			viewer.stop(msg);
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
