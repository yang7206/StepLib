package cn.ezon.www.steplib.weather;

import android.content.Context;
import android.text.TextUtils;

import cn.ezon.www.steplib.utils.ResUtils;

/**
 * Created by Administrator on 2016/4/29.
 */
public class WeatherHodler {

    public String name;
    public String stateDetailed;
    public String lowTemp;
    public String hightTemp;
    public String shidu;
    boolean isSuccess = false;

    @Override
    public String toString() {
        return "WeatherHodler{" +
                "name='" + name + '\'' +
                ", stateDetailed='" + stateDetailed + '\'' +
                ", lowTemp='" + lowTemp + '\'' +
                ", hightTemp='" + hightTemp + '\'' +
                ", shidu='" + shidu + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }

    public int getWeatherIcon(Context context) {
        if (TextUtils.isEmpty(stateDetailed))
            return ResUtils.getDrawableRes(context, "ezon_ic_w_sun");
        if (stateDetailed.indexOf("云") != -1||stateDetailed.indexOf("阴") != -1) {
            return ResUtils.getDrawableRes(context, "ezon_ic_w_cloud");
        } else if (stateDetailed.indexOf("雨") != -1) {
            return ResUtils.getDrawableRes(context, "ezon_ic_w_rain");
        } else if (stateDetailed.indexOf("雪") != -1) {
            return ResUtils.getDrawableRes(context, "ezon_ic_w_snow");
        } else if (stateDetailed.indexOf("晴") != -1) {
            return ResUtils.getDrawableRes(context, "ezon_ic_w_sun");
        }
        return ResUtils.getDrawableRes(context, "ezon_ic_w_sun");
    }
}
