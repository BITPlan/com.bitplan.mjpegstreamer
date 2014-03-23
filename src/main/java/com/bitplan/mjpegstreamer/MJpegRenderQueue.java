package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author wf inspired by
 *         http://stackoverflow.com/questions/12669368/java-how-
 *         to-extend-inputstream-to-read-from-a-jtextfield
 */
public class MJpegRenderQueue implements MJpegRenderer {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.mjpegstreamer");
	
	int maxSize;
	private long timeStamp = 0;
	protected boolean started = false;
	private boolean stopped = false;
	private Queue<BufferedImage> imageBuffer = new ConcurrentLinkedQueue<BufferedImage>();

	/**
	 * set the maximum size of the Queue
	 * 
	 * @param maxSize
	 */
	public MJpegRenderQueue(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * @param started the started to set
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}
	
	/**
	 * @return the imageBuffer
	 */
	public Queue<BufferedImage> getImageBuffer() {
		return imageBuffer;
	}

	/**
	 * @param imageBuffer the imageBuffer to set
	 */
	public void setImageBuffer(Queue<BufferedImage> imageBuffer) {
		this.imageBuffer = imageBuffer;
	}
	@Override
	public void renderNextImage(BufferedImage jpegImg) {
		if (getImageBuffer().size() < maxSize)
			getImageBuffer().add(jpegImg);
		else {
			long newTimeStamp = System.currentTimeMillis();
			if (newTimeStamp - timeStamp > 2000) {
				LOGGER.log(Level.WARNING, "Buffer overrun for MJpegRenderQueue");
				timeStamp = newTimeStamp;
			}
		}
	}

	@Override
	public void showMessage(String msg) {
		LOGGER.log(Level.WARNING, msg);
	}

	@Override
	public void init() {
		setStarted(true);
	}

	@Override
	public void stop(String msg) {
		LOGGER.log(Level.INFO,msg);
		setStopped(true);
	}

	/**
	 * @return the stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * @param stopped the stopped to set
	 */
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

}