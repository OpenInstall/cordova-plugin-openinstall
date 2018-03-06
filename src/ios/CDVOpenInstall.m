//
//  CDVOpenInstall.m
//  OpenInstallSDK
//
//  Created by cooper on 2018/2/27.
//  Copyright © 2018年 com.cooper.fenmiao. All rights reserved.
//

#import "CDVOpenInstall.h"

#define PARAMS @"getInstallParamsFromOpenInstall_params"

@interface CDVOpenInstall()

@property (nonatomic, strong) NSMutableArray *timeoutTimersArr;

@end

@implementation CDVOpenInstall

#pragma mark "API"
- (void)pluginInitialize {
    NSString* appKey = [[self.commandDelegate settings] objectForKey:@"com.openinstall.app_key"];
    if (appKey){
        self.appkey = appKey;
        [OpenInstallSDK setAppKey:self.appkey withDelegate:self];
    }
    
}

-(void)getInstall:(CDVInvokedUrlCommand *)command{
    
    self.currentCallbackId = command.callbackId;
    [self isTimeout:8 WithCompletion:^(BOOL isTimeout) {
        
        if (isTimeout) {
            
            [self failWithCallbackID:command.callbackId withMessage:@"getInstall timeout"];

        }else{
            
            NSDictionary *installDic = [[NSUserDefaults standardUserDefaults] objectForKey:PARAMS];
            CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:installDic];
            [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
            
        }
        
        self.currentCallbackId = nil;
    }];

}

-(void)getWakeUp:(CDVInvokedUrlCommand *)command{
    
   
}

-(void)reportRegister:(CDVInvokedUrlCommand *)command{
    
    [OpenInstallSDK reportRegister];
    
}

-(void)isTimeout:(NSTimeInterval)timeoutInterval WithCompletion:(void (^)(BOOL isTimeout))callBack{
    
    __block NSTimeInterval timeout = timeoutInterval;//超时时间
    
    dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER,
                                                     0, 0, dispatch_get_global_queue(0, 0));
    
    if (timer)
    {
        [self.timeoutTimersArr addObject:timer];
        dispatch_source_set_timer(timer, dispatch_walltime(NULL, 0), 500 * NSEC_PER_MSEC, 0);
        __block int times = 0;
        __block BOOL isTimeout = NO;
        dispatch_source_set_event_handler(timer, ^{
            
            if (times > 0) {
                timeout = timeout - 500 * NSEC_PER_MSEC;
            }
            
            if (timeout <= 0) {
                
                NSDictionary *installDic = [[NSUserDefaults standardUserDefaults] objectForKey:PARAMS];
                if (installDic) {
                    isTimeout = NO;
                }else{
                    isTimeout = YES;
                }
                if (callBack) {
                    dispatch_async(dispatch_get_main_queue(), ^{
            
                        callBack(isTimeout);
                        
                    });
                }
                dispatch_source_cancel(timer);
                [self.timeoutTimersArr removeObject:timer];
            }else{
                
                NSDictionary *installDic = [[NSUserDefaults standardUserDefaults] objectForKey:PARAMS];
                if (installDic) {
                    
                    if (callBack) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            
                            callBack(NO);

                        });

                    }
                    dispatch_source_cancel(timer);
                    [self.timeoutTimersArr removeObject:timer];
                }
                
            }
            
            times ++;
        });
        dispatch_resume(timer);
    }
    
}

#pragma mark "OpenInstallDelegate"
/**
 * 安装时获取自定义h5页面参数（使用控制中心提供的渠道统计时，渠道编号也会返回给开发者）
 * @ param params 动态参数
 * @ return void
 */
- (void)getInstallParamsFromOpenInstall:(NSDictionary *) params withError: (NSError *) error{
    
    if (!error) {
        if (params) {
            
            [[NSUserDefaults standardUserDefaults] setValue:params forKey:PARAMS];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }else{

        }
    }
    
}


/**
 * 唤醒时获取h5页面参数（如果是渠道链接，渠道编号会一起返回）
 * @ param type 链接类型（区分渠道链接和自定义分享h5链接）
 * @ param params 动态参数
 * @ return void
 */
- (void)getWakeUpParamsFromOpenInstall: (NSDictionary *) params withError: (NSError *) error{
    
    if (!error) {
        if (params) {
            
        }else{
            
        }
    }

}

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
    
    //判断是否通过OpenInstall Universal Links 唤起App
    if ([OpenInstallSDK continueUserActivity:userActivity]){
        
        return YES;
    }else{
        //其他代码；
        return YES;
    }
    
}

- (void)failWithCallbackID:(NSString *)callbackID withError:(NSError *)error
{
    [self failWithCallbackID:callbackID withMessage:[error localizedDescription]];
}

- (void)failWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

@end
