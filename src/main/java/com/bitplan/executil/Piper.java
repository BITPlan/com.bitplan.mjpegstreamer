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
