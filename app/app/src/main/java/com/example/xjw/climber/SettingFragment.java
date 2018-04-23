package com.example.xjw.climber;


import android.os.Bundle;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

/**
 * Created by xjw on 2018/4/22.
 */

public class SettingFragment extends PreferenceFragmentCompat  {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.prefs);
        SwitchPreferenceCompat remindPreference=(SwitchPreferenceCompat)findPreference("remind");
        SwitchPreferenceCompat alwayslightPreference=(SwitchPreferenceCompat)findPreference("alwaysLight");
        remindPreference.setOnPreferenceChangeListener(new RemindonPreferenceChangerListener());
        alwayslightPreference.setOnPreferenceChangeListener(new AlwaysLightonPreferenceChangerListener());

    }


class RemindonPreferenceChangerListener implements Preference.OnPreferenceChangeListener {
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast.makeText(getActivity(), "震动后是否提醒？" + newValue, Toast.LENGTH_SHORT).show();
        return true;
    }
}
class AlwaysLightonPreferenceChangerListener implements Preference.OnPreferenceChangeListener{
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast.makeText(getActivity(), "屏幕是否常亮？"+newValue, Toast.LENGTH_SHORT).show();
        return true;
    }
}

}



