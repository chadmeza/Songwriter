/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import java.io.IOException;
import java.util.List;

import android.app.ListFragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.chadmeza.songwriter.db.SongsDataSource;
import com.chadmeza.songwriter.model.Audio;
import com.chadmeza.songwriter.model.Song;

public class AudioFragment extends ListFragment {

	protected List<Audio> mAudioFiles;
	protected boolean mIsPlaying = false;
	
	protected ImageView mPlayButton;
	
	protected Song mSong;
	
	public SongsDataSource mDatasource;
	
	protected String mFileName = null;
	protected MediaPlayer mPlayer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		/**
		 * Retrieves DetailActivity's passed in intent, which contains
		 * the selected mSong.
		 */
		Bundle b = getActivity().getIntent().getExtras();
		mSong = b.getParcelable(MainFragment.EXTRA_SONG);
		
		mDatasource = new SongsDataSource(getActivity());
		mDatasource.close();
		mDatasource.open();
		
		refreshDisplay();
	}

	/**
	 * Uses the current mSong's ID to retrieve a list of all audio files
	 * that are attached to this mSong. The list is used to instantiate
	 * mAudioFiles. Then mAudioFiles is passed in to create a the ArrayAdapter,
	 * which is an instance of the custom AudioArrayAdapter. Finally, the
	 * adapter is set.
	 * 
	 * @return void
	 */
	public void refreshDisplay() {
		mAudioFiles = mDatasource.showAllAudio(mSong.getId());
		ArrayAdapter<Audio> adapter = new AudioArrayAdapter(getActivity(), mAudioFiles);
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_audio, container, false);
		ListView listView = (ListView) v.findViewById(android.R.id.list);
		registerForContextMenu(listView);
		return v;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		/**
		 * When the play button is clicked, the image resource is swapped
		 * with a stop button. When the stop button is clicked, the image
		 * resource is set back to the play button.
		 */
		Audio audio = mAudioFiles.get(position);
		mFileName = audio.getFilename();
		
		mPlayButton = (ImageView) v.findViewById(R.id.playButton);
		if (mIsPlaying) {
			stopPlaying();
			mIsPlaying = false;
			mPlayButton.setImageResource(R.drawable.ic_av_play_circle_fill);
		} else {
			startPlayTask();
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
	            mDatasource.deleteAudio(mAudioFiles.get(info.position));
	            refreshDisplay();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDatasource.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void startPlayTask() {
		PlayTask task = new PlayTask();
    	task.execute();
	}
	
	protected void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer player) {
					stopPlaying();
				}
			});
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            
        }
    }

    protected void stopPlaying() {
        if (mPlayer != null) {
	    	mPlayer.release();
	        mPlayer = null;
	        
	        mIsPlaying = false;
			mPlayButton.setImageResource(R.drawable.ic_av_play_circle_fill);
        }
    }
    
    private class PlayTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mIsPlaying = true;
			mPlayButton.setImageResource(R.drawable.ic_av_stop);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			startPlaying();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
		}
	}
}
