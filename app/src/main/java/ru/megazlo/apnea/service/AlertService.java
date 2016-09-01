package ru.megazlo.apnea.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.RowState;

@EBean
public class AlertService implements TextToSpeech.OnInitListener, Closeable {

    public static final int VIBRATE_TIME = 200;

    private boolean vibrate;
    private boolean textSpeech;
    private boolean alertMinute;
    private boolean alertHalfMinute;
    private boolean alertTenSecond;
    private boolean alertCountDown;

    private TextToSpeech tts;

    @SystemService
    Vibrator vibrator;

    @RootContext
    Context context;

    @AfterInject
    void init() {
        final SharedPreferences pr = PreferenceManager.getDefaultSharedPreferences(context);
        vibrate = pr.getBoolean("pref_notify_vibrate", false);
        textSpeech = pr.getBoolean("pref_notify_speech", false);
        tts = new TextToSpeech(context, this);

        alertMinute = pr.getBoolean("pref_notify_minute", false);
        alertHalfMinute = pr.getBoolean("pref_notify_30sec", false);
        alertTenSecond = pr.getBoolean("pref_notify_10sec", false);
        alertCountDown = pr.getBoolean("pref_notify_5sec", false);
    }

    public void sayImOk() {
        if (textSpeech) {
            final String string = context.getResources().getString(R.string.speech_im_ok);
            notifyAlertSpeech(string);
        }
    }

    public void sayState(RowState state) {
        if (textSpeech) {
            if (state == RowState.BREATHE){
                final String string = context.getResources().getString(R.string.speech_breathe);
                notifyAlertSpeech(string);
            } else if (state == RowState.HOLD) {
                final String string = context.getResources().getString(R.string.speech_hold);
                notifyAlertSpeech(string);
            }
        }
    }

    public void checkNotifications(int sec) {
        if (alertMinute && sec % 60 == 0) {
            final String string = context.getResources().getQuantityString(R.plurals.speech_minute, sec / 60, sec / 60);
            notifyAlert(string);
        } else if (alertHalfMinute && sec == 30) {
            final String string = context.getResources().getString(R.string.speech_30sec);
            notifyAlert(string);
        } else if (alertTenSecond && sec == 10) {
            final String string = context.getResources().getString(R.string.speech_10sec);
            notifyAlert(string);
        } else if (alertCountDown && sec <= 5) {
            notifyAlert(sec + "");
        }
    }

    private void notifyAlert(String textToSpeech) {
        notifyAlertVibrate();
        notifyAlertSpeech(textToSpeech);
    }

    private void notifyAlertVibrate() {
        if (vibrate) {
            vibrator.vibrate(VIBRATE_TIME);
        }
    }

    private void notifyAlertSpeech(String textToSpeech) {
        if (textSpeech) {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textSpeech = false;
                Log.e("TTS", "Извините, этот язык не поддерживается");
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
