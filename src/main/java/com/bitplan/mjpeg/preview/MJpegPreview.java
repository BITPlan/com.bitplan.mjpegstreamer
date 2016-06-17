package com.bitplan.mjpeg.preview;

import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.MJpegRenderQueue;

/**
 * a preview that uses a Render Queue
 * @author wf
 *
 */
public class MJpegPreview extends PreviewBase {

	public static int QUEUE_SIZE=1000;
	
	/**
	 * construct this preview
	 */
	public MJpegPreview() {
		runner = new MJpegReaderRunner2();
		viewer = new MJpegRenderQueue(QUEUE_SIZE);
		runner.setViewer(viewer);
	}

	/**
	 * get the render queue
	 * @return the render queue
	 */
	public MJpegRenderQueue getRenderQueue() {
		return (MJpegRenderQueue) viewer;
	}
}
