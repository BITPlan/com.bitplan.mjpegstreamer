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
package com.bitplan.executil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * This class is intended to be used with the SystemCommandExecutor class to let
 * users execute system commands from Java applications.
 * 
 * This class is based on work that was shared in a JavaWorld article named
 * "When System.exec() won't". That article is available at this url:
 * 
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 * 
 * Documentation for this class is available at this URL:
 * 
 * http://devdaily.com/java/java-processbuilder-process-system-exec
 * 
 * 
 * Copyright 2010 alvin j. alexander, devdaily.com.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Please see the following page for the LGPL license:
 * http://www.gnu.org/licenses/lgpl.txt
 * 
 */
class ThreadedStreamHandler extends Thread {
	InputStream inputStream;
	String adminPassword;
	OutputStream outputStream;

	PrintWriter printWriter;
	StringBuilder outputBuffer = new StringBuilder();
	private boolean sudoIsRequested = false;

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * @return the outputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * A simple constructor for when the sudo command is not necessary. This
	 * constructor will just run the command you provide, without running sudo
	 * before the command, and without expecting a password.
	 * 
	 * @param inputStream
	 * @param streamType
	 */
	ThreadedStreamHandler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Use this constructor when you want to invoke the 'sudo' command. The
	 * outputStream must not be null. If it is, you'll regret it. :)
	 * 
	 * TODO this currently hangs if the admin password given for the sudo command
	 * is wrong.
	 * 
	 * @param inputStream
	 * @param streamType
	 * @param outputStream
	 * @param adminPassword
	 */
	ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream,
			String adminPassword) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.printWriter = new PrintWriter(outputStream);
		this.adminPassword = adminPassword;
		if (adminPassword!=null)
			this.sudoIsRequested = true;
	}

	public void run() {
		// on mac os x 10.5.x, when i run a 'sudo' command, i need to write
		// the admin password out immediately; that's why this code is
		// here.
		if (sudoIsRequested) {
			// doSleep(500);
			printWriter.println(adminPassword);
			printWriter.flush();
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				outputBuffer.append(line + "\n");
			}
		} catch (IOException ioe) {
			// TODO handle this better
			ioe.printStackTrace();
		} catch (Throwable t) {
			// TODO handle this better
			t.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// ignore this one
			}
		}
	}

	/**
	 * sleep a bit
	 * 
	 * @param millis
	 */
	public void doSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * get the outputBuffer
	 * @return
	 */
	public String getOutputBuffer() {
		String out = outputBuffer.toString();
		// clear StringBuilder once it's been read
		this.outputBuffer = new StringBuilder();
		return out;
	}

}