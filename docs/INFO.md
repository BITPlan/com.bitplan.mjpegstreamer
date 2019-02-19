## Links
* https://en.wikipedia.org/wiki/Motion_JPEG

## Screenshot
![Screenshot](examples/Screenshot.png)
## Usage
```
java -jar mjpeg.jar --help
Help
MJpegViewer Version: 0.0.8

 github: https://github.com/BITPlan/com.bitplan.mjpeg

  usage: java com.bitplan.mjpeg.MJpegViewer
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
java -jar mjpeg.jar --url http://iris.not.iac.es/axis-cgi/mjpg/video.cgi?resolution=640x480 --start --title "NORDIC Telescope on Observatorio del Roque de Los Muchachos - La Palma"

![Screenshot](examples/nordictelescope.png)

java -jar mjpeg.jar --url http://87.139.217.70/mjpg/video.mjpg?resolution=640x480 --start --title "Dollnstein/Germany"

![Screenshot](examples/dollnstein.png)

see http://www.insecam.org for more publicly accessible test cameras

### gphoto2 stream e.g. for Canon EOS Preview
gphoto2 --capture-movie --stdout | java -jar mjpeg.jar -u - --start --title "Canon EOS preview"
![Screenshot](examples/canonpreview.png)


see preview script for more details on how to handle the release of the USB port which might be blocked by the PTPCamera process on MacOSX

## Installation
```
git clone https://github.com/BITPlan/com.bitplan.mjpeg
mvn install
```
to use as a library

```
mvn clean compile assembly:single
```

or
```
./release
```

to create a stand alone jar
