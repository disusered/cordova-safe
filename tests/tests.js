/* jshint unused: true */
var platform = cordova.platformId.match(/ios/i) ? 'ios' : 'android';

var safe = cordova.plugins.disusered.safe;

var key = 'someKey';
var prefix = 'https://raw.githubusercontent.com/disusered/cordova-safe/develop/tests/';

exports.defineAutoTests = function() {
  describe('cordova.plugins.disusered', function() {
    it('should exist', function() {
      expect(window.cordova.plugins.disusered).toBeDefined();
    });

    it('should pass a test', function() {
      expect(window).toBeDefined();
    });
  });

  describe('cordova.plugins.disusered.safe', function() {
    it('should exist', function() {
      expect(safe).toBeDefined();
    });

    it('should have an "encrypt" method', function() {
      expect(safe.encrypt).toBeDefined();
    });

    it('should have an "decrypt" method', function() {
      expect(safe.decrypt).toBeDefined();
    });
  });

  describe('decrypted test file', function() {
    var uri;
    beforeEach(function(done) {
      var url = prefix + 'decrypted.png';
      function success(path) { test(path); }
      function error(code) { test(); }
      function test(path) { uri = path; done(); }
      download(url, success, error, true);
    });

    it('should exist locally', function() {
      expect(uri).toBeDefined();
    });

    describe('encrypt method', function() {
      var encryptedFile;

      beforeEach(function(done) {
        function success(path) { test(path); }
        function error(code) { test(); }
        function test(path) { encryptedFile = path; done(); }
        safe.encrypt(uri, key, success, error);
      });

      it('should encrypt the test file', function() {
        expect(encryptedFile).toBeDefined();
      });
    });
  });

  describe('encrypted test file', function() {
    var uri;
    beforeEach(function(done) {
      var url = prefix + 'encrypted-' + platform + '.png';
      function success(path) { test(path); }
      function error(code) { test(); }
      function test(path) { uri = path; done(); }
      download(url, success, error, true);
    });

    it('should exist locally', function() {
      expect(uri).toBeDefined();
    });

    describe('decrypt method', function() {
      var decryptedFile;

      beforeEach(function(done) {
        function success(path) { test(path); }
        function error(code) { test(); }
        function test(path) { decryptedFile = path; done(); }
        safe.decrypt(uri, key, success, error);
      });

      it('should decrypt the test file', function() {
        expect(decryptedFile).toBeDefined();
      });
    });
  });
};

exports.defineManualTests = function(contentEl, createActionButton) {
  var testInfo;

  var decrypted = prefix + 'decrypted.png';
  var encrypted = prefix + 'encrypted-' + platform + '.png';

  testInfo = '<h3>Press Open File and a test file will open in a ' +
    'native context</h3><div id="crypto"></div>' +
    'Expected result: File crypto operation succeeds.';

  contentEl.innerHTML = testInfo;

  function error(code) {
    if (code.error === 1 || code === 1) {
      console.log('No file handler found');
    } else {
      console.log('Undefined error');
    }
    return Promise.reject(code);
  }

  function promisify(uri, action, resolve, reject) {
    safe[action](uri, key, function(encryptedFileUri) {
      resolve(encryptedFileUri);
    }, function() {
      reject('Error with cryptographic operation');
    });
  }

  createActionButton('Decrypt', function() {
    download(encrypted, true).then(function(uri) {
      return new Promise(promisify.bind(this, uri, 'decrypt'));
    }).then(function(uri) {
      console.log(uri);
    }).catch(error);
  }, 'crypto');

  createActionButton('Encrypt', function() {
    download(decrypted, true).then(function(uri) {
      return new Promise(promisify.bind(this, uri, 'encrypt'));
    }).then(function(uri) {
      console.log(uri);
    }).catch(error);
  }, 'crypto');
};

/**
 * download
 *
 * @param {String} url File URI
 * @param {Function} success Success callback
 * @param {Function} error Failure callback
 * @param {Boolean} trustAllCertificates Trusts any certificate when the connection is done over HTTPS.
 * @returns {void}
 */
function download(url, success, error, trustAllCertificates) {
  var ft = new FileTransfer(); // eslint-disable-line no-undef
  var ios = cordova.file.cacheDirectory;
  var ext = cordova.file.externalCacheDirectory;
  var dir = (ext) ? ext : ios;
  var name = url.substring(url.lastIndexOf('/') + 1);
  var path = dir + name;

  if (typeof success === 'boolean') {
    trustAllCertificates = success;
  }

  return new Promise(function(resolve, reject) {
    function onSuccess(uri, callback) {
      if (typeof callback === 'function') {
        callback(uri);
      }
      resolve(uri);
      return uri;
    }

    function onError(callback) {
      var code = (arguments.length > 1) ? arguments[1] : 0;
      if (typeof callback === 'function') {
        callback(code);
      }
      reject(code);
      return code;
    }

    if (typeof trustAllCertificates !== 'boolean') {
      // Defaults to false
      trustAllCertificates = false;
    }

    ft.download(encodeURI(url), path, function(entry) {
          var file = entry.toURL();
          onSuccess(file, success);
        }, onError.bind(this, error), trustAllCertificates);

  });
}
