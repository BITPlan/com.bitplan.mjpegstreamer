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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Encryption class to be use for password encryption for test cases
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class Crypt {

  private char[] cypher;
  byte[] salt;

  /**
   * @return the cypher
   */
  public String getCypher() {
    return new String(cypher);
  }
  
  /**
   * return the salt
   * @return the salt
   */
  public String getSalt() {
    return new String(salt);
  }

  /**
   * @param cypher the cypher to set
   */
  public void setCypher(char[] cypher) {
    this.cypher = cypher;
  }

  /**
   * create me from a password and salt
   * 
   * @param pCypher
   * @param pSalt
   */
  Crypt(String pCypher, String pSalt) {
    this.setCypher(pCypher.toCharArray());
    this.salt = pSalt.getBytes();
  }

  /**
   * generate a Random key
   * @param pLength
   * @return the reandom key with the given length
   */
  public static String generateRandomKey(int pLength) {
    int asciiFirst = 48;
    int asciiLast = 122;
    Integer[] exceptions = { 58,59,60,61,62,63,91,92,93,94,96 };

    List<Integer> exceptionsList = Arrays.asList(exceptions);
    SecureRandom random = new SecureRandom();
    StringBuilder builder = new StringBuilder();
    for (int i=0; i<pLength; i++) {
        int charIndex;
        do {
            charIndex = random.nextInt(asciiLast - asciiFirst + 1) + asciiFirst;
        }
        while (exceptionsList.contains(charIndex));

        builder.append((char) charIndex);
    }

    return builder.toString();
}

  /**
   * get a random Crypt
   * @return a new crypt with a 32 byte random key and 8byte salt 
   */
  public static Crypt getRandomCrypt() {
    String lCypher=generateRandomKey(32);
    String lSalt=generateRandomKey(8);
    Crypt result=new Crypt(lCypher,lSalt);
    return result;
  }

  /**
   * test this
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Crypt pcf=getRandomCrypt();
    String originalPassword = "secretPassword";
    System.out.println("Original password: " + originalPassword);
    String encryptedPassword = pcf.encrypt(originalPassword);
    System.out.println("Encrypted password: " + encryptedPassword);
    String decryptedPassword = pcf.decrypt(encryptedPassword);
    System.out.println("Decrypted password: " + decryptedPassword);
  }

  /**
   * encrypt the given property
   * @param property
   * @return
   * @throws GeneralSecurityException
   * @throws UnsupportedEncodingException
   */
  String encrypt(String property) throws GeneralSecurityException,
      UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory
        .getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cypher));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));
    return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
  }

  private static String base64Encode(byte[] bytes) {
    return new BASE64Encoder().encode(bytes);
  }

  String decrypt(String property) throws GeneralSecurityException,
      IOException {
    SecretKeyFactory keyFactory = SecretKeyFactory
        .getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cypher));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
  }

  private static byte[] base64Decode(String property) throws IOException {
    return new BASE64Decoder().decodeBuffer(property);
  }

}