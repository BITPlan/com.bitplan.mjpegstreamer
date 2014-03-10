package com.bitplan.mjpegstreamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * View Panel
 * 
 * @author wf
 * 
 */
public class ViewPanel extends JPanel implements ActionListener, MJpegRenderer {

	private static final long serialVersionUID = 7976060967404295181L;
	private static final String ROTATE_BUTTON_ICON_PATH = "/images/paper0r.png";
	private static final String START_BUTTON_ICON_PATH = "/images/start.png";
	// https://www.iconfinder.com/icons/49386/settings_icon#size=64
	private static final String SETTINGS_BUTTON_ICON_PATH = "/images/1394392895_settings.png";
	private String url;

	// GUI elements
	protected static JFrame frame;
	private BufferedImage image;
	protected JTextArea urlArea;
	private JTextArea msgArea;
	private JPanel urlPanel;
	private JPanel bottomPanel;
	private ImageIcon imageIcon = new ImageIcon();
	private boolean debug = false;
	private JButton startButton;
	private JButton rotateButton;
	private JPanel buttonPanel;
	private int rotation;
	private JButton settingsButton;

	/**
	 * set the Buffered Image
	 * @param pImage
	 */
	public void setBufferedImage(BufferedImage pImage) {
		image = this.getRotatedImage(pImage, rotation);
		imageIcon.setImage(image);
	}
	
	/**
	 * set the Image and render it
	 * 
	 * @param pImage
	 */

	public void renderNextImage(BufferedImage pImage) {
		setBufferedImage(pImage);
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));		
		repaint();
	}

	/**
	 * set the failed string
	 * 
	 * @param msg
	 */
	public void setFailedString(String msg) {
		this.msgArea.setText(msg);
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
	public BufferedImage getBufferedImage(String resourcePath) throws Exception  {
		BufferedImage result;
		try {
			result = ImageIO.read(getClass().getResource(resourcePath));
		} catch (Throwable th) {
			throw new Exception("couldn't get buffered Image for "+resourcePath,th);
		}
		return result;
	}

	/**
	 * add a Button
	 * 
	 * @param title
	 * @param iconPath
	 * @param mnemonic
	 * @return
	 * @throws Exception
	 */
	public JButton addButton(String title, String iconPath, int mnemonic)
			throws Exception {
		ImageIcon buttonIcon = new ImageIcon(getBufferedImage(iconPath));
		JButton result = new JButton(title, buttonIcon);
		result.setMnemonic(mnemonic);
		result.addActionListener(this);
		return result;
	}

	/**
	 * setup the ViewPanel
	 * 
	 * @param title
	 * @param url
	 * @param autoStart
	 *          -if true start the streaming
	 * @param debug
	 * @throws Exception TODO
	 */
	public void setup(String title, String url, boolean autoStart, boolean debug)
			throws Exception {
		this.debug = debug;
		BufferedImage bg = getBufferedImage("/images/screen640x480.png");
		setBufferedImage(bg);
		this.setLayout(new BorderLayout());
		startButton = addButton("start", START_BUTTON_ICON_PATH, KeyEvent.VK_S);
		rotateButton = addButton("rotate", ROTATE_BUTTON_ICON_PATH, KeyEvent.VK_R);
		// https://www.iconfinder.com/icons/49386/settings_icon#size=48
		settingsButton =addButton("settings", SETTINGS_BUTTON_ICON_PATH, KeyEvent.VK_T);
		JLabel label = new JLabel("", imageIcon, JLabel.CENTER);

		this.bottomPanel = new JPanel(new BorderLayout());
		this.buttonPanel = new JPanel(new FlowLayout());
		this.urlPanel = new JPanel(new BorderLayout());
		this.urlArea = new JTextArea();
		this.urlArea.setText(url);
		this.url = url;
		this.urlPanel.add(urlArea, BorderLayout.WEST);
		this.buttonPanel.add(settingsButton);
		this.buttonPanel.add(rotateButton);
		this.buttonPanel.add(startButton);
		this.urlPanel.add(buttonPanel, BorderLayout.EAST);

		this.msgArea = new JTextArea();
		this.bottomPanel.add(msgArea, BorderLayout.NORTH);
		this.bottomPanel.add(urlPanel, BorderLayout.SOUTH);

		add(label, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		createFrame(title);
		if (autoStart) {
			this.startStreaming();
		}
	}

	/**
	 * create a Frame around me with the given title
	 * 
	 * @param title
	 */
	public void createFrame(String title) {
		frame = new JFrame(title);

		frame.getContentPane().add("Center", this);
		frame.setSize(640, 480);

		frame.pack(); // makes the frame shrink to minimum size
		frame.setLocation(100, 100);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

	}

	MJpegReaderRunner runner;

	/**
	 * start the streaming
	 */
	public void startStreaming() {
		url = this.urlArea.getText();
		if (runner != null) {
			runner.stop();
		}
		try {
			// runner = new MJpegReaderRunner1();
			runner = new MJpegReaderRunner2();
			runner.init(this, url, null, null);
			runner.setDebug(debug);
			runner.start();
		} catch (Exception ex) {
			handle(ex);
		}
	}

	// http://forum.codecall.net/topic/69182-java-image-rotation/

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
		case 1:
			result = this.rotate90ToRight(result);
			break;
		case 2:
			result = this.rotate180(result);
			break;
		case 3:
			result = this.rotate90ToLeft(result);
			break;
		}
		return result;
	}

	/**
	 * start Button hit
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("start".equals(cmd)) {
			startStreaming();
		} else if ("rotate".equals(cmd)) {
			rotation++;
			if (rotation >= 4)
				rotation = 0;
			BufferedImage rotateButtonIcon;
			try {
				rotateButtonIcon = getBufferedImage(ViewPanel.ROTATE_BUTTON_ICON_PATH);
				rotateButtonIcon = this.getRotatedImage(rotateButtonIcon, rotation);
				ImageIcon buttonIcon = new ImageIcon(rotateButtonIcon);
				rotateButton.setIcon(buttonIcon);
			} catch (Exception e1) {
				handle(e1);
			}
			this.repaint();
		}
	}

	/**
	 * set Failed String
	 * 
	 * @param ex
	 */
	private void handle(Exception ex) {
		this.setFailedString(ex.getMessage());
	}

}
