package com.disusered;

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
import org.apache.cordova.CordovaResourceApi;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.content.Context;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
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
  private Uri SOURCE_URI;
  private File SOURCE_FILE;
  private File TEMP_FILE;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
          throws JSONException {
    if (action.equals(ENCRYPT_ACTION) || action.equals(DECRYPT_ACTION)) {
      CordovaResourceApi resourceApi = webView.getResourceApi();

      String path = args.getString(0);
      String pass = args.getString(1);
      Uri normalizedPath = resourceApi.remapUri(Uri.parse(path));

      this.cryptOp(normalizedPath.toString(), pass, action, callbackContext);

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
        this.writeFile(INPUT_STREAM, encryptedOutputStream, callbackContext);
      } else if (action.equals(DECRYPT_ACTION)) {
        // create decrypted input stream
        InputStream decryptedInputStream = CRYPTO.getCipherInputStream(INPUT_STREAM, ENTITY);
        // write to temp file
        this.writeFile(decryptedInputStream, OUTPUT_STREAM, callbackContext);
      }

      // delete original file after write
      boolean deleted = SOURCE_FILE.delete();
      if (deleted) {
        File src = TEMP_FILE;
        File dst = new File(SOURCE_URI.getPath());

        this.copyFile(src, dst);

        callbackContext.success(dst.getPath());
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
  }

  private void initCrypto(String path, String password, CallbackContext callbackContext) {
    if (path != null && path.length() > 0 && password != null && password.length() > 0) {
      SOURCE_URI  = Uri.parse(path);
      FILE_NAME = SOURCE_URI.getLastPathSegment();

      CONTEXT = cordova.getActivity().getApplicationContext();
      ENTITY = new Entity(password);

      SOURCE_FILE = new File(SOURCE_URI.getPath());

      // initialize crypto object
      CRYPTO = new Crypto(new SharedPrefsBackedKeyChain(CONTEXT), new SystemNativeCryptoLibrary());

      // check for whether crypto is available
      if (!CRYPTO.isAvailable()) {
        callbackContext.error(1);
        return;
      }

      try {
        // initialize temp file
        TEMP_FILE = File.createTempFile(FILE_NAME, null, CONTEXT.getExternalCacheDir());
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

  private void writeFile(InputStream inputStream, OutputStream outputStream, CallbackContext callbackContext) {
    try {
      // create new byte object with source file length
      byte[] data = new byte[(int) SOURCE_FILE.length()];

      // read contents of source file byte by byte
      int buffer = 0;
      while ((buffer = inputStream.read(data)) > 0) {
        // write contents to encrypted output stream
        outputStream.write(data, 0, buffer);
        outputStream.flush();
      }

      // close output stream
      outputStream.close();
      inputStream.close();
    } catch (IOException e) {
      callbackContext.error(e.getMessage());
      e.printStackTrace();
    }
  }

  public void copyFile(File source, File dest) throws IOException {
      InputStream in = new FileInputStream(source);
      OutputStream out = new FileOutputStream(dest);

      // Transfer bytes from in to out
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
      }
      in.close();
      out.close();
  }
}
