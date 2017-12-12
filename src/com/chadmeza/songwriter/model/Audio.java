/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter.model;

public class Audio {
	private long id;
	private String filename;
	private long songId;
	
	public Audio() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSongId() {
		return songId;
	}

	public void setSongId(long songId) {
		this.songId = songId;
	}
}
