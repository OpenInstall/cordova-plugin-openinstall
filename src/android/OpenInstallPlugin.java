package io.openinstall.cordova;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.fm.openinstall.Configuration;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenInstallPlugin extends CordovaPlugin {

    private static final String TAG = "OpenInstallPlugin";

    private static final String METHOD_CONFIG = "config";
    private static final String METHOD_INIT = "init";
    private static final String METHOD_INSTALL = "getInstall";
    private static final String METHOD_WAKEUP = "registerWakeUpHandler";
    private static final String METHOD_REGISTER = "reportRegister";
    private static final String METHOD_EFFECT = "reportEffectPoint";

    private Configuration configuration = null;
    private CallbackContext wakeupCallbackContext = null;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        // init();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute # action=" + action);
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        if (METHOD_CONFIG.equals(action)) {
            config(args, callbackContext);
            return true;
        } else if (METHOD_INIT.equals(action)) {
            init();
            return true;
        } else if (METHOD_INSTALL.equals(action)) {
            getInstall(args, callbackContext);
            return true;
        } else if (METHOD_WAKEUP.equals(action)) {
            registerWakeUpHandler(callbackContext);
            return true;
        } else if (METHOD_REGISTER.equals(action)) {
            reportRegister(args, callbackContext);
            return true;
        } else if (METHOD_EFFECT.equals(action)) {
            reportEffectPoint(args, callbackContext);
            return true;
        }
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (wakeupCallbackContext != null) {
            getWakeUp(intent, wakeupCallbackContext);
        }
    }

    protected void config(CordovaArgs args, final CallbackContext callbackContext) {

        boolean adEnabled = args.optBoolean(0);
        boolean macDisabled = args.optBoolean(1);
        boolean imeiDisabled = args.optBoolean(2);
        String gaid = args.optString(3);
        String oaid = args.optString(4);

        Log.d(TAG, "config # " + "adEnabled = " + adEnabled + ", macDisabled = " + macDisabled
                + ", imeiDisabled = " + imeiDisabled + ", gaid = " + gaid + ", oaid = " + oaid);
        Configuration.Builder builder = new Configuration.Builder();
        builder.adEnabled(adEnabled);
        if (macDisabled) {
            builder.macDisabled();
        }
        if (imeiDisabled) {
            builder.imeiDisabled();
        }
        builder.gaid(gaid).oaid(oaid);
        configuration = builder.build();
    }

    protected void init() {
        Log.d(TAG, "init");
        OpenInstall.init(cordova.getActivity(), configuration);
    }

    protected void getInstall(CordovaArgs args, final CallbackContext callbackContext) {
        int timeout = -1;
        if (args != null && !args.isNull(0)) {
            timeout = args.optInt(0);
        }
        Log.d(TAG, "getInstall # " + timeout + "s");
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                Log.d(TAG, "onInstallFinish # " + (appData == null ? "AppData is null" : appData.toString()));
                if (appData != null) {
                    String channel = appData.getChannel();
                    String data = appData.getData();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("channel", channel);
                        jsonObject.put("data", data);
                    } catch (JSONException e) {
//            e.printStackTrace();
                    }
                    callbackContext.success(jsonObject);
                } else {
                    callbackContext.success();
                }
            }
        }, timeout);
    }

    private void getWakeUp(Intent intent, final CallbackContext callbackContext) {
        Log.d(TAG, "getWakeUp # intent : " + intent.getDataString());
        OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
            @Override
            public void onWakeUp(AppData appData) {
                Log.d(TAG, "onWakeUpFinish # " + (appData == null ? "AppData is null" : appData.toString()));
                if (appData != null) {
                    String channel = appData.getChannel();
                    String data = appData.getData();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("channel", channel);
                        jsonObject.put("data", data);
                    } catch (JSONException e) {
//              e.printStackTrace();
                    }
                    PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                } else {
                    PluginResult result = new PluginResult(PluginResult.Status.OK);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }

    protected void registerWakeUpHandler(CallbackContext callbackContext) {
        this.wakeupCallbackContext = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);

        Intent intent = cordova.getActivity().getIntent();
        if (intent == null || TextUtils.isEmpty(intent.getDataString())) {
            return;
        }
        if (wakeupCallbackContext != null) {
            getWakeUp(intent, wakeupCallbackContext);
        }
    }

    protected void reportRegister(CordovaArgs args, final CallbackContext callbackContext) {
        Log.d(TAG, "reportRegister");
        OpenInstall.reportRegister();
    }

    protected void reportEffectPoint(CordovaArgs args, final CallbackContext callbackContext) {
        if (args != null && !args.isNull(0) && !args.isNull(1)) {
            String pointId = args.optString(0);
            long pointValue = args.optLong(1);
            Log.d(TAG, "reportEffectPoint # pointId:" + pointId + ", pointValue:" + pointValue);
            OpenInstall.reportEffectPoint(pointId, pointValue);
        }
    }

}
