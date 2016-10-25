package cn.ezon.www.steplib.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by Administrator on 2016/4/28.
 */
public class StepSensorManager implements SensorEventListener {
    private SensorManager sensorManager;
    private StepDcretor stepDetector;
    //计步传感器类型 0-counter 1-detector
    private int stepSensorType = -1;
    private final int STEP_SENSOR_TYPE_COUNTER = 0;
    private final int STEP_SENSOR_TYPE_DETECTOR = 1;

    private static StepSensorManager mInstance;
    private Context context;

    public StepSensorManager init(Context context) {
        this.context = context;
        return this;
    }

    private StepSensorManager() {
    }

    public synchronized static StepSensorManager getInstance() {
        if (mInstance == null) {
            mInstance = new StepSensorManager();
        }
        return mInstance;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();
    }

    public void recycle() {
        if (sensorManager != null && stepDetector != null) {
            sensorManager.unregisterListener(stepDetector);
            sensorManager = null;
            stepDetector = null;
        }
    }

    private void startStepDetector() {
        recycle();
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            addCountStepListener();
        } else {
            addBasePedoListener();
        }
    }

    private void addCountStepListener() {
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Log.d("StepSensorManager", "detectorSensor :" + detectorSensor + ",countSensor :" + countSensor);
        if (countSensor != null) {
            stepSensorType = STEP_SENSOR_TYPE_COUNTER;
            sensorManager.registerListener(StepSensorManager.this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else if (detectorSensor != null) {
            stepSensorType = STEP_SENSOR_TYPE_DETECTOR;
            sensorManager.registerListener(StepSensorManager.this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            addBasePedoListener();
        }
    }

    private void addBasePedoListener() {
        stepDetector = new StepDcretor(context);
        // 获得传感器的类型，这里获得的类型是加速度传感器
        // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sensorManager.unregisterListener(stepDetector);
        sensorManager.registerListener(stepDetector, sensor,
                SensorManager.SENSOR_DELAY_UI);
        stepDetector
                .setOnSensorChangeListener(onSensorChangeListener);
    }

    private OnSensorChangeListener onSensorChangeListener;

    public StepSensorManager setOnSensorChangeListener(
            OnSensorChangeListener onSensorChangeListener) {
        this.onSensorChangeListener = onSensorChangeListener;
        return this;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //   i++;
        Log.d("StepSensorManager","stepSensorType :"+stepSensorType);
        if (stepSensorType == STEP_SENSOR_TYPE_COUNTER) {
            Log.d("StepSensorManager","accuracy :"+event.accuracy +", event.timestamp :"+event.timestamp +", sensor :"+event.sensor);
            for (int i = 0; i < event.values.length; i++) {
                Log.d("StepSensorManager","value["+i+"] :"+event.values[i]);
            }
            if (onSensorChangeListener != null) {
                long time = System.currentTimeMillis();
                int step = (int) event.values[0];
//                onSensorChangeListener.onNewStep(time, step);
                onSensorChangeListener.onStepChange(time);
            }
        } else if (stepSensorType == STEP_SENSOR_TYPE_DETECTOR) {
            if (onSensorChangeListener != null) {
                long time = System.currentTimeMillis();
                onSensorChangeListener.onStepChange(time);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
