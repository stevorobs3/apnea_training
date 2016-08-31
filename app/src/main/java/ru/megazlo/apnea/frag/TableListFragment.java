package ru.megazlo.apnea.frag;

import android.app.ListFragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableType;
import ru.megazlo.apnea.extend.TableListAdapter;

@EFragment(R.layout.table_list)
public class TableListFragment extends ListFragment implements FabClickListener {

    @StringRes(R.string.def_tab_title_o2)
    protected String titleO2;
    @StringRes(R.string.def_tab_title_co2)
    protected String titleCO2;

    private AdapterView.OnItemClickListener listener;

    /*@OrmLiteDao(helper = DatabaseHelper.class)
    protected Dao<TableApnea, Integer> tableDao;*/

    @AfterViews
    protected void afterView() {
        Context ctx = this.getActivity();
        TableListAdapter adapter = new TableListAdapter(ctx, 0);
        adapter.addAll(getAllTables());
        this.setListAdapter(adapter);
        if (listener != null) {
            ListView listView = this.getListView();
            listView.setOnItemClickListener(listener);
        }
    }

    public List<TableApnea> getAllTables() {
        List<TableApnea> userTables = loadAllForTitle();
        userTables.add(0, getTableApnea(0xff368ec9, titleO2, TableType.O2));
        userTables.add(0, getTableApnea(0xffff7925, titleCO2, TableType.CO2));
        return userTables;
    }

    protected List<TableApnea> loadAllForTitle() {
        /*try {
            return tableDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return new ArrayList<>();
    }

    @NonNull
    private TableApnea getTableApnea(int color, String title, TableType type) {
        TableApnea tab = new TableApnea();
        tab.setColor(color);
        tab.setTitle(title);
        tab.setDescription(getString(R.string.table_description_calc));
        tab.setType(type);
        return tab;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void clickByContext(View view) {
        Snackbar.make(view, R.string.snack_tab_dev, Snackbar.LENGTH_SHORT).setAction(R.string.ok, null).show();
    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (FloatingActionButton) view;
        fab.setImageResource(R.drawable.ic_add_plus);
    }

    @Override
    public void backPressed() {
    }
}
