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
 * This class echoes a string called from JavaScript.
 */
public class Safe extends CordovaPlugin {

  public static final String ENCRYPT_ACTION = "encrypt";
  public static final String DECRYPT_ACTION = "decrypt";

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
      throws JSONException {
    if (action.equals(ENCRYPT_ACTION)) {
      String path = args.getString(0);
      String pass = args.getString(1);
      this.encryptFile(path, pass, callbackContext);
      return true;
    }

    if (action.equals(DECRYPT_ACTION)) {
      String path = args.getString(0);
      String pass = args.getString(1);
      this.decryptFile(path, pass, callbackContext);
      return true;
    }

    return false;
  }

  private void encryptFile(String path, String password, CallbackContext callbackContext) {
    if (path != null && path.length() > 0 && password != null && password.length() > 0) {
      // get cordova context
      Context context = cordova.getActivity().getApplicationContext();

      // create entity based on pass
      Entity entity = new Entity(password);

      // get uri of source file based on path
      Uri uri = Uri.parse(path);

      // get source file based on uri
      File file = new File(uri.getPath());

      // get name of source file based on uri
      String fileName = uri.getLastPathSegment();

      // create temp file for writing encrypted output stream
      File tmpFile = null;
      try {
        tmpFile = File.createTempFile(fileName, null, context.getCacheDir());
      } catch (IOException e) {
        callbackContext.error(0);
        e.printStackTrace();
      }

      // create crypto object with defaults
      Crypto crypto =
          new Crypto(new SharedPrefsBackedKeyChain(context), new SystemNativeCryptoLibrary());

      // check for whether crypto is available
      if (!crypto.isAvailable()) {
        callbackContext.error(1);
        return;
      }

      // create output stream to temp file
      OutputStream outputStream = null;
      try {
        outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
      } catch (FileNotFoundException e) {
        callbackContext.error(4);
        e.printStackTrace();
      }

      // create output stream which encrypts the data as
      // it is written to it and writes out to the file
      try {
        OutputStream encryptedOutputStream = crypto.getCipherOutputStream(outputStream, entity);

        // create input stream from source file
        InputStream inputStream = new FileInputStream(file);

        // create new byte object with source file length
        byte[] data = new byte[(int) file.length()];

        // read contents of source file byte by byte
        int buffer = 0;
        while ((buffer = inputStream.read(data)) > 0) {
          // write contents to encrypted output stream
          encryptedOutputStream.write(data, 0, buffer);
          encryptedOutputStream.flush();
        }

        // close encrypted output stream
        encryptedOutputStream.close();
        inputStream.close();

        // delete original file
        boolean deleted = file.delete();
        if (deleted) {
          callbackContext.success(tmpFile.getPath());
        } else {
          callbackContext.error(1);
        }
      } catch (IOException e) {
        callbackContext.error(e.getMessage());
      } catch (CryptoInitializationException e) {
        callbackContext.error(e.getMessage());
      } catch (KeyChainException e) {
        callbackContext.error(e.getMessage());
      } catch (Exception e) {
        callbackContext.error(e.getMessage());
      }
    } else {
      callbackContext.error(2);
    }
  }

  private void decryptFile(String path, String password, CallbackContext callbackContext) {
    if (path != null && path.length() > 0) {
      callbackContext.success(path);
    } else {
      callbackContext.error(2);
    }
  }
}
