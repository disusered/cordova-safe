/**
 * disusered.safe.js
 *
 * @overview Easy to use cryptographic operations for Cordova.
 * @author Carlos Antonio
 * @license MIT
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
   * @returns {void}
   */
  encrypt: function(path, password, success, error) {
    var encryptSuccess, encryptError;

    if (!path || arguments.length === 0) return;

    encryptSuccess = onSuccess.bind(null, success);
    encryptError = onError.bind(null, error);

    exec(encryptSuccess, encryptError, 'Safe', 'encrypt', [path, password]);
  },

  /**
   * decrypt
   *
   * @param {String} path File URI
   * @param {String} password Password for decryption
   * @param {Function} success Success callback
   * @param {Function} error Failure callback
   * @returns {void}
   */
  decrypt: function(path, password, success, error) {
    var decryptSuccess, decryptError;

    if (!path || arguments.length === 0) return;

    decryptSuccess = onSuccess.bind(null, success);
    decryptError = onError.bind(null, error);

    exec(decryptSuccess, decryptError, 'Safe', 'decrypt', [path, password]);
  }

};

/**
 * onSuccess
 *
 * @param {Function} success Success callback
 * @param {String} path Encrypted file URI
 * @returns {String} Encrypted file URI
 */
function onSuccess(success, path) {
  if (typeof success === 'function') {
    window.requestFileSystem(window.PERSISTENT, 0, function(fs) {
      fs.root.getFile(path.split('/').pop(), {create: false}, function(file) {
        file.file(function(fileObj) {
          success(fileObj);
        }, onError);
      }, onError);
    }, onError);
  }
}

/**
 * onError
 *
 * @param {String} error Error callback
 * @param {Function} code Error code
 * @returns {String} Decrypted file URI
 */
function onError(error, code) {
  if (typeof error === 'function') error(code);
  return code;
}

exports.safe = safe;
