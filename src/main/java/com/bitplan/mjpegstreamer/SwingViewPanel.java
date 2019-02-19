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

import java.awt.BorderLayout;
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

/**
 * View Panel
 * 
 * @author wf
 * 
 */
public class SwingViewPanel extends ViewPanelImpl implements ActionListener {

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
  private JPanel panel;


  public JPanel getPanel() {
    return panel;
  }

  public void setPanel(JPanel panel) {
    this.panel = panel;
  }

  /**
   * construct me
   */
  public SwingViewPanel() {
    setPanel(new JPanel());
  }
  
  /**
   * set the url to the given value
   * 
   * @param url
   */
  public void setUrl(String url) {
    this.urlArea.setText(url);
    this.url = url;
  }


  /**
   * set the Buffered Image
   * 
   * @param pImage
   */
  public void setBufferedImage(BufferedImage pImage) {
    image = pImage;
    imageIcon.setImage(pImage);
  }

  /**
   * set the Image and render it
   * 
   * @param pImage
   */

  public void renderNextImage(BufferedImage pImage) {
    setBufferedImage(pImage);
    getPanel().setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    getPanel().repaint();
  }

  /**
   * set the failed string
   * 
   * @param msg
   */
  public void showMessage(String msg) {
    this.msgArea.setText(msg);
    getPanel().repaint();
  }

  /**
   * add a Button
   * 
   * @param title
   * @param iconPath
   * @param mnemonic
   * @return the newly added button
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
   * setup the viewPanel
   * 
   * @param url
   * @throws Exception
   */
  public void setup(String url) throws Exception {
    setup();
    this.setUrl(url);
  }

  /**
   * setup the viewPanel
   * @throws Exception
   */
  public void setup() throws Exception {
    BufferedImage bg = getBufferedImage("/images/screen640x480.png");
    if (bg != null)
      setBufferedImage(bg);
    getPanel().setLayout(new BorderLayout());
    startButton = addButton("start", START_BUTTON_ICON_PATH, KeyEvent.VK_S);
    rotateButton = addButton("rotate", ROTATE_BUTTON_ICON_PATH, KeyEvent.VK_R);
    // https://www.iconfinder.com/icons/49386/settings_icon#size=48
    settingsButton = addButton("settings", SETTINGS_BUTTON_ICON_PATH,
        KeyEvent.VK_T);
    JLabel label = new JLabel("", imageIcon, JLabel.CENTER);

    this.bottomPanel = new JPanel(new BorderLayout());
    this.buttonPanel = new JPanel(new FlowLayout());
    this.urlPanel = new JPanel(new BorderLayout());
    this.urlArea = new JTextArea();
    this.urlPanel.add(urlArea, BorderLayout.WEST);
    this.buttonPanel.add(settingsButton);
    this.buttonPanel.add(rotateButton);
    this.buttonPanel.add(startButton);
    this.urlPanel.add(buttonPanel, BorderLayout.EAST);

    this.msgArea = new JTextArea();
    this.bottomPanel.add(msgArea, BorderLayout.NORTH);
    this.bottomPanel.add(urlPanel, BorderLayout.SOUTH);

    getPanel().add(label, BorderLayout.CENTER);
    getPanel().add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * create a Frame around me with the given title
   * 
   * @param title
   */
  public void createFrame(String title) {
    frame = new JFrame(title);

    frame.getContentPane().add("Center", getPanel());
    frame.setSize(640, 480);

    frame.pack(); // makes the frame shrink to minimum size
    frame.setLocation(100, 100);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }


  // http://forum.codecall.net/topic/69182-java-image-rotation/
  /**
   * react on actions e.g. a button hit
   */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("start".equals(cmd)) {
      url=this.urlArea.getText();
      startStreaming(url);
    } else if ("rotate".equals(cmd)) {
      int rotation = this.getViewerSetting().rotation;
      rotation += 90;
      if (rotation >= 360)
        rotation = 0;
      this.getViewerSetting().rotation = rotation;
      BufferedImage rotateButtonIcon;
      try {
        rotateButtonIcon = getBufferedImage(SwingViewPanel.ROTATE_BUTTON_ICON_PATH);
        rotateButtonIcon = runner.getRotatedImage(rotateButtonIcon, rotation);
        ImageIcon buttonIcon = new ImageIcon(rotateButtonIcon);
        rotateButton.setIcon(buttonIcon);
      } catch (Exception e1) {
        handle(e1);
      }
      getPanel().repaint();
    }
  }

  /**
   * close the frame
   */
  public void close() {
    frame.dispose();
  }

}
