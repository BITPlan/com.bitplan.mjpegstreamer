package com.bitplan.mjpegstreamer;

import java.net.*;
import java.util.Base64;
import java.io.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

/**
 * Alternative MJpegRunner implementation
 * 
 * @author muf
 * 
 */
public class MJpegRunner2 extends MJpegRunnerBase {
	private byte[] curFrame;
	private boolean frameAvailable;
	private HttpURLConnection conn;
	private BufferedInputStream httpIn;
	private ByteArrayOutputStream jpgOut;

	public final static String VERSION = "0.1.0";

	/**
	 * create a MJpegRunner
	 * 
	 * @param parent
	 * @param urlString
	 * @param user
	 * @param pass
	 */
	public void init(ViewPanel viewer, String urlString, String user,
			String pass) {
		this.viewer = viewer;
		this.urlString = urlString;
		this.user = user;
		this.pass = pass;
		this.curFrame = new byte[0];
		this.frameAvailable = false;
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
	 * stop reading
	 */
	public void stop() {
		try {
			jpgOut.close();
			httpIn.close();
		} catch (IOException e) {
			handle("Error closing streams: ", e);
		}
		conn.disconnect();
	}

	

	/**
	 * run me
	 */
	public void run() {
		URL url;
		Base64.Encoder base64 = Base64.getEncoder();

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			System.err.println("Invalid URL");
			return;
		}

		try {
			conn = (HttpURLConnection) url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				byte[] encoded_credentials = base64.encode(credentials
						.getBytes());
				conn.setRequestProperty("Authorization", "Basic "
						+ encoded_credentials);
			}
			httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
		} catch (IOException e) {
			handle("Unable to connect: ",e);
			return;
		}

		int prev = 0;
		int cur = 0;

		try {
			while (httpIn != null && (cur = httpIn.read()) >= 0) {
				if (prev == 0xFF && cur == 0xD8) {
					jpgOut = new ByteArrayOutputStream(8192);
					jpgOut.write((byte) prev);
				}
				if (jpgOut != null) {
					jpgOut.write((byte) cur);
				}
				if (prev == 0xFF && cur == 0xD9) {
					synchronized (curFrame) {
						curFrame = jpgOut.toByteArray();
					}
					frameAvailable = true;
					jpgOut.close();
				}
				prev = cur;
			}
		} catch (IOException e) {
			System.err.println("I/O Error: " + e.getMessage());
		}
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
			viewer.setBufferedImage(bufImg);
			viewer.repaint();

		} catch (IOException e) {
			handle("Error acquiring the frame: ",e);
		}
	}
}
