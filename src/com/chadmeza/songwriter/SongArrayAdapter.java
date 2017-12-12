/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chadmeza.songwriter.model.Song;

public class SongArrayAdapter extends ArrayAdapter<Song> {
	
	private Context mContext;
	private List<Song> mSongs;
	
	public SongArrayAdapter(Context context, List<Song> songs) {
		super(context, android.R.id.content, songs);
		this.mContext = context;
		this.mSongs = songs;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        view = vi.inflate(R.layout.listitem_song, null);
		}
		
        Song song = mSongs.get(position);
        
        TextView textView = (TextView) view.findViewById(R.id.songTitle);
        textView.setText(song.getText());
        
        return view;
	}
}
