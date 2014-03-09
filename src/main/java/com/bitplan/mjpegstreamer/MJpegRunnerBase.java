package com.bitplan.mjpegstreamer;

/**
 * base class for MJPegRunners
 * @author muf
 *
 */
public abstract class MJpegRunnerBase implements MJpegRunner {
	protected ViewPanel viewer;
	protected int frameCount;
	private Thread streamReader;

	/**
	 * start reading
	 */
	public void start() {
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
	}
	
	/**
	 * handle the given exception with the given title
	 * 
	 * @param title
	 * @param e
	 */
	public void handle(String title, Exception e) {
		String msg = title + e.getMessage();
		System.err.println(msg);
		viewer.setFailedString(msg);
	}

	/**
	 * when disposing stop
	 */
	public void dispose() {
		stop();
	}
}
