package com.greenledge.quran;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class Word{
	
	public String wordId;
	public int lineId;
	public int sid;
	public int vid;
	public int wid;
	public String transliteration;
	public String meaning;
	
	public static List<Word>infoWords=new ArrayList<Word>(77430);
	public static List<Integer>startIndexOfSura=new ArrayList<Integer>(115);
	public static List<Integer>startIndexOfAyah=new ArrayList<Integer>(6241);
	
	public static boolean isLoadingCompleted;
	
	public Word()
	{
		isLoadingCompleted=false;
		//or, info is loading=true
		//it is not included in load() func
		//because, if it will return false till the load() executes
	}
	
	public Word(String wordId, String transliteration, String meaning) {
		this.wordId = wordId;
		this.transliteration = transliteration;
		this.meaning = meaning;
	}

	public Word(int lineId, int wid, String transliteration, String meaning) {
		this.lineId = lineId;
		this.wid = wid;
		this.transliteration = transliteration;
		this.meaning = meaning;
	}
	
	public Word(int sid, int vid, int wid, String transliteration, String meaning) {
		this.wid = wid;
		this.sid = sid;
		this.vid = vid;
		this.transliteration = transliteration;
		this.meaning = meaning;
	}
	
	@Override
	public String toString() {
		return    "\nwordId=" + wordId
				+ "\ntransLiteration=" + transliteration 
				+ "\nmeaning="+ meaning;
	}
	
	public static Word getWord(int index){
		return infoWords.get(index);
	}
	
	public void load(Context context)
	{
		InputStream inStream=context.getResources().openRawResource(R.raw.wbw_short_info);
		BufferedReader reader=null;
		try
		{
			Log.i("Word.load", "loading word Info");
			reader=new BufferedReader(new InputStreamReader(inStream,"utf-8"));
			String text;
			Word tempInfo=null;
			int fieldsCovered=0;
			String fields[]=new String[3];
			while((text=reader.readLine())!=null)
			{
				if(text.startsWith("#"))
					continue;
				
				else{
					fields[fieldsCovered]=text;
					fieldsCovered++;
					if(fieldsCovered==3){
						infoWords.add(new Word(fields[0],//wordId
										fields[1],//transliteration
										fields[2]));//meanings
						
						fieldsCovered=0;
					}
				}	
			}
			
			reader.close();
			Log.i("Word.load"," loading success");
		}catch(IOException ie){
			ie.printStackTrace();
		}
		
		organizeWord();
	
		
		isLoadingCompleted=true;
	}
	
	private void organizeWord()
	{	
		int i;
		for(i=0;i<infoWords.size();i++)
		{
			WordId tempId=formatWordId(infoWords.get(i).wordId);
			if(tempId.ayahNo==1 && tempId.wordNo==1)
			{
				startIndexOfSura.add(i);
			}
			if(tempId.wordNo==1)
			{
				startIndexOfAyah.add(i);
			}
		}
		startIndexOfAyah.add(i);//for advantage, otherwise invalid ayahIndex
		Log.i("Word.organize","organized");
	}
	
	private WordId formatWordId(String wordId)
	{
		String withoutBracket=
				wordId.substring(wordId.indexOf('(')+1, wordId.indexOf(')',1));
		
		String[] numbers=withoutBracket.split(":");
		
		int suraNo=Integer.parseInt(numbers[0]);
		int ayahNo=Integer.parseInt(numbers[1]);
		int wordNo=Integer.parseInt(numbers[2]);
		
		return new WordId(suraNo, ayahNo, wordNo);
	}
	

	public static void returnToInitialState(){
		isLoadingCompleted=false;
		
		//if(!infoWords.isEmpty())
			infoWords.clear();
		//if(startIndexOfAyah.isEmpty())
			startIndexOfAyah.clear();
		//if(startIndexOfSura.isEmpty())
			startIndexOfSura.clear();
	}
}

class WordId
{
	int suraNo;
	int ayahNo;
	int wordNo;
	
	public WordId(int i, int j, int k) 
	{
		suraNo=i;
		ayahNo=j;
		wordNo=k;
	}
	
	public String toString()
	{
		return "\nSuraNo "+suraNo
				+"\nAyahNo "+ayahNo
				+"\nWordNo "+wordNo+"\n";
		
	}
}

