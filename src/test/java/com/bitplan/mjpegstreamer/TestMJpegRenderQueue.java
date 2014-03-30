/**
 * Copyright (C) 2014 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.mjpegstreamer;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.mjpeg.preview.MJpegPreview;
import com.bitplan.mjpeg.preview.MJpegSwingPreview;
import com.bitplan.mjpeg.preview.Preview;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

/**
 * test the MJpegRenderQueue
 * 
 * @author wf
 * 
 */
public class TestMJpegRenderQueue {
	protected static Logger LOGGER = Logger
			.getLogger("com.bitplan.mjpegstreamer");
	public static int count;
	Preview preview;

	/**
	 * check the preview
	 * 
	 * @param url
	 * @param frames
	 * @throws Exception
	 */
	private void checkPreview(String url, int frames) throws Exception {
		checkPreview(url, null, null, frames);
	}

	/**
	 * check the preview
	 * 
	 * @param url
	 * @param user
	 * @param pass
	 * @param frames
	 * @throws Exception
	 */
	public void checkPreview(String url, String user, String pass, int frames)
			throws Exception {
		LOGGER.log(Level.INFO, "reading from url " + url);
		preview.getRunner().init(url, user, pass);
		ViewerSetting settings = preview.getViewer().getViewerSetting();
		settings.setPictureCount(frames);
		// settings.setDebugMode(DebugMode.Verbose);
		settings.setDebugMode(DebugMode.FPS);
		ImageListener listener = new ImageListener() {

			@Override
			public void onRead(Object context, BufferedImage image) {
				count++;
			}

		};
		preview.getRunner().addImageListener(listener);
		count = 0;
		preview.start();
		preview.getRunner().join();
		boolean debug = true;
		/*
		 * long timeout = System.currentTimeMillis() + 2000; int count=0; while
		 * (System.currentTimeMillis() < timeout) { if (renderQueue.isStarted()) {
		 * BufferedImage image; while ((image = renderQueue.getImageBuffer().poll())
		 * != null) { count++; assertNotNull(image); } if (renderQueue.isStopped())
		 * break; try { Thread.sleep(1); } catch (InterruptedException e) { } } }
		 */
		if (debug)
			LOGGER.log(Level.INFO, "found " + count + " frames");
		assertEquals(settings.pictureCount, count);
	}

	/**
	 * authorized access
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAuthorization() throws Exception {
		String user = "test";
		String pass = "test-2014-03-30";
		// String url="http://cam1/videostream.cgi";
		String url = "http://cam2/mjpeg.cgi";
		preview = new MJpegPreview();
		checkPreview(url, user, pass, 10);
	}

	/**
	 * test the Preview
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPreview() throws Exception {
		String urls[] = {
				ClassLoader.getSystemResource("testmovie/movie.mjpg").toExternalForm(),
				"http://cam2/mjpeg.cgi", "http://2.0.0.75:8080/video" };
		int frames[] = { 51, 11, 11 };
		int index = 0;
		for (String url : urls) {
			Preview[] previews = {  new MJpegSwingPreview(),new MJpegPreview() };
			for (Preview lpreview : previews) {
				preview = lpreview;
				checkPreview(url, frames[index]);
				// Wait a bit before next test
				Thread.sleep(150);
			}
			index++;
		}
	} // testPreview
}
