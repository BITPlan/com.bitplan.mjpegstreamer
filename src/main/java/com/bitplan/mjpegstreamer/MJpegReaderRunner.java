package com.bitplan.mjpegstreamer;

import java.io.IOException;
import java.io.InputStream;

/**
 * runner interface
 * @author muf
 * http://www.gdcl.co.uk/2013/05/02/Motion-JPEG.html
 */
public interface MJpegReaderRunner extends Runnable {
	/**
	 * initialize
	 * @param viewer
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException 
	 */
	public void init(MJpegRenderer viewer, String urlString, String user,
			String pass) throws IOException;

	/**
	 * initialize
	 * @param viewer
	 * @param inputStream
	 * @throws IOException 
	 */
	public void init(MJpegRenderer viewer, InputStream inputStream) throws IOException;
	
	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug);
		
	/**
	 * connect
	 * set the inputStream (only if it has not been set via init)
	 */
	public void connect();
	
	/**
	 * start
	 */
	public void start();
	/**
	 * stop
	 */
	public void stop();
	
	/**
	 * is there a new frame?
	 * 
	 * @return
	 */
	public boolean isAvailable();
	
	
	/**
	 * read
	 */
	public void read();
}
