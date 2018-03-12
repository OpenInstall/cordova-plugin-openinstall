package io.openinstall.cordova;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;


public class OpenInstallPlugin extends CordovaPlugin {

  public static final String TAG = "OpenInstallPlugin";

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
    } else if ("getWakeUp".equals(action)) {
      getWakeUp(args, callbackContext);
      return true;
    } else if ("reportRegister".equals(action)) {
      reportRegister(args, callbackContext);
      return true;
    } else if ("setDebug".equals(action)) {
      setDebug(args, callbackContext);
      return true;
    }
    return false;
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
    }, timeout * 1000);
  }

  protected void getWakeUp(CordovaArgs args, final CallbackContext callbackContext) {
    String uri = "";
    if (args != null && !args.isNull(0)) {
      uri = args.optString(0);
    }
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
//              e.printStackTrace();
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

  protected void reportRegister(CordovaArgs args, final CallbackContext callbackContext) {
    Log.d(TAG, "reportRegister");
    OpenInstall.reportRegister();
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
