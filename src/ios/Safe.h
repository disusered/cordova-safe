/**
 * Safe.h
 *
 * Copyright (C) 2015 Carlos Antonio
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 */

#import <Foundation/Foundation.h>
#import <Security/SecRandom.h>
#import <Cordova/CDV.h>
#import <Cordova/NSData+Base64.h>
#import "RNEncryptor.h"
#import "RNDecryptor.h"


@interface Safe : CDVPlugin {
}

- (void)encrypt:(CDVInvokedUrlCommand*)command;
- (void)decrypt:(CDVInvokedUrlCommand*)command;
- (NSString*)crypto:(NSString*)action command:(CDVInvokedUrlCommand*)command;
@end
