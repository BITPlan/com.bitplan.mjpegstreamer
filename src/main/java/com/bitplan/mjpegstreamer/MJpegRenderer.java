package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;

public interface MJpegRenderer {

	/**
	 * render the next image
	 * @param jpegImg
	 */
	public void renderNextImage(BufferedImage jpegImg);

	/**
	 * set the string if something failed
	 * @param msg
	 */
	void setFailedString(String msg);

}
