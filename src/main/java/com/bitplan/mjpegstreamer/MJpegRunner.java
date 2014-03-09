package com.bitplan.mjpegstreamer;

import java.io.IOException;

/**
 * runner interface
 * @author muf
 *
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
