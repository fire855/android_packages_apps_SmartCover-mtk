package de.fire855.smartcover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by fire855 on 19.09.15.
 */
public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean enable = sharedPref.getBoolean("enableCover", false);
            SensorService.mScreenOn = sharedPref.getBoolean("screenOn", false);
            SensorService.mScreenOff = sharedPref.getBoolean("screenOff", false);

            if (enable) {
                context.startService(new Intent(context, SensorService.class));
            }
        }
    }
}
