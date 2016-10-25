package cn.ezon.www.steplib.utils;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/4/29.
 */
public class UserUtils {

    public static final int TYPE_HEIGHT_HIGH = 1;
    public static final int TYPE_HEIGHT_NORMAL = 2;
    public static final int TYPE_HEIGHT_LOW = 3;

    // 步长 = (21 - k) * 身高 / 42    k为  大个 1/3  中个 1/2  矮个 2/3  男中个为 168 - 175 女 中个为158-165
    public static int getStepLength(float height, boolean isMale) {
        int type = TYPE_HEIGHT_NORMAL;
        if (isMale) {
            if (height > 175) {
                type = TYPE_HEIGHT_HIGH;
            } else if (height >= 168 && height <= 175) {
                type = TYPE_HEIGHT_NORMAL;
            } else {
                type = TYPE_HEIGHT_LOW;
            }
        } else {
            if (height > 165) {
                type = TYPE_HEIGHT_HIGH;
            } else if (height >= 158 && height <= 165) {
                type = TYPE_HEIGHT_NORMAL;
            } else {
                type = TYPE_HEIGHT_LOW;
            }
        }
        float k = 1 / 2;
        switch (type) {
            case TYPE_HEIGHT_HIGH:
                k = 1f / 3f;
                break;
            case TYPE_HEIGHT_NORMAL:
                k = 1f / 2f;
                break;
            case TYPE_HEIGHT_LOW:
                k = 2f / 3f;
                break;
        }
        float stepLen = (21f - k) * height / 42f;
        return Math.round(stepLen);
    }


    public static float getDistance(int step, int stepSize) {
        return step * stepSize;
    }

    /**
     *
     * 这里取普通的步行速度 均值
     *
     * 卡路里(kcal) = METs*體重(kg)*運動時間(hr)
     speed(m/min)    METs
     54              2.8
     67              3
     94              4.3
     107             5
     121             7
     134             8.3
     161             9.8
     188             11
     201             11.8
     215             11.8
     241             12.8
     268             14.5
     295             16
     322             19
     349             19.8
     376             23
     * @return
     */
    public static String getKcal(float weight,int min){
        float kcal = 3f * weight * (min / 60f) / 10f;
        return  formatKeepOneNumber(kcal);
    }

    public static String formatKeepOneNumber(float number) {
        DecimalFormat formater = new DecimalFormat("##0.0");
        return formater.format(number);
    }
    public static String formatKeepZeroNumber(float number) {
        DecimalFormat formater = new DecimalFormat("##0");
        return formater.format(number);
    }
}
