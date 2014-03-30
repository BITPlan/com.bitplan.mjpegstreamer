package com.bitplan.mjpegstreamer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * a listener that will draw rectangles on the image
 * 
 * @author wf
 * 
 */
public class RectangleOverlay implements ImageListener {
	private static Logger LOGGER=Logger
			.getLogger("com.bitplan.mjpegstreamer");
	
	static long counter;
	private Color color;
	int dwidth;
	int dheight;
	int x;
	int y;

	private boolean debug=false;

	/**
	 * create a RectangleOverlay based on the given parameters
	 * 
	 * @param x
	 * @param y
	 * @param dwidth
	 * @param dheight
	 * @param color
	 */
	public RectangleOverlay(int x, int y, int dwidth, int dheight, Color color) {
		this.color = color;
		this.x = x;
		this.y = y;
		this.dwidth = dwidth;
		this.dheight = dheight;
	}

	@Override
	public void onRead(Object context, BufferedImage image) {
		Graphics2D g2d = image.createGraphics();

		// Draw on the buffered image
		g2d.setColor(color);
		//g2d.drawRect(x, y, image.getWidth()-dwidth, image.getHeight()-dheight);
		int width=image.getWidth()-dwidth-x;
		int height=image.getHeight()-dheight-y;
		if (debug)
			LOGGER.log(Level.INFO,""+x+","+y+" "+width+"x"+height);
		// http://stackoverflow.com/questions/19803276/rectangle-not-drawing-on-bufferedimage
		g2d.draw(new Rectangle2D.Double(x,y,width,height));
		g2d.dispose();
		counter++;
	}

	@Override
	public boolean isPostListener() {
		return false;
	}

}
