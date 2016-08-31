package ru.megazlo.apnea.frag;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        Snackbar.make(view, R.string.snack_reset_sets, Snackbar.LENGTH_SHORT).setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreferenceManager().getSharedPreferences().edit().clear().commit();
                PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_main, true);
                setPreferenceScreen(null);
                addPreferencesFromResource(R.xml.pref_main);
            }
        }).show();
    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (FloatingActionButton) view;
        fab.setImageResource(R.drawable.ic_reset);
    }

    @Override
    public void backPressed() {
    }
}
