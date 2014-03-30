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
		int readTimeOut=1000;
		DebugMode debugMode=DebugMode.None;
		ImageListener imageListener=null;
		boolean autoClose=false;
		boolean autoStart=false;
		
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
		 * @return the pictureCount
		 */
		public int getPictureCount() {
			return pictureCount;
		}
		/**
		 * @param pictureCount the pictureCount to set
		 */
		public void setPictureCount(int pictureCount) {
			this.pictureCount = pictureCount;
		}
		/**
		 * @return the fpsLimit
		 */
		public int getFpsLimit() {
			return fpsLimit;
		}
		/**
		 * @param fpsLimit the fpsLimit to set
		 */
		public void setFpsLimit(int fpsLimit) {
			this.fpsLimit = fpsLimit;
		}
		/**
		 * @return the previewTime
		 */
		public int getPreviewTime() {
			return previewTime;
		}
		/**
		 * @param previewTime the previewTime to set
		 */
		public void setPreviewTime(int previewTime) {
			this.previewTime = previewTime;
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
		 * @return the debugMode
		 */
		public DebugMode getDebugMode() {
			return debugMode;
		}
		/**
		 * @param debugMode the debugMode to set
		 */
		public void setDebugMode(DebugMode debugMode) {
			this.debugMode = debugMode;
		}
		/**
		 * @return the imageListener
		 */
		public ImageListener getImageListener() {
			return imageListener;
		}
		/**
		 * @param imageListener the imageListener to set
		 */
		public void setImageListener(ImageListener imageListener) {
			this.imageListener = imageListener;
		}
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
}
