package com.bitplan.mjpegstreamer;

/**
 * preview Settings
 * @author wf
 *
 */
public class ViewerSetting {
	
	public enum DebugMode{None,FPS,Verbose};
	
	  String title="preview";
		int rotation=0;                     // 0,90,180 or 270 degrees rotated
		int pictureCount=Integer.MAX_VALUE; // when to stop - pictureCount*fpsLimit gives the number of seconds to run
		int fpsLimit=50;                    // maximum number of Frames per seconds
		int previewTime;
		int readTimeOut=100;
		DebugMode debugMode=DebugMode.None;
		ImageListener imageListener=null;
		boolean autoClose=false;
		boolean autoStart=false;
}
