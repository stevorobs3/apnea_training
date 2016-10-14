package ru.megazlo.apnea.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.ormlite.annotations.OrmLiteDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.component.Utils;
import ru.megazlo.apnea.db.DatabaseHelper;
import ru.megazlo.apnea.entity.*;

@EBean(scope = EBean.Scope.Singleton)
public class ApneaService {

	private final static int ID_O2 = -2;
	private final static int ID_CO2 = -1;

	@RootContext
	Context context;

	@Pref
	ApneaPrefs_ pref;

	@OrmLiteDao(helper = DatabaseHelper.class)
	Dao<TableApnea, Integer> tableDao;
	@OrmLiteDao(helper = DatabaseHelper.class)
	Dao<TableApneaRow, Integer> rowDao;

	public List<TableApneaRow> getRowsForTable(TableApnea table) {
		if (table.getId() == ID_O2 && validateO2Settings()) {
			return createOxygenSeries();
		} else if (table.getId() == ID_CO2 && validateCO2Settings()) {
			return createCarbonSeries();
		}
		return loadDbSeries(table.getId());
	}

	private List<TableApneaRow> loadDbSeries(Integer tableId) {
		try {
			return rowDao.queryForEq("table_id", tableId);
		} catch (SQLException e) {
			Log.w("ApneaService", "Error load series");
			return new ArrayList<>();
		}
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

	private boolean validateO2Settings() {
		int breTime = Utils.getTotalSeconds(pref.o2Timeout().get());
		int holTimeStart = Utils.getTotalSeconds(pref.o2StartTime().get());
		int holTimeEnd = Utils.getTotalSeconds(pref.o2EndTime().get());
		int increaseSec = pref.o2IncreaseTime().get();
		if (breTime == 0 || increaseSec == 0 || holTimeStart >= holTimeEnd) {
			Toast.makeText(context, R.string.valid_o2_simple, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean validateCO2Settings() {
		int myMax = Utils.getTotalSeconds(pref.bestRecord().get());
		int hold = (int) (myMax * pref.co2Percent().get() / 100.0);
		int breTimeStart = Utils.getTotalSeconds(pref.co2StartTime().get());
		int breTimeEnd = Utils.getTotalSeconds(pref.co2EndTime().get());
		int reduceSec = pref.co2Reduce().get();
		if (hold == 0 || reduceSec == 0 || breTimeStart < breTimeEnd) {
			Toast.makeText(context, R.string.valid_co2_simple, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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

	public List<TableApnea> loadAllTables() {
		List<TableApnea> userTables;
		try {
			userTables = tableDao.queryForAll();
		} catch (SQLException e) {
			userTables = new ArrayList<>();
		}
		userTables.add(0, createTableApnea(ID_O2, R.color.list_lung_o, R.string.def_tab_title_o2));
		userTables.add(0, createTableApnea(ID_CO2, R.color.list_lung_co, R.string.def_tab_title_co2));
		return userTables;
	}

	public boolean saveNewTable(TableApnea table, List<TableApneaRow> rows) {
		try {
			tableDao.create(table);
			int order = 0;
			for (TableApneaRow r : rows) {
				r.setTable(table.getId());
				r.setOrder(order++);
			}
			rowDao.create(rows);
			return true;
		} catch (SQLException ignored) {
			Log.w("ApneaService", "Can`t create table with rows");
			return false;
		}
	}

	private TableApnea createTableApnea(int id, int color, int titleRes) {
		TableApnea tab = new TableApnea(id);
		tab.setColor(context.getResources().getColor(color));
		tab.setTitle(context.getResources().getString(titleRes));
		tab.setDescription(context.getResources().getString(R.string.table_description_calc));
		tab.setType(TableType.CALC);
		return tab;
	}

	public void deleteTableById(Integer id) {
		final DeleteBuilder<TableApneaRow, Integer> builder = rowDao.deleteBuilder();
		try {
			builder.where().eq("table_id", id);
			builder.delete();
		} catch (SQLException e) {
			Log.w("ApneaService", "Can`t delete series with table id " + id);
		}
		try {
			tableDao.deleteById(id);
		} catch (SQLException ignored) {
			Log.w("ApneaService", "Can`t delete table with id " + id);
		}
	}

	public List<OxiSession> loadAllSessions() {
		final ArrayList<OxiSession> rez = new ArrayList<>();
		rez.add(new OxiSession());
		return rez;
	}
}
