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
