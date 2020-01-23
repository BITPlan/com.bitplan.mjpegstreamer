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

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * view panel implementation
 * 
 * @author wf
 *
 */
public abstract class ViewPanelImpl implements ViewPanel {
  protected static final String ROTATE_BUTTON_ICON_PATH = "/images/paper0r.png";
  protected static final String START_BUTTON_ICON_PATH = "/images/start.png";
  // https://www.iconfinder.com/icons/49386/settings_icon#size=64
  protected static final String SETTINGS_BUTTON_ICON_PATH = "/images/1394392895_settings.png";

  private ViewerSetting viewerSetting = new ViewerSetting();
  private String url;
  BooleanProperty open = new SimpleBooleanProperty();

  protected BufferedImage image;

  /**
   * set the JPeg Image
   * 
   * @param jpeg
   */
  @Override
  public void setBufferedImage(BufferedImage image) {
    this.image=image;
  }

  /**
   * set the Image and render it
   * 
   * @param pImage
   */
  @Override
  public void renderNextImage(JPeg jpeg) {
    setBufferedImage(jpeg.getImage());
  }

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

  /**
   * set the url to the given value
   * 
   * @param url
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * setup the viewPanel
   * 
   * @param url
   * @throws Exception
   */
  public void setup(String url) throws Exception {
    setup();
    this.setUrl(url);
  }

  public void start(String url) {
    this.setUrl(url);
    if (viewerSetting.autoStart) {
      this.startStreaming();
    }
  }

  @Override
  public void init() {
    this.showMessage("started");
    open.set(true);
  }

  @Override
  public void stop(String msg) {
    this.showMessage("stopped:" + msg);
    if (viewerSetting.autoClose)
      this.close();
  }

  @Override
  public BooleanProperty isOpen() {
    return open;
  }

  public void close() {
    open.set(false);
  }

  /**
   * get a new rotated Icon for a rotate button
   * 
   * @return the icons's Buffered Image
   */
  public BufferedImage rotate() {
    int rotation = this.getViewerSetting().rotation;
    rotation += 90;
    if (rotation >= 360)
      rotation = 0;
    this.getViewerSetting().rotation = rotation;
    BufferedImage rotateButtonIcon = null;
    try {
      rotateButtonIcon = getBufferedImage(
          SwingViewPanel.ROTATE_BUTTON_ICON_PATH);
      rotateButtonIcon = MJpegHelper.getRotatedImage(rotateButtonIcon,
          rotation);
    } catch (Exception e1) {
      handle(e1);
    }
    return rotateButtonIcon;
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

  /**
   * prepare me with an empty image
   */
  public void setEmptyImage() throws Exception {
    BufferedImage bg = getBufferedImage("/images/screen640x480.png");
    if (bg != null)
      setBufferedImage(bg);
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
  @Override
  public MJpegReaderRunner startStreaming() {
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
