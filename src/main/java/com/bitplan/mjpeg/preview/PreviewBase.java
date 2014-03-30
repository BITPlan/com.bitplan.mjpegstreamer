package com.bitplan.mjpeg.preview;

import java.util.logging.Logger;

import com.bitplan.mjpegstreamer.MJpegReaderRunner;
import com.bitplan.mjpegstreamer.MJpegRenderer;

/**
 * Base class for Preview Handling
 * @author wf
 *
 */
public class PreviewBase  implements Preview {
	protected Logger LOGGER=Logger.getLogger("com.bitplan.mjpeg.preview");
	protected MJpegRenderer viewer;
	protected MJpegReaderRunner runner;

	/**
	 * @return the viewer
	 */
	public MJpegRenderer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(MJpegRenderer viewer) {
		this.viewer = viewer;
	}
	/**
	 * @return the runner
	 */
	public MJpegReaderRunner getRunner() {
		return runner;
	}

	/**
	 * @param runner the runner to set
	 */
	public void setRunner(MJpegReaderRunner runner) {
		this.runner = runner;
	}

	@Override
	public void start() throws Exception {
		runner.start();
	}
}
