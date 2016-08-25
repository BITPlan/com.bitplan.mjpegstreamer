/**
 * Copyright (c) 2013-2016 BITPlan GmbH
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;

import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

/**
 * Alternative MJpegRunner implementation
 * http://code.google.com/p/ipcapture/source/browse/IPCapture.java?r=0d
 * f4452208266f77fdc09b427682eaee09054fcb for an alternative implementation
 * Copyright (c) 2014 Wolfgang Fahl
 * 
 */
public class MJpegReaderRunner2 extends MJpegRunnerBase {

	private ByteArrayOutputStream jpgOut;

	public final static String VERSION = "0.1.1";

	/**
	 * no args default constructor
	 */
	public MJpegReaderRunner2() {

	}

	/**
	 * create a MJpegRunner
	 * 
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException
	 */
	@Override
	public void init(String urlString, String user, String pass)
			throws IOException {
		this.urlString = urlString;
		this.user = user;
		this.pass = pass;
		init(null);
	}

	@Override
	public void init(InputStream inputStream) throws IOException {
		this.curFrame = new byte[0];
		this.frameAvailable = false;
		if (inputStream != null)
			this.inputStream = new BufferedInputStream(inputStream);
		// if (debug)
		// debugTrace("init called");
	}

	/**
	 * stop reading
	 */
	public synchronized void stop(String msg) {
		try {
			if (jpgOut != null)
				jpgOut.close();
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			handle("Error closing streams: ", e);
		}
		DebugMode debugMode = DebugMode.None;
		if (viewer != null)
			debugMode = viewer.getViewerSetting().debugMode;
		if ((debugMode == DebugMode.Verbose) && (conn!=null))
			LOGGER
					.log(Level.INFO, "stopping connection " + conn.getClass().getName());
		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpcon = (HttpURLConnection) conn;
			if (debugMode == DebugMode.Verbose)
				LOGGER.log(Level.INFO, "disconnecting " + this.getUrlString());
			httpcon.disconnect();
		}
		if (debugMode == DebugMode.Verbose)
			debugTrace("stop with msg: " + msg, this);
		super.stop(msg);
	}

	/**
	 * run me
	 */
	public void run() {
		connect();
		if (!connected)
			throw new IllegalStateException(
					"connection lost immediately after connect");
		int prev = 0;
		int cur = 0;

		try {
			// EOF is -1
			readloop: while (connected && (inputStream != null)
					&& ((cur = inputStream.read()) >= 0)) {
				if (prev == 0xFF && cur == 0xD8) {
					jpgOut = new ByteArrayOutputStream(INPUT_BUFFER_SIZE);
					jpgOut.write((byte) prev);
				}
				if (jpgOut != null) {
					jpgOut.write((byte) cur);
					if (prev == 0xFF && cur == 0xD9) {
						synchronized (curFrame) {
							curFrame = jpgOut.toByteArray();
						}
						frameAvailable = true;
						jpgOut.close();
						// the image is now available - read it and check if we reached the
						// limit
						// e.g. maxFrameCount
						connected = read();
						// LOGGER.log(Level.INFO,this.getTimeMsg());
						if (!connected) {
							break readloop;
						}
					}
				}
				prev = cur;
			}
			// end of input stream reached
			String msg = "end of inputstream " + this.getTimeMsg();
			if (viewer!=null)
				msg+=" read time out is set at "+viewer.getViewerSetting().readTimeOut+" msecs";
			stop(msg);
		} catch (IOException e) {
			handle("I/O Error " + this.getTimeMsg() + ":", e);
		}
	}

}
