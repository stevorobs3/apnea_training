package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.view.View;

import org.androidannotations.annotations.EFragment;

import ru.megazlo.apnea.R;

@EFragment(R.layout.info_fragment_layout)
public class InfoFragment extends Fragment implements FabClickListener {

    @Override
    public void clickByContext(View view) {

    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.VISIBLE);
    }
}
