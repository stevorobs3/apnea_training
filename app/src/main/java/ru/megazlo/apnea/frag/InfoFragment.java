package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.*;
import java.util.Formatter;
import java.util.Locale;

import ru.megazlo.apnea.BuildConfig;
import ru.megazlo.apnea.R;

@EFragment(R.layout.info_fragment_layout)
public class InfoFragment extends Fragment implements FabClickListener {

	private int resRawId = -1;

	@ViewById(R.id.info_text)
	TextView infoView;

	@AfterViews
	void init() {
		if (resRawId == -1) {
			Log.e("InfoFragment", "Raw resource not found");
			return;
		}
		infoView.setText(Html.fromHtml(readTxt()));
	}

	@NonNull
	private String readTxt() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(resRawId)));
		StringBuilder bld = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				bld.append(line).append("\r\n");
			}
			reader.close();
		} catch (IOException ignored) {
		}
		return bld.toString();
	}

	@Override
	public void clickByContext(View view) {
	}

	@Override
	public void modifyToContext(View view) {
		view.setVisibility(View.GONE);
	}

	@Override
	public boolean backPressed() {
		return true;
	}

	public InfoFragment setResRawId(int resRawId) {
		this.resRawId = resRawId;
		return this;
	}
}
