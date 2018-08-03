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
package com.bitplan.executil;

/**
 * http://blog.art-of-coding.eu/piping-between-processes/
 * 
 * @author wf
 * 
 */
public class Piper implements java.lang.Runnable {

	/**
	 * the two parts of the pipe,
	 */
	private java.io.InputStream input;
	private java.io.OutputStream output;
	private int bufferlen;

	/**
	 * create a Piper
	 * 
	 * @param input
	 * @param output
	 * @param bufferlen
	 */
	public Piper(java.io.InputStream input, java.io.OutputStream output,
			int bufferlen) {
		this.input = input;
		this.output = output;
		this.bufferlen=bufferlen;
	}

	/**
	 * run 
	 */
	public void run() {
		try {
			// Create bufferlen bytes buffer
			byte[] b = new byte[bufferlen];
			int read = 1;
			// As long as data is read; -1 means EOF
			while (read > -1) {
				// Read bytes into buffer
				read = input.read(b, 0, b.length);
				// System.out.println("read: " + new String(b));
				if (read > -1) {
					// Write bytes to output
					output.write(b, 0, read);
				}
			}
		} catch (Exception e) {
			// Something happened while reading or writing streams; pipe is broken
			throw new RuntimeException("Broken pipe", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
			}
			try {
				output.close();
			} catch (Exception e) {
			}
		}
	}

}
