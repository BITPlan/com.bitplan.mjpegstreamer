package com.bitplan.mjpegstreamer;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

/**
 * Test command Line of mjpegstreamer
 * @author muf
 *
 */
public class TestCommandLine {
	
	/**
	 * test the MJPegStreamer
	 * 
	 * @param args - command line arguments
	 * @param expectedExit - the expected exit code
	 * @param sleepTime
	 */
	public void testMJpegStreamer(String args[], int expectedExit, int sleepTime) {
		MJpegViewer.testMode = true;
		MJpegViewer.main(args);
		assertEquals(expectedExit, MJpegViewer.exitCode);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	@Test
	public void testHelp() {
		String[] args = { "-h"};
		testMJpegStreamer(args,1,0);
	}
	
	@Test
	public void testCameraUrl() throws InterruptedException {
		String[] args = { "-u", "http://cam2/mjpeg.cgi","--start"};
		testMJpegStreamer(args,0,1250);
	}
	
	@Test
	public void testFileUrl() throws InterruptedException {
		URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
		String[] args = { "-u", movieUrl.toExternalForm(),"--start"};
		testMJpegStreamer(args,0,1000);
	}

}
