package cn.ezon.www.steplib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/4/29.
 */
public class StoreUtils {

    public static int getUserHeight(Context context) {
        return getIntValue(context, "userHeight", 170);
    }

    public static void setUserHeight(Context context, int userHeight) {
        setIntValue(context, "userHeight", userHeight);
    }

    public static int getUserWeight(Context context) {
        return getIntValue(context, "userWeight", 60);
    }

    public static void setUserWeight(Context context, int userWeight) {
        setIntValue(context, "userWeight", userWeight);
    }

    public static boolean isUserMale(Context context) {
        return getBooleanValue(context, "userMale", true);
    }

    public static void setUserMale(Context context, boolean isMale) {
        setBooleanValue(context, "userMale", isMale);
    }

    public static boolean isFirstSetting(Context context) {
        return getBooleanValue(context, "isFirstSetting", true);
    }

    public static void setFirstSettinged(Context context) {
        setBooleanValue(context, "isFirstSetting", false);
    }

    private static void setBooleanValue(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ezon_setting", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    private static boolean getBooleanValue(Context context, String key, boolean defalutValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ezon_setting", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defalutValue);
    }


    private static void setIntValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ezon_setting", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    private static int getIntValue(Context context, String key, int defalutValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ezon_setting", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defalutValue);
    }

}
