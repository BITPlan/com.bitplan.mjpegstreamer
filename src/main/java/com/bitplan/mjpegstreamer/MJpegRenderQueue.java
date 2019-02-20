/**
 * Copyright (c) 2013-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.mjpegstreamer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author wf inspired by http://stackoverflow.com/questions/12669368/java-how-
 *         to-extend-inputstream-to-read-from-a-jtextfield
 */
public class MJpegRenderQueue implements MJpegRenderer {
	protected static Logger LOGGER = Logger
			.getLogger("com.bitplan.mjpegstreamer");

	int maxSize;
	// private long timeStamp = 0;
	protected boolean started = false;
	private boolean stopped = false;
	private Queue<BufferedImage> imageBuffer = new ConcurrentLinkedQueue<BufferedImage>();
	private long timeStamp;

	private ViewerSetting viewerSetting = new ViewerSetting();

	private boolean active=true;

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

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
	 * @param started
	 *          the started to set
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
	 * @param imageBuffer
	 *          the imageBuffer to set
	 */
	public void setImageBuffer(Queue<BufferedImage> imageBuffer) {
		this.imageBuffer = imageBuffer;
	}

	@Override
	public void renderNextImage(BufferedImage jpegImg) {
		if (!active)
			return;
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
		// idempotent - will not stop twice (would end in an endless loop)
		if (!stopped) {
			switch (viewerSetting.getDebugMode()) {
			case Verbose:
				MJpegRunnerBase.debugTrace(msg, this);
			case FPS:
				msg = "stop of renderqueue: " + msg + " queue size is "
						+ this.imageBuffer.size();
				LOGGER.log(Level.INFO, msg);
				break;
			default:
				break;
			}
			setStarted(false);
			setStopped(true);
		}
	}

	/**
	 * @return the stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * @param stopped
	 *          the stopped to set
	 */
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	@Override
	public ViewerSetting getViewerSetting() {
		return viewerSetting;
	}

	@Override
	public void setViewerSetting(ViewerSetting viewerSetting) {
		this.viewerSetting = viewerSetting;
	}

  @Override
  public void showProgress(int framesReadCount,long bytesRead, int totalSize) {
    
    
  }

}