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