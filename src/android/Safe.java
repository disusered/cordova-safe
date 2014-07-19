/**
 * Safe.java
 * 
 * Copyright (C) 2014 Carlos Antonio
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
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
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      if (action.equals(ENCRYPT_ACTION)) {
        String path = args.getString(0);
        this.encryptFile(path, callbackContext);
        return true;
      }

      if (action.equals(DECRYPT_ACTION)) {
        String path = args.getString(0);
        this.decryptFile(path, callbackContext);
        return true;
      }

      return false;
    }

    private void encryptFile(String path, CallbackContext callbackContext) {
      if (path != null && path.length() > 0) {
        Entity entity = null;
        Context context = cordova.getActivity().getApplicationContext();

        Uri uri = Uri.parse(path);
        String fileName = uri.getLastPathSegment();

        File file = new File(uri.getPath());
        File tmpFile = null;
        try {
          tmpFile = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
          callbackContext.error(0);
          e.printStackTrace();
        }

        OutputStream fileStream = null;
        try {
          fileStream = new BufferedOutputStream(
              new FileOutputStream(tmpFile));
        } catch (FileNotFoundException e) {
          callbackContext.error(4);
          e.printStackTrace();
        }

        Crypto crypto = new Crypto(
            new SharedPrefsBackedKeyChain(context),
            new SystemNativeCryptoLibrary());

        if (!crypto.isAvailable()) {
          callbackContext.error(1);
          return;
        }

        try {
          OutputStream outputStream = crypto.getCipherOutputStream(
            fileStream,
            entity);

          // TODO: Write contents of file to encryption stream
          // outputStream.write(contents);
          outputStream.close();

          callbackContext.success(path);
        } catch (IOException e) {
          callbackContext.error(0);
          e.printStackTrace();
        } catch (CryptoInitializationException e) {
          callbackContext.error(1);
          e.printStackTrace();
        } catch (KeyChainException e) {
          callbackContext.error(3);
          e.printStackTrace();
        }
      } else {
        callbackContext.error(2);
      }
    }

    private void decryptFile(String path, CallbackContext callbackContext) {
      if (path != null && path.length() > 0) {
        callbackContext.success(path);
      } else {
        callbackContext.error(2);
      }
    }
}
