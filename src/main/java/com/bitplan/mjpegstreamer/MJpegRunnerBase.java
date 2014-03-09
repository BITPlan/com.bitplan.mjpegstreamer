package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
	protected ViewPanel viewer;
	protected String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream urlStream;

	protected URL url;
	protected byte[] curFrame;
	protected int frameCount;
	private Thread streamReader;
	protected URLConnection conn;
	public static boolean debug = true;
	public static int READ_TIME_OUT=1000;

    /**
     * connect
     */
	public void connect() {

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			handle("Invalid URL", e);
			return;
		}

		try {
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64.Encoder base64 = Base64.getEncoder();
				byte[] encoded_credentials = base64.encode(credentials
						.getBytes());
				conn.setRequestProperty("Authorization", "Basic "
						+ encoded_credentials);
			}
			// change the timeout to taste, I like 1 second
			conn.setReadTimeout(READ_TIME_OUT);
			conn.connect();
			urlStream = new BufferedInputStream(conn.getInputStream(), 8192);
		} catch (IOException e) {
			handle("Unable to connect: ", e);
			return;
		}
	}

	/**
	 * start reading
	 */
	public void start() {
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
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
			viewer.setBufferedImage(bufImg);
			viewer.repaint();
			if (debug)
				viewer.setFailedString("" + frameCount);

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
