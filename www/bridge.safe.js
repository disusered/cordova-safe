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
   * @param {String} args File URI
   * @param {String} password Password for encryption
   * @param {Function} success Success callback
   * @param {Function} error Failure callback
   */
  encrypt: function(args, password, success, error) {
    if (!args || arguments.length === 0) return;
    exec(onSuccess, onError, "Safe", "encrypt", [args]);
  },

  /**
   * decrypt
   *
   * @param {String} args File URI
   * @param {String} password Password for decryption
   * @param {Function} success Success callback
   * @param {Function} error Failure callback
   */
  decrypt: function(args, password, success, error) {
    if (!args || arguments.length === 0) return;
    exec(onSuccess, onError, "Safe", "decrypt", [args]);
  }

};

function onSuccess(path) {
  if (typeof success === 'function') success(path);
  return path;
}

function onError(code) {
  var error = code || 0;
  if (typeof error === 'function') error(error);
  return error;
}

exports.safe = safe;
