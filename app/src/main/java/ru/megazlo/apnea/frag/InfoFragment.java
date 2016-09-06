package ru.megazlo.apnea.frag;

import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
		infoView.setText(Html.fromHtml(readTxt(resRawId)));
		//Linkify.addLinks(infoView, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
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
	public boolean backPressed() {
		return true;
	}

	public InfoFragment setResRawId(int resRawId) {
		this.resRawId = resRawId;
		return this;
	}
}
