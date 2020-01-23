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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * https://stackoverflow.com/a/35202191/1497139
 * 
 * @author wf
 *
 */
public class WrappedImageView extends ImageView {
  WrappedImageView(Image image) {
    super(image);
    setPreserveRatio(false);
  }

  @Override
  public double minWidth(double height) {
    return 40;
  }

  @Override
  public double prefWidth(double height) {
    Image I = getImage();
    if (I == null)
      return minWidth(height);
    return I.getWidth();
  }

  @Override
  public double maxWidth(double height) {
    return 16384;
  }

  @Override
  public double minHeight(double width) {
    return 40;
  }

  @Override
  public double prefHeight(double width) {
    Image I = getImage();
    if (I == null)
      return minHeight(width);
    return I.getHeight();
  }

  @Override
  public double maxHeight(double width) {
    return 16384;
  }

  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public void resize(double width, double height) {
    setFitWidth(width);
    setFitHeight(height);
  }
}
