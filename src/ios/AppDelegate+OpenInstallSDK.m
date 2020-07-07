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
#import <objc/runtime.h>

#define CDVOPENINSTALLPARAMS @"openinstallplugin"

@interface AppDelegate (OpenInstallSDK)

@end

@implementation AppDelegate (OpenInstallSDK)

static BOOL systemFuncExist = NO;

+ (void)load {
    NSProcessInfo *processInfo = [NSProcessInfo processInfo];
    if ([processInfo respondsToSelector:@selector(operatingSystemVersion)]) {
        Method systemFunc = class_getInstanceMethod(self, @selector(application:continueUserActivity:restorationHandler:));
        Method cooFunc = class_getInstanceMethod(self, @selector(cooFuncApplication:continueUserActivity:restorationHandler:));
        if (systemFunc) {
            method_exchangeImplementations(systemFunc, cooFunc);
            systemFuncExist = YES;
        } else {
            const char *typeEncoding = method_getTypeEncoding(cooFunc);
            class_addMethod(self, @selector(application:continueUserActivity:restorationHandler:), method_getImplementation(cooFunc), typeEncoding);
        }
    }
}

-(BOOL)cooFuncApplication:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
    if (systemFuncExist) {
        [self cooFuncApplication:application continueUserActivity:userActivity restorationHandler:restorationHandler];
    }
    CDVOpenInstall *plugin = [self.viewController getCommandInstance:CDVOPENINSTALLPARAMS];
    if (plugin==nil) {
        return NO;
    }
    return [plugin setUniversallinksHandler:userActivity];
}

@end
