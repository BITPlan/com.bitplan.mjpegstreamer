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
/**

 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.radolan
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
 *
 * Parts which are derived from https://gitlab.cs.fau.de/since/radolan are also
 * under MIT license.
 */
package com.bitplan.mjpegstreamer;

import org.junit.BeforeClass;

import com.bitplan.gui.App;

/**
 * Test the Internationalization
 * @author wf
 *
 */
public class TestI18n extends com.bitplan.i18n.TestI18n {

  @BeforeClass 
  public static void initShow()
  {
    TestI18n.show=true;
  }
  
  /**
   * configure the app
   */
  public App getApp() throws Exception {
    App app = App.getInstance(MJpegApp.MJPEG_APP_PATH);
    return app;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class getI18nClass() {
    return MJpegI18n.class;
  }

  @Override
  public String getI18nName() {
    return "mjpeg";
  }
  
}