package cn.ezon.www.steplib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import cn.ezon.www.steplib.config.Constant;
import cn.ezon.www.steplib.core.OnSensorChangeListener;
import cn.ezon.www.steplib.core.StepOperater;
import cn.ezon.www.steplib.core.StepSensorManagerProxy;
import cn.ezon.www.steplib.db.entity.StepEntity;

public class StepService extends Service implements OnSensorChangeListener {
    private Messenger messenger = new Messenger(new MessenerHandler());

    @Override
    public void onStepChange(long time) {
        StepOperater.getInstance().onStepChange(time);
    }

    @Override
    public void onNewStep(long time, int step) {
        StepOperater.getInstance().onNewStep(time, step);
    }

    public void sendStep(StepEntity stepEntity) {
        try {
            if (clientMessenger != null) {
                int step = 0;
                int min = 0;
                if (stepEntity != null) {
                    step = stepEntity.getStep();
                    min = stepEntity.getActiveMin();
                }

                Message replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER);
                Bundle bundle = new Bundle();
                bundle.putInt("step", step);
                bundle.putInt("activeMin", min);
                replyMsg.setData(bundle);
                clientMessenger.send(replyMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Messenger clientMessenger;

    private class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    clientMessenger = msg.replyTo;
                    StepOperater.getInstance().restoreData();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("StepService", "onCreate");
        StepOperater.getInstance().init(this);
        StepSensorManagerProxy.getInstance().init(getApplicationContext()).setOnSensorChangeListener(this).start();

        startForeground(0, null);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        StepOperater.getInstance().destory();
        StepSensorManagerProxy.getInstance().destory();
        Log.d("StepService", "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


}
