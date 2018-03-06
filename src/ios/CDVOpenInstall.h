//
//  CDVOpenInstall.h
//  OpenInstallSDK
//
//  Created by cooper on 2018/2/27.
//  Copyright © 2018年 com.cooper.fenmiao. All rights reserved.
//

#import "CDVPlugin.h"
#import "CDV.h"
#import "OpenInstallSDK.h"

@interface CDVOpenInstall : CDVPlugin<OpenInstallDelegate,UIApplicationDelegate>

@property (nonatomic, copy) NSString *appkey;
@property (nonatomic, strong) NSString *currentCallbackId;

-(void)getInstall:(CDVInvokedUrlCommand *)command;
-(void)getWakeUp:(CDVInvokedUrlCommand *)command;
-(void)reportRegister:(CDVInvokedUrlCommand *)command;

@end
