package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;

/**
 * generic MJpegRendering interface
 * @author wf
 *
 */
public interface MJpegRenderer {

	
	/**
	 * initialize the Renderer with DebugMode None
	 */
	public void init();
	
	/**
	 * render the next image
	 * @param jpegImg
	 */
	public void renderNextImage(BufferedImage jpegImg);
	
	/**
	 * stop rendering
	 */
	public void stop();
	

	/**
	 * show a Message e.g. if something failed
	 * @param msg
	 */
	void showMessage(String msg);

}
