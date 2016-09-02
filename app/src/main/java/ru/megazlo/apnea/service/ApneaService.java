package ru.megazlo.apnea.service;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

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

	@Pref
	ApneaPrefs_ pref;

	public List<TableApneaRow> getRowsForTable(TableApnea table) {
		if (table.getType() == TableType.O2) {
			return createOxygenSeries();
		} else if (table.getType() == TableType.CO2) {
			return createCarbonSeries();
		} else {
			return loadDbSeries();
		}
	}

	private List<TableApneaRow> loadDbSeries() {
		List<TableApneaRow> rez = new ArrayList<>();

		return rez;
	}

	private List<TableApneaRow> createOxygenSeries() {
		List<TableApneaRow> rez = new ArrayList<>();
		int breTime = Utils.getTotalSeconds(pref.o2Timeout().get());
		int holTimeStart = Utils.getTotalSeconds(pref.o2StartTime().get());
		int holTimeEnd = Utils.getTotalSeconds(pref.o2EndTime().get());
		int increaseSec = pref.o2IncreaseTime().get();

		int pos = 1;
		do {
			TableApneaRow r = new TableApneaRow();
			r.setBreathe(breTime);
			r.setHold(holTimeStart);
			r.setOrder(pos);
			r.setId(pos);
			rez.add(r);

			holTimeStart += increaseSec;
			pos++;
		} while (holTimeStart <= holTimeEnd);
		return rez;
	}

	private List<TableApneaRow> createCarbonSeries() {
		List<TableApneaRow> rez = new ArrayList<>();
		int myMax = Utils.getTotalSeconds(pref.bestRecord().get());
		int hold = (int) (myMax * pref.co2Percent().get() / 100.0);
		int breTimeStart = Utils.getTotalSeconds(pref.co2StartTime().get());
		int breTimeEnd = Utils.getTotalSeconds(pref.co2EndTime().get());
		int reduceSec = pref.co2Reduce().get();

		int pos = 1;
		do {
			TableApneaRow r = new TableApneaRow();
			r.setHold(hold);
			r.setBreathe(breTimeStart);
			r.setOrder(pos);
			r.setId(pos);
			rez.add(r);
			breTimeStart -= reduceSec;
			pos++;
		} while (breTimeStart >= breTimeEnd);

		correctSeries(rez);
		return rez;
	}

	private void correctSeries(List<TableApneaRow> rows) {
		if (rows == null || rows.size() == 0) {
			return;
		}
		while (rows.size() < 8) {
			final TableApneaRow orig = rows.get(rows.size() - 1);
			TableApneaRow r = new TableApneaRow();
			r.setHold(orig.getHold());
			r.setBreathe(orig.getBreathe());
			r.setOrder(orig.getOrder() + 1);
			rows.add(r);
		}
	}
}
