package com.moxi.wechatpay.access;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesUtils {

	private static SharedPreferences sp;
	public final static String SP_NAME = "config";
	private static final String LOAD_TYPE = "isFirstIn";
	public static void saveString(Context context, String key, String value) {

		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);

		sp.edit().putString(key, value).commit();
	}
	public static String getString(Context context, String key, String defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);

		return sp.getString(key, defValue);

	}
	public static void saveBoolean(Context context, String key, Boolean value) {

		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);

		sp.edit().putBoolean(key, value).commit();
	}
	public static Boolean getBoolean(Context context, String key, Boolean defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);

		return sp.getBoolean(key, defValue);

	}
	public static void saveInt(Context context, String key, int value) {
		
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		sp.edit().putInt(key, value).commit();
	}
	public static int getInt(Context context, String key, int defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		return sp.getInt(key, defValue);
		
	}
	public static void saveLong(Context context, String key, long value) {
		
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		sp.edit().putLong(key, value).commit();
	}
	public static long getLong(Context context, String key, long defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		return sp.getLong(key, defValue);
		
	}
	@SuppressLint("NewApi") public static void saveSet(Context context, String key, Set<String> value) {
		
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		sp.edit().putStringSet(key, value).commit();
	}
	@SuppressLint("NewApi") public static Set<String> getSet(Context context, String key, Set<String> defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		
		return sp.getStringSet(key, defValue);
		
	}
	public static void clear(){
		if (sp!=null) {
			sp.edit().clear().commit(); 
		}
	}

	
	private static SharedPreferences getSp(Context ctx){
		return ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	}

	public static void setFirstLoad(Context ctx, boolean isFirstLoad){
		
		getSp(ctx).edit().putBoolean(LOAD_TYPE, isFirstLoad).commit();
	}
	
	public static boolean isFirstLoad(Context ctx){
		
		return getSp(ctx).getBoolean(LOAD_TYPE, true);
	}
	
}
