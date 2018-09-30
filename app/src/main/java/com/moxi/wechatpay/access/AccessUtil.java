package com.moxi.wechatpay.access;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by zhou on 2018/3/6.
 */

public class AccessUtil {

    private static final String TAG = "AccessService" ;

    public static boolean isAccessibilitySettingsOn(Context context) {

        int accessibilityEnabled = 0;

        try {

            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),

                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {

            Log.i("URL", "错误信息为："+e.getMessage());

        }



        if (accessibilityEnabled == 1) {

            String services = Settings.Secure.getString(context.getContentResolver(),

                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (services != null) {

                return services.toLowerCase().contains(context.getPackageName().toLowerCase());

            }

        }

        return false;

    }
}
