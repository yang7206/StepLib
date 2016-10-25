package cn.ezon.www.steplib.weather;

import android.os.Environment;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/29.
 */
public class WeatherGetter {

    private static WeatherGetter mInstance;

    private WeatherGetter() {
    }

    public synchronized static WeatherGetter getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherGetter();
        }
        return mInstance;
    }

    public void requestWeather() {
        new Thread() {
            @Override
            public void run() {
                getWeather();
            }
        }.start();
    }

    private void getWeather() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String name = format.format(new Date());
        String filePath = Environment.getExternalStorageDirectory() + File.separator + "ezon_weather" + File.separator + "fuzhou_" + name;
        if (!new File(filePath).getParentFile().exists()) {
            new File(filePath).getParentFile().mkdirs();
        } else {
            File files[] = new File(filePath).getParentFile().listFiles();
            for (File f : files) {
                if (!filePath.equals(f.getAbsolutePath())) {
                    f.delete();
                }
            }
        }
        if (new File(filePath).exists()) {
            try {
                parse(new FileInputStream(filePath));
            } catch (Exception e) {
                requestWeatherXML(filePath);
                e.printStackTrace();
            }
        } else {
            requestWeatherXML(filePath);
        }
    }

    private void requestWeatherXML(String filePath) {
        HttpURLConnection urlConnection = null;
        try {
            String weatherUrl = "http://flash.weather.com.cn/wmaps/xml/fuzhou.xml";
            URL url = new URL(weatherUrl);
            String host = android.net.Proxy.getDefaultHost();
            if (!TextUtils.isEmpty(host)) {
                int port = android.net.Proxy.getDefaultPort();
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            int code = urlConnection.getResponseCode();
            if (code == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                FileOutputStream fos = new FileOutputStream(filePath);
                int read = 0;
                byte[] b = new byte[1024];
                while ((read = in.read(b)) != -1) {
                    fos.write(b, 0, read);
                }
                fos.flush();
                fos.close();
                in.close();
                Thread.sleep(1000);
                parse(new FileInputStream(filePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    private void parse(InputStream in) {
        WeatherHodler hodler = new WeatherHodler();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, "UTF_8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_TAG) {
                    if ("city".equals(parser.getName())) {
                        boolean isBreak = false;
                        int count = parser.getAttributeCount();
                        boolean isFind = false;
                        for (int i = 0; i < count; i++) {
                            String attrName = parser.getAttributeName(i);
                            String value = parser.getAttributeValue(i);
                            if ("cityname".equals(attrName) && "福州市".equals(value)) {
                                isBreak = true;
                                isFind = true;
                                hodler.isSuccess = true;
                                hodler.name = value;
                            } else if (isFind) {
                                if ("stateDetailed".equals(attrName)) {
                                    hodler.stateDetailed = value;
                                } else if ("tem1".equals(attrName)) {
                                    hodler.hightTemp = value;
                                } else if ("tem2".equals(attrName)) {
                                    hodler.lowTemp = value;
                                } else if ("humidity".equals(attrName)) {
                                    hodler.shidu = value;
                                }
                            }
                        }
                        if (isBreak) {
                            break;
                        }
                    }
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (onWeatherCallback != null && hodler.isSuccess) {
            onWeatherCallback.onWeatherGet(hodler);
        }
    }

    private OnWeatherCallback onWeatherCallback;

    public WeatherGetter setOnWeatherCallback(OnWeatherCallback callback) {
        this.onWeatherCallback = callback;
        return this;
    }

    public interface OnWeatherCallback {
        void onWeatherGet(WeatherHodler hodler);
    }
}
