### com.bitplan.mjpegstreamer
[Java library for handing MJPEG streams - can e.g. display stream from a Webcam as a preview](http://www.bitplan.com/MJpegStreamer) 

[![Travis (.org)](https://img.shields.io/travis/BITPlan/com.bitplan.mjpegstreamer.svg)](https://travis-ci.org/BITPlan/com.bitplan.mjpegstreamer)
[![Maven Central](https://img.shields.io/maven-central/v/com.bitplan/com.bitplan.mjpegstreamer.svg)](https://search.maven.org/artifact/com.bitplan/com.bitplan.mjpegstreamer/0.0.7/jar)
[![GitHub issues](https://img.shields.io/github/issues/BITPlan/com.bitplan.mjpegstreamer.svg)](https://github.com/BITPlan/com.bitplan.mjpegstreamer/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/BITPlan/com.bitplan.mjpegstreamer.svg)](https://github.com/BITPlan/com.bitplan.mjpegstreamer/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/BITPlan/com.bitplan.mjpegstreamer.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

### Documentation
* [Wiki](http://www.bitplan.com/MJpegStreamer)
### Maven dependency

Maven dependency
```xml
<dependency>
  <groupId>com.bitplan</groupId>
  <artifactId>com.bitplan.mjpegstreamer</artifactId>
  <version>0.0.7</version>
</dependency>
```

[Current release at repo1.maven.org](http://repo1.maven.org/maven2/com/bitplan/com.bitplan.mjpegstreamer/0.0.7/)

### How to build
```
git clone https://github.com/BITPlan/com.bitplan.mjpegstreamer
cd com.bitplan.mjpegstreamer
mvn install
```
## Links
* https://en.wikipedia.org/wiki/Motion_JPEG

## Screenshot
![Screenshot](examples/Screenshot.png)
## Usage
```
java -jar mjpegstreamer.jar --help
Help
MJpegViewer Version: 0.0.6

 github: https://github.com/BITPlan/com.bitplan.mjpegstreamer

  usage: java com.bitplan.mjpegstreamer.MJpegViewer
 -ac (--autoclose)      : auto close
                          close when stream is finished
 -d (--debug)           : debug
                          adds debugging output
 -h (--help)            : help
                          show this usage
 -o (--overlay)         : adds a rectangle overlay
 -r (--rotation) N      : rotation e.g. 0/90/180/270 degrees
 -rto (--readtimeout) N : readtimeout in milliseconds
                          default is 5000 millisecs
 -s (--start)           : auto start
                          start streaming immediately
 -t (--title) VAL       : title
                          title to be used
 -u (--url) VAL         : url
                          url to be used
 -v (--version)         : showVersion
                          show current version if this switch is used
```
## Examples
java -jar mjpegstreamer.jar --url http://iris.not.iac.es/axis-cgi/mjpg/video.cgi?resolution=640x480 --start --title "NORDIC Telescope on Observatorio del Roque de Los Muchachos - La Palma"

![Screenshot](examples/nordictelescope.png)

java -jar mjpegstreamer.jar --url http://87.139.217.70/mjpg/video.mjpg?resolution=640x480 --start --title "Dollnstein/Germany"

![Screenshot](examples/dollnstein.png)

see http://www.insecam.org for more publicly accessible test cameras

### gphoto2 stream e.g. for Canon EOS Preview
gphoto2 --capture-movie --stdout | java -jar mjpegstreamer.jar -u - --start --title "Canon EOS preview"
![Screenshot](examples/canonpreview.png)


see preview script for more details on how to handle the release of the USB port which might be blocked by the PTPCamera process on MacOSX

## Installation
```
git clone https://github.com/BITPlan/com.bitplan.mjpegstreamer
mvn install
```
to use as a library 

```
mvn clean compile assembly:single
```
to create a stand alone jar

## Version history
* 0.0.1: 2013       Internal Project at BITPlan
* 0.0.3: 2016-08-25 Released as Open Source Project
* 0.0.6: 2018-08-03 version number upgrade
* 0.0.7: 2081-08-22 upgrades to Java 8 and uses com.bitplan.pom


