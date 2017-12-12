/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chadmeza.songwriter.model.Audio;

public class AudioArrayAdapter extends ArrayAdapter<Audio> {
	private Context mContext;
	private List<Audio> mAudioFiles;
	
	private TextView mAudioInfo;
	private ImageView mShareButton;
	
	public Uri mAudioUri;
	
	public AudioArrayAdapter(Context context, List<Audio> audioFiles) {
		super(context, android.R.id.content, audioFiles);
		this.mContext = context;
		this.mAudioFiles = audioFiles;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        view = vi.inflate(R.layout.listitem_audio, null);
		}
		
		Audio audio = mAudioFiles.get(position);
		long fileDuration = (long) getFileDuration(audio.getFilename());
		long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(fileDuration);
		long durationMinutes = durationSeconds / 60;
		long durationHours = durationMinutes / 60;
		String audioDuration = String.format("%02d:%02d:%02d", durationHours, durationMinutes, durationSeconds % 60);;
        
        mAudioInfo = (TextView) view.findViewById(R.id.audioInfo);
        mAudioInfo.setText(audioDuration);
        
        File requestFile = new File(audio.getFilename());
        requestFile.setReadable(true, false);

        mAudioUri = Uri.fromFile(requestFile);
        		
        mShareButton = (ImageView) view.findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, mAudioUri);
				shareIntent.setType("audio/*");
				getContext().startActivity(shareIntent);
			}
		});
        
        return view;
	}
	
	private int getFileDuration(String filename) {
        MediaPlayer audioPlayer = new MediaPlayer();
        try {
        	audioPlayer.setDataSource(filename);
        	audioPlayer.prepare();
        	return audioPlayer.getDuration();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return 0;
	}

}
