/**
 * Copyright (c) 2013-2020 BITPlan GmbH
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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.SocketTimeoutException;

/**
 * Given an extended JPanel and URL read and create BufferedImages to be
 * displayed from a MJPEG stream
 * 
 * @author shrub34 Copyright (c) 2012 Free for reuse, just please give me a
 *         credit if it is for a redistributed package see
 *         http://thistleshrub.net/Joomla/index.php?option=com_content&view=article&id=115:displaying-streamed-mjpeg-in-java&catid=43:robotics&Itemid=64
 *         for the original article
 * 
 *         Copyright (c) 2014 Wolfgang Fahl 
 */
public class MJpegReaderRunner1 extends MJpegRunnerBase {
	private static final String CONTENT_LENGTH = "Content-length: ";
	private static final String CONTENT_TYPE = "Content-type: image/jpeg";
	private StringWriter stringWriter;

	@Override
	public void init(InputStream inputStream)
			throws IOException {
		if (inputStream!=null)
			this.inputStream=new BufferedInputStream(inputStream);
		stringWriter = new StringWriter(128);
	}
	
	/**
	 * Keeps running while process() returns true
	 * 
	 * Each loop asks for the next JPEG image and then sends it to our JPanel to
	 * draw
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		connect();
		while (connected) {
			try {
				this.curFrame= retrieveNextImage();
				read();
			} catch (SocketTimeoutException ste) {
				handle("Lost Camera connection: " ,ste);
				stop("lost camera connection");
			} catch (IOException e) {
				handle("failed stream read: ", e);
				stop("failed stream read");
			}
		}

		// close streams
		try {
			inputStream.close();
		} catch (IOException ioe) {
			handle("Failed to close the stream: ", ioe);
		}
	}

	/**
	 * Using the <i>urlStream</i> get the next JPEG image as a byte[]
	 * 
	 * @return byte[] of the JPEG
	 * @throws IOException
	 */
	private byte[] retrieveNextImage() throws IOException {
		boolean haveHeader = false;
		int currByte = -1;

		String header = null;
		// build headers
		// the DCS-930L stops it's headers
		while ((currByte = inputStream.read()) > -1 && !haveHeader) {
			stringWriter.write(currByte);

			String tempString = stringWriter.toString();
			int indexOf = tempString.indexOf(CONTENT_TYPE);
			if (indexOf > 0) {
				haveHeader = true;
				header = tempString;
			}
		}

		// 255 indicates the start of the jpeg image
		while ((inputStream.read()) != 255) {
			// just skip extras
		}

		// rest is the buffer
		int contentLength = contentLength(header);
		byte[] imageBytes = new byte[contentLength + 1];
		// since we ate the original 255 , shove it back in
		imageBytes[0] = (byte) 255;
		int offset = 1;
		int numRead = 0;
		while (offset < imageBytes.length
				&& (numRead = inputStream.read(imageBytes, offset,
						imageBytes.length - offset)) >= 0) {
			offset += numRead;
		}

		stringWriter = new StringWriter(128);

		return imageBytes;
	}

	// dirty but it works content-length parsing
	private static int contentLength(String header) {
		int indexOfContentLength = header.indexOf(CONTENT_LENGTH);
		int valueStartPos = indexOfContentLength + CONTENT_LENGTH.length();
		int indexOfEOL = header.indexOf('\n', indexOfContentLength);

		String lengthValStr = header.substring(valueStartPos, indexOfEOL)
				.trim();

		int retValue = Integer.parseInt(lengthValStr);

		return retValue;
	}

}