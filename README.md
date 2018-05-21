# cordova-plugin-openinstall
openinstall 的 cordova 插件

## 安装插件

```
$ cordova plugin add cordova-plugin-openinstall --variable OPENINSTALL_APPKEY=[appkey] --variable OPENINSTALL_SCHEME=[scheme]

```

## 调用API

### 获取安装参数
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

### 拉起参数获取
调用以下代码注册拉起回调，应尽早调用。如在 `deviceready` 事件回调之时注册
``` js
window.openinstall.registerWakeUpHandler(function(data){
  console.log("openinstall.wakeup success : channel=" + data.channel + ", data=" + data.data);
}, function(msg){
  console.log("openinstall.wakeup error : " + msg)
});
```
__注意__：对于 iOS，为确保能正常跳转，AppID 必须开启 Associated Domains 功能，请到[苹果开发者网站](https://developer.apple.com)，选择 Certificate, Identifiers & Profiles，选择相应的 AppID，开启 Associated Domains。注意：当 AppID 重新编辑过之后，需要更新相应的 mobileprovision 证书。(详细步骤请参考[openinstall官方文档](https://www.openinstall.io))

### 注册上报
``` js
window.openinstall.reportRegister();
```

### 效果点统计
``` js
window.openinstall.reportEffectPoint("effect_test", 1);
```
第一个参数为“效果点ID”，字符串类型  
第二个参数为“效果点值”，数字类型

