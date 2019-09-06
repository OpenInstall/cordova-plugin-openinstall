//
//  CDVOpenInstallStorage.m
//  cordovaFile
//
//  Created by cooper on 2019/8/27.
//

#import "CDVOpenInstallStorage.h"

@implementation CDVOpenInstallStorage

+ (CDVOpenInstallStorage *)shareInstance
{
    static CDVOpenInstallStorage *_instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (_instance == nil)
        {
            _instance = [[CDVOpenInstallStorage alloc] init];
        }
    });
    
    return _instance;
}
@end
