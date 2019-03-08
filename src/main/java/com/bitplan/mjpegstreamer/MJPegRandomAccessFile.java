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

/**
 * MJPeg handler class
 * 
 * @author wf
 *
 */
public class MJPegRandomAccessFile extends MJPegImpl implements MJPeg {
 
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
  public MJPegRandomAccessFile(File file) {
    init(file);
  }

  /**
   * construct me from an url
   * 
   * @param url
   * @throws URISyntaxException
   */
  public MJPegRandomAccessFile(String url) throws URISyntaxException {
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
   * get the statistics for this mjpeg file
   * 
   * @guessFromSize - the maximum size to read - after that guess based on the
   *                current average
   * @return the statistics
   * @throws Exception
   */
  public Stats getStats(long guessFromSize) throws Exception {
    open();
    stats.len=raf.length();
    if (debug)
      LOGGER.log(Level.INFO,
          String.format("reading %d MB", stats.len / 1024 / 1024));
    byte prev = 0;
    byte cur = 0;
    long bytesRead = 0;
    int readCount = 0;
    long offset = 0;
    int frameIndex=0;

    int bi = 0; // bufferIndex
    JPegImpl jpeg = null;
    while (((readCount = raf.read(buffer)) > 0)
        && (bytesRead <= guessFromSize)) {
      bytesRead += readCount;
      if (debug) {
        LOGGER.log(Level.INFO, String.format("%d frames at %d MB %d",
            stats.count, bytesRead / 1024 / 1024, readCount));
        LOGGER.log(Level.INFO, stats.toString());
      }
      bi = 0;
      while (bi < buffer.length) {
        cur = buffer[bi++];
        offset++;
        if (prev == (byte) 0xFF && cur == (byte) 0xD8) {
          add(jpeg, offset);
          jpeg = new JPegImpl(this,frameIndex++,offset);
        }
        prev = cur;
      }
    }
    add(jpeg, offset);
    close();
    stats.extrapolate(guessFromSize, bytesRead);
    return stats;
  }

}
