package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;

/**
 * generic MJpegRendering interface
 * @author wf
 *
 */
public interface MJpegRenderer {
	
	/**
	 * initialize the Renderer
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
	 * set the string if something failed
	 * @param msg
	 */
	void setFailedString(String msg);

}
