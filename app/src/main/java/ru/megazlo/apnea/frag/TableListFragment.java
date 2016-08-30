package ru.megazlo.apnea.frag;

import android.app.ListFragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.EagerForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.megazlo.apnea.R;
import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.entity.TableRow;
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
        userTables.add(0, getO2Table());
        userTables.add(0, getCO2Table());
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

    public TableApnea getO2Table() {
        TableApnea tab = getTableApnea(0xff368ec9, titleO2);
        return tab;
    }

    public TableApnea getCO2Table() {
        TableApnea tab = getTableApnea(0xffff7925, titleCO2);
        return tab;
    }

    @NonNull
    private TableApnea getTableApnea(int color, String title) {
        TableApnea tab = new TableApnea();
        tab.setColor(color);
        tab.setTitle(title);
        return tab;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void clickByContext(View view) {
        Snackbar.make(view, "В разработке. Можно будет создавать свои таблицы", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (FloatingActionButton) view;
        fab.setImageResource(R.drawable.ic_add_plus);
    }
}
