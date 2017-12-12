/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongsDBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "songwriter.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_SONGS = "songs";
	public static final String COLUMN_SONG_ID = "songId";
	public static final String COLUMN_SONG_TEXT = "songText";

	private static final String TABLE_SONGS_CREATE = 
			"CREATE TABLE " + TABLE_SONGS + " (" +
					COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_SONG_TEXT + " TEXT" +
					")";

	public static final String TABLE_AUDIO = "audio";
	public static final String COLUMN_AUDIO_ID = "audioId";
	public static final String COLUMN_AUDIO_FILENAME = "audioFilename";

	private static final String TABLE_AUDIO_CREATE = 
			"CREATE TABLE " + TABLE_AUDIO + " (" +
					COLUMN_AUDIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_AUDIO_FILENAME + " TEXT, " +
					COLUMN_SONG_ID + " INTEGER" +
					")";
	
	public SongsDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_SONGS_CREATE);
		db.execSQL(TABLE_AUDIO_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO);
		onCreate(db);
	}

}
