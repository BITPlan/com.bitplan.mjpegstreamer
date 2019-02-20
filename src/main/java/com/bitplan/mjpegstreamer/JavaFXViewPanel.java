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
import java.io.InputStream;

import org.controlsfx.control.StatusBar;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Java FX Version of View panel
 * 
 * @author wf
 *
 */
public class JavaFXViewPanel extends ViewPanelImpl
    implements EventHandler<ActionEvent> {

  private ImageView imageView;
  private Pane pane;
  private ButtonBar buttonBar;
  private StatusBar statusBar;
  private Button startButton;
  private Button rotateButton;
  private Button settingsButton;
  private TextArea urlArea;
  private Slider slider;

  /**
   * construct me
   */
  public JavaFXViewPanel() {
  }

  /**
   * add a Button with the given title
   * 
   * @param title
   * @param iconPath
   * @param kc
   * @return
   */
  protected Button addButton(String title, String iconPath, KeyCode kk) {
    InputStream input = getClass().getResourceAsStream(iconPath);

    Image image = new Image(input);
    ImageView imageView = new ImageView(image);
    Button button = new Button(title, imageView);
    button.setOnAction(this);
    // TODO - fix later
    // KeyCombination kc = new KeyCodeCombination(kk,KeyCombination.ALT_ANY);
    // Mnemonic mn = new Mnemonic(button, kc);
    // scene.addMnemonic(mn);
    ButtonBar.setButtonData(button, ButtonData.LEFT);
    buttonBar.getButtons().add(button);
    return button;
  };

  public void initImage() throws Exception {
    imageView.setFitHeight(480);
    imageView.setFitWidth(640);
    imageView.setPreserveRatio(true);
    imageView.fitHeightProperty().bind(pane.heightProperty());
    imageView.fitWidthProperty().bind(pane.widthProperty());
    setEmptyImage();
    pane.requestLayout();
  }

  @Override
  public void setup() throws Exception {
    imageView = new ImageView();

    slider = new Slider();
    slider.setMin(0);
    slider.setMax(60);
    buttonBar = new ButtonBar();
    startButton = addButton("start", START_BUTTON_ICON_PATH, KeyCode.S);
    rotateButton = addButton("rotate", ROTATE_BUTTON_ICON_PATH, KeyCode.R);
    // https://www.iconfinder.com/icons/49386/settings_icon#size=48
    // @TODO potentially activate again
    //settingsButton = addButton("settings", SETTINGS_BUTTON_ICON_PATH,
    //    KeyCode.P);
    urlArea = new TextArea();
    statusBar = new StatusBar();

    pane = new VBox();
    pane.getChildren().add(imageView);
    pane.getChildren().add(slider);
    pane.getChildren().add(urlArea);
    pane.getChildren().add(statusBar);
    pane.getChildren().add(buttonBar);
    initImage();
  }

  @Override
  public void showMessage(String msg) {
    statusBar.setText(msg);
    pane.requestLayout();
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
    pane.requestLayout();
  }

  /**
   * set the Buffered Image
   * 
   * @param pImage
   */
  @Override
  public void setBufferedImage(BufferedImage pImage) {
    super.setBufferedImage(pImage);
    WritableImage writableImage = getImage(pImage);
    imageView.setImage(writableImage);
  }

  /**
   * get the image for the given Swing BufferedImage
   * 
   * @param pImage
   * @return the JavaFX writeAble Image
   */
  public WritableImage getImage(BufferedImage pImage) {
    final WritableImage writableImage = new WritableImage(pImage.getWidth(),
        pImage.getHeight());
    SwingFXUtils.toFXImage(pImage, writableImage);
    return writableImage;
  }

  /**
   * set the Image and render it
   * 
   * @param pImage
   */
  @Override
  public void renderNextImage(BufferedImage pImage) {
    setBufferedImage(pImage);
  }

  @Override
  public void close() {
    super.close();
  }

  @Override
  public void handle(ActionEvent event) {
    Object eventSource = event.getSource();
    if (eventSource instanceof Button) {
      Button eventButton = (Button) eventSource;
      if (eventButton.equals(startButton)) {
        String url = this.urlArea.getText();
        this.setUrl(url);
        startStreaming();
      } else if (eventButton.equals(rotateButton)) {
        BufferedImage rotateButtonIcon = rotate();
        if (rotateButtonIcon != null) {
          WritableImage writableImage = getImage(rotateButtonIcon);
          rotateButton.setGraphic(new ImageView(writableImage));
        }
      }
    }

  }

  @Override
  public Node getPanel() {
    return pane;
  }

  @Override
  public void showProgress(int framesReadCount, long bytesRead, int totalSize) {
    this.slider.setMax(totalSize);
    this.slider.setValue(bytesRead);
  }

}
