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
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class ViewPanelImpl implements ViewPanel {
  private ViewerSetting viewerSetting = new ViewerSetting();
  
  /**
   * @return the viewerSetting
   */
  public ViewerSetting getViewerSetting() {
    return viewerSetting;
  }

  /**
   * @param viewerSetting
   *          the viewerSetting to set
   */
  public void setViewerSetting(ViewerSetting viewerSetting) {
    this.viewerSetting = viewerSetting;
  }
  
  public void start(String url) {
    if (viewerSetting.autoStart) {
      this.startStreaming(url);
    }
  }

  @Override
  public void init() {
    this.showMessage("started");
  }

  @Override
  public void stop(String msg) {
    this.showMessage("stopped:" + msg);
    if (viewerSetting.autoClose)
      this.close();
  }
  
  /**
   * set Failed String
   * 
   * @param ex
   */
  public void handle(Exception ex) {
    this.showMessage(ex.getMessage());
  }
  
  /**
   * get the given buffered Image
   * 
   * @param resourcePath
   *          - path in class path /jar
   * @return the image
   * @throws Exception
   * @throws IOException
   */
  public BufferedImage getBufferedImage(String resourcePath) throws Exception {
    BufferedImage result;
    try {
      java.net.URL imgURL = getClass().getResource(resourcePath);
      if (imgURL != null) {
        result = ImageIO.read(imgURL);
      } else {
        throw new Exception("couldn't get buffered Image for '" + resourcePath
            + "' invalid resourcePath");
      }
    } catch (Throwable th) {
      throw new Exception("couldn't get buffered Image for " + resourcePath,
          th);
    }
    return result;
  }

  
  MJpegReaderRunner runner;

  /**
   * start Streaming with the given runner
   * 
   * @param runner
   * @return the MJpegReaderRunner
   */
  public MJpegReaderRunner startStreaming(MJpegReaderRunner runner) {
    runner.start();
    return runner;
  }

  /**
   * start the streaming
   * 
   * @return the runner
   */
  public MJpegReaderRunner startStreaming(String url) {
    if (runner != null) {
      runner.stop("stopping earlier runner");
    }
    try {
      // runner = new MJpegReaderRunner1();
      runner = new MJpegReaderRunner2();
      runner.init(url, null, null);
      runner.setViewer(this);
      this.startStreaming(runner);
    } catch (Exception ex) {
      handle(ex);
    }
    return runner;
  }
}
