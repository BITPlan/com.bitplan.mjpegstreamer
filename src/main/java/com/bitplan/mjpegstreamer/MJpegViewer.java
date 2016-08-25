/**
 * Copyright (c) 2013-2016 BITPlan GmbH
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

import javax.swing.JPanel;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

/**
 * Viewer for MJPeg
 * 
 * @author wf
 * 
 */
public class MJpegViewer extends JPanel {

	/**
	 * VersionUID
	 */
	private static final long serialVersionUID = -8411137280369817459L;

	/**
	 * current Version of the tool
	 */
	public static final String VERSION = "0.0.3";

	@Option(name = "-d", aliases = { "--debug" }, usage = "debug\nadds debugging output")
	boolean debug = false;

	@Option(name = "-h", aliases = { "--help" }, usage = "help\nshow this usage")
	boolean showHelp = false;

	@Option(name = "-o", aliases = { "--overlay" }, usage = "adds a rectangle overlay")
	boolean overlay = false;

	@Option(name = "-r", aliases = { "--rotation" }, usage = "rotation e.g. 0/90/180/270 degrees")
	int rotation;
	
	@Option(name = "-rto", aliases = { "--readtimeout" }, usage = "readtimeout in milliseconds\ndefault is 5000 millisecs")
	int readTimeOut = 5000;
	
	@Option(name = "-s", aliases = { "--start" }, usage = "auto start\nstart streaming immediately")
	boolean autoStart=false;

	@Option(name = "-ac", aliases = { "--autoclose" }, usage = "auto close\nclose when stream is finished")
	boolean autoClose=false;

	@Option(name = "-t", aliases = { "--title" }, usage = "title\ntitle to be used")
	String title = "MJpegViewer";
	
	@Option(name = "-u", aliases = { "--url" }, usage = "url\nurl to be used")
	String url = "http://cam2/mjpeg.cgi";
	
	@Option(name = "-v", aliases = { "--version" }, usage = "showVersion\nshow current version if this switch is used")
	boolean showVersion = false;

	private ViewPanel viewPanel = new ViewPanel();

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
	 * @param autoClose the autoClose to set
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
	 * @param readTimeOut the readTimeOut to set
	 */
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	/**
	 * @return the viewPanel
	 */
	public ViewPanel getViewPanel() {
		return viewPanel;
	}

	/**
	 * @param viewPanel the viewPanel to set
	 */
	public void setViewPanel(ViewPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
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
	 * @param overlay the overlay to set
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
	 * @param rotation the rotation to set
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
	 * @param autoStart the autoStart to set
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
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the testMode
	 */
	public static boolean isTestMode() {
		return testMode;
	}

	/**
	 * @param testMode the testMode to set
	 */
	public static void setTestMode(boolean testMode) {
		MJpegViewer.testMode = testMode;
	}

	/**
	 * show the Version
	 */
	public void showVersion() {
		System.err.println("MJpegViewer Version: " + VERSION);
		System.err.println();
		System.err
				.println(" github: https://github.com/WolfgangFahl/mjpegviewer.git");
		System.err.println("");
	}

	/**
	 * handle the given Throwable
	 * 
	 * @param t
	 */
	public void handle(Throwable t) {
		System.out.flush();
		String msg="";
		if (t.getMessage()!=null)
		  msg+=":"+t.getMessage();
		System.err.println(t.getClass().getSimpleName()+msg);
		if (debug)
			t.printStackTrace();
	}

	/**
	 * display usage
	 * 
	 * @param msg
	 *            - a message to be displayed (if any)
	 */
	public void usage(String msg) {
		System.err.println(msg);
		showVersion();
		System.err
				.println("  usage: java com.bitplan.mjpegstreamer.MJpegViewer");
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
	 * @return the view panel for setup
	 * @throws Exception
	 */
	public ViewPanel setupViewPanel() throws Exception {
		ViewerSetting s = viewPanel.getViewerSetting();
		s.title=title;
		s.autoStart=autoStart;
		s.readTimeOut=readTimeOut;
		s.rotation=rotation;
		s.autoClose=autoClose;
		if (overlay)
			s.imageListener=new RectangleOverlay(50,50,50,50,Color.BLUE);
		if (debug)
			s.debugMode=DebugMode.Verbose;
		viewPanel.setup(url);
		return viewPanel;
	}
	
	/**
	 * main routine
	 * 
	 * @param args
	 */
	public int maininstance(String[] args) {
		parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
			if (debug)
				showVersion();
			if (this.showVersion)  {
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
