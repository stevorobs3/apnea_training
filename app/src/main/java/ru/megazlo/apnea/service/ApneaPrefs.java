package ru.megazlo.apnea.service;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.UNIQUE)
public interface ApneaPrefs {

    @DefaultString("2:40")
    String bestRecord();

    //region оповещения
    @DefaultBoolean(true)
    boolean notifyVibrate();

    @DefaultBoolean(true)
    boolean notifySpeech();

    @DefaultBoolean(true)
    boolean notifyMinute();

    @DefaultBoolean(true)
    boolean notify30sec();

    @DefaultBoolean(true)
    boolean notify10sec();

    @DefaultBoolean(true)
    boolean notify5sec();
    //endregion

    //region кислородное голодание
    @DefaultString("2:00")
    String o2Timeout();

    @DefaultString("1:00")
    String o2StartTime();

    @DefaultInt(15)
    int o2IncreaseTime();

    @DefaultString("2:30")
    String o2EndTime();
    //endregion

    //region избыток углекислого газа
    @DefaultInt(50)
    int co2Percent();

    @DefaultString("2:30")
    String co2StartTime();

    @DefaultInt(15)
    int co2Reduce();

    @DefaultString("1:00")
    String co2EndTime();
    //endregion

}
