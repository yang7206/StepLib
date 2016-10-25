package cn.ezon.www.steplib.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.widget.TextView;

public class TextUtils {

    private static Typeface blackBoldTypeFace;

    public static synchronized Typeface getWaterMarkBlackBoldTypeFace(Context context) {
        if (blackBoldTypeFace == null) {
            blackBoldTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/black_bold.ttf");
        }
        return blackBoldTypeFace;
    }

    public static int getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    public static void setPaintAutoSize(TextView tv, String text) {
        float w = tv.getPaint().measureText(text);
        int maxWidth = (tv.getMaxWidth() == -1 ? Integer.MAX_VALUE : tv.getMaxWidth()) - tv.getPaddingLeft() - tv.getPaddingRight();
        while (w > maxWidth) {
            float size = tv.getPaint().getTextSize();
            tv.getPaint().setTextSize(size - 1);
            w = tv.getPaint().measureText(text);
        }
    }

    public static int getInt(String text){
        int result = 0;
        try{
            result = Integer.parseInt(text);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
