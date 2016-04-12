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
 * @author muf
 * 
 */
public class TestCommandLine {

  private boolean debug = true;

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
    String[] args = { "-u", "http://cam2/mjpeg.cgi", "--start", "--autoclose" };
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
    java.lang.Process p2 = rt.exec("grep Java");
    // Create and start Piper
    Piper pipe = new Piper(p1.getInputStream(), p2.getOutputStream(), 512);
    new Thread(pipe).start();
    // Wait for second process to finish
    p2.waitFor();
    Thread.sleep(100);
    String sErr1 = getStreamContent(p1.getErrorStream(), debug);
    String sErr2 = getStreamContent(p2.getErrorStream(), debug);
    String sOut = getStreamContent(p2.getInputStream(), debug);
    String err1 = "stdErr of ps ax   is : '" + sErr1 + "'";
    String err2 = "stdErr of grep Java is: '" + sErr2 + "'";
    if (debug) {
      System.out.println(err1);
      System.out.println(err2);
    }
    System.out.println();
    assertTrue("'" + sOut + "' should contain Java ", sOut.contains("Java"));
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
