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
import java.io.File;

/**
 * Interface for Jpeg images
 * @author wf
 *
 */
public interface JPeg {
  
  /**
   * get the MJPeg I am belonging to
   */
  public MJPeg getMJPeg();
  
  /**
   * what's my index in the MJPeg
   * @return the index starting from 0 for the first frame
   */
  public int getFrameIndex();
  
  public long getLength();

  public void setLength(long length);
  
  public long getOffset();
  
  /**
   * get the image
   * @return
   */
  public default BufferedImage getImage() {
    return getRotatedImage(0);
  }
  
  /**
   * modify the image
   * @param image
   */
  public void setImage(BufferedImage image);
  
  /**
   * get a rotated image
   * @param rotation e.g. 0/90/180/270
   * @return the rotated image
   */
  public BufferedImage getRotatedImage(int rotation);

  /**
   * rotate the image with the given rotation
   * @param rotation
   */
  public void rotate(int rotation);
  
  /**
   * save me to the default location
   * @throws Exception 
   */
  public default void save() throws Exception {
    save(getMJPeg().getImageFile(this));
  };
  
  /**
   * save me to the given image file
   * @param imageFile
   * @throws Exception 
   */
  public void save(File imageFile) throws Exception;
}
