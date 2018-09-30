package com.moxi.wechatpay.access;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.moxi.wechatpay.R;
import com.moxi.wechatpay.getdb.NoficationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AirAccessibilityService extends AccessibilityService {
    public static boolean ALL = false;
    private static final String TAG = "AirAccessibilityService";
    private List<AccessibilityNodeInfo> parents;
    private boolean auto = false;
    private int lastbagnum;
    String pubclassName;
    String lastMAIN;
    private boolean WXMAIN = false;

    private boolean enableKeyguard = true;//默认有屏幕锁
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    //唤醒屏幕相关
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        parents = new ArrayList<>();
        getNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void getNotification() {
        if (!NoficationUtil.isNotificationEnabled(this)){
            Toast.makeText(this,"通知被关闭",Toast.LENGTH_LONG).show();
        }
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
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        //获取当前聊天页面的根布局
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        Log.e(TAG, "有事件" + eventType);

//        TYPE_VIEW_CLICKED    // 当View被点击时发送此事件。
//                TYPE_VIEW_LONG_CLICKED    // 当View被长按时发送此事件。
//        TYPE_VIEW_FOCUSED    // 当View获取到焦点时发送此事件。
//                TYPE_WINDOW_STATE_CHANGED    // 当Window发生变化时发送此事件。
//        TYPE_VIEW_SCROLLED    // 当View滑动时发送此事件。
        switch (eventType) {

            //当屏幕内容发生改变时TYPE_WINDOW_CONTENT_CHANGED
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e(TAG, "youtongzhi1");
                //获取聊天信息
//                getWeChatLog(rootNode);
                List<CharSequence> texts1 = event.getText();
                if (!texts1.isEmpty()) {
                    for (CharSequence text : texts1) {
                        String content = text.toString();
                        Log.e(TAG, "youtongzhi"+content);
                        if (content.contains("微信支付")) {
                            if (event.getParcelableData() != null &&
                                    event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    auto = true;
                                    wakeAndUnlock2(true);
                                    pendingIntent.send();
                                    Log.e(TAG, "进入微信" + auto + event.getClassName().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            //当通知栏发生改变时TYPE_NOTIFICATION_STATE_CHANGED
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e(TAG, "youtongzhi");
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        Log.e(TAG, "youtongzhi"+content);
                        if (content.contains("微信支付")) {
                            if (event.getParcelableData() != null &&
                                    event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    auto = true;
                                    wakeAndUnlock2(true);
                                    pendingIntent.send();
                                    Log.e(TAG, "进入微信" + auto + event.getClassName().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //获取聊天信息
                getWeChatLog(rootNode);
                break;
            // 当View滑动时发送此事件。
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                //获取聊天信息
                getWeChatLog(rootNode);
                break;
        }
    }

    /**
     * 遍历所有控件获取聊天信息
     *
     * @param rootNode
     */

    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            String listviewId = SharedPreferencesUtils.getString(this,"listviewId","com.tencent.mm:id/a_e");
            String payId = SharedPreferencesUtils.getString(this,"payId","com.tencent.mm:id/apx");
            String numId = SharedPreferencesUtils.getString(this,"numId","com.tencent.mm:id/j4");
            //获取所有聊天的线性布局
            List<AccessibilityNodeInfo> listChatRecord = rootNode.findAccessibilityNodeInfosByViewId(listviewId);
            Log.d(TAG, "listChatRecord.size():  "+listChatRecord.size());
            if (listChatRecord.size() == 0) {
                //获取聊天主界面的消息
                List<AccessibilityNodeInfo> listRootChatRecordString = rootNode.findAccessibilityNodeInfosByViewId(payId);
                List<AccessibilityNodeInfo> listRootChatRecordNum = rootNode.findAccessibilityNodeInfosByViewId(numId);
                Log.d(TAG, "listRootChatRecordString.size():  "+listRootChatRecordString.size());
                Log.d(TAG, "listRootChatRecordNum.size():  "+listRootChatRecordNum.size());
                for (int o = 0 ; o < listRootChatRecordNum.size(); o++){
                    Log.d(TAG, "listRootChatRecordNum.getText():  "+o+"  "+listRootChatRecordNum.get(o).getText());
                    Log.d(TAG, "listRootChatRecordNum.getClassName():  "+o+"  "+listRootChatRecordNum.get(o).getParent().getClassName());
                }
               if (listRootChatRecordString.size() == 0 || listRootChatRecordNum.size() == 0){
                   return;
               }
               else {

                   //获取第一行跟布局（即是最新的那条消息）
                   AccessibilityNodeInfo finalNodeRoot = listRootChatRecordString.get(0);

                   if (finalNodeRoot == null){
                       return;
                   }

                   if ("微信支付".equals(finalNodeRoot.getText().toString())){
                       Log.d(TAG, "微信支付:  跟界面");

                       if (finalNodeRoot.getParent().getChild(0) != null){
                           Log.d(TAG, "微信支付finalNodeRoot       " + finalNodeRoot.getParent().getChild(0).getText());
                           finalNodeRoot.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                       }
                   }

                   Log.d(TAG, "finalNodeRoot.getText():  "+finalNodeRoot.getText());


//                   if (finalNodeRoot.getParent().getChild(0) != null){
//                       Log.d(TAG, "finalNodeRoot.getParent().getChild(0).getText()       " + finalNodeRoot.getParent().getChild(0).getText());
//                       finalNodeRoot.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                   }

               }
            }
            else {
                //获取最后一行聊天的线性布局（即是最新的那条消息）
                AccessibilityNodeInfo finalNode = listChatRecord.get(listChatRecord.size() - 1);
                AccessibilityNodeInfo finalNode2 = finalNode.getChild(finalNode.getChildCount() - 1);

                StringBuffer stringBuffer = new StringBuffer();

                if (finalNode2 == null){
                    Log.d(TAG, "finalNode2:  ");
                    return;
                }

                for (int i = 0; i < finalNode2.getChildCount(); i++) {
                    AccessibilityNodeInfo node1 = finalNode2.getChild(i);
                    if (node1 == null){
                        continue;
                    }
                    if (node1.getText()!=null){
                        Log.d(TAG, "getWeChatLog: "+node1.getText());
                        stringBuffer.append(node1.getText());
                    }
                    for (int a = 0; a < node1.getChildCount(); a++){
                        AccessibilityNodeInfo node12 = node1.getChild(a);

                        if (node12 == null){
                            continue;
                        }

                        if (node12.getText()!=null){

                            if(a == 0 && !node12.getText().toString().equals("收款到账通知")){
                            return;
                            }

                            Log.d(TAG, "getWeChatLog2: "+node12.getText()+"   a   "+a);
                            stringBuffer.append(node12.getText());
                        }
                        for (int b = 0; b < node12.getChildCount(); b++){
                            AccessibilityNodeInfo node13 = node12.getChild(b);
                            if (node13 == null){
                                continue;
                            }
                            if (node13.getText()!=null){
                                Log.d(TAG, "getWeChatLog3: "+node13.getText());
                                stringBuffer.append(node13.getText());
                            }
                        }
                    }

                }

                Log.d(TAG, "getWeChatLog4: "+stringBuffer.toString());


                String url = "http://47.91.167.130:8032/appInterface/wechat/collect.do";
                OkHttpClient okHttpClient = new OkHttpClient();

                FormBody.Builder body = new FormBody.Builder();
                body.add("content", stringBuffer.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .post(body.build())
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "getWeChatLogonFailure: "+e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "getWeChatLogonResponse: "+response.body().string());
                    }
                });


                //发送广播
//            Intent intent=new Intent();
//            //设置Action
//            intent.setAction("aa");
//            //携带数据
//            intent.putExtra("data",stringBuffer.toString());
//            //发送广播
//            sendBroadcast(intent);
//            Toast.makeText(getApplicationContext(), "发送广播成功"+stringBuffer.toString(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void click(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void getLastPacket() {

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
        Log.e(TAG, "当前页面红包数老方法" + parents.size());
        if (parents.size() > 0) {
            parents.get(parents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            lastbagnum = parents.size();
            parents.clear();
        }
    }

    private void getLastPacket(int c) {

        Log.e(TAG, "新方法" + parents.size());
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
        Log.e(TAG, "last++" + lastbagnum + "当前页面红包数" + parents.size());
        if (parents.size() > 0 && WXMAIN) {
            Log.e(TAG, "页面大于O且在微信界面");
            if (lastbagnum < parents.size())
                parents.get(parents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            lastbagnum = parents.size();
            parents.clear();
        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        try {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    if ("领取红包".equals(info.getText().toString())) {
                        if (info.isClickable()) {
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        AccessibilityNodeInfo parent = info.getParent();
                        while (parent != null) {
                            if (parent.isClickable()) {
                                parents.add(parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle(info.getChild(i));
                    }
                }
            }
        } catch (Exception e) {


        }
    }
    private void wakeAndUnlock2(boolean b)
    {
        if(b)
        {
            //获取电源管理器对象
            pm=(PowerManager) getSystemService(Context.POWER_SERVICE);

            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

            //点亮屏幕
            wl.acquire();

            //得到键盘锁管理器对象
            km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");

            //解锁
            kl.disableKeyguard();
        }
        else
        {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent();
        intent.setAction("com.moxi.killedPayService");
        sendBroadcast(intent);
        super.onDestroy();
    }
}
