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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.mjpegstreamer.MJpegReaderRunner.DebugMode;

/**
 * test the MJpegRenderQueue
 * 
 * @author wf
 * 
 */
public class TestMJpegRenderQueue {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.mjpegstreamer");
	/**
	 * test the Render Queue
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMJPegRenderQueue() throws IOException {
		URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
		MJpegReaderRunner runner = new MJpegReaderRunner2();
		MJpegRenderQueue renderQueue = new MJpegRenderQueue(1000);
		runner.init(renderQueue, movieUrl.toExternalForm(), null, null);
		boolean debug = true;
		runner.setDebugMode(DebugMode.FPS);
		runner.setFPSLimit(15);
		runner.start();
		long timeout = System.currentTimeMillis() + 20000;
		int count=0;
		while (System.currentTimeMillis() < timeout) {
			if (renderQueue.isStarted()) {
				BufferedImage image;
				while ((image = renderQueue.getImageBuffer().poll()) != null) {
					count++;
					assertNotNull(image);
				}
				if (renderQueue.isStopped())
					break;
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}
		if (debug)
			LOGGER.log(Level.INFO,"found "+count+" frames");
		assertTrue(count>=11);
		
	}
}
