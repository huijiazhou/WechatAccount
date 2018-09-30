package com.moxi.wechatpay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.moxi.wechatpay.access.AccessUtil;
import com.moxi.wechatpay.access.AirAccessibilityService;
import com.moxi.wechatpay.access.SharedPreferencesUtils;
import com.moxi.wechatpay.access.WechatMeg;
import com.moxi.wechatpay.getdb.DeviceUtil;
import com.moxi.wechatpay.getdb.GsonTools;
import com.moxi.wechatpay.getdb.MoxiPayService;
import com.moxi.wechatpay.getdb.RootUtil;
import com.moxi.wechatpay.getdb.ServiceUtils;
import com.moxi.wechatpay.getdb.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button access;
    private Button getDb;
    private Button db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = findViewById(R.id.home_btn_search_db);
        access = findViewById(R.id.home_btn_access_service);
        getDb = findViewById(R.id.home_btn_get_db);

        access.setOnClickListener(this);
        db.setOnClickListener(this);
        getDb.setOnClickListener(this);
        getWechatMeg();
        initService();


    }

    private void initService() {
        if (!AccessUtil.isAccessibilitySettingsOn(MainActivity.this)){
            AirAccessibilityService.ALL = false;
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }else{
            access.setBackgroundResource(R.drawable.red);
            access.setText("实时获取到账已开启");
            AirAccessibilityService.ALL = true;
        }
        if (!ServiceUtils.isServiceRunning(this, "MoxiPayService")) {
            startService(new Intent(MainActivity.this, MoxiPayService.class));
        }
        db.setBackgroundResource(R.drawable.red);
        db.setText("获取到账数据库已开启");
    }

    public void getWechatMeg() {

        PackageManager pckMan = getPackageManager();
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);

        for (PackageInfo pInfo : packageInfo) {

            if (pInfo.packageName.equals("com.tencent.mm")){

                Log.i("微信","pInfo.versionName--------"+pInfo.versionName);
                Log.i("微信","pInfo.versionCode--------"+pInfo.versionCode);

                String url = "http://47.91.167.130:8032/appInterface/wechat/getVersionInfo.do";
                OkHttpClient okHttpClient = new OkHttpClient();

                FormBody.Builder body = new FormBody.Builder();
                body.add("versionCode", String.valueOf(pInfo.versionCode));

                final Request request = new Request.Builder()
                        .url(url)
                        .post(body.build())
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("WeChatVersion", "getWeChatVersionFailure: "+e.getMessage());
                        Toast.makeText(MainActivity.this,"请打开网络",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        WechatMeg wechatMeg = GsonTools.changeGsonToBean(response.body().string(),WechatMeg.class);
                        SharedPreferencesUtils.saveString(MainActivity.this,"payId",wechatMeg.result.payId);
                        SharedPreferencesUtils.saveString(MainActivity.this,"numId",wechatMeg.result.numId);
                        SharedPreferencesUtils.saveString(MainActivity.this,"listviewId",wechatMeg.result.listviewId);
                    }
                });
            }

        }
    }
    private boolean checkroot(){
        if (!DeviceUtil.isAppInstalled(this, "com.tencent.mm")){

            Toast.makeText(this,"未安装微信",Toast.LENGTH_LONG).show();
            return false;
        }

        if (!RootUtil.isDeviceRooted()){
            Toast.makeText(this,"手机未ROOT",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!RootUtil.isGrantRootPermission()){
            Toast.makeText(this,"应用未ROOT",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //开启读取数据库service,1小时一次
            case R.id.home_btn_search_db:
                if (checkroot()) {
                    if (!ServiceUtils.isServiceRunning(this, "MoxiPayService")) {
                        startService(new Intent(MainActivity.this, MoxiPayService.class));
                        ((Button) v).setBackgroundResource(R.drawable.red);
                        ((Button) v).setText("获取到账数据库已开启");
                    }
                }
                break;
                //手动获取一个小时内的数据库
            case R.id.home_btn_get_db:
                if (checkroot()) {
                    new Thread() {
                        @Override
                        public void run() {
                            Utils.init(MainActivity.this);
                            Utils.copyDatabase(MainActivity.this);
                        }
                    }.start();
                }
                break;
                //开启辅助服务，获取实时到账
            case R.id.home_btn_access_service:
                if (!AccessUtil.isAccessibilitySettingsOn(MainActivity.this)){
                    AirAccessibilityService.ALL = false;
                    ((Button) v).setBackgroundResource(R.drawable.gray);
                    ((Button) v).setText("实时获取到账已关闭");
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }else{
                    AirAccessibilityService.ALL = true;
                    ((Button) v).setBackgroundResource(R.drawable.red);
                    ((Button) v).setText("实时获取到账已开启");

                }
                break;
        }
    }
}
