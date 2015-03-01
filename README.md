safe
====

[![Build Status](https://travis-ci.org/disusered/cordova-safe.svg)](https://travis-ci.org/disusered/cordova-safe) [![Code Climate](https://codeclimate.com/github/disusered/cordova-safe/badges/gpa.svg)](https://codeclimate.com/github/disusered/cordova-safe) 

File encryption for Cordova.

## Install

```bash
$ cordova plugin add org.bridge.safe
```

## Usage

The plugin exposes the following methods:

```javascript
cordova.plugins.bridge.safe.encrypt(file, key, success, error);
cordova.plugins.bridge.safe.decrypt(file, key, success, error);
```

#### Parameters:

* __file:__ A string representing a local URI
* __key:__ A key for the crypto operations
* __success:__ Optional success callback
* __error:__ Optional error callback

## Example

#### Default usage

```javascript
var safe = cordova.plugins.bridge.safe;

// encrypt
safe.encrypt('file:/storage/sdcard/DCIM/Camera/1404177327783.jpg', 'foo');

// decrypt
safe.decrypt('file:/storage/sdcard/my-encrypted-file.txt', 'bar');
```

#### With optional callbacks

```javascript
var safe = cordova.plugins.bridge.safe,
    key = 'someKey';


function success(encryptedFile) {
  console.log('Encrypted file: ' + encryptedFile);

  safe.decrypt(encryptedFile, key, function(decryptedFile) {
    console.log('Decrypted file: ' + decryptedFile);
  }, error);
}

function error() {
  console.log('Error with cryptographic operation');
}

safe.encrypt('file:/storage/sdcard/DCIM/Camera/1404177327783.jpg', key, success, error);
```

## Dependencies
- [Node](http://nodejs.org/)
- [Gradle](https://gradle.org/)
- [CocoaPods](http://cocoapods.org/)
