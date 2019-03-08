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

/**
 * MJPeg video interface
 * @author wf
 *
 */
public interface MJPeg {

  /**
   * get the statistics for this mjpeg file
   * 
   * @guessFromSize - the maximum size to read - after that guess based on the
   *                current average
   * @return the statistics
   * @throws Exception
   */
  public Stats getStats(long guessFromSize) throws Exception;
  
  public Stats getStats();
  
  /**
   * add the given jpeg
   * 
   * @param jpeg
   * @param offset
   */
  public void add(JPeg jpeg, long offset);

  /**
   * get the file the given JPeg should be store in
   * @param jPeg
   * @return
   */
  public File getImageFile(JPeg jPeg);

}
