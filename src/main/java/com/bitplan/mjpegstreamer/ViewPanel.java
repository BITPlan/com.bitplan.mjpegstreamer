package com.bitplan.mjpegstreamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
public class ViewPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7976060967404295181L;
	private BufferedImage image;
	protected JTextArea urlArea;
	private String url;
	private JTextArea msgArea;
	private JPanel urlPanel;
	private JPanel bottomPanel;
	private ImageIcon imageIcon = new ImageIcon();

	/**
	 * set the Image
	 * 
	 * @param pImage
	 */
	public void setBufferedImage(BufferedImage pImage) {
		image = pImage;
		imageIcon.setImage(pImage);
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * set the failed string
	 * 
	 * @param msg
	 */
	public void setFailedString(String msg) {
		this.msgArea.setText(msg);
	}

	protected static JFrame frame;

	/**
	 * get the given buffered Image
	 * 
	 * @param resourcePath
	 *            - path in class path /jar
	 * @return the image
	 * @throws IOException
	 */
	public BufferedImage getBufferedImage(String resourcePath)
			throws IOException {
		BufferedImage result = ImageIO.read(getClass()
				.getResource(resourcePath));
		return result;
	}

	/**
	 * setup the ViewPanel
	 * 
	 * @param title
	 * @param url
	 * @param autoStart
	 *            -if true start the streaming
	 * @throws IOException
	 */
	public void setup(String title, String url, boolean autoStart)
			throws IOException {
		BufferedImage bg = getBufferedImage("/images/screen640x480.png");
		setBufferedImage(bg);
		this.setLayout(new BorderLayout());
		ImageIcon startIcon = new ImageIcon(
				getBufferedImage("/images/start.png"));
		JButton startButton = new JButton("start", startIcon);
		startButton.setMnemonic(KeyEvent.VK_S);
		startButton.addActionListener(this);

		JLabel label = new JLabel("", imageIcon, JLabel.CENTER);

		this.bottomPanel = new JPanel(new BorderLayout());
		this.urlPanel = new JPanel(new BorderLayout());
		this.urlArea = new JTextArea();
		this.urlArea.setText(url);
		this.url = url;
		this.urlPanel.add(urlArea, BorderLayout.WEST);
		this.urlPanel.add(startButton, BorderLayout.EAST);

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

	MJpegRunner runner;

	/**
	 * start the streaming
	 */
	public void startStreaming() {
		url = this.urlArea.getText();
		if (runner != null) {
			runner.stop();
		}
		try {
			runner = new MJpegRunner1();
			//runner = new MJpegRunner2();
			runner.init(this,url,null,null);
			runner.start();
		} catch (Exception ex) {
			handle(ex);
		}
	}

	/**
	 * start Button hit
	 */
	public void actionPerformed(ActionEvent e) {
		if ("start".equals(e.getActionCommand())) {
			startStreaming();
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
