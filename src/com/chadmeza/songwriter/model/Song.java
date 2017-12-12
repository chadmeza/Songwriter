/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
	private long id;
	private String text;
	
	public Song() {
		text = "";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
	
	public Song(Parcel in) {
        id = in.readLong();
        text = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
  
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
        dest.writeString(text);
	}

	public static final Parcelable.Creator<Song> CREATOR =
             new Parcelable.Creator<Song>() {

        @Override
        public Song createFromParcel(Parcel source) {
             return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
             return new Song[size];
        }

	};
}
