//
//  CDVOpenInstall.h
//  OpenInstallSDK
//
//  Created by cooper on 2018/2/27.
//  Copyright © 2018年 com.cooper.fenmiao. All rights reserved.
//

#import <Cordova/CDVPlugin.h>
#import "OpenInstallSDK.h"

@interface CDVOpenInstall : CDVPlugin<OpenInstallDelegate,UIApplicationDelegate>

@property (nonatomic, copy) NSString *appkey;
@property (nonatomic, strong) NSString *currentCallbackId;

-(void)getInstall:(CDVInvokedUrlCommand *)command;//获取动态安装参数
-(void)getWakeUp:(CDVInvokedUrlCommand *)command;//获取动态唤醒参数，ios暂不支持
-(void)reportRegister:(CDVInvokedUrlCommand *)command;//注册统计
-(void)reportEffectPoint:(CDVInvokedUrlCommand *)command;//渠道效果统计
@end
