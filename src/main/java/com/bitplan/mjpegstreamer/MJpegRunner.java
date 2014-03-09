package com.bitplan.mjpegstreamer;

import java.io.IOException;

/**
 * runner interface
 * @author muf
 * http://www.gdcl.co.uk/2013/05/02/Motion-JPEG.html
 */
public interface MJpegRunner extends Runnable {
	/**
	 * initialize
	 * @param viewer
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException 
	 */
	public void init(ViewPanel viewer, String urlString, String user,
			String pass) throws IOException;

	/**
	 * start
	 */
	public void start();
	/**
	 * stop
	 */
	public void stop();
}
