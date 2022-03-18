package com.greenledge.quran;

public class Date{
	public Date(Date date) {
		this.day = date.day;
		this.month = date.month;
		this.year = date.year;
	}
	public Date(int day,int month,int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public Date() {
	}
	public int day;
	public int month;
	public int year;
}
