package io.openinstall.cordova;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
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

    public static final String TAG = "OpenInstallPlugin";

    private CallbackContext callbackContext;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        init();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute # action=" + action);
        this.callbackContext = callbackContext;
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        if ("getInstall".equals(action)) {
            getInstall();
        } else if ("getWakeUp".equals(action)) {
            String uri = "";
            if (args != null && !args.isNull(0)) {
                uri = args.optString(0);
            }
            getWakeUp(uri);
        } else if ("reportRegister".equals(action)) {
            reportRegister();
        } else if ("setDebug".equals(action)) {
            boolean debug = false;
            if (args != null && !args.isNull(0)) {
                debug = args.optBoolean(0);
            }
            setDebug(debug);
        }
        return false;
    }

    protected void init() {
        Log.d(TAG, "init");
        OpenInstall.init(cordova.getActivity());
    }

    protected void getInstall() {
        Log.d(TAG, "getInstall");
        OpenInstall.getInstall(new AppInstallListener() {
            @Override
            public void onInstallFinish(AppData appData, Error error) {
                if (error == null) {
                    Log.d(TAG, "onInstallFinish # " + (appData == null ? "AppData is null" : appData.toString()));
                    if (appData != null) {
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
                    } else {
                        callbackContext.success();
                    }
                } else {
                    Log.d(TAG, "onInstallFinish # " + error.toString());
                    callbackContext.error(error.toString());
                }
            }
        });
    }

    protected void getWakeUp(String uri) {
        Log.d(TAG, "getWakeUp # " + uri);
        Intent intent = new Intent();
        intent.setData(Uri.parse(uri));
        Log.d(TAG, "getWakeUp # intent : " + intent.getDataString());
        OpenInstall.getWakeUp(intent, new AppWakeUpListener() {
            @Override
            public void onWakeUpFinish(AppData appData, Error error) {
                if (error == null) {
                    Log.d(TAG, "onWakeUpFinish # " + (appData == null ? "AppData is null" : appData.toString()));
                    if (appData != null) {
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
                    } else {
                        callbackContext.success();
                    }
                } else {
                    Log.d(TAG, "onWakeUpFinish # " + error.toString());
                    callbackContext.error(error.toString());
                }
            }
        });
    }

    protected void reportRegister() {
        Log.d(TAG, "reportRegister");
        OpenInstall.reportRegister();
    }

    protected void setDebug(boolean debug) {
        Log.d(TAG, "setDebug # " + debug);
        OpenInstall.setDebug(debug);
    }

}
