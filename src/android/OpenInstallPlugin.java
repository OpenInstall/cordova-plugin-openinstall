package io.openinstall.cordova;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.fm.openinstall.Configuration;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppInstallRetryAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

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
    private static final String METHOD_INSTALL_RETRY = "getInstallCanRetry";
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
            init(callbackContext);
            return true;
        } else if (METHOD_INSTALL.equals(action)) {
            getInstall(args, callbackContext);
            return true;
        } else if (METHOD_INSTALL_RETRY.equals(action)) {
            getInstallCanRetry(args, callbackContext);
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

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        callbackContext.sendPluginResult(result);
    }

    protected void init(CallbackContext callbackContext) {
        OpenInstall.init(cordova.getActivity(), configuration);

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        callbackContext.sendPluginResult(result);
    }

    protected void getInstall(CordovaArgs args, final CallbackContext callbackContext) {
        int timeout = 10;
        if (args != null && !args.isNull(0)) {
            timeout = args.optInt(0);
        }
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                Log.d(TAG, "getInstall # " + appData.toString());
                callbackContext.success(parseData(appData));
            }
        }, timeout);
    }

    protected void getInstallCanRetry(CordovaArgs args, final CallbackContext callbackContext) {
        int timeout = 3;
        if (args != null && !args.isNull(0)) {
            timeout = args.optInt(0);
        }
        OpenInstall.getInstallCanRetry(new AppInstallRetryAdapter() {
            @Override
            public void onInstall(AppData appData, boolean retry) {
                Log.d(TAG, "getInstallCanRetry # " + appData.toString());
                JSONObject jsonObject = parseData(appData);
                try {
                    jsonObject.put("retry", retry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callbackContext.success(jsonObject);
            }
        }, timeout);
    }

    protected void registerWakeUpHandler(CallbackContext callbackContext) {
        this.wakeupCallbackContext = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        // 首次调用，程序启动时
        Intent intent = cordova.getActivity().getIntent();
        getWakeUp(intent, wakeupCallbackContext);
    }

    private void getWakeUp(Intent intent, final CallbackContext callbackContext) {
        OpenInstall.getWakeUpAlwaysCallback(intent, new AppWakeUpListener() {
            @Override
            public void onWakeUpFinish(AppData appData, Error error) {
                if (error != null) {
                    Log.d(TAG, "getWakeUpAlwaysCallback # " + error.toString());
                }
                JSONObject jsonObject = parseData(appData);
                PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
        });
    }

    protected void reportRegister(CordovaArgs args, final CallbackContext callbackContext) {
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

    private JSONObject parseData(AppData appData) {
        JSONObject jsonObject = new JSONObject();
        if (appData != null) {
            try {
                jsonObject.put("channel", appData.getChannel());
                jsonObject.put("data", appData.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

}
