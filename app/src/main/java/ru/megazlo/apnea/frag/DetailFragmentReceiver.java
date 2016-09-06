package ru.megazlo.apnea.frag;

import android.content.BroadcastReceiver;

public abstract class DetailFragmentReceiver extends BroadcastReceiver {
    public final static String ACTION_UPDATER = "APNEA_DETAIL_UPDATE";

    public final static String KEY_ID = "TABLE_ID";
    public final static String KEY_MAX = "KEY_MAX";
    public final static String KEY_ENDED = "KEY_ENDED";
    public final static String KEY_PROGRESS = "KEY_PROGRESS";
    public final static String KEY_ROW = "KEY_ROW";
    public final static String KEY_ROW_TYPE = "KEY_ROW_TYPE";
}
