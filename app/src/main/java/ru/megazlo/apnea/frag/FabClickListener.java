package ru.megazlo.apnea.frag;

import android.view.View;

public interface FabClickListener extends BackPressHandler {
    /**
     * Обработка нажатия плавающей кнопки в зависимости от контекста
     */
    void clickByContext(View view);

    /**
     * Подстройка контекста приложения и окружения
     */
    void modifyToContext(View view);
}
