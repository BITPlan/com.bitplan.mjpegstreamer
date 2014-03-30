package com.bitplan.mjpeg.preview;

import java.awt.Color;

import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.MJpegViewer;
import com.bitplan.mjpegstreamer.RectangleOverlay;
import com.bitplan.mjpegstreamer.ViewerSetting;

/**
 * swing preview via mJpegViewer
 * @author wf
 *
 */
public class MJpegSwingPreview extends PreviewBase {
	private MJpegViewer mJpegViewer;

	/**
	 * default constructor
	 */
	public MJpegSwingPreview() {
		mJpegViewer = new MJpegViewer();
		viewer=mJpegViewer.getViewPanel();
		runner = new MJpegReaderRunner2();
		runner.setViewer(viewer);
		ViewerSetting s = viewer.getViewerSetting();
		s.setAutoStart(false); // we start ourselves in the start function below
		s.setAutoClose(true);
		s.setImageListener(new RectangleOverlay(50,50,50,50,Color.BLUE));
		s.setReadTimeOut(1000); // watchit -lower than default!
	}
	
	@Override
	/**
	 * make sure the viewpanel is prepared
	 */
	public void start() throws Exception {
		mJpegViewer.getViewPanel().setup(runner.getUrlString());
		super.start();
	}
}
