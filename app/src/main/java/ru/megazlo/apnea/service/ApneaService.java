package ru.megazlo.apnea.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableApneaRow;
import ru.megazlo.apnea.entity.TableType;

@EBean
public class ApneaService {

    @RootContext
    Context context;

    public List<TableApneaRow> getRowsForTable(TableApnea table) {
        if (table.getType() == TableType.O2) {
            return createOxygenSeries();
        } else if (table.getType() == TableType.CO2) {
            return createCarbonSeries();
        } else {
            return loadDbSeries();
        }
    }

    private List<TableApneaRow> createOxygenSeries() {
        List<TableApneaRow> rez = new ArrayList<>();
        final SharedPreferences p = getSettings();

        return rez;
    }

    private List<TableApneaRow> createCarbonSeries() {
        List<TableApneaRow> rez = new ArrayList<>();
        final SharedPreferences p = getSettings();
        int myMax = Utils.getTotalSeconds(p.getString("pref_best_record", ""));
        int percOfMax = p.getInt("pref_co2_percent", 1);
        int hold = (int) (myMax * percOfMax / 100.0);
        int breTimeStart = Utils.getTotalSeconds(p.getString("pref_co2_start_time", ""));
        int breTimeEnd = Utils.getTotalSeconds(p.getString("pref_co2_end_time", ""));
        int reduceSec = p.getInt("pref_co2_reduce", 1);

        int pos = 1;
        do {
            TableApneaRow r = new TableApneaRow();
            r.setHold(hold);
            r.setBreathe(breTimeStart);
            r.setOrder(pos);
            rez.add(r);
            breTimeStart -= reduceSec;
            pos++;
        } while (breTimeStart >= breTimeEnd);

        correctSeries(rez);
        return rez;
    }

    private void correctSeries(List<TableApneaRow> rows) {
        while (rows.size() < 8) {
            final TableApneaRow orig = rows.get(rows.size() - 1);
            TableApneaRow r = new TableApneaRow();
            r.setHold(orig.getHold());
            r.setBreathe(orig.getBreathe());
            r.setOrder(orig.getOrder() + 1);
            rows.add(r);
        }
    }

    private List<TableApneaRow> loadDbSeries() {
        List<TableApneaRow> rez = new ArrayList<>();

        return rez;
    }

    private SharedPreferences getSettings() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
