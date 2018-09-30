package com.moxi.wechatpay.getdb;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.moxi.wechatpay.R;


/**
 * Created by zhou on 2018/3/4.
 */

public class MoxiPayService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        getNotification();
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer() {
        Intent myIntent = new Intent();
        myIntent.setAction("com.moxi.wechatpay.TIMER_ACTION_REPEATING");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, myIntent,
                0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30*60*1000, sender);

    }

    public void getNotification() {
        //得到NotificationManager的对象，用来实现发送Notification
        NotificationManager notifyManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)

                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("微信到账通知获取")
                //设置通知内容
                .setContentText("不要杀死我哦")
                .setDefaults(Notification.FLAG_NO_CLEAR|Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONGOING_EVENT );

        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());


    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent();
        intent.setAction("com.moxi.killedPayService");
        sendBroadcast(intent);
        super.onDestroy();

    }
}
