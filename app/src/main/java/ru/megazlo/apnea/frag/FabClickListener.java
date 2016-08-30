package ru.megazlo.apnea.frag;

import android.view.View;

public interface FabClickListener {
    /**
     * Обработка нажатия плавающей кнопки в зависимости от контекста
     */
    void clickByContext(View view);

    /**
     * Подстройка плавающей кнопки под контекст приложения и окружения
     */
    void modifyToContext(View view);
}
