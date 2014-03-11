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
	private boolean processing = true;

	/**
	 * initialize
	 * 
	 * @param viewer
	 * @param url
	 * @param user
	 * @param pass
	 * @throws IOException
	 */
	public void init(MJpegRenderer viewer, String urlString, String user,
			String pass) throws IOException {
		this.urlString=urlString;
		this.user=user;
		this.pass=pass;
		init(viewer,null);
	}

	@Override
	public void init(MJpegRenderer viewer, InputStream inputStream)
			throws IOException {
		if (inputStream!=null)
			this.inputStream=new BufferedInputStream(inputStream);
		this.viewer = viewer;
		stringWriter = new StringWriter(128);
	}
	
	/**
	 * Stop the loop, and allow it to clean up
	 */
	public synchronized void stop() {
		processing = false;
		viewer.stop();
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
		while (processing) {
			try {
				this.curFrame= retrieveNextImage();
				read();
			} catch (SocketTimeoutException ste) {
				handle("Lost Camera connection: " ,ste);
				stop();
			} catch (IOException e) {
				handle("failed stream read: ", e);
				stop();
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