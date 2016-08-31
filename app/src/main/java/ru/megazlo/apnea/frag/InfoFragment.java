package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.megazlo.apnea.R;

@EFragment(R.layout.info_fragment_layout)
public class InfoFragment extends Fragment implements FabClickListener {

    @ViewById(R.id.info_text)
    TextView infoView;

    @AfterViews
    void init() {
        infoView.setText(Html.fromHtml(readTxt(R.raw.info)));
    }

    private String readTxt(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            int i = inputStream.read();
            while (i != -1) {
                bos.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toString();
    }

    @Override
    public void clickByContext(View view) {

    }

    @Override
    public void modifyToContext(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void backPressed() {
    }
}
