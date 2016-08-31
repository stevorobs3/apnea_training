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

@EBean
public class AlertService implements TextToSpeech.OnInitListener, Closeable {

    @SystemService
    Vibrator vibrator;

    @RootContext
    Context context;

    private TextToSpeech tts;

    private boolean vibrate;
    private boolean textSpeech;

    private boolean alertMinute;
    private boolean alertHalfMinute;
    private boolean alertTenSecond;
    private boolean alertCountDown;

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

    public void checkNotifications(int sec) {
        if (alertMinute && sec % 60 == 0) {
            notifyAlert("minute");
        } else if (alertHalfMinute && sec == 30) {
            notifyAlert("30 seconds");
        } else if (alertTenSecond && sec == 10) {
            notifyAlert("10 seconds");
        } else if (alertCountDown && sec <= 5) {
            notifyAlert("" + sec);
        }
    }

    private void notifyAlert(String textToSpeech) {
        if (vibrate) {
            vibrator.vibrate(100);
        }
        if (textSpeech) {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("ru_RU");
            if (!Locale.getDefault().equals(locale)) {
                locale = Locale.US;
            }
            int result = tts.setLanguage(locale);
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
