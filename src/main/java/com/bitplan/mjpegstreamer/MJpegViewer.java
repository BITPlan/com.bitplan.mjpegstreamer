/**
 * 
 */
package com.bitplan.mjpegstreamer;

import javax.swing.JPanel;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

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
	public static final String VERSION = "0.0.1";

	@Option(name = "-d", aliases = { "--debug" }, usage = "debug\nadds debugging output")
	boolean debug = false;

	@Option(name = "-h", aliases = { "--help" }, usage = "help\nshow this usage")
	boolean showHelp = false;

	@Option(name = "-r", aliases = { "--rotation" }, usage = "rotation e.g. 0/90/180/270 degrees")
	int rotation;
	
	@Option(name = "-s", aliases = { "--start" }, usage = "auto start\nstart streaming immediately")
	boolean autoStart=false;

	@Option(name = "-t", aliases = { "--title" }, usage = "title\ntitle to be used")
	String title = "MJpegViewer";
	
	@Option(name = "-u", aliases = { "--url" }, usage = "url\nurl to be used")
	String url = "http://cam2/mjpeg.cgi";

	@Option(name = "-v", aliases = { "--version" }, usage = "showVersion\nshow current version if this switch is used")
	boolean showVersion = false;

	public static boolean testMode = false;

	private static CmdLineParser parser;
	public static int exitCode;

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
		System.err.println(t.getClass().getSimpleName()+":"+t.getMessage());
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
				ViewPanel viewPanel = new ViewPanel();
				viewPanel.setup(title, url, autoStart,rotation,debug);
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
		MJpegViewer viewer = new MJpegViewer();
		int result = viewer.maininstance(args);
		if (!testMode && result != 0)
			System.exit(result);
	}
}
