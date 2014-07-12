/**
 * @author Carlos Antonio
 * @version 0.1.0
*/
package com.bridge;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.content.Context;

import com.facebook.crypto.Crypto;
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
        callbackContext.success(path);
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
