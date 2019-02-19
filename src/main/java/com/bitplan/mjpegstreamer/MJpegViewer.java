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
/**
 * 
 */
package com.bitplan.mjpegstreamer;

import java.awt.Color;
import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.bitplan.error.SoftwareVersion;
import com.bitplan.i18n.Translator;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;
import com.bitplan.mjpegstreamer.SwingViewPanel;

/**
 * Viewer for MJPeg
 * 
 * @author wf
 * 
 */
public class MJpegViewer  {

  /**
   * current Version of the tool
   */
  public static final String VERSION = "0.0.8";

  @Option(name = "-d", aliases = {
      "--debug" }, usage = "debug\nadds debugging output")
  boolean debug = true;

  @Option(name = "-h", aliases = { "--help" }, usage = "help\nshow this usage")
  boolean showHelp = false;

  @Option(name = "-j", aliases = { "--javafx" }, usage = "javafx\nuse javafx")
  boolean javaFx = true;

  @Option(name = "-o", aliases = {
      "--overlay" }, usage = "adds a rectangle overlay")
  boolean overlay = false;

  @Option(name = "-r", aliases = {
      "--rotation" }, usage = "rotation e.g. 0/90/180/270 degrees")
  int rotation;

  @Option(name = "-rto", aliases = {
      "--readtimeout" }, usage = "readtimeout in milliseconds\ndefault is 5000 millisecs")
  int readTimeOut = 5000;

  @Option(name = "-s", aliases = {
      "--start" }, usage = "auto start\nstart streaming immediately")
  boolean autoStart = false;

  @Option(name = "-ac", aliases = {
      "--autoclose" }, usage = "auto close\nclose when stream is finished")
  boolean autoClose = false;

  @Option(name = "-t", aliases = {
      "--title" }, usage = "title\ntitle to be used")
  String title = "MJpegViewer";

  @Option(name = "-u", aliases = { "--url" }, usage = "url\nurl to be used")
  String url = "http://cam2/mjpeg.cgi";

  @Option(name = "-f", aliases = { "--file" }, usage = "file\nfile to open")
  String fileName = null;

  @Option(name = "-v", aliases = {
      "--version" }, usage = "showVersion\nshow current version if this switch is used")
  boolean showVersion = false;

  private MJpegApp mJpegApp;

  private ViewPanel viewPanel;

  public static boolean testMode = false;

  private static CmdLineParser parser;
  public static int exitCode;

  private static MJpegViewer viewer;

  /**
   * @return the autoClose
   */
  public boolean isAutoClose() {
    return autoClose;
  }

  /**
   * @param autoClose
   *          the autoClose to set
   */
  public void setAutoClose(boolean autoClose) {
    this.autoClose = autoClose;
  }

  /**
   * @return the readTimeOut
   */
  public int getReadTimeOut() {
    return readTimeOut;
  }

  /**
   * @param readTimeOut
   *          the readTimeOut to set
   */
  public void setReadTimeOut(int readTimeOut) {
    this.readTimeOut = readTimeOut;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the overlay
   */
  public boolean isOverlay() {
    return overlay;
  }

  /**
   * @param overlay
   *          the overlay to set
   */
  public void setOverlay(boolean overlay) {
    this.overlay = overlay;
  }

  /**
   * @return the rotation
   */
  public int getRotation() {
    return rotation;
  }

  /**
   * @param rotation
   *          the rotation to set
   */
  public void setRotation(int rotation) {
    this.rotation = rotation;
  }

  /**
   * @return the autoStart
   */
  public boolean isAutoStart() {
    return autoStart;
  }

  /**
   * @param autoStart
   *          the autoStart to set
   */
  public void setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public ViewPanel getViewPanel() {
    return viewPanel;
  }

  public void setViewPanel(ViewPanel viewPanel) {
    this.viewPanel = viewPanel;
  }

  /**
   * @return the testMode
   */
  public static boolean isTestMode() {
    return testMode;
  }

  /**
   * @param testMode
   *          the testMode to set
   */
  public static void setTestMode(boolean testMode) {
    MJpegViewer.testMode = testMode;
  }

  public static class MJpegVersion implements SoftwareVersion {

    @Override
    public String getVersion() {
      return VERSION;
    }

    @Override
    public String getSupportEMail() {
      return "support@bitplan.com";
    }

    @Override
    public String getSupportEMailPreamble() {
      return "Dear BITPlan support team\n";
    }

    @Override
    public String getName() {
      return "MJpeg";
    }

    @Override
    public String getUrl() {
      return "http://www.bitplan.com/MJpegStreamer";
    }
  }

  /**
   * show the Version
   */
  public void showVersion() {
    System.err.println("MJpegViewer Version: " + VERSION);
    System.err.println();
    System.err.println(
        " github: https://github.com/BITPlan/com.bitplan.mjpegstreamer");
    System.err.println("");
  }

  /**
   * handle the given Throwable
   * 
   * @param t
   */
  public void handle(Throwable t) {
    System.out.flush();
    String msg = "";
    if (t.getMessage() != null)
      msg += ":" + t.getMessage();
    System.err.println(t.getClass().getSimpleName() + msg);
    if (debug)
      t.printStackTrace();
  }

  /**
   * display usage
   * 
   * @param msg
   *          - a message to be displayed (if any)
   */
  public void usage(String msg) {
    System.err.println(msg);
    showVersion();
    System.err.println("  usage: java com.bitplan.mjpegstreamer.MJpegViewer");
    parser.printUsage(System.err);
    exitCode = 1;
  }

  /**
   * show Help
   */
  public void showHelp() {
    usage("Help");
  }

  /**
   * setup the view panel
   * 
   * @return the view panel for setup
   * @throws Exception
   */
  public ViewPanel setupViewPanel() throws Exception {
    if (javaFx) {
      mJpegApp = MJpegApp.getInstance(new MJpegVersion(), debug);
      MJpegApp.toolkitInit();
      mJpegApp.setViewPanel(new JavaFXViewPanel());
      viewPanel = mJpegApp.getViewPanel();
    } else {
      viewPanel = new SwingViewPanel();
    }
    ViewerSetting s = viewPanel.getViewerSetting();
    s.title = title;
    s.autoStart = autoStart;
    s.readTimeOut = readTimeOut;
    s.rotation = rotation;
    s.autoClose = autoClose;
    if (overlay)
      s.imageListener = new RectangleOverlay(50, 50, 50, 50, Color.BLUE);
    if (debug)
      s.debugMode = DebugMode.Verbose;
    if (fileName != null) {
      File file = new File(fileName);
      if (!file.canRead()) {
        throw new Exception("Can't read " + file.getPath());
      }
      url = file.toURI().toString();
    }
    viewPanel.setup(url);

    if (javaFx) {
      mJpegApp.show();
      mJpegApp.waitOpen();
    } else {
      if (viewPanel instanceof SwingViewPanel) {
        SwingViewPanel swingViewPanel = (SwingViewPanel) viewPanel;
        swingViewPanel.createFrame(s.title);
      }
    }
    viewPanel.start(url);
    return viewPanel;
  }

  /**
   * main routine
   * 
   * @param args
   */
  public int maininstance(String[] args) {
    Translator.APPLICATION_PREFIX = "mjpeg";
    Translator.initialize("mjpeg", "en");
    parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
      if (debug)
        showVersion();
      if (this.showVersion) {
        showVersion();
      } else if (this.showHelp) {
        showHelp();
      } else {
        setupViewPanel();
        exitCode = 0;
      }
    } catch (CmdLineException e) {
      // handling of wrong arguments
      usage(e.getMessage());
    } catch (Exception e) {
      handle(e);
      // System.exit(1);
      exitCode = 1;
    }
    return exitCode;
  }

  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    viewer = new MJpegViewer();
    int result = viewer.maininstance(args);
    if (!testMode && result != 0)
      System.exit(result);
  }

}
