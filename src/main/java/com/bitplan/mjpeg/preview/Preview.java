/**
 * Copyright (c) 2013-2020 BITPlan GmbH
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
	 * @throws Exception 
	 */
	public void start() throws Exception;

  public void stop();
}
