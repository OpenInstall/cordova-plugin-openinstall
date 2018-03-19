var exec = require('cordova/exec');

module.exports = {
    
    /**
     * 获取安装参数
     * @param onSuccess 成功回调：数据格式为 {'channel': 1002, 'data': {'key': 'value'}}
     * @param time 超时时间：整形值，单位秒
     */
    getInstall: function (onSuccess, onError, time){
        exec(onSuccess, onError, "OpenInstallPlugin", "getInstall", [time]);
    },

    /**
     * 获取唤醒参数（App未运行被唤醒时）
     * 仅适用Android平台
     */
    getWakeUp: function (onSuccess, onError){
        exec(onSuccess, onError, "OpenInstallPlugin", "getWakeUp", []);
    },

    /**
     * 注册唤醒监听（App后台运行时被拉起）
     * 仅适用Android平台
     */
    registerWakeUpHandler: function(onSuccess, onError){
        exec(onSuccess, onError, "OpenInstallPlugin", "registerWakeUpHandler", []);
    },

    /**
     * 上报注册量
     */
    reportRegister: function(){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "reportRegister", []);
    }

};
