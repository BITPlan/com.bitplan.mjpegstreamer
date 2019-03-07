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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.StatusBar;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;

import com.bitplan.javafx.ImageViewPane;
import com.bitplan.javafx.XYTabPane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
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
  protected Logger LOGGER=Logger.getLogger("com.bitplan.mjpegstreamer");
  public boolean debug=false;
  public static int ICON_SIZE=24;
  private ImageView imageView;
  private Pane pane;
  private ButtonBar buttonBar;
  private StatusBar statusBar;
  private Button startButton;
  private Button rotateButton;
  private Button settingsButton;
  private TextArea urlArea;
  private Slider slider;
  private Button pauseButton;
  private Button stopButton;

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
    Button button=addButton(title,imageView,kk);
    return button;
  }
  
  /**
   * add a button with the given title, glyph and keycode using the xyTabPane
   * @param title
   * @param glyph
   * @param kk
   * @return the Button
   */
  protected Button addButton(String title, Glyph glyph, KeyCode kk) {
    Button button=addButton(title,XYTabPane.getIcon(glyph.name(), ICON_SIZE),kk);
    return button;
  }
  
  /**
   * add a button with the given title icon and keycode
   * @param title
   * @param icon
   * @param kk
   * @return the button
   */
  private Button addButton(String title, Node icon, KeyCode kk) {
    Button button = new Button();
    button.setGraphic(icon);
    button.setTooltip(new Tooltip(title));
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
    // imageView.setFitHeight(480);
    // imageView.setFitWidth(640);
    imageView.setPreserveRatio(true);
    // imageView.fitHeightProperty().bind(pane.heightProperty());
    // imageView.fitWidthProperty().bind(pane.widthProperty());
    setEmptyImage();
    
  }
  
  public void refresh() {
    if (debug) {
      LOGGER.log(Level.INFO,"refreshing pane");
    }
    pane.requestLayout();
  }

  @Override
  public void setup() throws Exception {
    imageView = new ImageView();

    slider = new Slider();
    slider.setMin(0);
    slider.setMax(60);
    buttonBar = new ButtonBar();
    // see https://fontawesome.com/v4.7.0/icons/
    // for potential icons
    startButton = addButton("start", FontAwesome.Glyph.PLAY, KeyCode.PLAY);
    pauseButton = addButton("pause",FontAwesome.Glyph.PAUSE,KeyCode.PAUSE);
    stopButton = addButton("stop",FontAwesome.Glyph.STOP,KeyCode.STOP);
    rotateButton = addButton("rotate", ROTATE_BUTTON_ICON_PATH, KeyCode.R);
       // https://www.iconfinder.com/icons/49386/settings_icon#size=48
    // @TODO potentially activate again
    // settingsButton = addButton("settings", SETTINGS_BUTTON_ICON_PATH,
    // KeyCode.P);
    urlArea = new TextArea();
    statusBar = new StatusBar();

    Pane bottomPane = new VBox();
    bottomPane.getChildren().add(slider);
    bottomPane.getChildren().add(urlArea);
    bottomPane.getChildren().add(statusBar);
    bottomPane.getChildren().add(buttonBar);

    pane = new VBox();
    pane.getChildren().add(new ImageViewPane(imageView));
    pane.getChildren().add(bottomPane);

    initImage();
  }

  @Override
  public void showMessage(String msg) {
    Platform.runLater(() -> {
      statusBar.setText(msg);
      refresh();
    });
  }

  /**
   * set the url to the given value
   * 
   * @param url
   */
  @Override
  public void setUrl(String url) {
    if (debug) {
      LOGGER.log(Level.INFO,String.format("run Later to set url to %s", url));
    }   
    Platform.runLater(()-> {
      if (debug) {
        LOGGER.log(Level.INFO,String.format("setting url to %s", url));
      }   
      this.urlArea.setText(url);
      super.setUrl(url);
      refresh();
    });
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
    com.bitplan.javafx.SwingFXUtils.toFXImage(pImage, writableImage);
    return writableImage;
  }

  /**
   * set the Image and render it
   * 
   * @param pImage
   */
  @Override
  public void renderNextImage(BufferedImage pImage) {
    if (debug) {
      LOGGER.log(Level.INFO,"rendering next image");
    }
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
      } else if (eventButton.equals(stopButton)) {
        this.stop("stopped");
      } else if (eventButton.equals(pauseButton)) {
        this.stop("paused"); // FIXME implement Toggle     
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
    refresh();
  }

}
