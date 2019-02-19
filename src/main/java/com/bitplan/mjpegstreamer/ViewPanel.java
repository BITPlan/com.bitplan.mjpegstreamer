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
package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;

public interface ViewPanel extends  MJpegRenderer {
  /**
   * @return the viewerSetting
   */
  public ViewerSetting getViewerSetting();

  /**
   * @param viewerSetting
   *          the viewerSetting to set
   */
  public void setViewerSetting(ViewerSetting viewerSetting);
  
  public void start(String url);

  public void init();

  public void stop(String msg);
  
  /**
   * set the Image and render it
   * 
   * @param pImage
   */

  public void renderNextImage(BufferedImage pImage);
  
  /**
   * set the failed string
   * 
   * @param msg
   */
  public void showMessage(String msg);
  
  /**
   * set Failed String
   * 
   * @param ex
   */
  public void handle(Exception ex);
  
  public MJpegReaderRunner startStreaming(MJpegReaderRunner runner);
  
  /**
   * start the streaming
   * 
   * @return the runner
   */
  public MJpegReaderRunner startStreaming(String url);
  
  public void close();
}
