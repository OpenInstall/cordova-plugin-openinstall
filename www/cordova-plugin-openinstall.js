var exec = require('cordova/exec');

module.exports = {
    
    // init: function (onSuccess, onError) {
    //     exec(onSuccess, onError, "OpenInstallPlugin", "init", []);
    // },

    getInstall: function (onSuccess, onError){
        exec(onSuccess, onError, "OpenInstallPlugin", "getInstall", []);
    },

    getWakeUp: function (onSuccess, onError, url){
        
        exec(onSuccess, onError, "OpenInstallPlugin", "getWakeUp", [url]);
    },

    reportRegister: function (onSuccess, onError){
        exec(onSuccess, onError, "OpenInstallPlugin", "reportRegister", []);
    },

    setDebug: function (onSuccess, onError, debug){

        exec(onSuccess, onError, "OpenInstallPlugin", "setDebug", []);
    },

};
