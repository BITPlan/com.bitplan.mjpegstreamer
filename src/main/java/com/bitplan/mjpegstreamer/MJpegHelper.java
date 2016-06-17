package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * helper for JPeg/MJpeg pictures
 * @author wf
 *
 */
public class MJpegHelper {
	/**
	 * rotate an Image 90 Degrees to the Right
	 * 
	 * @param inputImage
	 * @return the rotated  image
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
	 * @param inputImage
	 * @return the rotated image
	 */
	public static BufferedImage rotate180(BufferedImage inputImage) {
		if (inputImage==null)
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
	public static BufferedImage getRotatedImage(BufferedImage inputImage, int rotation) {
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
	 * @param jpgIn
	 * @return the image
	 * @throws IOException
	 */
	public static BufferedImage getImage(InputStream jpgIn) throws IOException {
		BufferedImage bufImg = ImageIO.read(jpgIn);
		jpgIn.close();
		return bufImg;
	}
}
