# cordova-plugin-openinstall
openinstalll的cordova插件

## 安装插件

下载zip文件或者克隆仓库到本地
```
$ cordova plugin add D:/cordova-plugin-openinstall --variable OPENINSTALL_APPKEY=[appkey] --variable OPENINSTALL_SCHEME=[scheme]

```

## 调用

### 获取安装参数
``` js
window.openinstall.getInstall(function(data){
    console.log('openinstall.getInstall success: ' + data);
}, function(msg){
    console.log('openinstall.getInstall error: ' + msg);
});
```
成功回调的data数据格式
``` json
{"channel": "渠道号", "data": "自定义数据"}
```

### 注册上报
``` js
window.openinstall.reportRegister();
```

