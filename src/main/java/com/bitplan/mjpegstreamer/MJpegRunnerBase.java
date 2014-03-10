package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * base class for MJPegRunners
 * 
 * @author muf
 * 
 */
public abstract class MJpegRunnerBase implements MJpegReaderRunner {
	protected MJpegRenderer viewer;
	protected String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream inputStream;

	protected URL url;
	protected byte[] curFrame;
	protected int frameCount;
	private Thread streamReader;
	protected URLConnection conn;
	// constants
	public boolean debug = true;
	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public static int READ_TIME_OUT = 1000;
	public static int INPUT_BUFFER_SIZE = 8192;

	/**
	 * open the connection
	 * @return
	 */
	public BufferedInputStream openConnection() {
		BufferedInputStream result = null;
		try {
			url = new URL(urlString);
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64.Encoder base64 = Base64.getEncoder();
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
		if ("-".equals(urlString))
			inputStream=new BufferedInputStream(System.in,INPUT_BUFFER_SIZE);
		else
			inputStream=openConnection();
	}

	/**
	 * start reading
	 */
	public void start() {
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
		viewer.init();
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
		if (debug)
			System.err.println(msg);
		viewer.setFailedString(msg);
	}

	/**
	 * read
	 */
	public void read() {
		try {
			ByteArrayInputStream jpgIn = new ByteArrayInputStream(curFrame);
			BufferedImage bufImg = ImageIO.read(jpgIn);
			jpgIn.close();
			frameCount++;
			frameAvailable = false;
			// debug repaint
			// image= viewer.getBufferedImage("/images/start.png");
			viewer.renderNextImage(bufImg);
			// viewer.repaint();
			if (debug)
				viewer.setFailedString("debug:frame=" + frameCount);

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
}
