package ru.megazlo.apnea.frag;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import ru.megazlo.apnea.*;
import ru.megazlo.apnea.service.ApneaPrefs_;

@EFragment
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements FabClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

	public final static String PREF_NAME = "ApneaPrefs";

	@Pref
	ApneaPrefs_ pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREF_NAME);
		addPreferencesFromResource(R.xml.pref_main);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			if (!BuildConfig.DEBUG && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				// отключаем настройку если нет bluetooth 4 с профилем LE
				getPreferenceManager().findPreference("allowBluetooth").setEnabled(false);
				pref.edit().allowBluetooth().put(false).apply();
			}
		}
	}

	@Override
	public void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void clickByContext(View view) {
		Snackbar.make(view, R.string.snack_reset_sets, Snackbar.LENGTH_SHORT).setAction(R.string.ok, v -> {
			pref.clear();
			getPreferenceManager().setSharedPreferencesName(PREF_NAME);
			setPreferenceScreen(null);
			addPreferencesFromResource(R.xml.pref_main);
		}).show();
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.VISIBLE);
		FloatingActionButton fab = (FloatingActionButton) view;
		fab.setImageResource(R.drawable.ic_reset);
	}

	@Override
	public boolean backPressed() {
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		ApneaBackupHelper.NEED_BACKUP = true;
	}
}
