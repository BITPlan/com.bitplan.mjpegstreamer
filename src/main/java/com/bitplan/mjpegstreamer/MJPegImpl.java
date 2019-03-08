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
import java.util.logging.Logger;

/**
 * MJPeg implementation
 * @author wf
 *
 */
public class MJPegImpl implements MJPeg {
  
  public boolean debug = false;
  protected Stats stats;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.mjpegstreamer");

  public MJPegImpl() {
    stats=new Stats();
  }
  
  /**
   * add the given jpeg
   * @param jpeg
   * @param offset
   */
  public void add(JPeg jpeg, long offset) {
    if (jpeg != null) {
      jpeg.setLength(offset - jpeg.getOffset());
      stats.add(jpeg.getLength());
    }
  }
  
  @Override
  public Stats getStats() {
    return stats;
  }

  @Override
  public File getImageFile(JPeg jPeg) {
    String filePath=String.format("/tmp/image%5d", jPeg.getFrameIndex());
    return new File(filePath);
  }

  @Override
  public Stats getStats(long guessFromSize) throws Exception {
    return getStats();
  }
}
