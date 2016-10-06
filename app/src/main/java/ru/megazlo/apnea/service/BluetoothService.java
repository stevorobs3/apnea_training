package ru.megazlo.apnea.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import org.androidannotations.annotations.EBean;

import java.util.Set;

/** Created by iGurkin on 04.10.2016. */
@TargetApi(18)
@EBean(scope = EBean.Scope.Singleton)
public class BluetoothService {

	public void searchDevices() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) {// устройство поддерживает Bluetooth и он включен
			Set<BluetoothDevice> devices = adapter.getBondedDevices();

		}
	}
}
