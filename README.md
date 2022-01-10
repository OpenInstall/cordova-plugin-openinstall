# cordova-plugin-openinstall
openinstall 的 cordova 插件   

[如何在 capacitor 中使用？](https://github.com/OpenInstall/cordova-plugin-openinstall/issues/1#issuecomment-655984502)

## 一、安装插件

前往 [openinstall 官网](https://www.openinstall.io/)，注册账户，登录管理控制台，创建应用后，跳过 "集成指引"，在 "应用集成" 的对应平台的 "应用配置" 中获取 `appkey` 和 `scheme` 以及 iOS 的关联域名。

![获取appkey和scheme](https://res.cdn.openinstall.io/doc/ios-appkey.png)

使用下列命令安装并配置 openinstall 插件
```
cordova plugin add cordova-plugin-openinstall --variable OPENINSTALL_APPKEY=appkey --variable OPENINSTALL_SCHEME=scheme
```

*如配置错误，可先卸载再安装插件*
```
cordova plugin rm cordova-plugin-openinstall --variable OPENINSTALL_APPKEY=appkey --variable OPENINSTALL_SCHEME=scheme
```

## 二、调用API

### 1 初始化
App 启动时，请确保用户同意《隐私政策》之后，再调用初始化；如果用户不同意，则不进行openinstall SDK初始化。参考 [应用合规指南](https://www.openinstall.io/doc/rules.html)   
``` js
window.openinstall.init();
```
### 2 快速安装和一键拉起
#### 拉起参数获取
调用以下代码注册拉起回调，应尽早调用。如在 `deviceready` 事件回调之时注册
``` js
window.openinstall.registerWakeUpHandler(function(data){
  console.log("openinstall.wakeup success : " + JSON.stringify(data));
}, function(msg){
  console.log("openinstall.wakeup error : " + msg)
});
```
成功回调的data数据格式  
``` json
{"channel": "渠道号", "data": {"自定义key": "自定义value"}}
```
__注意__：对于 iOS，iOS9.0以后建议使用通用链接（Universal links）实现一键唤醒，为确保能正常跳转，AppID 必须开启 Associated Domains 功能，请到[苹果开发者网站](https://developer.apple.com)，选择 Certificate, Identifiers & Profiles，选择相应的 AppID，开启 Associated Domains。注意：当 AppID 重新编辑过之后，需要更新相应的 mobileprovision 证书。(图文步骤请参考[Cordova接入指南](https://www.openinstall.io/doc/cordova_sdk.html))  
- 在左侧导航器中点击您的项目  
- 选择'Capabilities'标签  
- 打开'Associated Domains'开关  
- 添加openinstall官网后台中应用对应的关联域名（openinstall应用控制台->iOS集成->iOS应用配置->关联域名(Associated Domains)）

**以下配置为可选项**  

openinstall可兼容微信openSDK1.8.6以上版本的通用链接跳转功能，注意微信SDK初始化方法中，传入正确格式的universal link链接：  
``` objc
//your_wxAppID从微信后台获取，yourAppkey从openinstall后台获取
[WXApi registerApp:@"your_wxAppID" universalLink:@"https://yourAppkey.openinstall.io/ulink/"];
```
- 使用 微信相关cordova插件 时，如果要传`universallink`参数的话，请和上面代码中的保持一致

- 微信开放平台后台Universal links配置，要和上面代码中的保持一致  

![微信后台配置](https://res.cdn.openinstall.io/doc/cordova-wx-ulink.jpg)  

- 如果使用了类似 `cordova-plugin-wechat` 插件，为了互相兼容，请注意Xcode工程->TARGETS->Build Phases->Compile Sources，  
检查`AppDelegate+Wechat.m`和`AppDelegate+OpenInstallSDK.m`是否按从上往下顺序排放：

![插件兼容](https://res.cdn.openinstall.io/doc/cordova-wx-sort.png)  

- 微信SDK更新参考[微信开放平台更新文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html)  


### 3 携带参数安装 （高级版功能）
#### 获取安装参数  
``` js
window.openinstall.getInstall(function(data){
    console.log('openinstall.getInstall success: ' + JSON.stringify(data));
}, function(msg){
    console.log('openinstall.getInstall error: ' + msg);
});
```
也可传入一个整形数值，单位秒，指定时间未返回将超时  
``` js
window.openinstall.getInstall(function(data){
    console.log('openinstall.getInstall success: ' + JSON.stringify(data));
}, function(msg){
    console.log('openinstall.getInstall error: ' + msg);
}, 10);
```
成功回调的data数据格式  
``` json
{"channel": "渠道号", "data": {"自定义key": "自定义value"}}
```

### 4 渠道统计 （高级版功能）  
SDK 会自动完成访问量、点击量、安装量、活跃量、留存率等统计工作。其它业务相关统计由开发人员使用 api 上报

#### 4.1 注册量统计  
根据自身的业务规则，在确保用户完成 app 注册的情况下调用 api  
``` js
window.openinstall.reportRegister();
```

#### 4.2 渠道效果统计  
统计终端用户对某些特殊业务的使用效果，如充值金额，分享次数等等。  
请在 [openinstall 控制台](https://developer.openinstall.io/) 的 “效果点管理” 中添加对应的效果点  
![创建效果点](https://res.cdn.openinstall.io/doc/effect_point.png)  
调用接口进行效果点的上报，第一个参数对应控制台中的 **效果点ID**  

``` js
window.openinstall.reportEffectPoint("effect_test", 1);
```


## 三、导出apk/ipa包并上传

代码集成完毕后，需要导出安装包上传openinstall后台，openinstall会自动完成所有的应用配置工作。  
![上传安装包](https://res.cdn.openinstall.io/doc/upload-ipa-jump.png)  

上传完成后即可开始在线模拟测试，体验完整的App安装/拉起流程；待测试无误后，再完善下载配置信息。  
![在线测试](https://res.cdn.openinstall.io/doc/js-test.png)

## 如有疑问

若您在集成或使用中有任何疑问或者困难，请 [咨询openinstall客服](https://www.openinstall.io/)。 

---

## 广告平台接入

### Android平台

1、针对广告平台接入，新增配置接口，在调用 init 之前调用。参考 [广告平台对接Android集成指引](https://www.openinstall.io/doc/ad_android.html)
``` js
    var options = {
        adEnabled: true, 
    }
    window.openinstall.configAndroid(options);
```
options 可选参数如下：   
| 参数名| 参数类型 | 描述 |  
| --- | --- | --- |
| adEnabled| bool | 广告平台接入开关（必须） |
| macDisabled | bool | 是否禁止 SDK 获取 mac 地址 |
| imeiDisabled | bool | 是否禁止 SDK 获取 imei |
| gaid | string | 通过 google api 获取到的 advertisingId，SDK 将不再获取gaid |
| oaid | string | 通过移动安全联盟获取到的 oaid，SDK 将不再获取oaid |

2、为了精准地匹配到渠道，需要获取设备唯一标识码（IMEI），因此需要在 AndroidManifest.xml 中添加权限声明 
``` xml
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```
3、请自行进行权限申请，在权限申请成功后，再进行openinstall初始化。**无论终端用户是否同意，都要调用初始化**

