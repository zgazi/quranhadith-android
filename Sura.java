					/* In the name of GOD, the Most Gracious, the Most Merciful */
//Not used at the moment
package com.greenledge.quran;

import java.util.ArrayList;

import android.content.Context;

public class Sura
{
	int sid;
	String name;
	String meaning;
	int ayahCount;
	private static ArrayList<Sura> surahInformations;
	
	public static int[] ayahCounts={7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123,
		111, 43, 52, 99, 128, 111, 110, 98, 135, 112, 78, 118, 64, 77, 227, 93,
		88, 69, 60, 34, 30, 73, 54, 45, 83, 182, 88, 75, 85, 54, 53, 89, 59,
		37, 35, 38, 29, 18, 45, 60, 49, 62, 55, 78, 96, 29, 22, 24, 13, 14, 11,
		11, 18, 12, 12, 30, 52, 52, 44, 28, 28, 20, 56, 40, 31, 50, 40, 46, 42,
		29, 19, 36, 25, 22, 17, 19, 26, 30, 20, 15, 21, 11, 8, 8, 19, 5, 8, 8,
		11, 11, 8, 3, 9, 5, 4, 7, 3, 6, 3, 5, 4, 5, 6};
	
	private int placeId;
	private String audioPath;
	private float size;
	private int memorize;
	private int stars;
	private int records;
	// -----------
		public int getMemorize() {
			return memorize;
		}

		public void setMemorize(int memorize) {
			this.memorize = memorize;
		}
		// -----------
		public int getStars() {
			return stars;
		}

		public void setStars(int stars) {
			this.stars = stars;
		}
		// -----------
		public int getRecords() {
			return records;
		}

		public void setRecords(int records) {
			this.records = records;
		}
	// -----------
	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	// -----------
	public int getSid() {
		return sid;
	}

	public void setSid(int id) {
		this.sid = id;
	}

	// -------------
	public int getAyahCount() {
		return ayahCount;
	}

	public void setAyahCount(int ayahCount) {
		this.ayahCount = ayahCount;
	}

	// -------------
	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public Sura()
	{
			sid = 1;
			ayahCount = 7;
	}
	
	public Sura(int id, String n, String m)
	{
			sid = id;
			name = n;
			meaning = m;
			ayahCount = ayahCounts[id-1]; //java 0
	}
	
	public int[] getSuraAyahCounts() {
		return ayahCounts;
	}
	
	public static int totalAyahsUpto(int surahNo)
	{
		int sum=0;
		for(int i=0;i<surahNo;i++)
			sum+=ayahCounts[i];
		
		return sum;
	}
	
	public static Sura getSuraInfo(int index){
		return surahInformations.get(index);
	}
	
	public static void loadAllSuraInfos(Context context){
		String suraNamesAR[] = context.getResources().getStringArray(R.array.suraNames_ar);
		String suraNames[] = context.getResources().getStringArray(R.array.suraNames);
		String suraMeanings[]=context.getResources().getStringArray(R.array.suraMeanings);
		
		surahInformations=new ArrayList<Sura>();
		
		for(int i=0;i<114;i++){
			Sura s=new Sura(i+1,suraNamesAR[i],suraNames[i]+" "+suraMeanings[i]);
			surahInformations.add(s);
		}
	}
}