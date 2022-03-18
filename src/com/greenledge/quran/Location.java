
package com.greenledge.quran;


public class Location     {
	public double degreeLong;  /* Longitude in decimal degree. */
	public double degreeLat;   /* Latitude in decimal degree. */
	public double gmtDiff;     /* GMT difference at regular time. */
	public int dst;            /* Daylight savings time switch (0 if not used).
                           Setting this to 1 should add 1 hour to all the
                           calculated prayer times */
	public double seaLevel;    /* Height above Sea level in meters */
	public double pressure;    /* Atmospheric pressure in millibars (the
                           astronomical standard value is 1010) */
	public double temperature; /* Temperature in Celsius degree (the astronomical
                           standard value is 10) */
	
	Location(Location loc) {
		this.degreeLong = loc.degreeLong;
		this.degreeLat = loc.degreeLat;
		this.gmtDiff = loc.gmtDiff;
		this.dst = loc.dst;
		this.seaLevel = loc.seaLevel;
		this.pressure = loc.pressure;
		this.temperature = loc.temperature;
	}

	public Location() {
	}
}
