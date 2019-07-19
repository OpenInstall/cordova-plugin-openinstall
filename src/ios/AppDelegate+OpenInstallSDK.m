//
//  AppDelegate+OpenInstallSDK.m
//  cordovaFile
//
//  Created by cooper on 2018/5/18.
//

#import "AppDelegate.h"
//#import "AppDelegate+OpenInstallSDK.h"
#import "CDVOpenInstall.h"

#define CDVOPENINSTALLPARAMS @"openinstallplugin"

@interface AppDelegate (OpenInstallSDK)

@end

@implementation AppDelegate (OpenInstallSDK)

-(BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
    CDVOpenInstall *plugin = [self.viewController getCommandInstance:CDVOPENINSTALLPARAMS];
    if (plugin == nil) {
        return NO;
    }
    
    return [plugin setUniversallinksHandler:userActivity];
}


@end
