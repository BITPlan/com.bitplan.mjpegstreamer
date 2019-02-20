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
package com.bitplan.mjpegstreamer;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.mjpeg.preview.MJpegPreview;
import com.bitplan.mjpeg.preview.MJpegJavaFXPreview;
import com.bitplan.mjpeg.preview.Preview;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;
import com.bitplan.user.User;

/**
 * test the MJpegRenderQueue
 * 
 * @author wf
 * 
 */
public class TestMJpegRenderQueue {
  // find examples e.g. at https://www.ipcams.ch/WebCam.aspx?nr=1729
  public static String TEST_URL2 = "http://213.193.89.202/axis-cgi/mjpg/video.cgi";
  // "http://iris.not.iac.es/axis-cgi/mjpg/video.cgi?resolution=320x240",
  // "http://cam2/mjpeg.cgi", // external camera
  // "http://2.0.0.75:8080/video" // smartPhone camera
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

      @Override
      public boolean isPostListener() {
        return true;
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
  @Ignore
  // @Test
  public void testAuthorization() throws Exception {
    // to activate this test case you might want to create an ini file with the
    // encrypted username/password
    // and the url of the webcam you'd like to test the authorization for
    // there is a main routine in User.java to create the basic ini file. When
    // done add the url line to get a
    // result like:
    /**
     * #Credentials for mjpegstreamer #Fri Aug 26 08:43:43 CEST 2016 secret=...
     * cypher=... salt=... username=... url=http://cam1/videostream.cgi
     **/

    User user = User.getUser("mjpegstreamer");
    String url = user.getProps().getProperty("url");
    preview = new MJpegPreview();
    checkPreview(url, user.getUsername(), user.getPassword(), 10);
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
        TEST_URL2 };
    int frames[] = { 51, 11, 11 };
    int index = 0;
    Preview[] previews = { new MJpegJavaFXPreview(), new MJpegPreview()  };
    for (String url : urls) {
      for (Preview lpreview : previews) {
        preview = lpreview;
        checkPreview(url, frames[index]);
        // Wait a bit before next test
        Thread.sleep(200);
      }
      index++;
    }
  } // testPreview
}
