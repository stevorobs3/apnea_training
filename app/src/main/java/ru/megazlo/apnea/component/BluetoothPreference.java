package ru.megazlo.apnea.component;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.List;

import ru.megazlo.apnea.BuildConfig;
import ru.megazlo.apnea.R;

/** Created by iGurkin on 26.10.2016. */
// BT LE поддерживается с android 4.3
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothPreference extends DialogPreference {

	private final static int SCAN_PERIOD = 10000;

	private String prefix;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeScanner mLEScanner;
	private BluetoothGatt mGatt;
	private Handler mHandler;

	private ListView list;

	private ArrayAdapter<String> adapter;

	public BluetoothPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStyles(context, attrs, 0);
	}

	public BluetoothPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initStyles(context, attrs, defStyleAttr);
	}

	private void initStyles(Context context, AttributeSet attrs, int defStyleAttr) {
		setPositiveButtonText(R.string.ok);
		setNegativeButtonText(R.string.cancel);
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimePreference, defStyleAttr, 0);
		prefix = attributes.getString(R.styleable.TimePreference_prefix);
		prefix = prefix == null ? "" : prefix + " ";
	}

	@Override
	protected View onCreateDialogView() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;

		list = new ListView(getContext());
		list.setLayoutParams(layoutParams);

		adapter = new ArrayAdapter<>(getContext(), R.layout.bth_row, R.id.label);
		list.setAdapter(adapter);
		list.setOnItemClickListener((adapterView, view, i, l) -> {
			Log.d("BluetoothPreference", "item selected " + i);
			final String item = adapter.getItem(i);
			setBluetoothAddress(item);
		});

		FrameLayout dialogView = new FrameLayout(getContext());
		dialogView.addView(list);


		mHandler = new Handler();
		final BluetoothManager bluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) getContext()).startActivityForResult(enableBtIntent, 1);
			Toast.makeText(getContext(), "You must enable bluetooth", Toast.LENGTH_SHORT).show();
		} else {
			scanLeDevice(true);
		}

		return dialogView;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(getContext(), "Bluetooth LE not supported", Toast.LENGTH_SHORT).show();
			if (!BuildConfig.DEBUG) {
				return super.onCreateView(parent);
			}
		}
		return super.onCreateView(parent);
	}

	@Override
	public void onActivityDestroy() {
		super.onActivityDestroy();
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		//list.setValue(getValue());
	}


	public void setBluetoothAddress(String address) {
		persistString(address);
		notifyDependencyChange(shouldDisableDependents());
		notifyChanged();
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			list.clearFocus();
			/*int newValue = list.getValue();
			if (callChangeListener(newValue)) {
				setValue(newValue);
			}*/
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(() -> mBluetoothAdapter.stopLeScan(mLeScanCallback), SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = (device, i, bytes) -> ((Activity) getContext()).runOnUiThread(() -> {
		Toast.makeText(getContext(), device.getName() + device.getAddress(), Toast.LENGTH_SHORT).show();
		/*Log.i("onLeScan", device.toString());
		connectToDevice(device);*/
	});
}
