var exec = require('cordova/exec');

module.exports = {
    

    /**
     * android初始化前配置
     * @param options 配置参数：
     * {
     *      adEnabled: true, 
     *      macDisabled: true,
     *      imeiDisabled: true,
     *      gaid: "通过 google api 获取到的 advertisingId",
     *      oaid: "通过移动安全联盟获取到的 oaid",
     *      ...
     * }
     */
    configAndroid: function(options){
        function pass() {};
        
        exec(pass, pass, "OpenInstallPlugin", "config", [options]);
    },
	
	/**
	 * Android平台初始化前配置
	 * @param enabled 布尔值，是否允许读取
	 */
	serialEnabled: function(enabled){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "serialEnabled", [enabled]);
    },
	
	clipBoardEnabled: function(enabled){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "clipBoardEnabled", [enabled]);
    },

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
     * @param onError 错误回调：返回错误信息
     * @param time 超时时间：整形值，单位秒
     */
    getInstall: function (onSuccess, onError, time){
        exec(onSuccess, onError, "OpenInstallPlugin", "getInstall", [time]);
    },

    /**
     * 获取安装参数（仅 Android 支持）
     * @param onSuccess 成功回调：数据格式为 {'channel': 1002, 'data': {'key': 'value'}}
     * @param onError 错误回调：返回错误信息
     * @param time 超时时间：整形值，单位秒
     */
    getInstallCanRetry: function (onSuccess, onError, time){
        exec(onSuccess, onError, "OpenInstallPlugin", "getInstallCanRetry", [time]);
    },

    /**
     * 注册唤醒监听
     * @param onSuccess 成功回调：数据格式为 {'channel': 1002, 'data': {'key': 'value'}}
     * @param onError 错误回调：返回错误信息
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
     * @param extras 效果点自定义参数 <string,string>
     */
    reportEffectPoint: function(pointId, pointValue, extras){
        function pass() {};
        exec(pass, pass, "OpenInstallPlugin", "reportEffectPoint", [pointId, pointValue, extras||{}]);
    }

};
