//
//  AppDelegate+OpenInstallSDK.m
//  cordovaFile
//
//  Created by cooper on 2018/5/18.
//

#import "AppDelegate.h"
//#import "AppDelegate+OpenInstallSDK.h"
#import "CDVOpenInstall.h"
#import "CDVOpenInstallStorage.h"

#define CDVOPENINSTALLPARAMS @"openinstallplugin"

@interface AppDelegate (OpenInstallSDK)

@end

@implementation AppDelegate (OpenInstallSDK)

-(BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
    CDVOpenInstall *plugin = [self.viewController getCommandInstance:CDVOPENINSTALLPARAMS];
    if (plugin == nil) {
        if (userActivity.webpageURL) {
            [CDVOpenInstallStorage shareInstance].userActivity = userActivity;
            [[NSNotificationCenter defaultCenter]removeObserver:self
                                                           name:CDVPluginHandleOpenURLNotification
                                                         object:nil];
        }
        return NO;
    }
    
    return [plugin setUniversallinksHandler:userActivity];
}

+ (void)initialize
{
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(getSchemeHandleUrl:)
                                                name:CDVPluginHandleOpenURLNotification object:nil];
}

+ (void)getSchemeHandleUrl:(NSNotification *)notification{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]]) {
        [CDVOpenInstallStorage shareInstance].schemeUrl = url;
    }
    [[NSNotificationCenter defaultCenter]removeObserver:self
                                                   name:CDVPluginHandleOpenURLNotification
                                                 object:nil];
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

@end
