//
//  CDVOpenInstall.m
//  OpenInstallSDK
//
//  Created by cooper on 2018/2/27.
//  Copyright © 2018年 com.cooper.fenmiao. All rights reserved.
//

#import "CDVOpenInstall.h"

#define PARAMS @"getInstallParamsFromOpenInstall_params"

NSString* const CDVOpenInstallUniversalLinksNotification = @"CDVOpenInstallUniversalLinksNotification";

@interface CDVOpenInstall()

@property (nonatomic, strong) NSMutableArray *timeoutTimersArr;

@end

@implementation CDVOpenInstall

#pragma mark "API"
- (void)pluginInitialize {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setUniversallinksHandler:) name:CDVOpenInstallUniversalLinksNotification object:nil];
    NSString* appKey = [[self.commandDelegate settings] objectForKey:@"com.openinstall.app_key"];
    [OpenInstallSDK defaultManager];
    if (appKey){
        self.appkey = appKey;
        [OpenInstallSDK setAppKey:self.appkey withDelegate:self];
    }
}
-(void)setUniversallinksHandler:(NSNotification *)obj{
    
    if (obj.object) {
        if ([obj.object isKindOfClass:[NSURL class]]) {
            NSUserActivity *activity = [[NSUserActivity alloc]initWithActivityType:NSUserActivityTypeBrowsingWeb];
            activity.webpageURL = obj.object;
            [OpenInstallSDK continueUserActivity:activity];
        }
    }
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
    float outtime = 5.0f;
    if (command.arguments.count != 0) {
        id time = [command.arguments objectAtIndex:0];
        if ([time isKindOfClass:[NSString class]]) {
            NSString *timeResult = (NSString *)time;
            if ([self isPureInt:timeResult]||[self isPureFloat:timeResult]) {
                outtime = [timeResult floatValue];
            }
        }else if ([time isKindOfClass:[NSNumber class]]){
            NSNumber *timeResult = (NSNumber *)time;
            outtime = [timeResult floatValue];
        }
    }
    [[OpenInstallSDK defaultManager] getInstallParmsWithTimeoutInterval:outtime completed:^(OpeninstallData * _Nullable appData) {
        
        if (!appData.data&&!appData.channelCode) {
            CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"channel": @"", @"data": @""}];
            [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
            return;
        }
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
        if ([val isKindOfClass:[NSString class]]) {
            NSString *valResult = (NSString *)val;
            if ([self isPureInt:valResult]||[self isPureFloat:valResult]) {
                value = [valResult longLongValue];
            }
        }else if ([val isKindOfClass:[NSNumber class]]){
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
    
    if (!appData.data&&!appData.channelCode) {
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"channel": @"", @"data": @""}];
        [self.commandDelegate sendPluginResult:commandResult callbackId:self.wakeupCallbackId];
        return;
    }

    NSString *channelID = @"";
    NSString *datas = @"";
    if (appData.data) {
        datas = [self jsonStringWithObject:appData.data];
    }
    if (appData.channelCode) {
        channelID = appData.channelCode;
    }
    NSDictionary *wakeupDicResult = @{@"channel":channelID,@"data":datas};
    
    if (self.wakeupCallbackId) {
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:wakeupDicResult];
        [self.commandDelegate sendPluginResult:commandResult callbackId:self.wakeupCallbackId];
    }

//    [self.commandDelegate evalJs:[NSString stringWithFormat:@"wakeUpCallBackFunction(%@)",[self jsonStringWithObject:wakeupDicResult]]];
}


//判断是否为整形：

- (BOOL)isPureInt:(NSString*)string{
    
    NSScanner* scan = [NSScanner scannerWithString:string];
    
    int val;
    
    return[scan scanInt:&val] && [scan isAtEnd];
    
}

//判断是否为浮点形：

- (BOOL)isPureFloat:(NSString*)string{
    
    NSScanner* scan = [NSScanner scannerWithString:string];
    
    float val;
    
    return[scan scanFloat:&val] && [scan isAtEnd];
    
}

- (NSString *)jsonStringWithObject:(id)jsonObject{
    // 将字典或者数组转化为JSON串
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonObject
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    
    NSString *jsonString = [[NSString alloc] initWithData:jsonData
                                                 encoding:NSUTF8StringEncoding];
    
    if ([jsonString length] > 0 && error == nil){
        
        jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
        
        return jsonString;
    }else{
        return @"";
    }
}

@end
