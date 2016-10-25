package cn.ezon.www.steplib.utils;

import android.content.Context;

/**
 * Created by Administrator on 2016/4/28.
 */
public class ResUtils {

    public static int getLayoutRes(Context context, String name) {
        return context.getResources().getIdentifier(name, "layout", context.getPackageName());
    }

    public static int getIdRes(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }
    public static int getDrawableRes(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getStyleRes(Context context, String name) {
        return context.getResources().getIdentifier(name, "style", context.getPackageName());
    }

}
