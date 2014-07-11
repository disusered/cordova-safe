
safe
====

Easy to use cryptographic operations for Cordova.

## Install

```bash
$ cordova plugin add https://github.com/cordova-bridge/safe
```

## Usage

The plugin exposes the following methods:

```javascript
cordova.plugins.bridge.safe.encrypt(file, password, success, error);
cordova.plugins.bridge.safe.decrypt(file, password, success, error);
```

#### Parameters:

* __file:__ A string representing a local URI
* __password:__ A password for the crypto operations
* __success:__ Optional success callback
* __error:__ Optional error callback

## Example

#### Default usage

```javascript
var safe = cordova.plugins.bridge.safe;

// encrypt
safe.encrypt('file:/storage/sdcard/DCIM/Camera/1404177327783.jpg', 'foo');

// decrypt
safe.decrypt('file:/storage/sdcard/DCIM/Camera/1404177327784.mp4', 'bar');
```

#### With optional callbacks

```javascript
var safe = cordova.plugins.bridge.safe,
    password = 'somePassword';


function success(encryptedFile) {
  console.log('Encrypted file: ' + encryptedFile);

  safe.decrypt(encryptedFile, password, function(decryptedFile) {
    console.log('Decrypted file: ' + decryptedFile);
  }, error);
}

function error() {
  console.log('Error with cryptographic operation');
}

safe.encrypt('file:/storage/sdcard/DCIM/Camera/1404177327783.jpg', password, success, error);
```

## Links

- http://docs.phonegap.com/en/3.5.0/plugin_ref_plugman.md.html#Using%20Plugman%20to%20Manage%20Plugins
- http://docs.phonegap.com/en/3.5.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide
- http://docs.phonegap.com/en/3.5.0/guide_platforms_android_plugin.md.html#Android%20Plugins
- http://docs.phonegap.com/en/3.5.0/guide_platforms_ios_plugin.md.html#iOS%20Plugins
- https://github.com/apache/cordova-ios/blob/master/CordovaLib/Classes/CDVPlugin.m
- https://developer.apple.com/library/ios/documentation/security/Conceptual/cryptoservices/GeneralPurposeCrypto/GeneralPurposeCrypto.html#//apple_ref/doc/uid/TP40011172-CH9-SW14
- https://github.com/shazron/KeychainPlugin
- http://developer.android.com/training/articles/security-tips.html
- http://stackoverflow.com/questions/8184492/best-way-to-secure-android-app-sensitive-data
