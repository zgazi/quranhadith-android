/**
 * 
 */
package com.greenledge.quran;

import java.util.Calendar;

import com.greenledge.common.Utils;

public class PrayerTime {

	public static final double KAABA_LAT =21.423333;
	public static final double KAABA_LONG =39.823333;
	public static final double DEF_NEAREST_LATITUDE =48.5;
	public static final double DEF_IMSAAK_ANGLE =1.5;
	public static final double DEF_IMSAAK_INTERVAL =10;
	public static final double DEF_ROUND_SEC =30;
	public static final double AGGRESSIVE_ROUND_SEC =1;
	
	private int year,month,day,hour,minute,second;

//	enum exmethods  { 
	public static final int	NONE_EX =0,
		LAT_ALL=1,
		LAT_ALWAYS=2,
		LAT_INVALID=3,
		GOOD_ALL=4,
		GOOD_INVALID=5,
		SEVEN_NIGHT_ALWAYS=6,
		SEVEN_NIGHT_INVALID=7,
		SEVEN_DAY_ALWAYS=8,
		SEVEN_DAY_INVALID=9,
		HALF_ALWAYS=10,
		HALF_INVALID=11,
		MIN_ALWAYS=12,
		MIN_INVALID=13,
		GOOD_INVALID_SAME=14; 
//		};

//		public enum methods    { 
		public static final int //NONE = 0,

			EGYPT_SURVEY = 0,
			KARACHI_SHAF = 1,
			KARACHI_HANAF = 2,
			NORTH_AMERICA = 3,
			MUSLIM_LEAGUE = 4,
			UMM_ALQURRA = 5,
			FIXED_ISHAA  = 6;
			//			};

//			enum salatType  {
			public static final int
				FAJR =(0),
				SHUROOQ=(1),
				THUHR=(2),
				ASSR=(3),
				MAGHRIB=(4),
				ISHAA=(5),
				IMSAAK=(6),
				NEXTFAJR=(7); 
//				
//				private int order;
//				
//				private salatType(int o) {
//					order = o;
//				}
//				
//				public int getOrder(){
//					return order;
//				}
//			
//			};

			public PrayerTime(int year, int month, int day, int hour, int minute, int second) {
				this.year = year;
				this.month = month;
				this.day = day;
				this.hour = hour;
				this.minute = minute;
				this.second = second;
			}
				static Astro astroCache = new Astro(); /* This global variable is used for caching values between
				 * multiple getPrayerTimesByDay() calls. You can disable this
				 * caching feature by moving this line to the start of the
				 * getPrayerTimesByDay() function. */

				public static void getPrayerTimes ( final Location loc, final Method conf, final Date date, Prayer[] pt)
				{  
					int[] lastDay = new int[1];
					double[] julianDay= new double[1];
					getDayInfo ( date, loc.gmtDiff, lastDay, julianDay);
					getPrayerTimesByDay( loc, conf, lastDay[0], julianDay[0], pt, FAJR);
				}

				public static PrayerTime [] getPrayerTimes(int year, int month, int day,double latitude,double longitude,float gmt,int dst,int method) {
					PrayerTime[] prayers = new PrayerTime[7];
				    int i;
				  
				    Location loc = new Location();
				    Method conf = new Method();
				    Date date = new Date();

				    Prayer[] ptList = new Prayer[6];
				    for (int j = 0; j < ptList.length; j++) {
						ptList[j] = new Prayer();
					}
				    Prayer[] imsaak = new Prayer[1];
				    imsaak[0] = new Prayer();
				    Prayer[] nextImsaak = new Prayer[1];
				    nextImsaak[0] = new Prayer();
				    Prayer[] nextFajr = new Prayer[1];
				    nextFajr[0] = new Prayer();
				    /* fill the Date structure */
				    date.day = day;
				    date.month = month;
				    date.year = year;
				    /* fill the location info. structure */
				    loc.degreeLat = latitude;
				    loc.degreeLong = longitude;
				    loc.gmtDiff = gmt;
				    loc.dst = dst;
				    loc.seaLevel = 0;
				    loc.pressure = 1010;
				    loc.temperature= 10;

				  
				    /* auto fill the method structure. Have a look at prayer.h for a
				     * list of supported methods */
				    // TODO Use another methods, not egypt survey only
				    getMethod(method, conf);
				    conf.round = 0;
				  
				    /* Call the main function to fill the Prayer times array of
				     * structures */
				    getPrayerTimes (loc, conf, date, ptList);

				    /* Call functions for other prayer times and qibla */
				    getImsaak (loc, conf, date, imsaak);
				    getNextDayFajr (loc, conf, date, nextFajr);
				    getNextDayImsaak (loc, conf, date, nextImsaak);
				     

				    for (i = 0; i < 6; i++) {
				    	prayers[i] = new PrayerTime(year,month,day,ptList[i].hour,ptList[i].minute,ptList[i].second);
				    }
				    prayers[6] = new PrayerTime(year,month,day+1,nextFajr[0].hour,nextFajr[0].minute,nextFajr[0].second);

					return prayers;
				}

				static void getPrayerTimesByDay ( final Location loc, final Method conf, 
						int lastDay, double julianDay, Prayer[] pt, 
						int type)
				{
					int i, invalid;
					double th, sh, mg, fj, is, ar;
					double lat, lon, dec;
					double tempPrayer[] = new double[6];
					Astro tAstro = new Astro();

					lat = loc.degreeLat; 
					lon = loc.degreeLong;
					invalid = 0;

					/* Start by filling the tAstro structure with the appropriate astronomical
					 * values for this day. We also pass the cache structure to update and check
					 * if the actual values are already available. */
					Astronomy.getAstroValuesByDay(julianDay, loc, astroCache, tAstro);
					dec = Astronomy.DEG_TO_RAD(tAstro.dec[1]);

					/* Get Prayer Times formulae results for this day of year and this
					 * location. The results are NOT the actual prayer times */
					fj   = getFajIsh (lat, dec, conf.fajrAng);
					sh   = Astronomy.getSunrise(loc, tAstro);
					th   = getThuhr (lon, tAstro);
					ar   = getAssr (lat, dec, (int) conf.mathhab);
					mg   = Astronomy.getSunset(loc, tAstro);
					is   = getFajIsh (lat, dec, conf.ishaaAng);

					/* Calculate all prayer times as Base-10 numbers in Normal circumstances */ 
					/* Fajr */   
					if (fj == 99) {
						tempPrayer[0] = 99;
						invalid = 1;
					} 
					else tempPrayer[0] = th - fj;

					if (sh == 99)
						invalid = 1;
					tempPrayer[1] = sh;

					tempPrayer[2] = th;

					/* Assr */
					if (ar == 99) {
						tempPrayer[3] = 99;
						invalid = 1;
					} 
					else tempPrayer[3] = th + ar;


					if (mg == 99)
						invalid = 1;
					tempPrayer[4] = mg;


					/* Ishaa */
					if (is == 99) {
						tempPrayer[5] = 99;
						invalid = 1;
					} 
					else tempPrayer[5] = th + is;


					/* Calculate all prayer times as Base-10 numbers in Extreme Latitudes (if
					 * needed) */

					/* Reset status of extreme switches */
					for (i=0; i<6; i++)
						pt[i].isExtreme = 0; 

					if ((conf.extreme != NONE_EX) && !((conf.extreme == GOOD_INVALID || 
							conf.extreme == LAT_INVALID ||
							conf.extreme == SEVEN_NIGHT_INVALID ||
							conf.extreme == SEVEN_DAY_INVALID ||
							conf.extreme == HALF_INVALID) &&
							(invalid == 0)))
					{
						double exdecPrev, exdecNext;
						double exTh=99, exFj=99, exIs=99, exAr=99, exSh=99, exMg=99;
						double portion = 0;
						double nGoodDay = 0;
						int exinterval = 0;
						Location exLoc = new Location(loc);
						Astro exAstroPrev;
						Astro exAstroNext;

						switch(conf.extreme)
						{
						/* Nearest Latitude (Method.nearestLat) */
						case LAT_ALL:
						case LAT_ALWAYS:
						case LAT_INVALID:

							/* FIXIT: we cannot compute this when interval is set because
							 * angle==0 . Only the if-invalid methods would work */
							exLoc.degreeLat = conf.nearestLat;
							exFj = getFajIsh(conf.nearestLat, dec, conf.fajrAng);
							/*exIm = */getFajIsh(conf.nearestLat, dec, conf.imsaakAng);
							exSh = Astronomy.getSunrise(exLoc, tAstro);
							exAr = getAssr(conf.nearestLat, dec, (int) conf.mathhab);
							exMg = Astronomy.getSunset(exLoc, tAstro);
							exIs = getFajIsh(conf.nearestLat, dec, conf.ishaaAng);


							switch(conf.extreme)
							{
							case LAT_ALL:
								tempPrayer[0] = th - exFj;
								tempPrayer[1] = exSh;
								tempPrayer[3] = th + exAr;
								tempPrayer[4] = exMg;
								tempPrayer[5] = th + exIs;
								pt[0].isExtreme = 1;
								pt[1].isExtreme = 1;
								pt[2].isExtreme = 1;
								pt[3].isExtreme = 1;
								pt[4].isExtreme = 1;
								pt[5].isExtreme = 1;
								break;

							case LAT_ALWAYS:
								tempPrayer[0] = th - exFj;
								tempPrayer[5] = th + exIs;
								pt[0].isExtreme = 1;
								pt[5].isExtreme = 1;
								break;

							case LAT_INVALID:
								if (tempPrayer[0] == 99) {
									tempPrayer[0] = th - exFj;
									pt[0].isExtreme = 1;
								}
								if (tempPrayer[5] == 99) {
									tempPrayer[5] = th + exIs;
									pt[5].isExtreme = 1;
								}
								break;
							}
							break;


							/* Nearest Good Day */
						case GOOD_ALL:
						case GOOD_INVALID:
						case GOOD_INVALID_SAME:

							exAstroPrev = astroCache;
							exAstroNext = astroCache;

							/* Start by getting last or next nearest Good Day */
							for(i=0; i <= lastDay; i++)
							{

								/* Last closest day */
								nGoodDay = julianDay - i;
								Astronomy.getAstroValuesByDay(nGoodDay, loc, exAstroPrev, tAstro);
								exdecPrev = Astronomy.DEG_TO_RAD(tAstro.dec[1]);
								exFj = getFajIsh(lat, exdecPrev, conf.fajrAng);
								if (exFj != 99)
								{
									exIs = getFajIsh(lat, exdecPrev, conf.ishaaAng);
									if (exIs != 99)
									{
										exTh = getThuhr (lon, tAstro);
										exSh = Astronomy.getSunrise (loc, tAstro);
										exAr = getAssr (lat, exdecPrev, (int) conf.mathhab);
										exMg = Astronomy.getSunset (loc, tAstro);
										break;
									}
								}

								/* Next closest day */
								nGoodDay = julianDay + i;
								Astronomy.getAstroValuesByDay(nGoodDay, loc, exAstroNext, tAstro);
								exdecNext = Astronomy.DEG_TO_RAD(tAstro.dec[1]);
								exFj = getFajIsh(lat, exdecNext, conf.fajrAng);
								if (exFj != 99)
								{
									exIs = getFajIsh(lat, exdecNext, conf.ishaaAng);
									if (exIs != 99)
									{
										exTh = getThuhr (lon, tAstro);
										exSh = Astronomy.getSunrise (loc, tAstro);
										exAr = getAssr (lat, exdecNext, (int) conf.mathhab);
										exMg = Astronomy.getSunset (loc, tAstro);
										break;
									}
								}
							}

							switch(conf.extreme)
							{
							case GOOD_ALL:
								tempPrayer[0] = exTh - exFj;
								tempPrayer[1] = exSh;
								tempPrayer[2] = exTh;
								tempPrayer[3] = exTh + exAr;
								tempPrayer[4] = exMg;
								tempPrayer[5] = exTh + exIs;
								for (i=0; i<6; i++)
									pt[i].isExtreme = 1;
								break;
							case GOOD_INVALID:
								if (tempPrayer[0] == 99) {
									tempPrayer[0] = exTh - exFj;
									pt[0].isExtreme = 1;
								}
								if (tempPrayer[5] == 99) {
									tempPrayer[5] = exTh + exIs;
									pt[5].isExtreme = 1;
								}
								break;
							case GOOD_INVALID_SAME:
								if ((tempPrayer[0] == 99) || (tempPrayer[5] == 99))
								{
									tempPrayer[0] = exTh - exFj;
									pt[0].isExtreme = 1;
									tempPrayer[5] = exTh + exIs;
									pt[5].isExtreme = 1;
								}
								break;
							}
							break;

						case SEVEN_NIGHT_ALWAYS:
						case SEVEN_NIGHT_INVALID:
						case SEVEN_DAY_ALWAYS:
						case SEVEN_DAY_INVALID:
						case HALF_ALWAYS:
						case HALF_INVALID:

							/* FIXIT: For clarity, we may need to move the HALF_* methods
							 * into their own separate case statement. */    
							switch(conf.extreme)
							{
							case SEVEN_NIGHT_ALWAYS:
							case SEVEN_NIGHT_INVALID:
								portion = (24 - (tempPrayer[4] - tempPrayer[1])) * (1/7.0);
								break;
							case SEVEN_DAY_ALWAYS:
							case SEVEN_DAY_INVALID:
								portion = (tempPrayer[4] - tempPrayer[1]) * (1/7.0);
								break;
							case HALF_ALWAYS:
							case HALF_INVALID:
								portion = (24 - tempPrayer[4] - tempPrayer[1]) * (1/2.0);
								break;
							}


							if (conf.extreme == SEVEN_NIGHT_INVALID ||
									conf.extreme == SEVEN_DAY_INVALID ||
									conf.extreme == HALF_INVALID)
							{
								if (tempPrayer[0] == 99) {
									if  (conf.extreme == HALF_INVALID)
										tempPrayer[0] =  portion - (conf.fajrInv / 60.0);
									else tempPrayer[0] = tempPrayer[1] - portion;
									pt[0].isExtreme = 1;
								}
								if (tempPrayer[5] == 99) {
									if  (conf.extreme == HALF_INVALID)
										tempPrayer[5] = portion + (conf.ishaaInv / 60.0) ;
									else tempPrayer[5] = tempPrayer[4] + portion;
									pt[5].isExtreme = 1;
								}
							} else { /* for the always methods */

								if  (conf.extreme == HALF_ALWAYS) {
									tempPrayer[0] = portion - (conf.fajrInv / 60.0);
									tempPrayer[5] = portion + (conf.ishaaInv / 60.0) ;
								}

								else {
									tempPrayer[0] = tempPrayer[1] - portion;
									tempPrayer[5] = tempPrayer[4] + portion;
								}
								pt[0].isExtreme = 1;
								pt[5].isExtreme = 1;
							}
							break;

						case MIN_ALWAYS:
							/* Do nothing here because this is implemented through fajrInv and
							 * ishaaInv structure members */
							tempPrayer[0] = tempPrayer[1];
							tempPrayer[5] = tempPrayer[4];
							pt[0].isExtreme = 1;
							pt[5].isExtreme = 1;
							break;

						case MIN_INVALID:
							if (tempPrayer[0] == 99) {
								exinterval = (int)(conf.fajrInv / 60.0);
								tempPrayer[0] = tempPrayer[1] - exinterval;
								pt[0].isExtreme = 1;
							}
							if (tempPrayer[5] == 99) {
								exinterval = (int)(conf.ishaaInv / 60.0);
								tempPrayer[5] = tempPrayer[4] + exinterval;
								pt[5].isExtreme = 1;
							}
							break;
						} /* end switch */
					} /* end extreme */

					/* Apply intervals if set */
					if (conf.extreme != MIN_INVALID && 
							conf.extreme != HALF_INVALID &&
							conf.extreme != HALF_ALWAYS) 
					{
						if (conf.fajrInv != 0) {
							if (tempPrayer[1] != 99)
								tempPrayer[0] = tempPrayer[1] - (conf.fajrInv / 60.0);
							else tempPrayer[0] = 99;
						}

						if (conf.ishaaInv != 0) {
							if (tempPrayer[4] != 99)
								tempPrayer[5] = tempPrayer[4] + (conf.ishaaInv / 60.0);
							else tempPrayer[5] = 99;
						}
					}

					/* Final Step: Fill the Prayer array by doing decimal degree to
					 * Prayer structure conversion*/
					if (type == IMSAAK || type == NEXTFAJR)
						base6hm(tempPrayer[0], loc, conf, pt[0], type);
					else {
						/*for (i=0; i<6; i++)*/ {
							i=0;
							int s = FAJR;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
							i++;
							s = SHUROOQ;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
							i++;
							s = THUHR;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
							i++;
							s = ASSR;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
							i++;
							s = MAGHRIB;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
							i++;
							s = ISHAA;
							base6hm(tempPrayer[i], loc, conf, pt[i], s);
						}
					}
				}

				static void base6hm(double bs, final Location loc, final Method conf, 
						Prayer pt, int type)
				{
					double min, sec;

					/* Set to 99 and return if prayer is invalid */
					if (bs == 99)
					{
						pt.hour = 99;
						pt.minute = 99;
						pt.second = 0;
						return;
					}

					/* Add offsets */
					if (conf.offset == 1) {
						if (type == IMSAAK || type == NEXTFAJR)
							bs += (conf.offList[0] / 60.0);
						else  bs += (conf.offList[type] / 60.0);
					}

					/* Fix after minus offsets before midnight */
					if (bs < 0) {
						while (bs < 0)
							bs = 24 + bs;
					}

					min = (bs - Math.floor(bs)) * 60;
					sec = (min - Math.floor(min)) * 60;

					/* Add rounding minutes */
					if (conf.round == 1)
					{
						if (sec >= DEF_ROUND_SEC)
							bs += 1/60.0;
						/* compute again */
						min = (bs - Math.floor(bs)) * 60;
						sec = 0;

					} else if (conf.round == 2 || conf.round == 3)
					{
						switch(type)
						{
						case FAJR:
						case THUHR:
						case ASSR:
						case MAGHRIB:
						case ISHAA:
						case NEXTFAJR:

							if (conf.round == 2) {
								if (sec >= DEF_ROUND_SEC) {
									bs += 1/60.0;
									min = (bs - Math.floor(bs)) * 60;
								}
							} else if (conf.round == 3)
							{
								if (sec >= AGGRESSIVE_ROUND_SEC) {
									bs += 1/60.0;
									min = (bs - Math.floor(bs)) * 60;
								}
							}
							sec = 0;
							break;

						case SHUROOQ:
						case IMSAAK:
							sec = 0;
							break;
						}
					}

					/* Add daylight saving time and fix after midnight times */
					bs += loc.dst;
					if (bs >= 24)
						bs = (bs%24);

					pt.hour   = (int)bs;
					pt.minute = (int)min;
					pt.second = (int)sec;
				}

				public static void getImsaak (final Location loc, final Method conf, final Date date, 
						Prayer[] pt)
				{

					Method tmpConf;
					int[] lastDay = new int[1];
					double[] julianDay= new double[1];
					Prayer[] temp= new Prayer[6];
					for (int i = 0; i < temp.length; i++) {
						temp[i] = new Prayer();
					}
					tmpConf = new Method(conf);

					if (conf.fajrInv != 0) { 
						if (conf.imsaakInv == 0)
							tmpConf.fajrInv += DEF_IMSAAK_INTERVAL;
						else tmpConf.fajrInv += conf.imsaakInv;

					} else if (conf.imsaakInv != 0) {
						/* use an inv even if al-Fajr is computed (Indonesia?) */       
						tmpConf.offList[0] += (conf.imsaakInv * -1);
						tmpConf.offset = 1;
					} else { 
						tmpConf.fajrAng += conf.imsaakAng;
					}

					getDayInfo ( date, loc.gmtDiff, lastDay, julianDay);
					getPrayerTimesByDay( loc, tmpConf, lastDay[0], julianDay[0], temp, IMSAAK);

					/* FIXIT: We probably need to check whether it's possible to compute
					 * Imsaak normally for some extreme methods first */
					/* In case of an extreme Fajr time calculation use intervals for Imsaak and
					 * compute again */
					if (temp[0].isExtreme != 0)
					{
						tmpConf = new Method(conf);
						if ( conf.imsaakInv == 0)
						{
							tmpConf.offList[0] -= DEF_IMSAAK_INTERVAL;
							tmpConf.offset = 1;
						} else
						{
							tmpConf.offList[0] -= conf.imsaakInv;
							tmpConf.offset = 1;
						}
						getPrayerTimesByDay( loc, tmpConf, lastDay[0], julianDay[0], temp, IMSAAK);
					}

					pt[0] = temp[0];

				}

				public static void getNextDayImsaak (final Location loc, final Method conf, final Date date, 
						Prayer[] pt)
				{
					/* Copy the date structure and increment for next day.*/
					Prayer[] temppt = new Prayer[1];
					Date tempd = new Date(date); 
					tempd.day++;

					getImsaak (loc, conf, tempd, temppt);

					pt[0] = temppt[0]; 
				}

				public static void getNextDayFajr (final Location loc, final Method conf, final Date date, 
						Prayer[] pt)
				{

					Prayer temp[] = new Prayer[6];
					for (int i = 0; i < temp.length; i++) {
						temp[i] = new Prayer();
					}
					int[] lastDay = new int[1];
					double[] julianDay = new double[1];

					getDayInfo ( date, loc.gmtDiff, lastDay, julianDay);
					getPrayerTimesByDay( loc, conf, lastDay[0], julianDay[0]+1, temp, NEXTFAJR);

					pt[0] = temp[0]; 
				}

				static double getFajIsh(double lat, double dec, double Ang)
				{
					double rlat = Astronomy.DEG_TO_RAD(lat);

					double part1 = Math.cos(rlat) * Math.cos(dec);
					double part2 = -Math.sin(Astronomy.DEG_TO_RAD(Ang)) - Math.sin(rlat) * Math.sin(dec);
					double part3 = part2 / part1;

					if ( part3 < -Astronomy.INVALID_TRIGGER || part3 > Astronomy.INVALID_TRIGGER)
						return 99;

					return Astronomy.DEG_TO_10_BASE * Astronomy.RAD_TO_DEG (Math.acos(part3) );
				}

				static double getThuhr(double lon, final Astro astro)
				{
					return Astronomy.getTransit(lon, astro);
				}

				static double getAssr(double lat, double dec, int mathhab)
				{
					double part1, part2, part3, part4;
					double rlat = Astronomy.DEG_TO_RAD(lat);

					part1 = mathhab + Math.tan(rlat - dec);
					if ((part1 < 1) || (lat < 0))
						part1 = mathhab - Math.tan(rlat - dec);

					part2 = (Astronomy.PI/2.0) - Math.atan(part1);
					part3 = Math.sin(part2) - Math.sin(rlat) * Math.sin(dec);
					part4 = (part3 / (Math.cos(rlat) * Math.cos(dec)));

					if ( part4 < -Astronomy.INVALID_TRIGGER || part4 > Astronomy.INVALID_TRIGGER)
						return 99;

					return Astronomy.DEG_TO_10_BASE * Astronomy.RAD_TO_DEG (Math.acos(part4));
				}

				static int getDayofYear(int year, int month, int day)
				{
					int i;
					int isLeap = (((year & 3) == 0) && ((year % 100) != 0 
							|| (year % 400) == 0))?1:0;

					//static 
					char dayList[][] = {
						{0,31,28,31,30,31,30,31,31,30,31,30,31},
						{0,31,29,31,30,31,30,31,31,30,31,30,31}
					};

					for (i=1; i<month; i++)
						day += dayList[isLeap][i];

					return day;
				}

				double dms2Decimal(int deg, int min, double sec, char dir)
				{
					double sum = deg + ((min/60.0)+(sec/3600.0));
					if (dir == 'S' || dir == 'W' || dir == 's' || dir == 'w')
						return sum * (-1.0);
					return sum;
				}

				public static void decimal2Dms(double decimal, int []deg, int []min, double []sec)
				{
					double tempmin, tempsec, n1[] = new double[1], n2[] = new double[1];

					tempmin = modf(decimal, n1) * 60.0;
					tempsec = modf(tempmin, n2) * 60.0;

					deg[0] = (int)n1[0];
					min[0] = (int)n2[0];
					sec[0] = tempsec;

				}

				private static double modf(double number, double[] intPart) {
					intPart[0] = (int)number;
					return number - intPart[0];
				}

				static void getDayInfo ( final Date date, double gmt, int []lastDay, 
						double[] julianDay)
				{
					int ld;
					double jd;
					ld = getDayofYear(date.year, 12, 31);
					jd = Astronomy.getJulianDay(date, gmt);
					lastDay[0] = ld;
					julianDay[0] = jd;
				}

				public static void getMethod(int n, Method conf)
				{
					int i;
					conf.fajrInv = 0; 
					conf.ishaaInv = 0; 
					conf.imsaakInv = 0;
					conf.mathhab = 1;
					conf.round = 2;
					conf.nearestLat = DEF_NEAREST_LATITUDE;
					conf.imsaakAng = DEF_IMSAAK_ANGLE;
					conf.extreme = GOOD_INVALID;
					conf.offset = 0;
					for (i = 0; i < 6; i++) {
						conf.offList[i] = 0; 
					}

					switch(n)
					{
					/*case NONE:
						conf.fajrAng = 0.0;
						conf.ishaaAng = 0.0;
						break;*/

					case EGYPT_SURVEY:
						conf.fajrAng = 19.5;
						conf.ishaaAng = 17.5;
						break;

					case KARACHI_SHAF:
						conf.fajrAng = 18;
						conf.ishaaAng = 18;
						break;

					case KARACHI_HANAF: 
						conf.fajrAng = 18;
						conf.ishaaAng = 18;
						conf.mathhab = 2;
						break;

					case NORTH_AMERICA:
						conf.fajrAng = 15;
						conf.ishaaAng = 15;
						break;

					case MUSLIM_LEAGUE: 
						conf.fajrAng = 18;
						conf.ishaaAng = 17;
						break;

					case UMM_ALQURRA: 
						conf.fajrAng = 19;
						conf.ishaaAng = 0.0;
						conf.ishaaInv = 90;
						break;

					case FIXED_ISHAA:
						conf.fajrAng = 0.0;
						conf.fajrInv = 80;
						conf.ishaaAng = 0.0;
						conf.ishaaInv = 80;
						
						break;
					}
				}

				/* Obtaining the direction of the shortest distance towards Qibla by using the
				 * great circle formula */ 
				public static double getNorthQibla(final Location loc)
				{
					/* FIXIT: reduce DEG_TO_RAD usage */
					double num, denom;
					num = Math.sin (Astronomy.DEG_TO_RAD (loc.degreeLong) - Astronomy.DEG_TO_RAD (KAABA_LONG));
					denom = (Math.cos (Astronomy.DEG_TO_RAD (loc.degreeLat)) * Math.tan (Astronomy.DEG_TO_RAD (KAABA_LAT))) -
					(Math.sin (Astronomy.DEG_TO_RAD (loc.degreeLat)) * ((Math.cos ((Astronomy.DEG_TO_RAD (loc.degreeLong) -
							Astronomy.DEG_TO_RAD(KAABA_LONG))))));
					return Astronomy.RAD_TO_DEG (Math.atan2 (num, denom));

				}
				
				public String getTime() {
					return Utils.int2str(hour)+":"+Utils.int2str(minute);
				}
				
				public Calendar getCalendar() {
					Calendar pt = Calendar.getInstance();
					pt.set(Calendar.YEAR, pt.get(Calendar.YEAR) );
					pt.set(Calendar.MONTH, pt.get(Calendar.MONTH) );
					pt.set(Calendar.DAY_OF_MONTH, pt.get(Calendar.DAY_OF_MONTH) );
					pt.set(Calendar.HOUR_OF_DAY, hour);
					pt.set(Calendar.MINUTE, minute);
					pt.set(Calendar.SECOND, 0);
					pt.set(Calendar.MILLISECOND, 0);
					return pt;
				}
}
