package ru.megazlo.apnea.receivers;

import android.content.*;

/** Created by iGurkin on 12.09.2016. */
public abstract class ChangeFragmentReceiver extends BroadcastReceiver {
	public final static String ACTION_FRAGMENT = "ru.megazlo.apnea.CHANGE_FRAGMENT";
	public final static String KEY_FRAG = "KEY_FRAG";
	public final static String KEY_DETAIL = "KEY_DETAIL";
	public final static String KEY_LIST = "KEY_LIST";
	public final static String KEY_EDIT = "KEY_EDIT";
	public final static String KEY_TABLE = "KEY_TABLE";
}
