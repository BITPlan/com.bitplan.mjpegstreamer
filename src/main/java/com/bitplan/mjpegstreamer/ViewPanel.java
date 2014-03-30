package com.bitplan.mjpegstreamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

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
	private JButton startButton;
	private JButton rotateButton;
	private JPanel buttonPanel;
	private JButton settingsButton;
	private ViewerSetting viewerSetting=new ViewerSetting();

	/**
	 * @return the viewerSetting
	 */
	public ViewerSetting getViewerSetting() {
		return viewerSetting;
	}

	/**
	 * @param viewerSetting the viewerSetting to set
	 */
	public void setViewerSetting(ViewerSetting viewerSetting) {
		this.viewerSetting = viewerSetting;
	}

	/**
	 * set the Buffered Image
	 * @param pImage
	 */
	public void setBufferedImage(BufferedImage pImage) {
		image=pImage;
		imageIcon.setImage(pImage);
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
	public void showMessage(String msg) {
		this.msgArea.setText(msg);
		repaint();
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
			 java.net.URL imgURL = getClass().getResource(resourcePath);
		   if (imgURL != null) {
		  	 result = ImageIO.read(imgURL);
		   } else {
					throw new Exception("couldn't get buffered Image for '"+resourcePath+"' invalid resourcePath");		  	 
		   }
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
	 * @param overlay
	 * @param autoStart
	 *          -if true start the streaming
	 * @param readTimeOut
	 * @param rotation 
	 * @param overlay
	 * @param debug
	 * @param autoClose 
	 * @throws Exception TODO
	 */
	public void setup(String url, boolean overlay, boolean debug)
			throws Exception {
		if (overlay)
			viewerSetting.imageListener=new RectangleOverlay(50,50,50,50,Color.BLUE);
		if (debug)
			viewerSetting.debugMode=DebugMode.Verbose;
		BufferedImage bg = getBufferedImage("/images/screen640x480.png");
		if (bg!=null)
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
		createFrame(viewerSetting.title);
		if (viewerSetting.autoStart) {
			this.startStreaming();
		}
	}

	@Override
	public void init() {
		this.showMessage("started");
	}

	@Override
	public void stop(String msg) {
		this.showMessage("stopped:"+msg);
		if (viewerSetting.autoClose)
			this.close();
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	MJpegReaderRunner runner;

	/**
	 * start Streaming with the given runner
	 * @param runner
	 * @return
	 */
	public MJpegReaderRunner startStreaming(MJpegReaderRunner runner) {
		runner.start();
		return runner;		
	}
	
	/**
	 * start the streaming
	 * @return the runner
	 */
	public MJpegReaderRunner startStreaming() {
		url = this.urlArea.getText();
		if (runner != null) {
			runner.stop("stopping earlier runner");
		}
		try {
			//runner = new MJpegReaderRunner1();
			runner = new MJpegReaderRunner2();
			runner.init(url, null, null);
			runner.setViewer(this);
			this.startStreaming(runner);
		} catch (Exception ex) {
			handle(ex);
		}
		return runner;
	}

	// http://forum.codecall.net/topic/69182-java-image-rotation/

	

	/**
	 * start Button hit
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("start".equals(cmd)) {
			startStreaming();
		} else if ("rotate".equals(cmd)) {
			int rotation = this.getViewerSetting().rotation;
			rotation+=90;
			if (rotation >= 360)
				rotation = 0;
			this.getViewerSetting().rotation=rotation;
			BufferedImage rotateButtonIcon;
			try {
				rotateButtonIcon = getBufferedImage(ViewPanel.ROTATE_BUTTON_ICON_PATH);
				rotateButtonIcon = runner.getRotatedImage(rotateButtonIcon, rotation);
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
		this.showMessage(ex.getMessage());
	}

	/**
	 * close the frame
	 */
	public void close() {
		frame.dispose();
	}

}
