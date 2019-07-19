//
//  CDVOpenInstall.m
//  OpenInstallSDK
//
//  Created by cooper on 2018/2/27.
//  Copyright © 2018年 com.cooper.fenmiao. All rights reserved.
//

#import "CDVOpenInstall.h"

@interface CDVOpenInstall()

@property (nonatomic, strong) NSMutableArray *timeoutTimersArr;

@end

@implementation CDVOpenInstall

#pragma mark "API"
- (void)pluginInitialize {
    
    NSString* appKey = [[self.commandDelegate settings] objectForKey:@"com.openinstall.app_key"];
    [OpenInstallSDK defaultManager];
    if (appKey){
        self.appkey = appKey;
        [OpenInstallSDK setAppKey:self.appkey withDelegate:self];
    }
}
-(BOOL)setUniversallinksHandler:(NSUserActivity *)userActivity{
    if ([OpenInstallSDK continueUserActivity:userActivity]) {
        return YES;
    }
    return NO;
}

#pragma mark "CDVPlugin Overrides"
- (void)handleOpenURL:(NSNotification *)notification
{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]])
    {
        [OpenInstallSDK handLinkURL:url];
    }
}

-(void)getInstall:(CDVInvokedUrlCommand *)command{
    
    self.currentCallbackId = command.callbackId;
    float outtime = 10.0f;
    if (command.arguments.count != 0) {
        id time = [command.arguments objectAtIndex:0];
        if ([time isKindOfClass:[NSNumber class]]){
            NSNumber *timeResult = (NSNumber *)time;
            outtime = [timeResult floatValue];
        }
    }
    [[OpenInstallSDK defaultManager] getInstallParmsWithTimeoutInterval:outtime completed:^(OpeninstallData * _Nullable appData) {
        
        NSString *channelID = @"";
        NSString *datas = @"";
        if (appData.data) {
            datas = [self jsonStringWithObject:appData.data];
        }
        if (appData.channelCode) {
            channelID = appData.channelCode;
        }
        NSDictionary *installDicResult = @{@"channel":channelID,@"data":datas};

        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:installDicResult];
        [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
        
        self.currentCallbackId = nil;
        
    }];
    
}
-(void)registerWakeUpHandler:(CDVInvokedUrlCommand *)command{
    
    NSDictionary *resultDic;
    @synchronized(self){
        if (self.wakeupDic) {
            resultDic = [self.wakeupDic copy];
        }
    }
    if (resultDic.count != 0) {
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDic];
        [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
        self.wakeupDic = nil;
    }
    self.wakeupCallbackId = command.callbackId;
}

-(void)getWakeUp:(CDVInvokedUrlCommand *)command{
    
}

-(void)reportRegister:(CDVInvokedUrlCommand *)command{
    
    [OpenInstallSDK reportRegister];
    
}

-(void)reportEffectPoint:(CDVInvokedUrlCommand *)command{
    
    NSString *effectPoint = @"";
    long value = 0;
    if (command.arguments.count != 0) {
        id point = [command.arguments objectAtIndex:0];
        if ([point isKindOfClass:[NSString class]]) {
            effectPoint = (NSString *)point;
        }
        id val = [command.arguments objectAtIndex:1];
        if ([val isKindOfClass:[NSNumber class]]){
            NSNumber *valResult = (NSNumber *)val;
            value = [valResult longValue];
        }
        [[OpenInstallSDK defaultManager] reportEffectPoint:effectPoint effectValue:value];
    }
}

#pragma mark "OpenInstallDelegate"
/**
 * 唤醒时获取h5页面动态参数（如果是渠道链接，渠道编号会一起返回）
 * @param appData 动态参数对象
 */
- (void)getWakeUpParams:(nullable OpeninstallData *)appData{
    
    NSString *channelID = @"";
    NSString *datas = @"";
    if (appData.data) {
        datas = [self jsonStringWithObject:appData.data];
    }
    if (appData.channelCode) {
        channelID = appData.channelCode;
    }
    NSDictionary *wakeupDicResult = @{@"channel":channelID,@"data":datas};
    
    NSString *wakeupcallbacId;
    @synchronized(self){
        wakeupcallbacId = [self.wakeupCallbackId copy];
    }
    if (wakeupcallbacId) {
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:wakeupDicResult];
        [self.commandDelegate sendPluginResult:commandResult callbackId:self.wakeupCallbackId];
    }else{
        @synchronized(self){
            self.wakeupDic = [[NSDictionary alloc]init];
            self.wakeupDic = wakeupDicResult;
        }
    }
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSString *)jsonStringWithObject:(id)jsonObject{
    
    id arguments = (jsonObject == nil ? [NSNull null] : jsonObject);
    
    NSArray* argumentsWrappedInArr = [NSArray arrayWithObject:arguments];
    
    NSString* argumentsJSON = [self cp_JSONString:argumentsWrappedInArr];
    
    argumentsJSON = [argumentsJSON substringWithRange:NSMakeRange(1, [argumentsJSON length] - 2)];
    
    return argumentsJSON;
}
- (NSString *)cp_JSONString:(NSArray *)array{
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:array
                                                       options:0
                                                         error:&error];
    
    NSString *jsonString = [[NSString alloc] initWithData:jsonData
                                                 encoding:NSUTF8StringEncoding];
    
    if ([jsonString length] > 0 && error == nil){
        return jsonString;
    }else{
        return @"";
    }
}

@end
