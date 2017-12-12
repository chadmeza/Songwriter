/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chadmeza.songwriter.model.Audio;
import com.chadmeza.songwriter.model.Song;

public class SongsDataSource {
	
	public SQLiteOpenHelper mDbhelper;
	public SQLiteDatabase mDatabase;

	private static final String[] allSongsColumns = {
		SongsDBOpenHelper.COLUMN_SONG_ID,
		SongsDBOpenHelper.COLUMN_SONG_TEXT};
	
	private static final String[] allAudioColumns = {
		SongsDBOpenHelper.COLUMN_AUDIO_ID,
		SongsDBOpenHelper.COLUMN_AUDIO_FILENAME,
		SongsDBOpenHelper.COLUMN_SONG_ID};

	public SongsDataSource(Context context) {
		mDbhelper = new SongsDBOpenHelper(context);
	}

	/**
	 * Gets a reference to the mDatabase.
	 * 
	 * @return void
	 */
	public void open() {
		mDatabase = mDbhelper.getWritableDatabase();
	}

	/**
	 * Closes the mDatabase connection.
	 * 
	 * @return void
	 */
	public void close() {
		mDbhelper.close();
	}

	/**
	 * Adds a mSong to the mDatabase.
	 * 
	 * @param Song mSong
	 * @return Song
	 */
	public Song addSong(Song song) {
		ContentValues values = new ContentValues();
		values.put(SongsDBOpenHelper.COLUMN_SONG_TEXT, song.getText());
		long insertid = mDatabase.insert(SongsDBOpenHelper.TABLE_SONGS, null, values);
		song.setId(insertid);
		return song;
	}
	
	/**
	 * Updates a mSong in the mDatabase.
	 * 
	 * @param Song mSong
	 * @return void
	 */
	public void updateSong(Song song) {
		ContentValues values = new ContentValues();
		values.put(SongsDBOpenHelper.COLUMN_SONG_TEXT, song.getText());

		String selection = SongsDBOpenHelper.COLUMN_SONG_ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(song.getId()) };

		int count = mDatabase.update(
				SongsDBOpenHelper.TABLE_SONGS,
		    values,
		    selection,
		    selectionArgs);
	}
	
	/**
	 * Deletes a mSong from the mDatabase.
	 * 
	 * @param Song mSong
	 * @return void
	 */
	public void deleteSong(Song song) {
		String whereClause = SongsDBOpenHelper.COLUMN_SONG_ID + " LIKE ?";
		String[] whereArgs = {String.valueOf(song.getId())};
		
		int count = mDatabase.delete(SongsDBOpenHelper.TABLE_SONGS, whereClause, whereArgs);
	}

	/**
	 * Retrieves a list of all songs in the mDatabase.
	 * 
	 * @return List<Song>
	 */
	public List<Song> showAllSongs() {

		Cursor cursor = mDatabase.query(SongsDBOpenHelper.TABLE_SONGS, allSongsColumns, 
				null, null, null, null, null);

		List<Song> songs = new ArrayList<Song>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Song song = new Song();
				song.setId(cursor.getLong(cursor.getColumnIndex(SongsDBOpenHelper.COLUMN_SONG_ID)));
				song.setText(cursor.getString(cursor.getColumnIndex(SongsDBOpenHelper.COLUMN_SONG_TEXT)));
				songs.add(song);
			}
		}
		
		return songs;
	}
	
	/**
	 * Retrieves a list of all audio files in the mDatabase
	 * that are linked to the passed in songId.
	 * 
	 * @param long songId
	 * @return List<Audio>
	 */
	public List<Audio> showAllAudio(long songId) {

		String query = "SELECT * FROM audio WHERE songId = " + songId;
		Cursor cursor = mDatabase.rawQuery(query, null);

		List<Audio> audioFiles = new ArrayList<Audio>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Audio audio = new Audio();
				audio.setId(cursor.getLong(cursor.getColumnIndex(SongsDBOpenHelper.COLUMN_AUDIO_ID)));
				audio.setFilename(cursor.getString(cursor.getColumnIndex(SongsDBOpenHelper.COLUMN_AUDIO_FILENAME)));
				audio.setSongId(cursor.getLong(cursor.getColumnIndex(SongsDBOpenHelper.COLUMN_SONG_ID)));
				audioFiles.add(audio);
			}
		}
		
		return audioFiles;
	}
	
	/**
	 * Adds an audio file to the mDatabase.
	 * 
	 * @param Audio audio
	 * @return Audio
	 */
	public Audio addAudio(Audio audio) {
		ContentValues values = new ContentValues();
		values.put(SongsDBOpenHelper.COLUMN_AUDIO_FILENAME, audio.getFilename());
		values.put(SongsDBOpenHelper.COLUMN_SONG_ID, audio.getSongId());
		long insertid = mDatabase.insert(SongsDBOpenHelper.TABLE_AUDIO, null, values);
		audio.setId(insertid);
		return audio;
	}
	
	/**
	 * Deletes an audio file from the mDatabase.
	 * 
	 * @param Audio audio
	 * @return void
	 */
	public void deleteAudio(Audio audio) {
		String whereClause = SongsDBOpenHelper.COLUMN_AUDIO_ID + " LIKE ?";
		String[] whereArgs = {String.valueOf(audio.getId())};
		
		int count = mDatabase.delete(SongsDBOpenHelper.TABLE_AUDIO, whereClause, whereArgs);
	}
}
