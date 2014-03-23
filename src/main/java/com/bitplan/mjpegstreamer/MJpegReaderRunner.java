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
	
	public enum DebugMode{None,FPS,Verbose};
	/*
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
	 * set my debug mode
	 * @param debugMode the debug to set
	 */
	public void setDebugMode(DebugMode debugMode);
		
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
	 * @return the rotation
	 */
	public int getRotation();

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(int rotation);

	/**
	 * get a rotated image
	 * @param image - the image to rotate
	 * @param rotation e.g. 0/90/180/270
	 * @return
	 */
	public BufferedImage getRotatedImage(BufferedImage image,
			int rotation);
	
	/**
	 * read image from current frame buffer
	 */
	public void read();

	/**
	 * add an imageListener
	 * @param listener
	 */
	public void addImageListener(ImageListener listener);

}
