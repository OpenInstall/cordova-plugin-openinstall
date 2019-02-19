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
    float outtime = 10.0f;
    if (command.arguments.count != 0) {
        id time = [command.arguments objectAtIndex:0];
        if ([time isKindOfClass:[NSNumber class]]){
            NSNumber *timeResult = (NSNumber *)time;
            outtime = [timeResult floatValue];
        }
    }
    [[OpenInstallSDK defaultManager] getInstallParmsWithTimeoutInterval:outtime completed:^(OpeninstallData * _Nullable appData) {
        
        NSDictionary *installDicResult = @{@"channel":appData.channelCode?:@"",@"data":appData.data?:@""};

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
    
    NSDictionary *wakeupDicResult = @{@"channel":appData.channelCode?:@"",@"data":appData.data?:@""};
    
    if (self.wakeupCallbackId) {
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:wakeupDicResult];
        [self.commandDelegate sendPluginResult:commandResult callbackId:self.wakeupCallbackId];
    }

//    [self.commandDelegate evalJs:[NSString stringWithFormat:@"wakeUpCallBackFunction(%@)",[self jsonStringWithObject:wakeupDicResult]]];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
