package io.openinstall.cordova;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

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

  public static final String TAG = "OpenInstallPlugin";

  private CallbackContext wakeupCallbackContext = null;

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    init();
  }

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    Log.d(TAG, "execute # action=" + action);
    if (TextUtils.isEmpty(action)) {
      return false;
    }
    if ("getInstall".equals(action)) {
      getInstall(args, callbackContext);
      return true;
    } else if ("registerWakeUpHandler".equals(action)) {
      registerWakeUpHandler(callbackContext);
      return true;
    } else if ("reportRegister".equals(action)) {
      reportRegister(args, callbackContext);
      return true;
    } else if ("reportEffectPoint".equals(action)) {
      reportEffectPoint(args, callbackContext);
      return true;
    } else if ("setDebug".equals(action)) {
      setDebug(args, callbackContext);
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

  protected void init() {
    Log.d(TAG, "init");
    OpenInstall.init(cordova.getActivity());
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

  protected void setDebug(CordovaArgs args, final CallbackContext callbackContext) {
    boolean debug = false;
    if (args != null && !args.isNull(0)) {
      debug = args.optBoolean(0);
    }
    Log.d(TAG, "setDebug # " + debug);
    OpenInstall.setDebug(debug);
  }

}
