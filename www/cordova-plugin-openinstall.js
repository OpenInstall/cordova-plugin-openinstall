var exec = require('cordova/exec');

module.exports = {
    /**
     * 初始化
     */
    init: function(){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "init", []);
    },
	
    /**
     * 获取安装参数
     * @param onSuccess 成功回调：数据格式为 {'channel': 1002, 'data': {'key': 'value'}}
     * @param time 超时时间：整形值，单位秒
     */
    getInstall: function (onSuccess, onError, time){
        exec(onSuccess, onError, "OpenInstallPlugin", "getInstall", [time]);
    },

    /**
     * 注册唤醒监听
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
    },

    /**
     * 上报效果点
     * @param pointId 效果点ID
     * @param pointValue 效果点值 (数字类型)
     */
    reportEffectPoint: function(pointId, pointValue){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "reportEffectPoint", [pointId, pointValue]);
    }

};
