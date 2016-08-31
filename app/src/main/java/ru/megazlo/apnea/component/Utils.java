package ru.megazlo.apnea.component;

import android.content.res.Resources;

import java.io.Serializable;
import java.net.PortUnreachableException;

public final class Utils {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * Форматирование секунд в строку mm:ss
     *
     * @param seconds полное количество секунд
     * @return строка в формате mm:ss
     */
    public static String formatMS(int seconds) {
        return formatMS(seconds / 60, seconds % 60);
    }

    /**
     * Форматирование таймера в строку mm:ss
     *
     * @param minutes минуты
     * @param seconds секунда, не больше 60
     * @return строка в формате mm:ss
     */
    public static String formatMS(int minutes, int seconds) {
        if (seconds > 60) {
            throw new RuntimeException("Invalid seconds count");
        }
        return minutes + ":" + formatInt(seconds);
    }

    private static String formatInt(int seconds) {
        return seconds < 10 ? "0" + seconds : "" + seconds;
    }

    public static int getTotalSeconds(String time) {
        return getMinutes(time) * 60 + getSeconds(time);
    }

    public static int getMinutes(String time) {
        return Integer.parseInt(time.split(":")[0]);
    }

    public static int getSeconds(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }
}
