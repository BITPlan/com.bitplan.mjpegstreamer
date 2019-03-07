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

  @Override
  public void stop() {
    
  }
}
