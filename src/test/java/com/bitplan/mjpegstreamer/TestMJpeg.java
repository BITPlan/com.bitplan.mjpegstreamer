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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * test MJPeg API
 * 
 * @author wf
 *
 */
public class TestMJpeg {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.mjpegstreamer");

  @Test
  public void testFrameCount() throws Exception {
    File bigFile=new File("/Volumes/Timelapse/timelapse.mpeg");
    List<String> urls=new ArrayList<String>();
    urls.add(ClassLoader.getSystemResource("testmovie/movie.mjpg")
            .toExternalForm());
    if (bigFile.exists()) {
      urls.add(bigFile.toURI().toString());
    }
    long[] expected= {51,360607}; 
    int index=0;
    for (String url : urls) {
      MJPegRandomAccessFile mjpeg=new MJPegRandomAccessFile(url);
      Stats stats = mjpeg.getStats(1024*1024*10);
      LOGGER.log(Level.INFO,String.format("%s = %d frames %s", url,stats.count,stats.guessed?"(guessed)":""));
      assertEquals(expected[index++],stats.count);
    }
  }

}
