package ru.megazlo.apnea.component;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TimePicker;

import ru.megazlo.apnea.R;

// Based on http://stackoverflow.com/a/7484289/922168
// https://gist.github.com/nickaknudson/5024416
@SuppressWarnings("deprecation")
public class TimePreference extends DialogPreference {
    private int mHour = 0;
    private int mMinute = 0;
    private TimePicker picker;
    private final static String DEFAULT_VALUE = "02:15";

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);
    }

    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
        String time = toTime(mHour, mMinute);
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    public String toTime(int hour, int minute) {
        return String.valueOf(hour) + ":" + String.valueOf(minute);
    }

    public void updateSummary() {
        String time = String.format(Locale.US, "%1$d:%2$02d", mHour, mMinute);
        setSummary(time);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(new ContextThemeWrapper(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(mHour);
        picker.setCurrentMinute(mMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int currHour = picker.getCurrentHour();
            int currMinute = picker.getCurrentMinute();

            if (!callChangeListener(toTime(currHour, currMinute))) {
                return;
            }

            // persist
            setTime(currHour, currMinute);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;

        if (restorePersistedValue) {
            time = getPersistedString(DEFAULT_VALUE);
        } else {
            time = defaultValue.toString();
        }

        int currHour = getHour(time);
        int currMinute = getMinute(time);
        // need to persist here for default value to work
        setTime(currHour, currMinute);
        updateSummary();
    }
}