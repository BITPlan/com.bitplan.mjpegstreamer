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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Statistics
 * @author wf
 *
 */
public class Stats {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.mjpegstreamer");
  
  long len;
  long min = Long.MAX_VALUE;
  long max = 0;
  long sum = 0;
  long count = 0;
  boolean guessed=false;

  /**
   * initialize me 
   */
  public Stats() {
  }

  /**
   * add the given value
   * @param value
   */
  public void add(long value) {
    sum += value;
    count++;
    if (value > max)
      max = value;
    if (value < min)
      min = value;
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
  
  public String toString() {
    String text=String.format("min %d max %d avg %.0f",
        min, max, getAverage());
    return text;
  }

  public void showDebug(int freq) {
   
    if (count % 100 == 0) {
      LOGGER.log(Level.INFO, toString());
    }
    
  }
}