package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;

/**
 * an Imagelistener
 * @author wf
 *
 */
public interface ImageListener {
	public void onRead(Object context,BufferedImage image);

}
