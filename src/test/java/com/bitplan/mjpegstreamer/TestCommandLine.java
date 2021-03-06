/**
 * Copyright (c) 2013-2020 BITPlan GmbH
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.executil.Piper;

/**
 * Test command Line of mjpegstreamer
 * 
 * @author wf
 * 
 */
public class TestCommandLine {
  
  // checked on 2020-01-23
  public static final String TEST_URL1="http://213.193.89.202/axis-cgi/mjpg/video.cgi";
  // you could use your own Camera here e.g.
  // "http://cam2/mjpeg.cgi", 
  

  private boolean debug = false;

  /**
   * test the MJPegStreamer
   * 
   * @param args
   *          - command line arguments
   * @param expectedExit
   *          - the expected exit code
   * @param sleepTime
   */
  public void testMJpegStreamer(String args[], int expectedExit, int sleepTime) {
    MJpegViewer.testMode = true;
    MJpegViewer.main(args);
    assertEquals("exitCode should be " + expectedExit, expectedExit,
        MJpegViewer.exitCode);
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      // ignore
    }
  }

  @Test
  public void testHelp() {
    String[] args = { "-h" };
    testMJpegStreamer(args, 1, 0);
  }

  @Test
  public void testCameraUrl() throws InterruptedException {
    // ad your own camera url here
    String[] args = { "-u", 
        TEST_URL1,
        "--start", "--autoclose", "--limit", "2" };
    testMJpegStreamer(args, 0, 1250);
  }

  @Test
  public void testFileUrl() throws InterruptedException {
    URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
    String[] args = { "-u", movieUrl.toExternalForm(), "--start", "--autoclose" };
    testMJpegStreamer(args, 0, 1000);
  }

  @Test
  public void testRotation() throws InterruptedException {
    URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
    String[] args = { "-u", movieUrl.toExternalForm(), "--start",
        "--autoclose", "--rotation", "90" };
    testMJpegStreamer(args, 0, 1000);
  }

  @Test
  public void testOverlay() throws InterruptedException {
    URL movieUrl = ClassLoader.getSystemResource("testmovie/movie.mjpg");
    String[] args = { "-u", movieUrl.toExternalForm(), "--start",
        "--autoclose", "--overlay", "--rotation", "90" };
    testMJpegStreamer(args, 0, 1000);
    assertTrue(RectangleOverlay.counter > 0);
  }
  
  @Test
  public void testRun() throws IOException, InterruptedException {
    java.lang.Runtime rt = java.lang.Runtime.getRuntime();
    // Start two processes: ps ax | grep rbe
    java.lang.Process p1 = rt.exec("ps ax");
    p1.waitFor();
    String sErr1 = getStreamContent(p1.getErrorStream(), debug);
    String sOut = getStreamContent(p1.getInputStream(), debug);
    assertNotNull(sErr1);
    assertNotNull(sOut);
  }

  @Test
  public void testPiping() throws IOException, InterruptedException {
    java.lang.Runtime rt = java.lang.Runtime.getRuntime();
    // Start two processes: ps ax | grep rbe
    java.lang.Process p1 = rt.exec("ps ax");
    // grep will wait for input on stdin
    java.lang.Process p2 = rt.exec("grep java");
    // Create and start Piper
    Piper pipe = new Piper(p1.getInputStream(), p2.getOutputStream(), 512);
    new Thread(pipe).start();
    // Wait for second process to finish
    p2.waitFor();
    Thread.sleep(300);
    String sErr1 = getStreamContent(p1.getErrorStream(), debug);
    String sErr2 = getStreamContent(p2.getErrorStream(), debug);
    String sOut = getStreamContent(p2.getInputStream(), debug);
    String err1 = "stdErr of ps ax   is : '" + sErr1 + "'";
    String err2 = "stdErr of grep java is: '" + sErr2 + "'";
    if (debug) {
      System.out.println(err1);
      System.out.println(err2);
      System.out.println("stdout is: '"+sOut+"'");
    }
    assertTrue("'" + sOut + "' should contain java ", sOut.contains("java"));
  }

  /**
   * get the content of the given inputStream
   * 
   * @param inputStream
   * @param debug
   *          - true if debugging is wanted
   * @return
   * @throws IOException
   */
  public String getStreamContent(InputStream inputStream, boolean debug)
      throws IOException {
    // Show output of second process
    java.io.BufferedReader r = new java.io.BufferedReader(
        new java.io.InputStreamReader(inputStream));
    String s = "";
    String line = null;
    while ((line = r.readLine()) != null) {
      s += line + "\n";
      if (debug)
        System.out.println(line);
    }
    return s;

  }

  @Ignore
  public void testStdIn() throws Exception {
    /**
     * commented out due to cycle dependency introduced
     * 
     * int pictureCount=10;
     * 
     * StreamResult cameraStream = GPhoto2.getCameraStream(CameraMode.preview,
     * pictureCount);
     * assertEquals(0,cameraStream.exitCode);
     * System.setIn(cameraStream.stream);
     * // getPipeContent(System.in,debug);
     * String[] args = { "-u", "-", "--start", "--autoclose" };
     * testMJpegStreamer(args, 0, 5000);
     */
  }

}
