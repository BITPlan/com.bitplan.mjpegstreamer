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
package com.bitplan.mjpegstreamer;

/**
 * generic MJpegRendering interface
 * @author wf
 *
 */
public interface MJpegRenderer {

	/**
	 * initialize the Renderer with DebugMode None
	 */
	public void init();
	
	/**
	 * render the next image
	 * @param jpeg
	 */
	public void renderNextImage(JPeg jpeg);
	
	/**
	 * stop rendering
	 * @param msg 
	 */
	public void stop(String msg);

	/**
	 * show a Message e.g. if something failed
	 * @param msg
	 */
	void showMessage(String msg);
	
	/**
	 * @return the viewerSetting
	 */
	public ViewerSetting getViewerSetting();

	/**
	 * @param viewerSetting the viewerSetting to set
	 */
	public void setViewerSetting(ViewerSetting viewerSetting);
	
	/**
	 * show the Progress
	 * @param MJPeg mjpeg
	 */
  public void showProgress(MJPeg mjpeg);

}
