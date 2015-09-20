package de.fire855.smartcover;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;

/**
 * Created by fire855 on 19.09.15.
 */
public class SensorService extends Service {

    public static boolean mScreenOn = false;
    public static boolean mScreenOff = false;
    public PowerManager mPowerManager;
    public PowerManager.WakeLock mWakeLock;
    public boolean mSensorRegistered = false;
    public SensorManager mSensorManager;
    public Sensor mProximitySensor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CoverWakelock");

        if (mScreenOn) {
            mWakeLock.acquire();
        }

        if (!mSensorRegistered) {
            mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(mSensorListener, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorRegistered = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSensorRegistered) {
            mSensorManager.unregisterListener(mSensorListener, mProximitySensor);
            mSensorRegistered = false;
        }

	if (mWakeLock.isHeld()) {
            mWakeLock.release();
	}
    }


    public SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values == null) return;
            if (event.values.length == 0) return;

            float value = event.values[0];

            if (value == 1.0 && mScreenOn) {
                mPowerManager.wakeUp(SystemClock.uptimeMillis());
            }
            else if (value == 0.0 && mScreenOff) {
                mPowerManager.goToSleep(SystemClock.uptimeMillis());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
