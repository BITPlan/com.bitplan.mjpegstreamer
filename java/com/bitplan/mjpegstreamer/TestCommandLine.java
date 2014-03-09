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
	 * test the pdf Indexer
	 * 
	 * @param args
	 */
	public void testMJpegStreamer(String args[]) {
		// MJpegViewer.testMode = true;
		MJpegViewer.main(args);
		assertEquals(0, MJpegViewer.exitCode);
	}
	
	@Test
	public void testHelp() {
		String[] args = { "-h"};
		testMJpegStreamer(args);
	}
	
	@Test
	public void testCameraUrl() throws InterruptedException {
		String[] args = { "-u", "http://cam2/mjpeg.cgi","--start"};
		testMJpegStreamer(args);
		Thread.sleep(1500);
	}
	
	@Test
	public void testFileUrl() throws InterruptedException {
		URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
		String[] args = { "-u", movieUrl.toExternalForm(),"--start"};
		testMJpegStreamer(args);
		Thread.sleep(2000);
	}

}
