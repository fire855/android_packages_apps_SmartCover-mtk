package de.fire855.smartcover;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SettingsActivity extends PreferenceActivity {

    public static SettingsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        instance = this;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean enable = sharedPref.getBoolean("enableCover", false);
        boolean screenOn = sharedPref.getBoolean("screenOn", false);
        boolean screenOff = sharedPref.getBoolean("screenOff", false);

        CheckBoxPreference enableCheckBox = (CheckBoxPreference) findPreference("enableCover");
        enableCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean value = (boolean) newValue;
                if (value) {
                    ((CheckBoxPreference) findPreference("screenOn")).setEnabled(true);
                    ((CheckBoxPreference) findPreference("screenOff")).setEnabled(true);
                    startService(new Intent(instance, SensorService.class));
                } else {
                    ((CheckBoxPreference) findPreference("screenOn")).setEnabled(false);
                    ((CheckBoxPreference) findPreference("screenOff")).setEnabled(false);
                    stopService(new Intent(instance, SensorService.class));
                }
                return true;
            }
        });

        CheckBoxPreference screenOnCheckBox = (CheckBoxPreference) findPreference("screenOn");
        screenOnCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SensorService.mScreenOn = (boolean) newValue;
                stopService(new Intent(instance, SensorService.class));
                startService(new Intent(instance, SensorService.class));
                return true;
            }
        });

        CheckBoxPreference screenOffCheckBox = (CheckBoxPreference) findPreference("screenOff");
        screenOffCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SensorService.mScreenOff = (boolean) newValue;
                return true;
            }
        });

        if (enable) {
            ((CheckBoxPreference) findPreference("screenOn")).setEnabled(true);
            ((CheckBoxPreference) findPreference("screenOff")).setEnabled(true);

            if (!isMyServiceRunning(SensorService.class)) {
                startService(new Intent(this, SensorService.class));
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
