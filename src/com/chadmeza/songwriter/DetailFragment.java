/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chadmeza.songwriter.db.SongsDataSource;
import com.chadmeza.songwriter.model.Audio;
import com.chadmeza.songwriter.model.Song;

public class DetailFragment extends Fragment {

	protected EditText mSongText;
	protected TextView mRecordAudio;
	protected ImageView mRecordImage;
	
	protected boolean mSetToRed = false;
	
	public SongsDataSource mDatasource;
	
	public Song mSong;
	
	protected String mFileName = null;
	protected MediaRecorder mRecorder = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/**
		 * Receives DetailActivity's passed in intent, which
		 * contains the selected mSong.
		 */
		Bundle b = getActivity().getIntent().getExtras();
		if (b != null) {
			mSong = b.getParcelable(MainFragment.EXTRA_SONG);
		}
		
		mDatasource = new SongsDataSource(getActivity());
		mDatasource.close();
		mDatasource.open();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_detail, container, false);
		
		mSongText = (EditText) v.findViewById(R.id.songText);
		mSongText.setText(mSong.getText());
		mSongText.setSelection(mSong.getText().length());
		mSongText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSong.setText(s.toString());
			}
			
		});
		
		mRecordAudio = (TextView) v.findViewById(R.id.recordAudio);
		mRecordAudio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				updateRecordViews();
			}

			
		});
		
		mRecordImage = (ImageView) v.findViewById(R.id.recordButton);
		mRecordImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				updateRecordViews();
			}
		});
		
		return v;
	}
	
	/**
	 * Manages the view for recording a new audio file, and is
	 * activated when the user clicks the mRecordAudio or mRecordImage
	 * view. It utilizes the mSetToRed variable to determine its 
	 * action. When mSetToRed is false, a new audio recording needs 
	 * to begin, and the views must be updated to show that recording
	 * has begun. When mSetToRed is true, an audio file has been
	 * recording, and it needs to stop and save. Once saved, the
	 * AudioFragment's refreshDisplay method is called to update
	 * the mAudioFiles list.
	 * 
	 * @return void
	 */
	public void updateRecordViews() {
		if (mSetToRed) {
			stopRecording();
			mSetToRed = false;
			mRecordAudio.setText(R.string.action_record);
			mRecordAudio.setTextColor(getResources().getColor(R.color.icon_on_dark_bg));
			mRecordImage.setImageResource(R.drawable.ic_av_mic);
		} else {
			startRecordTask();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
		switch (item.getItemId()) {
		case android.R.id.home:
			intent = new Intent();
			intent.putExtra(MainFragment.EXTRA_SONG, mSong);
			getActivity().setResult(getActivity().RESULT_OK, intent);
			getActivity().finish();
			break;
		
		case R.id.action_share_song:
			intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, mSong.getText());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		
		case R.id.action_delete_song:
			intent = new Intent(getActivity(), MainActivity.class);
			intent.putExtra(MainFragment.EXTRA_DELETE_SONG, mSong);
			startActivity(intent);
			break;
		
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        
		Intent intent = new Intent();
		intent.putExtra(MainFragment.EXTRA_SONG, mSong);
		getActivity().setResult(MainFragment.RESULT_RETURN, intent);
		getActivity().finish();
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
	
	private void startRecordTask() {
		createFileName();
        
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + mFileName;
        File f = new File(file);

        if (f.exists()) {
        	startRecordTask();
        	return;
        } else {
        	RecordTask task = new RecordTask();
        	task.execute();
        }
	}

	private void createFileName() {
		Random rand = new Random();
		int randNum = rand.nextInt(9999999);
		
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/songwriter_" + randNum + ".3gp";
	}
	
	protected void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            
        }

        mRecorder.start();
    }

    protected void stopRecording() {
        if (mRecorder != null) {
	    	mRecorder.stop();
	        mRecorder.release();
	        mRecorder = null;
        }
        
        Audio audio = new Audio();
        audio.setFilename(mFileName);
        audio.setSongId(mSong.getId());
        audio = mDatasource.addAudio(audio);
        
        AudioFragment audioFrag = (AudioFragment) getActivity().getFragmentManager().findFragmentById(R.id.audioFragment);
		audioFrag.refreshDisplay();
    }
	
	private class RecordTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mSetToRed = true;
			mRecordAudio.setText(R.string.action_stop_record);
			mRecordAudio.setTextColor(getResources().getColor(R.color.icon_red_highlight));
			mRecordImage.setImageResource(R.drawable.ic_av_mic_red);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			startRecording();
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
