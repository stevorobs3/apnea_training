package ru.megazlo.apnea.receivers;

import android.content.BroadcastReceiver;

/** Created by iGurkin on 08.09.2016. */
public abstract class ApneaForeReceiver extends BroadcastReceiver {
	public static final String ACTION = "ru.megazlo.apnea.APNEA_FORE_ACTION";
	public static final String ACTION_TYPE = "ru.megazlo.apnea.APNEA_ACTION_TYPE";
	public static final String ACTION_SKIP = "ru.megazlo.apnea.APNEA_END_CURRENT";
	public static final String ACTION_PAUSE = "ru.megazlo.apnea.APNEA_PAUSE_CURRENT";
	public static final String ACTION_RESUME = "ru.megazlo.apnea.APNEA_RESUME_CURRENT";
	public static final String ACTION_ADD_TIME = "ru.megazlo.apnea.APNEA_ADD_TIME_CURRENT";
}
