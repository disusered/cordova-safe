/**
 * Safe.java
 *
 * Copyright (C) 2014 Carlos Antonio
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 */

package com.bridge;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.content.Context;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

/**
 * This class encrypts and decrypts files using the Conceal encryption lib
 */
public class Safe extends CordovaPlugin {

  public static final String ENCRYPT_ACTION = "encrypt";
  public static final String DECRYPT_ACTION = "decrypt";

  private Context CONTEXT;
  private Crypto CRYPTO;
  private Entity ENTITY;

  private OutputStream OUTPUT_STREAM;
  private InputStream INPUT_STREAM;

  private String FILE_NAME;
  private File SOURCE_FILE;
  private File TEMP_FILE;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
      throws JSONException {
    if (action.equals(ENCRYPT_ACTION) || action.equals(DECRYPT_ACTION)) {
      String path = args.getString(0);
      String pass = args.getString(1);

      this.cryptOp(path, pass, action, callbackContext);

      return true;
    }

    return false;
  }

  private void cryptOp(String path, String password, String action, CallbackContext callbackContext) {
    // init crypto variables
    this.initCrypto(path, password, callbackContext);

    // create output stream which encrypts the data as
    // it is written to it and writes out to the file
    try {
      if (action.equals(ENCRYPT_ACTION)) {
        // create encrypted output stream
        OutputStream encryptedOutputStream = CRYPTO.getCipherOutputStream(OUTPUT_STREAM, ENTITY);
        // write to temp file
        this.writeFile(encryptedOutputStream, callbackContext);
      } else if (action.equals(DECRYPT_ACTION)) {
        InputStream decryptedInputStream = CRYPTO.getCipherInputStream(INPUT_STREAM, ENTITY);
      }
      callbackContext.success(TEMP_FILE.getPath());
    } catch (IOException e) {
      callbackContext.error(e.getMessage());
    } catch (CryptoInitializationException e) {
      callbackContext.error(e.getMessage());
    } catch (KeyChainException e) {
      callbackContext.error(e.getMessage());
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
  }

  private void initCrypto(String path, String password, CallbackContext callbackContext) {
    if (path != null && path.length() > 0 && password != null && password.length() > 0) {
      Uri uri = Uri.parse(path);

      CONTEXT = cordova.getActivity().getApplicationContext();
      ENTITY = new Entity(password);

      FILE_NAME = uri.getLastPathSegment();
      SOURCE_FILE = new File(uri.getPath());

      // initialize crypto object
      CRYPTO = new Crypto(new SharedPrefsBackedKeyChain(CONTEXT), new SystemNativeCryptoLibrary());

      // check for whether crypto is available
      if (!CRYPTO.isAvailable()) {
        callbackContext.error(1);
        return;
      }

      try {
        // initialize temp file
        TEMP_FILE = File.createTempFile(FILE_NAME, null, CONTEXT.getCacheDir());
        // initialize output stream for temp file
        OUTPUT_STREAM = new BufferedOutputStream(new FileOutputStream(TEMP_FILE));
        // create input stream from source file
        INPUT_STREAM = new FileInputStream(SOURCE_FILE);
      } catch (FileNotFoundException e) {
        callbackContext.error(e.getMessage());
        e.printStackTrace();
      } catch (IOException e) {
        callbackContext.error(e.getMessage());
        e.printStackTrace();
      }
    } else {
      callbackContext.error(2);
    }
  }

  private void writeFile(OutputStream outputStream, CallbackContext callbackContext) {
    try {
      // create new byte object with source file length
      byte[] data = new byte[(int) SOURCE_FILE.length()];

      // read contents of source file byte by byte
      int buffer = 0;
      while ((buffer = INPUT_STREAM.read(data)) > 0) {
        // write contents to encrypted output stream
        outputStream.write(data, 0, buffer);
        outputStream.flush();
      }

      // close output stream
      outputStream.close();
      INPUT_STREAM.close();
    } catch (IOException e) {
      callbackContext.error(e.getMessage());
      e.printStackTrace();
    }
  }
}
