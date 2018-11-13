//
//  AppDelegate+OpenInstallSDK.m
//  cordovaFile
//
//  Created by cooper on 2018/5/18.
//

#import "AppDelegate.h"
//#import "AppDelegate+OpenInstallSDK.h"
#import "CDVOpenInstall.h"

@interface AppDelegate (OpenInstallSDK)

@end

@implementation AppDelegate (OpenInstallSDK)

-(BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
    
    ////判断是否通过OpenInstall Universal Link 唤起App
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.8 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:CDVOpenInstallUniversalLinksNotification object:userActivity.webpageURL];
    });
    
    //其他第三方回调；
    return YES;
}

@end
