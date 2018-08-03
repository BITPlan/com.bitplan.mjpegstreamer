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
