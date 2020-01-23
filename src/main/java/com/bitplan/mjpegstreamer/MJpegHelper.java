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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * helper for JPeg/MJpeg pictures
 * 
 * @author wf
 *
 */
public class MJpegHelper {
  /**
   * rotate an Image 90 Degrees to the Right
   * 
   * @param inputImage
   * @return the rotated image
   */
  public static BufferedImage rotate90ToRight(BufferedImage inputImage) {
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    BufferedImage returnImage = new BufferedImage(height, width,
        inputImage.getType());

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        returnImage.setRGB(height - y - 1, x, inputImage.getRGB(x, y));
      }
    }
    return returnImage;
  }

  /**
   * rotate 90 to Left
   * 
   * @param inputImage
   * @return the rotated image
   */
  public static BufferedImage rotate90ToLeft(BufferedImage inputImage) {
    // The most of code is same as before
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    BufferedImage returnImage = new BufferedImage(height, width,
        inputImage.getType());
    // We have to change the width and height because when you rotate the image
    // by 90 degree, the
    // width is height and height is width <img
    // src='http://forum.codecall.net/public/style_emoticons/<#EMO_DIR#>/smile.png'
    // class='bbc_emoticon' alt=':)' />

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        returnImage.setRGB(y, width - x - 1, inputImage.getRGB(x, y));
      }
    }
    return returnImage;

  }

  /**
   * rotate 180
   * 
   * @param inputImage
   * @return the rotated image
   */
  public static BufferedImage rotate180(BufferedImage inputImage) {
    if (inputImage == null)
      return inputImage;
    // We use BufferedImage because it’s provide methods for pixel manipulation
    int width = inputImage.getWidth(); // the Width of the original image
    int height = inputImage.getHeight();// the Height of the original image

    BufferedImage returnImage = new BufferedImage(width, height,
        inputImage.getType());
    // we created new BufferedImage, which we will return in the end of the
    // program
    // it set up it to the same width and height as in original image
    // inputImage.getType() return the type of image ( if it is in RBG, ARGB,
    // etc. )

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        returnImage.setRGB(width - x - 1, height - y - 1,
            inputImage.getRGB(x, y));
      }
    }
    // so we used two loops for getting information from the whole inputImage
    // then we use method setRGB by whitch we sort the pixel of the return image
    // the first two parametres is the X and Y location of the pixel in
    // returnImage and the last one is the //source pixel on the inputImage
    // why we put width – x – 1 and height –y – 1 is hard to explain for me, but
    // when you rotate image by //180degree the pixel with location [0, 0] will
    // be in [ width, height ]. The -1 is for not to go out of
    // Array size ( remember you always start from 0 so the last index is lower
    // by 1 in the width or height
    // I enclose Picture for better imagination ... hope it help you
    return returnImage;
    // and the last return the rotated image

  }

  /**
   * get the rotated Image
   * 
   * @param inputImage
   * @param rotation
   * @return the rotated image
   */
  public static BufferedImage getRotatedImage(BufferedImage inputImage,
      int rotation) {
    BufferedImage result = inputImage;
    switch (rotation) {
    case 90:
      result = rotate90ToRight(result);
      break;
    case 180:
      result = rotate180(result);
      break;
    case 270:
      result = rotate90ToLeft(result);
      break;
    }
    return result;
  }

  /**
   * get the image from the given bytes
   * 
   * @param imageBytes
   * @return the image
   * @throws IOException
   */
  public static BufferedImage getImage(byte[] imageBytes) throws IOException {
    ByteArrayInputStream jpgIn = new ByteArrayInputStream(imageBytes);
    BufferedImage bufImg = getImage(jpgIn);
    return bufImg;
  }

  /**
   * get the Image from the given Input Stream
   * 
   * @param jpgIn
   * @return the image
   * @throws IOException
   */
  public static BufferedImage getImage(InputStream jpgIn) throws IOException {
    BufferedImage bufImg = ImageIO.read(jpgIn);
    jpgIn.close();
    return bufImg;
  }

  /**
   * get the difference image between two images
   * see https://stackoverflow.com/a/25151302/1497139
   * @param img1
   * @param img2
   * @return - the difference image
   */
  public static BufferedImage getDifferenceImage1(BufferedImage img1,
      BufferedImage img2, int width, int height,Rectangle r) {
    // convert images to pixel arrays...
        
    final int[] p1 = img1.getRGB(r.x, r.y, r.width, r.height, null, 0, r.width);
    final int[] p2 = img2.getRGB(r.x, r.y, r.width, r.height, null, 0, r.width);
    // compare img1 to img2, pixel by pixel. If different, highlight img1's
    // pixel...
    for (int i = 0; i < p1.length; i++) {
      int rgb1=p1[i];
      int rgb2=p2[i];
      int r1 = (rgb1 >> 16) & 0xff;
      int g1 = (rgb1 >> 8) & 0xff;
      int b1 = (rgb1) & 0xff;
      int r2 = (rgb2 >> 16) & 0xff;
      int g2 = (rgb2 >> 8) & 0xff;
      int b2 = (rgb2) & 0xff;

      int rd=r1>r2?r1-r2:r2-r1;
      int gd=g1>g2?g1-g2:g2-g1;
      int bd=b1>b2?b1-b2:b2-b1;
      int d = (rd << 16) | (gd << 8) | bd;
      p1[i] = d;
    }
    // save img1's pixels to a new BufferedImage, and return it...
    // (May require TYPE_INT_ARGB)
    final BufferedImage out = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
    out.setRGB(r.x, r.y, r.width, r.height, p1, 0, r.width);
    return out;
  }
  
  /**
   * get the difference image
   * @param img1
   * @param img2
   * @return
   */
  public static BufferedImage getDifferenceImage1(BufferedImage img1,
      BufferedImage img2) {
    int w=img1.getWidth();
    int h=img1.getHeight();
    Rectangle r=new Rectangle(0,0,w,h);
    return getDifferenceImage1(img1,img2,w,h,r); 
  }
      
  
  /**
   * get a difference image see
   * https://stackoverflow.com/a/25024344/1497139
   * @param img1
   * @param img2
   * @return
   */
  public static BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
    int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
    int width2 = img2.getWidth(); // take no arguments
    int height1 = img1.getHeight();
    int height2 = img2.getHeight();
    if ((width1 != width2) || (height1 != height2)) {
        System.err.println("Error: Images dimensions mismatch");
        System.exit(1);
    }

    // NEW - Create output Buffered image of type RGB
    BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

    // Modified - Changed to int as pixels are ints
    int diff;
    int result; // Stores output pixel
    for (int i = 0; i < height1; i++) {
        for (int j = 0; j < width1; j++) {
            int rgb1 = img1.getRGB(j, i);
            int rgb2 = img2.getRGB(j, i);
            int r1 = (rgb1 >> 16) & 0xff;
            int g1 = (rgb1 >> 8) & 0xff;
            int b1 = (rgb1) & 0xff;
            int r2 = (rgb2 >> 16) & 0xff;
            int g2 = (rgb2 >> 8) & 0xff;
            int b2 = (rgb2) & 0xff;
            diff = Math.abs(r1 - r2); // Change
            diff += Math.abs(g1 - g2);
            diff += Math.abs(b1 - b2);
            diff /= 3; // Change - Ensure result is between 0 - 255
            // Make the difference image gray scale
            // The RGB components are all the same
            result = (diff << 16) | (diff << 8) | diff;
            outImg.setRGB(j, i, result); // Set result
        }
    }

    // Now return
    return outImg;
}
}
