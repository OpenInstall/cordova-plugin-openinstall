//
//  CDVOpenInstallStorage.h
//  cordovaFile
//
//  Created by cooper on 2019/8/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CDVOpenInstallStorage : NSObject

@property (nonatomic, strong)NSUserActivity *_Nullable userActivity;
@property (nonatomic, strong) NSURL *_Nullable schemeUrl;
@property (nonatomic, copy) NSString *wakeupCallbackId;

+ (CDVOpenInstallStorage *)shareInstance;

@end

NS_ASSUME_NONNULL_END
