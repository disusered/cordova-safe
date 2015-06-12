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
