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
import com.bitplan.mjpegstreamer.MJpegViewer;
import com.bitplan.mjpegstreamer.ViewPanel;

/**
 * swing preview via mJpegViewer
 * @author wf
 *
 */
public class MJpegJavaFXPreview extends PreviewBase {
	private MJpegViewer mJpegViewer;
  private ViewPanel viewPanel;

	/**
	 * default constructor
	 * @throws Exception 
	 */
	public MJpegJavaFXPreview() throws Exception {
		mJpegViewer = new MJpegViewer();
		mJpegViewer.setOverlay(true);
		mJpegViewer.setAutoStart(false);// we start ourselves in the start function below
		mJpegViewer.setAutoClose(true);
		mJpegViewer.setReadTimeOut(1000); // watchit -lower than default!
		mJpegViewer.setupViewPanel(); // does also show and waitOpen
		viewPanel=mJpegViewer.getViewPanel();
		viewer=viewPanel;
		runner = new MJpegReaderRunner2();
		runner.setViewer(viewer);
	}
	
	@Override
	/**
	 * make sure the view panel is prepared
	 */
	public void start() throws Exception {
	  String url=runner.getUrlString();
		viewPanel.setUrl(url);
		super.start();
	}

  @Override
  public void stop() {
    this.mJpegViewer.close();
  }
}
