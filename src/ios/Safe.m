/**
 * Safe.m
 *
 * Copyright (C) 2014 Carlos Antonio
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

  NSString *path = [command.arguments objectAtIndex:0];
  NSString *password = [command.arguments objectAtIndex:1];

  CDVPluginResult *pluginResult = nil;

  // encrypt file at path with password
  NSData *encryptedData = [self crypto:@"encrypt" uri:path pass:password];

  // if file crypto returned data
  if (encryptedData != nil) {
    // write to generated path
    [encryptedData writeToFile:path atomically:YES];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
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
  NSString *echo = [command.arguments objectAtIndex:0];

  if (echo != nil && [echo length] > 0) {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                     messageAsString:echo];
  } else {
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
  }

  [self.commandDelegate sendPluginResult:pluginResult
                              callbackId:command.callbackId];
}

/**
 *  Returns the data after being encrypted or decrypted
 *
 *  @param action   Cryptographic operation ie encrypt/decrypt
 *  @param path     String URI of file we're to encrypt
 *  @param password Key for cryptographic operation
 *
 *  @return Encrypted or decrypted NSData
 */
- (NSData *)crypto:(NSString *)action
               uri:(NSString *)path
              pass:(NSString *)password {

  NSData *data = nil;

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
    }
  }

  return data;
}

@end
