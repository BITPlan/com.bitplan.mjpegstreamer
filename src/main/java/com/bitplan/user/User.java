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
package com.bitplan.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * access username and password in an encrypted fashion
 * 
 * @author wf
 *
 */
public class User {
  /**
   * Logging
   */
  protected static java.util.logging.Logger LOGGER = java.util.logging.Logger
      .getLogger("com.bitplan.user");

  String username;
  String password;
  Properties props;

  /**
   * get the property file for the given area
   * @param area
   * @param area
   * @return the property File
   */
  public static File getPropertyFile(String area) {
    // get username and userhome from Operating system
    String user = System.getProperty("user.name");
    String userPropertiesFileName=System.getProperty("user.home") + "/."
        + area + "/" + user + ".ini";
    File propFile = new File(userPropertiesFileName);
    return propFile;
  }
  
  /**
   * get the encrypted user information from the given ini area
   * 
   * @param area
   *          - the name of the ini area
   * @return the User with username and password decrypted from the ini file
   * @throws Exception
   */
  public static User getUser(String area) throws Exception {
    File propFile = getPropertyFile(area);
    User result = new User();
    result.props.load(new FileReader(propFile));
    result.props = new Properties();
    result.setUsername(result.props.getProperty("username"));
    Crypt pcf = new Crypt(result.props.getProperty("cypher"),
        result.props.getProperty("salt"));
    result.setPassword(pcf.decrypt(result.props.getProperty("secret")));
    return result;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *          the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *          the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  /**
   * @return the props
   */
  public Properties getProps() {
    return props;
  }

  /**
   * @param props the props to set
   */
  public void setProps(Properties props) {
    this.props = props;
  }

  /**
   * get input from standard in
   * @param name
   * @param br - the buffered reader to read from
   * @return the input returned
   * @throws IOException 
   */
  public static String getInput(String name, BufferedReader br) throws IOException {
    // prompt the user to enter the given name
    System.out.print("Please Enter " + name + ": ");

    String value = br.readLine();
    return value;
  }

  /**
   * create a credentials ini file from the command line
   * 
   * @param userid
   *          - the area to create the credentials for
   */
  public static void createIniFile(String area) {
    try {
      // open up standard input
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      if (area==null) {
        area=getInput("area",br);
      }
      String username = getInput("username",br);
      String password = getInput("password",br);
      File propFile=getPropertyFile(area);
      String remember= getInput("shall i store "+username+"'s credentials encrypted in "+propFile.getName()+" y/n?",br);
      if (remember.trim().toLowerCase().startsWith("y")) {
        Crypt lCrypt=Crypt.getRandomCrypt();
        Properties props = new Properties();
        props.setProperty("cypher", lCrypt.getCypher());
        props.setProperty("salt", lCrypt.getSalt());
        props.setProperty("user", username);
        props.setProperty("secret", lCrypt.encrypt(password));
        if (!propFile.getParentFile().exists()) {
          propFile.getParentFile().mkdirs();
        }
        FileOutputStream propsStream=new FileOutputStream(propFile);
        String title="Credentials for "+area;
        props.store(propsStream, title);
        propsStream.close();
        System.out.println("Storing "+title+" dne.");
      }
    } catch (IOException e1) {
      LOGGER.log(Level.SEVERE,e1.getMessage());
    } catch (GeneralSecurityException e1) {
      LOGGER.log(Level.SEVERE,e1.getMessage());
    }
  }

  /**
   * main program
   * 
   * @param args
   */
  public static void main(String args[]) {
    if (args.length == 0)
      createIniFile(null);
    else
      createIniFile(args[0]);
  }
}
