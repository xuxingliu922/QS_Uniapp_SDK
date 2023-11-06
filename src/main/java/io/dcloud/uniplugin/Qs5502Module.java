package io.dcloud.uniplugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.qs.wiget.PrintUtils;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


public class Qs5502Module extends UniModule {

    //扫描信息接收广播
    private ScanBroadcastReceiver scanBroadcastReceiver=null;
    String TAG = "PrintModule";
    public static int REQUEST_CODE = 1000;

    static boolean printInit=false;

    /**
     * 获取扫描信息
     *
     * @author wu
     *
     */
    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String barCode = intent.getExtras().getString("data");
            Log.d(TAG,"扫描信息：" + barCode);
            Map<String,Object> params=new HashMap<>();
            params.put("scandata",barCode);
            mUniSDKInstance.fireGlobalEventCallback("scanEvent", params);
        }
    }

    @UniJSMethod(uiThread = true)
    public void exitsys (String name, UniJSCallback callback) {
        System.exit(0 );
        callback.invoke(1);
    }

    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject scan(){
        if(scanBroadcastReceiver==null){
            scanBroadcastReceiver = new ScanBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.qs.scancode");
            mUniSDKInstance.getOriginalContext().registerReceiver(scanBroadcastReceiver, intentFilter);
        }

        JSONObject data = new JSONObject();
        data.put("code", "success");
        Intent mIntent = new Intent("hbyapi.intent.key_scan_down");
        // 触发扫描
        mUniSDKInstance.getOriginalContext().sendBroadcast(mIntent);
        return data;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && data.hasExtra("respond")) {
            Log.e("PrintModule", "原生页面返回----"+data.getStringExtra("respond"));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @UniJSMethod (uiThread = true)
    public void gotoNativePage(){
        if(mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mUniSDKInstance.getContext(), NativePageActivity.class);
            ((Activity)mUniSDKInstance.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if(scanBroadcastReceiver!=null) {
            mUniSDKInstance.getOriginalContext().unregisterReceiver(scanBroadcastReceiver);
        }
    }
}
