package fr.cyrian.coachrunning;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class PersonnalSettings extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personnal_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create prefs change listener to force user giving valid settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("length")) {
                    int value;
                    try {
                        value = Integer.valueOf(prefs.getString("length","str"));
                    } catch (NumberFormatException e) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("length", "170");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter valid length", Toast.LENGTH_SHORT).show();
                    }
                    value = Integer.valueOf(prefs.getString("length","str"));
                    if (value <= 0){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("length", "170");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter positive length", Toast.LENGTH_SHORT).show();
                    }
                }
                if (key.equals("weight")) {
                    int value;
                    try {
                        value = Integer.valueOf(prefs.getString("weight","str"));
                    } catch (NumberFormatException e) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("weight", "60");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter valid weight", Toast.LENGTH_SHORT).show();
                    }
                    value = Integer.valueOf(prefs.getString("weight","str"));
                    if (value <= 0){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("weight", "60");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter positive weight", Toast.LENGTH_SHORT).show();
                    }
                }
                if (key.equals("difficulty_preference")) {
                    Double value;
                    try {
                        value = Double.valueOf(prefs.getString("difficulty_preference","str"));
                    } catch (NumberFormatException e) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("difficulty_preference", "1.0");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter valid difficulty coefficient", Toast.LENGTH_SHORT).show();
                    }
                    value = Double.valueOf(prefs.getString("difficulty_preference","str"));
                    if (value <= 0.0){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("difficulty_preference", "1.0");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Please enter positive difficulty coefficient", Toast.LENGTH_SHORT).show();
                    }
                }
                if (savedInstanceState == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.settings, new SettingsFragment())
                            .commitAllowingStateLoss();
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}