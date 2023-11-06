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
import android.widget.Toast;
import android.zyapi.CommonApi;

import com.alibaba.fastjson.JSONObject;
import com.qs.wiget.PrintUtils;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;


public class Qs5501Module extends UniModule {

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

    //run ui thread
    @UniJSMethod(uiThread = false)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--"+options);
        if(callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", "success");
            callback.invoke(data);
            //callback.invokeAndKeepAlive(data);
        }
    }

    //run JS thread
    @UniJSMethod (uiThread = false)
    public JSONObject scan(){
        if(scanBroadcastReceiver==null) {
            if (!printInit) {
                PrintUtils.initPrintUtils(mUniSDKInstance.getOriginalContext());
                printInit = true;
            }

            scanBroadcastReceiver = new ScanBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.qs.scancode");
            mUniSDKInstance.getOriginalContext().registerReceiver(scanBroadcastReceiver, intentFilter);
        }

        JSONObject data = new JSONObject();
        data.put("code", "success");
        Intent mIntent = new Intent("ismart.intent.scandown");
        // 触发扫描
        mUniSDKInstance.getOriginalContext().sendBroadcast(mIntent);
        return data;
    }

    @UniJSMethod(uiThread = false)
    public JSONObject printText(JSONObject options) {
        JSONObject data = new JSONObject();
        try {
            if (!printInit) {
                PrintUtils.initPrintUtils(mUniSDKInstance.getOriginalContext());
                printInit = true;
            }
            String text = options.get("text") != null ? options.get("text").toString() : "";
            int size = options.get("size") != null ? Integer.parseInt(options.get("size").toString()) : 1;
            int align = options.get("align") != null ? Integer.parseInt(options.get("align").toString()) : 1;
            boolean isLabel = options.get("isLabel") != null ? Boolean.parseBoolean(options.get("isLabel").toString()) : false;
            Log.e(TAG, "testAsyncFunc--" + options + "," + text);
            PrintUtils.printText(size, align, text, isLabel);
            data.put("code", 10);
            data.put("msg", true);
        }catch (Exception ex){
            data.put("code", 1);
            data.put("msg", ex.getMessage());
        }
        return data;
    }

    @UniJSMethod(uiThread = false)
    public JSONObject printBitmap(JSONObject options) {
        JSONObject data = new JSONObject();
        try {
            if (!printInit) {
                PrintUtils.initPrintUtils(mUniSDKInstance.getOriginalContext());
                printInit = true;
            }
            String bitmapBase64 = options.get("bitmap") != null ? options.get("bitmap").toString() : "";
            byte[] bitmapData=Base64.decode(bitmapBase64,Base64.NO_WRAP);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapData,0,bitmapData.length);
            int align = options.get("align") != null ? Integer.parseInt(options.get("align").toString()) : 1;
            boolean isLabel = options.get("isLabel") != null ? Boolean.parseBoolean(options.get("isLabel").toString()) : false;
            PrintUtils.printBitmap(align, bitmap, isLabel);
            data.put("code", 0);
            data.put("msg", true);
        }catch (Exception ex){
            data.put("code", 1);
            data.put("msg", ex.getMessage());
        }
        return data;
    }

    @UniJSMethod(uiThread = false)
    public JSONObject printBarCode(JSONObject options) {
        JSONObject data = new JSONObject();
        try {
            if (!printInit) {
                PrintUtils.initPrintUtils(mUniSDKInstance.getOriginalContext());
                printInit = true;
            }
            String dataStr = options.get("data") != null ? options.get("data").toString() : "";
            int size = options.get("size") != null ? Integer.parseInt(options.get("size").toString()) : 1;
            int align = options.get("align") != null ? Integer.parseInt(options.get("align").toString()) : 1;
            int width = options.get("width") != null ? Integer.parseInt(options.get("width").toString()) : 1;
            int height = options.get("height") != null ? Integer.parseInt(options.get("height").toString()) : 1;
            boolean isShowBarStr = options.get("isShowBarStr") != null ? Boolean.parseBoolean(options.get("isShowBarStr").toString()) : false;
            boolean isLabel = options.get("isLabel") != null ? Boolean.parseBoolean(options.get("isLabel").toString()) : false;
            PrintUtils.printBarCode(align, width,height,dataStr, isShowBarStr,size,isLabel);
            data.put("code", 0);
            data.put("msg", true);
        }catch (Exception ex){
            data.put("code", 1);
            data.put("msg", ex.getMessage());
        }
        return data;
    }

    @UniJSMethod(uiThread = false)
    public JSONObject printQRCode(JSONObject options) {
        JSONObject data = new JSONObject();
        try {
            if (!printInit) {
                PrintUtils.initPrintUtils(mUniSDKInstance.getOriginalContext());
                printInit = true;
            }
            String dataStr = options.get("data") != null ? options.get("data").toString() : "";
            int size = options.get("size") != null ? Integer.parseInt(options.get("size").toString()) : 1;
            int align = options.get("align") != null ? Integer.parseInt(options.get("align").toString()) : 1;
            int width = options.get("width") != null ? Integer.parseInt(options.get("width").toString()) : 1;
            int height = options.get("height") != null ? Integer.parseInt(options.get("height").toString()) : 1;
            boolean isShowBarStr = options.get("isShowBarStr") != null ? Boolean.parseBoolean(options.get("isShowBarStr").toString()) : false;
            boolean isLabel = options.get("isLabel") != null ? Boolean.parseBoolean(options.get("isLabel").toString()) : false;
            PrintUtils.printQRCode(align, width,height,dataStr, isShowBarStr,size,isLabel);
            data.put("code", 0);
            data.put("msg", true);
        }catch (Exception ex){
            data.put("code", 1);
            data.put("msg", ex.getMessage());
        }
        return data;
    }

    @UniJSMethod(uiThread = false)
    public JSONObject printEscCommand(byte[] cmdData) {
        Log.d(TAG,PrintUtils.byteToString(cmdData,cmdData.length));
        JSONObject data = new JSONObject();
        //byte[] cmdData=null;
        PrintUtils.send(cmdData);
        data.put("code", 0);
        data.put("msg", true);
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
