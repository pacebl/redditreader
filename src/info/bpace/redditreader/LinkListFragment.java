package info.bpace.redditreader;

import info.bpace.redditreader.api.Link;
import info.bpace.redditreader.api.Reddit;
import info.bpace.redditreader.api.Thing;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A fragment representing a single Subreddit detail screen. This fragment is
 * either contained in a {@link SubredditListActivity} in two-pane mode (on
 * tablets) or a {@link SubredditDetailActivity} on handsets.
 */
public class LinkListFragment extends ListFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	private static final String TAG = "linklistfragment";
	
	private List<Link> objects = new ArrayList<Link>();
	private ArrayAdapter<Link> aa = null;
	private LinkAdapter la = null;
	private Activity a = null;
	private int text = 0;
	private int layout = 0;
	private String stringUrl;
	
	private Callbacks mCallbacks = sDummyCallbacks;
	
	private static Callbacks sDummyCallbacks = new Callbacks() {
		public void readLink(Link link) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LinkListFragment() {
	}
	
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void readLink(Link link);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		a = getActivity();
	    layout = android.R.layout.simple_list_item_activated_1;
	    text = android.R.id.text1;
	    aa = new ArrayAdapter<Link>(a, layout, text, objects);
	    
	    Bundle extras = getArguments();
	    if(extras != null) {
	    	Log.d(TAG, "My extra is: " + extras.getString(ARG_ITEM_ID));
	    	stringUrl = getArguments().getString(ARG_ITEM_ID);
	    } else {
	    	Log.d(TAG, "No extras :(");
	    	stringUrl = "http://www.reddit.com/hot";
	    }
	    
	    ConnectivityManager connMgr = 
	    		(ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if(networkInfo != null && networkInfo.isConnected()) {
	    	new FrontpageTask().execute();
	    }
	    
	    //setListAdapter(aa);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.readLink((Link) la.getItem(position));
	}

	public class FrontpageTask extends AsyncTask<Void, String, Thing[]> {

		@Override
		protected Thing[] doInBackground(Void... arg0) {
			return Reddit.Subreddits.subreddit(stringUrl);
		}
		
		@Override
		protected void onPostExecute(Thing[] result) {
			Link[] links = new Link[result.length];
			for(int i = 0; i < result.length; i++) {
				if(result[i] instanceof Link) {
					links[i] = (Link) result[i];
				}
			}
			la = new LinkAdapter(getActivity(), links);
			setListAdapter(la);
		}
	}
}
