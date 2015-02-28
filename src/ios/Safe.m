/**
 * Safe.m
 *
 * Copyright (C) 2015 Carlos Antonio
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 */

#import "Safe.h"

@implementation Safe

/**
 *  encrypt
 *
 *  @param command An array of arguments passed from javascript
 */
- (void)encrypt:(CDVInvokedUrlCommand *)command {

  CDVPluginResult *pluginResult = nil;

  NSString *path = [self crypto:@"encrypt" command:command];

  if (path != nil) {
    pluginResult =
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                          messageAsString:path];
  } else {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
  }

  [self.commandDelegate sendPluginResult:pluginResult
                              callbackId:command.callbackId];
}

/**
 *  decrypt
 *
 *  @param command An array of arguments passed from javascript
 */
- (void)decrypt:(CDVInvokedUrlCommand *)command {

  CDVPluginResult *pluginResult = nil;

  NSString *path = [self crypto:@"decrypt" command:command];

  if (path != nil) {
    pluginResult =
        [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                          messageAsString:path];
  } else {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
  }

  [self.commandDelegate sendPluginResult:pluginResult
                              callbackId:command.callbackId];
}

/**
 *  Encrypts or decrypts file at given URI.
 *
 *
 *  @param action  Cryptographic operation
 *  @param command Cordova arguments
 *
 *  @return Boolean value representing success or failure
 */
- (NSString*)crypto:(NSString *)action command:(CDVInvokedUrlCommand *)command {

  NSData *data = nil;
  NSString *path = [command.arguments objectAtIndex:0];
  NSString *password = [command.arguments objectAtIndex:1];

  // if path and password args exist
  if (path != nil && [path length] > 0 && password != nil &&
      [password length] > 0) {

    // if file exists
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {

      // get file data
      NSData *fileData = [NSData dataWithContentsOfFile:path];

      NSError *error;
      if ([action isEqualToString:@"encrypt"]) {
        // encrypt data
        data = [RNEncryptor encryptData:fileData
                           withSettings:kRNCryptorAES256Settings
                               password:password
                                  error:&error];

      } else if ([action isEqualToString:@"decrypt"]) {
        // decrypt data
        data = [RNDecryptor decryptData:fileData
                           withPassword:password
                                  error:&error];
      }

      // write to generated path
      [data writeToFile:path atomically:YES];
    }
  }

  return path;
}

@end
