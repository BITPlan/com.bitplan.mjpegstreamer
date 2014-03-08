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
	 * current Version of the tool
	 */
	public static final String VERSION = "0.0.1";

	@Option(name = "-u", aliases = { "--url" }, usage = "url\nurl to be used")
	String url="http://cam2/mjpeg.cgi";
	
	@Option(name = "-v", aliases = { "--version" }, usage = "showVersion\nshow current version if this switch is used")
	boolean showVersion = false;

	@Option(name = "-t", aliases = { "--title" }, usage = "title\ntitle to be used")
	String title="MJpegViewer";
	

	private int exitCode;

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
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * get the viewer
	 * 
	 */
	public MJpegViewer() {

	}

	private static boolean testMode=false;

	private static CmdLineParser parser;

	
	
	/**
	 * handle the given Throwable
	 * 
	 * @param t
	 */
	public void handle(Throwable t) {
		System.out.flush();
		t.printStackTrace();
		usage(t.getMessage());
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
		System.err.println("  usage: java com.bitplan.pdfindexer.Pdfindexer");
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
			ViewPanel viewPanel=new ViewPanel();
			viewPanel.setup(title,url);
			exitCode = 0;
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
		if (!testMode && result!=0)
			System.exit(result);
	}
}
