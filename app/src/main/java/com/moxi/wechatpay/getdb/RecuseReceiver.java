package com.moxi.wechatpay.getdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moxi.wechatpay.MainActivity;
import com.moxi.wechatpay.access.AirAccessibilityService;

/**
 * Created by zhou on 2018/3/7.
 */

public class RecuseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.moxi.killedPayService")){
            Log.i("RecuseReceiver","开始救服务");
            context.startService(new Intent(context, MoxiPayService.class));
            context.startService(new Intent(context, AirAccessibilityService.class));
        }
    }
}
