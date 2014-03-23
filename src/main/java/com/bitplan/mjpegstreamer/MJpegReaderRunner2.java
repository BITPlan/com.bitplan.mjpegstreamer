package com.bitplan.mjpegstreamer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


/**
 * Alternative MJpegRunner implementation
 *  http://code.google.com/p/ipcapture/source/browse/IPCapture.java?r=0d
 *         f4452208266f77fdc09b427682eaee09054fcb for an alternative
 *         implementation
 * Copyright (c) 2014 Wolfgang Fahl
 * 
 */
public class MJpegReaderRunner2 extends MJpegRunnerBase {

	private ByteArrayOutputStream jpgOut;

	public final static String VERSION = "0.1.0";
	
	/**
	 * no args default constructor
	 */
	public MJpegReaderRunner2() {
		
	}
	
	/**
	 * create and init me
	 * @param viewer
	 * @param inputStream
	 * @throws IOException 
	 */
	public MJpegReaderRunner2(MJpegRenderer viewer, InputStream inputStream) throws IOException {
		init(viewer,inputStream);
	}

	/**
	 * create a MJpegRunner
	 * 
	 * @param parent
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException 
	 */
	@Override
	public void init(MJpegRenderer viewer, String urlString, String user,
			String pass) throws IOException {
		this.urlString = urlString;
		this.user = user;
		this.pass = pass;
		init(viewer,null);
	}

	@Override
	public void init(MJpegRenderer viewer, InputStream inputStream)
			throws IOException {
		this.curFrame = new byte[0];
		this.viewer = viewer;
		this.frameAvailable = false;		
		if (inputStream!=null)
			this.inputStream=new BufferedInputStream(inputStream);
	}
	
	/**
	 * stop reading
	 */
	public void stop(String msg) {
		try {
			if (jpgOut!=null)
				jpgOut.close();
			if (inputStream!=null)
				inputStream.close();
		} catch (IOException e) {
			handle("Error closing streams: ", e);
		}
		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpcon=(HttpURLConnection) conn;
			httpcon.disconnect();
		}
		viewer.stop(msg);
	}

	/**
	 * run me
	 */
	public void run() {
		connect();
		
		int prev = 0;
		int cur = 0;

		try {
			while (inputStream != null && (cur = inputStream.read()) >= 0) {
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
					read();
				}
				prev = cur;
			}
			stop("end of inputstream");
		} catch (IOException e) {
			handle("I/O Error: ",e);
		}
	}

	
}
