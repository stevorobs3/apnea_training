package ru.megazlo.apnea.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.frag.SettingsFragment;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {

    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = false;

    private int min;
    private int max;
    private int value;
    private String suffix;

    private NumberPicker picker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyles(context, attrs, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyles(context, attrs, defStyleAttr);
    }

    private void initStyles(Context context, AttributeSet attrs, int defStyleAttr) {
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference, defStyleAttr, 0);
        min = attributes.getInteger(R.styleable.NumberPickerPreference_min, 0);
        max = attributes.getInteger(R.styleable.NumberPickerPreference_max, 1);
        suffix = attributes.getString(R.styleable.NumberPickerPreference_suffix);
        suffix = suffix == null ? "" : " " + suffix;
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(new ContextThemeWrapper(getContext(), R.style.NumberPickerTextColorStyle));
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
            setSummary(newValue + suffix);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, min);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        int value = restorePersistedValue ? getPersistedInt(max) : (Integer) defaultValue;
        setValue(value);
        setSummary(value + suffix);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}