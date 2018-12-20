# cordova-plugin-openinstall
openinstall 的 cordova 插件

### 安装插件
注册并创建应用，获取 openinstall 为应用分配的 appkey 和 scheme，使用下列命令安装并配置 openinstall 插件  
```
cordova plugin add cordova-plugin-openinstall --variable OPENINSTALL_APPKEY=[appkey] --variable OPENINSTALL_SCHEME=[scheme]
```

### 调用API

#### 1 快速下载
如果只需要快速下载功能，无需其它功能（携带参数安装、渠道统计、一键拉起），完成初始化即可(这里指安装插件)

#### 2 一键拉起
##### 拉起参数获取
调用以下代码注册拉起回调，应尽早调用。如在 `deviceready` 事件回调之时注册
``` js
window.openinstall.registerWakeUpHandler(function(data){
  console.log("openinstall.wakeup success : channel=" + data.channel + ", data=" + data.data);
}, function(msg){
  console.log("openinstall.wakeup error : " + msg)
});
```
__注意__：对于 iOS，iOS9.0以后建议使用通用链接（Universal links）实现一键唤醒，为确保能正常跳转，AppID 必须开启 Associated Domains 功能，请到[苹果开发者网站](https://developer.apple.com)，选择 Certificate, Identifiers & Profiles，选择相应的 AppID，开启 Associated Domains。注意：当 AppID 重新编辑过之后，需要更新相应的 mobileprovision 证书。(详细步骤请参考[openinstall官方文档](https://www.openinstall.io))  
- 在左侧导航器中点击您的项目  
- 选择'Capabilities'标签  
- 打开'Associated Domains'开关  
- 添加openinstall官网后台中应用对应的关联域名（iOS集成->iOS应用配置->关联域名(Associated Domains)）


#### 3 携带参数安装 <span style="margin-left: 5px;display: inline-block;background: red;color: #fff;border-radius: 3px;padding: 2px 3px;font-size: 12px;">（高级版功能）</span>
##### 获取安装参数  
``` js
window.openinstall.getInstall(function(data){
    console.log('openinstall.getInstall success: ' + data);
}, function(msg){
    console.log('openinstall.getInstall error: ' + msg);
});
```
也可传入一个整形数值，单位秒，指定时间未返回将超时  
``` js
window.openinstall.getInstall(function(data){
    console.log('openinstall.getInstall success: ' + data);
}, function(msg){
    console.log('openinstall.getInstall error: ' + msg);
}, 10);
```
成功回调的data数据格式  
``` json
{"channel": "渠道号", "data": "自定义数据"}
```

#### 4 渠道统计 <span style="margin-left: 5px;display: inline-block;background: red;color: #fff;border-radius: 3px;padding: 2px 3px;font-size: 12px;">（高级版功能）</span>  
SDK 会自动完成访问量、点击量、安装量、活跃量、留存率等统计工作。

##### 4.1 注册量统计  
如需统计每个渠道的注册量（对评估渠道质量很重要），可根据自身的业务规则，在确保用户完成 app 注册的情况下调用相关api  
``` js
window.openinstall.reportRegister();
```

##### 4.2 渠道效果统计  
效果点建立在渠道基础之上，主要用来统计终端用户对某些特殊业务的使用效果，如充值金额，分享次数等等。  

调用接口前，请先进入管理后台“效果点管理”中添加效果点  

![效果点管理](./resources/effect_point.png)  

调用接口时，请保证代码中的效果点ID与后台创建的效果点ID一致  
``` js
window.openinstall.reportEffectPoint("effect_test", 1);
```
第一个参数为“效果点ID”，字符串类型  
第二个参数为“效果点值”，数字类型  
  
调用接口后，可在后台查看效果点统计数据

