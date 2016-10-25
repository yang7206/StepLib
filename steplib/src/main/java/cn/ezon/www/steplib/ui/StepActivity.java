package cn.ezon.www.steplib.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ezon.www.steplib.config.Constant;
import cn.ezon.www.steplib.db.dao.DBDaoFactory;
import cn.ezon.www.steplib.db.entity.StepEntity;
import cn.ezon.www.steplib.service.StepService;
import cn.ezon.www.steplib.utils.DeviceUtils;
import cn.ezon.www.steplib.utils.ResUtils;
import cn.ezon.www.steplib.utils.ScreenShotUtil;
import cn.ezon.www.steplib.utils.StoreUtils;
import cn.ezon.www.steplib.utils.TextUtils;
import cn.ezon.www.steplib.utils.UserUtils;
import cn.ezon.www.steplib.view.BuyEzonWatchDialog;
import cn.ezon.www.steplib.view.DayHourStepPillarLayout;
import cn.ezon.www.steplib.view.EzonSetDialog;
import cn.ezon.www.steplib.weather.WeatherGetter;
import cn.ezon.www.steplib.weather.WeatherHodler;

/**
 * Created by Administrator on 2016/4/27.
 */
public class StepActivity extends Activity implements WeatherGetter.OnWeatherCallback, Handler.Callback {
    private ImageView ezon_iv_weather;
    private TextView ezon_tv_weather_info, ezon_tv_today_step, ezon_tv_today_distance, ezon_tv_today_kcal, ezon_tv_today_activemin;
    private DayHourStepPillarLayout layout;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResUtils.getLayoutRes(StepActivity.this, "ezon_activity_step"));
        setupTitle();
        setupWeather();
        setupBottomBar();
        initStepContent();
        initChart();

        WeatherGetter.getInstance().setOnWeatherCallback(this).requestWeather();
        bindStepService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else if (setDialog != null && setDialog.isShowing()) {
            setDialog.dismiss();
        }
        if (layout != null) {
            layout.reDraw();
            StepEntity stepEntity = restoreData();
            restoreChartData(stepEntity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StepEntity stepEntity = restoreData();
        setupStepContent(stepEntity);
        restoreChartData(stepEntity);
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private StepEntity restoreData() {
        StepEntity stepEntity = DBDaoFactory.getStepEntityDao(StepActivity.this).queryDayStepNotReturnNull(getTodayDate());
        return stepEntity;
    }

    private void setupTitle() {
        ((View) findView("ezon_iv_back")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((View) findView("ezon_iv_share")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tackScreenShot();
            }
        });

    }

    private void setupWeather() {
        ezon_iv_weather = findView("ezon_iv_weather");
        ezon_tv_weather_info = findView("ezon_tv_weather_info");
        ((View) findView("ezon_iv_set")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEzonSetDialog();
            }
        });
        if (StoreUtils.isFirstSetting(StepActivity.this)){
            showEzonSetDialog();
            setDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    StoreUtils.setFirstSettinged(StepActivity.this);
                }
            });
        }
    }

    private void initStepContent() {
        ezon_tv_today_step = findView("ezon_tv_today_step");
        ezon_tv_today_distance = findView("ezon_tv_today_distance");
        ezon_tv_today_kcal = findView("ezon_tv_today_kcal");
        ezon_tv_today_activemin = findView("ezon_tv_today_activemin");
        getUserInfo();
    }

    private int height;
    private int weight;
    private int stepSize;

    private void getUserInfo() {
        height = StoreUtils.getUserHeight(StepActivity.this);
        weight = StoreUtils.getUserWeight(StepActivity.this);
        stepSize = UserUtils.getStepLength(height, StoreUtils.isUserMale(StepActivity.this));
    }


    private void setupStepContent(StepEntity entity) {
        int step = 0;
        int min = 0;
        if (entity != null) {
            step = entity.getStep();
            min = entity.getActiveMin();
        }
        setupStepContent(step, min);
    }

    private void setupStepContent(int step, int min) {
        ezon_tv_today_step.setText(step + "");
        float distance = ((UserUtils.getDistance(step, stepSize) / 100f) / 1000f);
        ezon_tv_today_distance.setText(UserUtils.formatKeepOneNumber(distance));
        ezon_tv_today_kcal.setText(UserUtils.getKcal(weight, min));
        ezon_tv_today_activemin.setText(min + "");
    }

    private void initChart() {
        LinearLayout parent = findView("ezon_parent_view_day_step_layout");
        layout = new DayHourStepPillarLayout(StepActivity.this);
        parent.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void restoreChartData(StepEntity stepEntity) {
        String hourStep[] = stepEntity.getHourStep().split(",");
        final List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < hourStep.length; i++) {
            list.add(TextUtils.getInt(hourStep[i]));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setStepData(list);
            }
        }, 300);
    }

    private void tackScreenShot() {
        if (!DeviceUtils.hasSdcard()) {
            Toast.makeText(StepActivity.this, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }
        final String storePath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "ezon" + System.currentTimeMillis() + ".jpg";
        File file = new File(storePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        ScreenShotUtil.shoot(StepActivity.this, file, new ScreenShotUtil.OnScreenShotCompleteListener() {
            @Override
            public void onShotComplete(boolean isSuccess, Bitmap b) {
                if (isSuccess) {
                    Toast.makeText(StepActivity.this, "截图已保存至" + storePath, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupBottomBar() {
        ((View) findView("ezon_iv_btn_competition")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepLibManager.getInstance().onCompetitionClick();
            }
        });
        ((View) findView("ezon_iv_btn_stadium")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepLibManager.getInstance().onStadiumClick();
            }
        });
        ((View) findView("ezon_iv_btn_ezonsport")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyEzonWatchDialog();
            }
        });
    }


    private void bindStepService() {
        Intent intent = new Intent(StepActivity.this, StepService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopBindSerevice() {
        if (connection != null) {
            unbindService(connection);
        }
    }

    private BuyEzonWatchDialog dialog;

    private void showBuyEzonWatchDialog() {
        if (dialog == null) {
            dialog = new BuyEzonWatchDialog(StepActivity.this);
        }
        dialog.setOnBuyButtonClickListener(new BuyEzonWatchDialog.OnBuyButtonClickListener() {
            @Override
            public void onBuyClick() {
                StepLibManager.getInstance().onBuyEzonWatchClick();
            }
        });
        dialog.show();
    }

    private EzonSetDialog setDialog;

    private void showEzonSetDialog() {
        if (setDialog == null) {
            setDialog = new EzonSetDialog(StepActivity.this);
        }
        setDialog.show();
    }

    @Override
    public void onWeatherGet(final WeatherHodler hodler) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ezon_iv_weather.setImageResource(hodler.getWeatherIcon(StepActivity.this));
                ezon_tv_weather_info.setText(hodler.lowTemp + "-" + hodler.hightTemp + "℃ " + hodler.stateDetailed + " 湿度" + hodler.shidu);
            }
        });
    }

    private <T> T findView(String idName) {
        return (T) findViewById(ResUtils.getIdRes(StepActivity.this, idName));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBindSerevice();
    }

    @Override
    public boolean handleMessage(Message msg) {
        setupStepContent(msg.getData().getInt("step"), msg.getData().getInt("activeMin"));
        return false;
    }
}
