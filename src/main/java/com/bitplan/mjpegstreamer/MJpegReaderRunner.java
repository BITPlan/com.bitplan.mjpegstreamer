package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
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
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException 
	 */
	public void init(String urlString, String user,
			String pass) throws IOException;

	/**
	 * get the urlString that was set by init (if any)
	 * @return the url
	 */
	public String getUrlString();
	
	/**
	 * initialize
	 * @param viewer
	 * @param inputStream
	 * @throws IOException 
	 */
	public void init(InputStream inputStream) throws IOException;
	
	/**
	 * set the viewer
	 * @param viewer
	 */
	public void setViewer(MJpegRenderer viewer);
	
	/**
	 * get teh viewer
	 * @return the viewer
	 */
	public MJpegRenderer getViewer();

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
	 * stop me with the given message
	 * @param msg
	 */
	public void stop(String msg);
	
	/**
	 * is there a new frame?
	 * 
	 * @return whether a frame is available
	 */
	public boolean isAvailable();
	
	
	/**
	 * are we connected?
	 * @return whether we are connected
	 */
	public boolean isConnected();

	/**
	 * get a rotated image
	 * @param image - the image to rotate
	 * @param rotation e.g. 0/90/180/270
	 * @return the rotated image
	 */
	public BufferedImage getRotatedImage(BufferedImage image,
			int rotation);
	
	/**
	 * read image from current frame buffer
	 * @return true if reading may continue
	 */
	public boolean read();

	/**
	 * add an imageListener
	 * @param listener
	 */
	public void addImageListener(ImageListener listener);
	
	/**
	 * retrieve the number of milliseconds the reader has run so far
	 * @return the elapsed time in milliseconds
	 */
	public long elapsedTimeMillisecs();
	
	/**
	 * how many frames where read?
	 * @return the number of frames read so far
	 */
	public int getFramesRead();

	/**
	 * wait until my stream is read
	 * @throws InterruptedException 
	 */
	public void join() throws InterruptedException;

}
