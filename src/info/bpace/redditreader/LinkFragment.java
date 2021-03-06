package info.bpace.redditreader;

import info.bpace.redditreader.api.WebUtils;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class LinkFragment extends Fragment {

	public static final String ARG_LINK_ID = "link_id";
	public static final String ARG_LINK_TITLE = "link_title";
	public static final String ARG_LINK_SELFTEXT = "link_selftext";
	public static final String ARG_LINK_URL = "link_url";
	private static final String TAG = "linkfragment";

	private String id;
	private String title;
	private String selftext;
	private String url;

	private FrameLayout frame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();
		if (extras != null) {
			Log.d(TAG, extras.getString(ARG_LINK_ID));
			id = extras.getString(ARG_LINK_ID);
			title = extras.getString(ARG_LINK_TITLE);
			selftext = extras.getString(ARG_LINK_SELFTEXT);
			url = extras.getString(ARG_LINK_URL);

			Log.d(TAG, extras.getString(ARG_LINK_SELFTEXT));
			Log.d(TAG, extras.getString(ARG_LINK_URL));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_post, container, false);
		frame = (FrameLayout) view.findViewById(R.id.post_content);

		((TextView) view.findViewById(R.id.post_title)).setText(title);

		if (selftext.isEmpty()) {
			try {
				new ImageTask().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			TextView tv = new TextView(getActivity());
			tv.setText(title);
			frame.addView(tv);
		}
		return view;
	}

	private class ImageTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... arg0) {
			return WebUtils.grabImage(url, false);
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			ImageView iv = new ImageView(getActivity());
			iv.setImageBitmap(bmp);
			frame.addView(iv);
		}
	}
}
