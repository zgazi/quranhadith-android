package com.greenledge.quran;

public class Method { 
	double fajrAng;     /* Fajr angle */
	double ishaaAng;    /* Ishaa angle */
	double imsaakAng;   /* The angle difference between Imsaak and Fajr (
                       default is 1.5)*/
	int fajrInv;        /* Fajr Interval is the amount of minutes between
                       Fajr and Shurooq (0 if not used) */
	int ishaaInv;       /* Ishaa Interval is the amount if minutes between
                       Ishaa and Maghrib (0 if not used) */
	int imsaakInv;      /* Imsaak Interval is the amount of minutes between
                       Imsaak and Fajr. The default is 10 minutes before
                       Fajr if Fajr Interval is set */
	public int round;          /* Method used for rounding seconds:
                       0: No Rounding. "Prayer.seconds" is set to the
                          amount of computed seconds.
                       1: Normal Rounding. If seconds are equal to
                          30 or above, add 1 minute. Sets
                          "Prayer.seconds" to zero.
                       2: Special Rounding. Similar to normal rounding
                          but we always round down for Shurooq and
                          Imsaak times. (default)
                       3: Aggressive Rounding. Similar to Special
                          Rounding but we add 1 minute if the seconds
                          value is equal to 1 second or more.  */
	double mathhab;     /* Assr prayer shadow ratio:
                       1: Shaf'i (default)
                       2: Hanafi */
	double nearestLat;  /* Latitude Used for the 'Nearest Latitude' extreme
                       methods. The default is 48.5 */
	int extreme;        /* Extreme latitude calculation method (see
                       below) */
	int offset;         /* Enable Offsets switch (set this to 1 to
                       activate). This option allows you to add or
                       subtract any amount of minutes to the daily
                       computed prayer times based on values (in
                       minutes) for each prayer in the offList array */     
	double offList[] = new double[6];  /* For Example: If you want to add 30 seconds to
                       Maghrib and subtract 2 minutes from Ishaa:
                            offset = 1
                            offList[4] = 0.5
                            offList[5] = -2
                       ..and than call getPrayerTimes as usual. */
	public Method(Method meth){
		this.fajrAng = meth.fajrAng;
		this.ishaaAng = meth.ishaaAng;
		this.imsaakAng = meth.imsaakAng;
		this.fajrInv = meth.fajrInv;
		this.ishaaInv = meth.ishaaInv;
		this.imsaakInv = meth.imsaakInv;
		this.round = meth.round;
		this.mathhab = meth.mathhab;
		this.nearestLat = meth.nearestLat;
		this.extreme = meth.extreme;
		this.offset = meth.offset;
		this.offList = new double[6];
		for (int i = 0; i < offList.length; i++) {
			this.offList[i] = meth.offList[i];
		}
		
	}
	public Method() {
	}

}
