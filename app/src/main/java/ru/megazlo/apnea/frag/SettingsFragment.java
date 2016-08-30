package ru.megazlo.apnea.frag;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import ru.megazlo.apnea.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements FabClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
    }

    @Override
    public void clickByContext(View view) {

    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.GONE);
    }
}
