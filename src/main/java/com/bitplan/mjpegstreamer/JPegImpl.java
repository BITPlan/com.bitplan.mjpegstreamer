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
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * a JPeg fragment of an MJPeg stream or file
 * 
 * @author wf
 *
 */
public class JPegImpl implements JPeg {
  // we try to keep the dataset small since at 60 fps we might need
  // 216.000 records of this kind per hour at 12 bytes per record
  // this would be 2.5 MBytes per hour
  long offset;
  long length;
  MJPeg mjpeg; // the MJPeg i am belonging to
  BufferedImage jpegImg;
  int frameIndex;
  
  @Override
  public long getOffset() {
    return offset;
  }

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }
  
  @Override
  public int getFrameIndex() {
    return frameIndex;
  }
 

  /**
   * a JPeg Image within the file
   * @param frameIndex - the index in the mjpeg video
   * @param mjpeg - the MJPeg i am belonging to
   * @param offset
   */
  public JPegImpl(MJPeg mjpeg, int frameIndex, long offset) {
    this.mjpeg=mjpeg;
    this.frameIndex=frameIndex;
    this.offset = offset;
  }

  /**
   * create me from the given frame
   * @param mjpeg - the MJPeg i am belonging to
   * @param frameIndex - the index in the mjpeg video
   * @param offset
   * @param frame
   * @throws Exception
   */
  public JPegImpl(MJPeg mjpeg,int frameIndex, long offset, byte[] frame) throws Exception {
    this(mjpeg,frameIndex,offset);
    this.length=frame.length;
    jpegImg= MJpegHelper.getImage(frame);
  }
  
  @Override
  public BufferedImage getRotatedImage(int rotation) {
    BufferedImage result = MJpegHelper.getRotatedImage(jpegImg, rotation);
    return result;
  }

  @Override
  public void rotate(int rotation) {
    this.jpegImg=getRotatedImage(rotation);
  }

  @Override
  public MJPeg getMJPeg() {
    return mjpeg;
  }

  @Override
  public void save(File imageFile) throws Exception {
    ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
    ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
    jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    jpgWriteParam.setCompressionQuality(0.7f);

    ImageOutputStream outputStream = new FileImageOutputStream(imageFile); 
    jpgWriter.setOutput(outputStream);
    IIOImage outputImage = new IIOImage(jpegImg, null, null);
    jpgWriter.write(null, outputImage, jpgWriteParam);
    jpgWriter.dispose(); 
  }
 
}