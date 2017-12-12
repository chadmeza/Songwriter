/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chadmeza.songwriter.db.SongsDataSource;
import com.chadmeza.songwriter.model.Song;

public class MainFragment extends ListFragment {

	public static final String EXTRA_DELETE_SONG = "deleteSong";
	public static final String EXTRA_UPDATE_SONG = "updateSong";
	public static final String EXTRA_SONG = ".model.Song";

	public static final int RESULT_UPDATE_SONG = 100;
	public static final int RESULT_RETURN = 777;

	private List<Song> mSongs;

	public SongsDataSource mDatasource;
	
	public MainFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		mDatasource = new SongsDataSource(getActivity());
		mDatasource.close();
		mDatasource.open();
		
		if (getActivity().getIntent().hasExtra(EXTRA_DELETE_SONG)) {
			Bundle b = getActivity().getIntent().getExtras();
			Song deleteSong = b.getParcelable(EXTRA_DELETE_SONG);
			mDatasource.deleteSong(deleteSong);
		}

		refreshDisplay();
	}

	/**
	 * Retrieves a list of all mSong files in the mDatabase.
	 * The list is used to instantiate mSongs. Then mSongs is 
	 * passed in to create a the ArrayAdapter, which is an 
	 * instance of the custom SongArrayAdapter. Finally, the
	 * adapter is set.
	 * 
	 * @return void
	 */
	public void refreshDisplay() {
		mSongs = mDatasource.showAllSongs();
		ArrayAdapter<Song> adapter = new SongArrayAdapter(getActivity(), mSongs);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		ListView listView = (ListView) v.findViewById(android.R.id.list);
		registerForContextMenu(listView);
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_song:
			Song song = new Song();
			song = mDatasource.addSong(song);

			editSong(song);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creates an intent to package the selected mSong,
	 * and view it in DetailActivity's fragments.
	 * 
	 * @param Song mSong
	 * @return void
	 */
	private void editSong(Song song) {
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra(EXTRA_SONG, song);
		startActivityForResult(intent, RESULT_UPDATE_SONG);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Song song = mSongs.get(position);
		
		editSong(song);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RESULT_UPDATE_SONG && resultCode == getActivity().RESULT_OK) {
			Song song = data.getParcelableExtra(EXTRA_SONG);
			mDatasource.updateSong(song);
			refreshDisplay();
		} else if (requestCode == RESULT_UPDATE_SONG && resultCode == RESULT_RETURN) {
			Song song = data.getParcelableExtra(EXTRA_SONG);
			mDatasource.updateSong(song);
			editSong(song);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.context_delete_song:
	            mDatasource.deleteSong(mSongs.get(info.position));
	            refreshDisplay();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }

	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshDisplay();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDatasource.close();
	}
}
