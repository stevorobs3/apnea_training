package ru.megazlo.apnea.component;

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
	private int minutes = 0;
	private int seconds = 0;
	private String prefix;
	private TimePicker picker;
	private final static String DEFAULT_VALUE = "02:15";

	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStyles(context, attrs, 0);
	}

	public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
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

	public static int getMinutes(String time) {
		return Integer.parseInt(time.split(":")[0]);
	}

	public static int getSeconds(String time) {
		return Integer.parseInt(time.split(":")[1]);
	}

	public void setTime(int hour, int minute) {
		minutes = hour;
		seconds = minute;
		persistString(Utils.formatMS(minutes, seconds));
		notifyDependencyChange(shouldDisableDependents());
		notifyChanged();
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
		picker.setCurrentHour(minutes);
		picker.setCurrentMinute(seconds);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int currMin = picker.getCurrentHour();
			int currSec = picker.getCurrentMinute();

			if (!callChangeListener(Utils.formatMS(currMin, currSec))) {
				return;
			}
			setTime(currMin, currSec);
			setSummary(prefix + Utils.formatMS(minutes, seconds));
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		String time = restorePersistedValue ? getPersistedString(DEFAULT_VALUE) : defaultValue.toString();
		// need to persist here for default value to work
		setTime(getMinutes(time), getSeconds(time));
		setSummary(prefix + Utils.formatMS(minutes, seconds));
	}
}