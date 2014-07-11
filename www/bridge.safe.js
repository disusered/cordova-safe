/**
 * @title Safe - cordova.plugins.bridge.safe
 * @overview Easy to use cryptographic operations for Cordova.
 * @copyright © 2014 cordova-bridge
 * @license GPLv2
 * @author Carlos Antonio
 * @version 0.1.0
*/

var exec = require('cordova/exec');

var safe = {
  /**
   * encrypt
   *
   * @param {String} path File URI
   * @param {String} password Password for encryption
   * @param {Function} success Success callback
   * @param {Function} error Failure callback
   */
  encrypt: function(path, password, success, error) {
    var encryptSuccess, encryptError;

    if (!path || arguments.length === 0) return;

    encryptSuccess = onSuccess.bind(null, path, success);
    encryptError = onError.bind(null, path, error);

    exec(encryptSuccess, encryptError, "Safe", "encrypt", [path]);
  },

  /**
   * decrypt
   *
   * @param {String} path File URI
   * @param {String} password Password for decryption
   * @param {Function} success Success callback
   * @param {Function} error Failure callback
   */
  decrypt: function(path, password, success, error) {
    var decryptSuccess, decryptError;

    if (!path || arguments.length === 0) return;

    decryptSuccess = onSuccess.bind(null, path, success);
    decryptError   = onError.bind(null, path, error);

    exec(decryptSuccess, decryptError, "Safe", "decrypt", [path]);
  }

};

function onSuccess(path, success) {
  if (typeof success === 'function') success(path);
  return path;
}

function onError(code, error) {
  var result = code || 0;
  if (typeof error === 'function') error(result);
  return result;
}

exports.safe = safe;
