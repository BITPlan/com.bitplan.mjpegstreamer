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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import javafx.embed.swing.SwingNode;
import javafx.scene.Node;

/**
 * View Panel
 * 
 * @author wf
 * 
 */
public class SwingViewPanel extends ViewPanelImpl implements ActionListener {
  public static boolean debug = false;
  // GUI elements
  protected static JFrame frame;

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

  private SwingNode swingNode;

  @Override
  public Node getPanel() {
    return swingNode;
  }

  /**
   * construct me
   */
  public SwingViewPanel() {
    panel = new JPanel();
    swingNode = new SwingNode();
    swingNode.setContent(panel);
  }

  /**
   * set the url to the given value
   * 
   * @param url
   */
  @Override
  public void setUrl(String url) {
    this.urlArea.setText(url);
    super.setUrl(url);
  }

  /**
   * set the Buffered Image
   * 
   * @param pImage
   */
  @Override
  public void setBufferedImage(BufferedImage pImage) {
    super.setBufferedImage(pImage);
    imageIcon.setImage(pImage);
  }

  /**
   * set the Image and render it
   * 
   * @param pImage
   */
  @Override
  public void renderNextImage(JPeg jpeg) {
    super.setBufferedImage(jpeg.getImage());
    panel
        .setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    panel.repaint();
  }

  /**
   * set the failed string
   * 
   * @param msg
   */
  public void showMessage(String msg) {
    this.msgArea.setText(msg);
    panel.repaint();
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
   * @throws Exception
   */
  @Override
  public void setup() throws Exception {
    setEmptyImage();
    panel.setLayout(new BorderLayout());
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

    panel.add(label, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * create a Frame around me with the given title
   * 
   * @param title
   */
  public void createFrame(String title) {
    frame = new JFrame(title);

    frame.getContentPane().add("Center", panel);
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
      String url = this.urlArea.getText();
      this.setUrl(url);
      startStreaming();
    } else if ("rotate".equals(cmd)) {
      BufferedImage rotateButtonIcon = rotate();
      if (rotateButtonIcon != null) {
        ImageIcon buttonIcon = new ImageIcon(rotateButtonIcon);
        rotateButton.setIcon(buttonIcon);
      }
      panel.repaint();
    }
  }

  /**
   * close the frame
   */
  public void close() {
    frame.dispose();
    super.close();
  }

  @Override
  public void showProgress(MJPeg mjpeg) {
    if (debug) {
      mjpeg.getStats().showDebug(100);
    }
  }

}
