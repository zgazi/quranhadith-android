
package com.greenledge.quran;

import java.io.Serializable;

/**
 * @author Rahat
 *
 */
public class Ayah implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int suraIndex;
	public int ayahIndex;
	public static String aayatESajdahString="***(Aayat e Sajdah)***";
	public static int aayatESajdahs[]={
		7,206,
		13,15,
		16,50,
		17,109,
		19,58,
		22,18,
		22,77,//(Shafi)
		25,60,
		27,26,
		32,15,
		38,24,//(Hanafi)
		41,38,
		53,62,
		84,21,
		96,19};

	public Ayah()
	{
		suraIndex=0;
		ayahIndex=0;
	}
	public Ayah(int suraIndex,int ayahIndex)
	{
		if(suraIndex>=0 && suraIndex<114)
			this.suraIndex=suraIndex;
		else
			this.suraIndex=113;
		
		if(ayahIndex>=0 && ayahIndex<Sura.ayahCounts[this.suraIndex])
			this.ayahIndex=ayahIndex;
		else
			this.ayahIndex=Sura.ayahCounts[this.suraIndex]-1;
	}
	
	public Ayah getNexTAyah()
	{
		if(ayahIndex+1<Sura.ayahCounts[suraIndex])
			return new Ayah(suraIndex,ayahIndex+1);
		
		else if(ayahIndex+1==Sura.ayahCounts[suraIndex] && suraIndex<113)
			return new Ayah(suraIndex+1,0);
		
		return null;
	}
	
	public Ayah getPrevAyah()
	{
		if(ayahIndex>0)
			return new Ayah(suraIndex,ayahIndex-1);
		
		else if(suraIndex>0)
			return new Ayah(suraIndex-1,Sura.ayahCounts[suraIndex-1]-1);
		
		return null;
	}
	
	@Override
	public String toString()
	{
		return (suraIndex+1)+":"+(ayahIndex+1);
	}
	
	public String toDetailedString(){
		String ayahString;
		ayahString=Sura.getSuraInfo(suraIndex).name;
		ayahString+="-"+this.toString();
		
		if(isAayatESajdah()){
			ayahString+=" "+aayatESajdahString;
		}
		
		return ayahString;
	}
	
	public boolean isAayatESajdah(){
		int aayateESazdahs[]=Ayah.aayatESajdahs;
		
		for(int i=0;i<aayateESazdahs.length && (suraIndex+1)>=aayateESazdahs[i];i+=2){
			if(suraIndex+1==aayateESazdahs[i] && ayahIndex+1==aayateESazdahs[i+1]){
				return true;
			}
		}
		
		return false;
	}
}
