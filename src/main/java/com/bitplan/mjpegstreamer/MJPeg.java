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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MJPeg handler class
 * 
 * @author wf
 *
 */
public class MJPeg {
  public boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.mjpegstreamer");
  private File file;
  private RandomAccessFile raf;
  private byte[] buffer;
  public static int BUFFER_SIZE = 512 * 1024; // read a decent block of data at
                                              // a
                                              // time

  /**
   * construct me from the given file
   * 
   * @param file
   */
  public MJPeg(File file) {
    init(file);
  }

  /**
   * construct me from an url
   * 
   * @param url
   * @throws URISyntaxException
   */
  public MJPeg(String url) throws URISyntaxException {
    URI uri = new URI(url);
    init(new File(uri));
  }

  public void init(File file) {
    this.file = file;
    buffer = new byte[BUFFER_SIZE];
  }

  /**
   * open
   * 
   * @throws FileNotFoundException
   */
  public void open() throws FileNotFoundException {
    raf = new RandomAccessFile(file, "r");
  }

  /**
   * close me
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    raf.close();
  }

  /**
   * a JPeg fragment
   * 
   * @author wf
   *
   */
  public class JPeg {
    long offset;
    long length;

    /**
     * a JPeg Image within the file
     * 
     * @param offset
     */
    public JPeg(long offset) {
      super();
      this.offset = offset;
    }

  }

  public class Stats {
    long len;
    long min = Long.MAX_VALUE;
    long max = 0;
    long sum = 0;
    long count = 0;
    boolean guessed=false;

    public Stats(long len) {
      this.len = len;
    }

    public void add(long value) {
      sum += value;
      count++;
      if (value > max)
        max = value;
      if (value < min)
        min = value;
    }

    public void add(JPeg jpeg, long offset) {
      if (jpeg != null) {
        jpeg.length = offset - jpeg.offset;
        add(jpeg.length);
      }
    }

    public double getAverage() {
      if (count == 0)
        return 0.0;
      return sum / count;
    }

    /**
     * potentially extrapolate (guess)
     * 
     * @param guessFromSize
     * @param bytesRead
     */
    public void extrapolate(long guessFromSize, long bytesRead) {
      if (bytesRead > guessFromSize) {
        long avg = (long) getAverage();
        count = len / avg;
        sum = avg * count;
        guessed=true;
      }
    }
  }

  /**
   * get the statistics for this mjpeg file
   * 
   * @guessFromSize - the maximum size to read - after that guess based on the
   *                current average
   * @return the statistics
   * @throws Exception
   */
  public Stats getStats(long guessFromSize) throws Exception {
    open();
    Stats stats = new Stats(raf.length());
    if (debug)
      LOGGER.log(Level.INFO,
          String.format("reading %d MB", stats.len / 1024 / 1024));
    byte prev = 0;
    byte cur = 0;
    long bytesRead = 0;
    int readCount = 0;
    long offset = 0;

    int bi = 0; // bufferIndex
    JPeg jpeg = null;
    while (((readCount = raf.read(buffer)) > 0)
        && (bytesRead <= guessFromSize)) {
      bytesRead += readCount;
      if (debug) {
        LOGGER.log(Level.INFO, String.format("%d frames at %d MB %d",
            stats.count, bytesRead / 1024 / 1024, readCount));
        LOGGER.log(Level.INFO, String.format("min %d max %d avg %.0f",
            stats.min, stats.max, stats.getAverage()));
      }
      bi = 0;
      while (bi < buffer.length) {
        cur = buffer[bi++];
        offset++;
        if (prev == (byte) 0xFF && cur == (byte) 0xD8) {
          stats.add(jpeg, offset);
          jpeg = new JPeg(offset);
        }
        prev = cur;
      }
    }
    stats.add(jpeg, offset);
    close();
    stats.extrapolate(guessFromSize, bytesRead);
    return stats;
  }

}
