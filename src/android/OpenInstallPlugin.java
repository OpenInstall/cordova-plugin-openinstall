package io.openinstall.cordova;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenInstallPlugin extends CordovaPlugin {

    public static final String TAG = "OpenInstallPlugin" ;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        init();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute # action=" + action);
        switch (action){
//            case "init":
//                init();
//                break;
            case "getInstall":
                getInstall(callbackContext);
                break;
            case "getWakeUp":
                String uri = "";
                if(args != null && !args.isNull(0)){
                    uri = args.optString(0);
                }
                getWakeUp(uri, callbackContext);
                break;
            case "reportRegister":
                reportRegister();
                break;
            case "setDebug":
                boolean debug = false;
                if(args != null && !args.isNull(0)){
                    debug = args.optBoolean(0);
                }
                setDebug(debug);
                break;
        }
        return false;
    }

    private void init(){
        Log.d(TAG, "init");
        com.fm.openinstall.OpenInstall.init(cordova.getActivity());
    }

    private void getInstall(CallbackContext callbackContext){
        Log.d(TAG, "getInstall");
        com.fm.openinstall.OpenInstall.getInstall(new AppInstallListener() {
            @Override
            public void onInstallFinish(AppData appData, Error error) {
                if(error == null){
                    Log.d(TAG, "onInstallFinish # " + (appData==null?"AppData is null":appData.toString()));
                    if(appData != null){
                        String channel = appData.getChannel();
                        String data = appData.getData();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("channel", channel);
                            jsonObject.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callbackContext.success(jsonObject);
                    }else {
                        callbackContext.success();
                    }
                }else{
                    Log.d(TAG, "onInstallFinish # " + error.toString());
                    callbackContext.error(error.toString());
                }
            }
        });
    }

    private void getWakeUp(String uri, CallbackContext callbackContext){
        Log.d(TAG, "getWakeUp # " + uri);
        Intent intent = new Intent();
        intent.setData(Uri.parse(uri));
        Log.d(TAG, "getWakeUp # intent : " + intent.getDataString());
        com.fm.openinstall.OpenInstall.getWakeUp(intent, new AppWakeUpListener() {
            @Override
            public void onWakeUpFinish(AppData appData, Error error) {
                if(error == null){
                    Log.d(TAG, "onWakeUpFinish # " + (appData==null?"AppData is null":appData.toString()));
                    if(appData != null){
                        String channel = appData.getChannel();
                        String data = appData.getData();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("channel", channel);
                            jsonObject.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callbackContext.success(jsonObject);
                    }else {
                        callbackContext.success();
                    }
                }else{
                    Log.d(TAG, "onWakeUpFinish # " + error.toString());
                    callbackContext.error(error.toString());
                }
            }
        });
    }

    private void reportRegister(){
        Log.d(TAG, "reportRegister");
        com.fm.openinstall.OpenInstall.reportRegister();
    }

    private void setDebug(boolean debug){
        Log.d(TAG, "setDebug # " + debug);
        com.fm.openinstall.OpenInstall.setDebug(debug);
    }

}
