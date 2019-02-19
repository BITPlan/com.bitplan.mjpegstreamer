package com.bitplan.mjpegstreamer;

import java.io.File;
import java.util.logging.Level;

import com.bitplan.error.SoftwareVersion;
import com.bitplan.gui.App;
import com.bitplan.javafx.GenericApp;
import com.bitplan.javafx.GenericDialog;
import com.bitplan.javafx.TaskLaunch;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * JavaFX application
 * 
 * @author wf
 *
 */
public class MJpegApp extends GenericApp {
  public static final String RESOURCE_PATH = "com/bitplan/mjpeg/gui";
  public static final String MJPEG_APP_PATH = RESOURCE_PATH + "/mjpeg.json";
  private static MJpegApp instance;
  private ViewPanel viewPanel;

  public ViewPanel getViewPanel() {
    return viewPanel;
  }

  public void setViewPanel(ViewPanel viewPanel) {
    this.viewPanel = viewPanel;
  }

  /**
   * get the title
   * 
   * @return the title including the version
   */
  String getTitle() {
    return this.softwareVersion.getName() + " "
        + this.softwareVersion.getVersion();
  }

  /**
   * construct this app
   * 
   * @param app
   * @param softwareVersion
   * @param resourcePath
   */
  public MJpegApp(App app, SoftwareVersion softwareVersion,
      String resourcePath) {
    super(app, softwareVersion, resourcePath);
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    stage.setTitle(getTitle());
    VBox vbox = new VBox();
    setRoot(vbox);
    double sWidth = super.getScreenWidth();
    double sHeight = super.getScreenHeight();
    double heightAdjust = xyTabPane.getTabSize() + 29;
    // steps of 25 for size for an expected 900x900 image
    double height = Math.floor((sHeight - heightAdjust) / 25) * 25;
    double width = height;
    Rectangle2D sceneBounds = new Rectangle2D((sWidth - width) / 2,
        (sHeight - height) / 2, width, height);

    setScene(
        new Scene(getRoot(), sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    stage.setX(sceneBounds.getMinX());
    stage.setY(sceneBounds.getMinY());
    // create a Menu Bar and show it
    setMenuBar(createMenuBar(getScene(), app));
    showMenuBar(getScene(), getMenuBar(), true);

    // add the XY TabPane and set it's growing
    setupXyTabPane();
    // setup the forms
    setup(app);
    setupContent();
    stage.sizeToScene();
    stage.show();
  }

  /**
   * setup the Content of the xyTabPanes
   */
  private void setupContent() {
    Tab videoTab = xyTabPane.getTab(MJpegI18n.VIDEO_FORM);
    final SwingNode swingNode = new SwingNode();
    swingNode.setContent(this.viewPanel);
    videoTab.setContent(swingNode);
  }

  @Override
  public void handle(ActionEvent event) {
    try {
      Object source = event.getSource();
      if (source instanceof MenuItem) {
        MenuItem menuItem = (MenuItem) source;
        switch (menuItem.getId()) {
        case MJpegI18n.FILE_MENU__QUIT_MENU_ITEM:
          close();
          break;
        case MJpegI18n.FILE_MENU__OPEN_MENU_ITEM:
          openFileDialog();
          break;
        case MJpegI18n.HELP_MENU__ABOUT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHome()));
          showAbout();
          break;
        case MJpegI18n.HELP_MENU__HELP_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getHelp()));
          break;
        case MJpegI18n.HELP_MENU__FEEDBACK_MENU_ITEM:
          GenericDialog.sendReport(softwareVersion,
              softwareVersion.getName() + " feedback", "...");
          break;
        case MJpegI18n.HELP_MENU__BUG_REPORT_MENU_ITEM:
          TaskLaunch.start(() -> showLink(app.getFeedback()));
          break;
        case MJpegI18n.PREVIEW_MENU__FILM_MENU_ITEM:
          this.selectTab(MJpegI18n.VIDEO_FORM);
          break;
        case MJpegI18n.PREVIEW_MENU__PICTURE_MENU_ITEM:
          this.selectTab(MJpegI18n.PICTURE_FORM);
          break;
        default:
          LOGGER.log(Level.WARNING, "unhandled menu item " + menuItem.getId()
              + ":" + menuItem.getText());
        }
      } else {
        LOGGER.log(Level.INFO,
            "event from " + source.getClass().getName() + " received");
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  /**
   * potentially open a new file with a dialog
   */
  private void openFileDialog() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open mjpeg file");
    fileChooser.getExtensionFilters().addAll(
        new ExtensionFilter("MJpeg Video Files", "*.mjpg"),
        new ExtensionFilter("All Files", "*.*"));
    File selectedFile = fileChooser.showOpenDialog(this.getStage());
    if (selectedFile != null) {
      Platform.runLater(() -> viewPanel.setUrl(selectedFile.toURI().toString()));
    }

  }

  /**
   * get an Instance of the Application
   * 
   * @param softwareVersion
   * @param debug
   * @return the instance
   * @throws Exception
   */
  public static MJpegApp getInstance(SoftwareVersion softwareVersion,
      boolean debug) throws Exception {
    if (instance == null) {
      App app = App.getInstance(MJPEG_APP_PATH);
      GenericApp.debug = debug;
      instance = new MJpegApp(app, softwareVersion, RESOURCE_PATH);
    }
    return instance;
  }

}
