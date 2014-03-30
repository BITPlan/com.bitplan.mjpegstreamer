package com.bitplan.mjpeg.preview;

import com.bitplan.mjpegstreamer.MJpegReaderRunner;
import com.bitplan.mjpegstreamer.MJpegRenderer;

/**
 * preview Handling
 * @author wf
 *
 */
public interface Preview {
	
	/**
	 * @return the viewer
	 */
	public MJpegRenderer getViewer();

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(MJpegRenderer viewer);
	/**
	 * @return the runner
	 */
	public MJpegReaderRunner getRunner();

	/**
	 * @param runner the runner to set
	 */
	public void setRunner(MJpegReaderRunner runner);
	
	/**
	 * start the preview
	 */
	public void start();
}
