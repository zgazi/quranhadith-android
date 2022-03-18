package com.greenledge.quran;

public class Prayer     {
	public int hour;       /* prayer time hour */
	public int minute;     /* prayer time minute */
	public int second;     /* prayer time second */
	public int isExtreme;  /* Extreme calculation status. The 'getPrayerTimes'
                       function sets this variable to 1 to indicate that
                       this particular prayer time has been calculated
                       through extreme latitude methods and NOT by
                       conventional means of calculation. */ 
}
