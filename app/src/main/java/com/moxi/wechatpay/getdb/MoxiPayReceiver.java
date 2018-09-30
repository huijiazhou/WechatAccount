package com.moxi.wechatpay.getdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moxi.wechatpay.MainActivity;

/**
 * Created by zhou on 2018/3/4.
 */

public class MoxiPayReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        new Thread(){
            @Override
            public void run() {
                Utils.init(context);
                Utils.copyDatabase(context);
            }
        }.start();
    }

}
