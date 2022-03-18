package com.greenledge.quran;


import java.io.Serializable;

public class Bookmark implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Ayah ayah;
	private String comment;
	private Bookmark bookmark;
	private static Bookmark[] bookmarks=null;
	private static Ayah[] items = null;
	
	public Bookmark(Ayah ayah, String comment){
		this.ayah=ayah;
		this.comment=comment;
	}
	
	public Ayah getAyah(){
		return ayah;
	}
	
	public String getComment(){
		return comment;
	}
	
	public void setComment(String text){
		comment=text;
	}
	
	public static Ayah getAyah(int index){
		ayah.ayahIndex = index;
		return ayah;
	}
	
	public static Ayah getBookmarkItem(int index){
		ayah.ayahIndex = index;
		return ayah;
	}
	
	public static int getBookmarkItemsSize(){
		return bookmarks.length;
	}
}

