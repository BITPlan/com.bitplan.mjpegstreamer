package com.bitplan.mjpegstreamer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
// JDK 8
// import java.util.Base64;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;

/**
 * base class for MJPegRunners
 * 
 * @author muf
 * 
 */
public abstract class MJpegRunnerBase implements MJpegReaderRunner {
	protected MJpegRenderer viewer;
	protected String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream inputStream;

	protected URL url;
	protected byte[] curFrame;
	protected int frameCount;
	private Thread streamReader;
	protected URLConnection conn;
	// constants
	public boolean debug = true;
	private int rotation=0;
	
	/**
	 * @return the rotation
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public static int READ_TIME_OUT = 4000;
	public static int INPUT_BUFFER_SIZE = 8192;

	/**
	 * get a Base64 Encoder
	 * @return
	 */
	public Base64 getEncoder() {
		// JDK 8
		// Base64.Encoder base64 = Base64.getEncoder();
		// Apache Commons codec
		Base64 base64=new Base64();
		return base64;
	}
	
	/**
	 * open the connection
	 * @return
	 */
	public BufferedInputStream openConnection() {
		BufferedInputStream result = null;
		try {
			url = new URL(urlString);
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64 base64=getEncoder();
				byte[] encoded_credentials = base64.encode(credentials.getBytes());
				conn.setRequestProperty("Authorization", "Basic " + encoded_credentials);
			}
			// change the timeout to taste, I like 1 second
			conn.setReadTimeout(READ_TIME_OUT);
			conn.connect();
			result = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		} catch (MalformedURLException e) {
			handle("Invalid URL", e);
		} catch (IOException ioe) {
			handle("Unable to connect: ", ioe);
		}
		return result;
	}

	/**
	 * connect
	 */
	public void connect() {
		// if inputStream has been set - keep it!
		if (inputStream!=null)
			return;
		if ("-".equals(urlString))
			inputStream=new BufferedInputStream(System.in,INPUT_BUFFER_SIZE);
		else
			inputStream=openConnection();
	}

	/**
	 * start reading
	 */
	public void start() {
		this.streamReader = new Thread(this, "Stream reader");
		streamReader.start();
		viewer.init();
	}

	/**
	 * is there a new frame?
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		return frameAvailable;
	}

	/**
	 * handle the given exception with the given title
	 * 
	 * @param title
	 * @param e
	 */
	public void handle(String title, Exception e) {
		String msg = title + e.getMessage();
		if (debug)
			System.err.println(msg);
		viewer.setFailedString(msg);
	}
	/**
	 * rotate an Image 90 Degrees to the Right
	 * 
	 * @param inputImage
	 * @return
	 */
	public BufferedImage rotate90ToRight(BufferedImage inputImage) {
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
	 * @return
	 */
	public BufferedImage rotate90ToLeft(BufferedImage inputImage) {
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
	 * @return
	 */
	public BufferedImage rotate180(BufferedImage inputImage) {
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
	 * @return
	 */
	public BufferedImage getRotatedImage(BufferedImage inputImage, int rotation) {
		BufferedImage result = inputImage;
		switch (rotation) {
		case 90:
			result = this.rotate90ToRight(result);
			break;
		case 180:	
			result = this.rotate180(result);
			break;
		case 270:
			result = this.rotate90ToLeft(result);
			break;
		}
		return result;
	}

	/**
	 * read
	 */
	public void read() {
		try {
			ByteArrayInputStream jpgIn = new ByteArrayInputStream(curFrame);
			BufferedImage bufImg = ImageIO.read(jpgIn);
			jpgIn.close();
			
			frameCount++;
			frameAvailable = false;
			// debug repaint
			// image= viewer.getBufferedImage("/images/start.png");
			BufferedImage rotatedImage = this.getRotatedImage(bufImg, rotation);
			viewer.renderNextImage(rotatedImage);
			// viewer.repaint();
			if (debug)
				viewer.setFailedString("debug:frame=" + frameCount);

		} catch (IOException e) {
			handle("Error acquiring the frame: ", e);
		}
	}

	/**
	 * when disposing stop
	 */
	public void dispose() {
		stop();
	}
}
