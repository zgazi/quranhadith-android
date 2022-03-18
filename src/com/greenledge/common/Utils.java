package com.greenledge.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.greenledge.quran.Date;

public class Utils
{
   public static double deg2rad(final double deg)
   {
  	 return deg*Math.PI/180;
   }

   public static float rad2deg(final double rad)
   {
  	 return (float)(rad*180/Math.PI);
   }
   
   public static String dms2str(final float rad)
   {
   	return (int)rad+"°"+int2str((int)((Math.abs(rad)%1)*60))+"' N";   	
   }

   public static String deg2str(final float deg)
   {
   	return (int)deg+"°"+(int)((Math.abs(deg)%1)*10000);   	
   }

   public static Integer str2int(final String str,final int defaut)
   {
	   return str.matches("[0-9\\-]+") ? Integer.parseInt(str) : defaut ;
   }
	public static String int2str(final Integer val)
	{
		return ((val<10)?"0":"")+Integer.toString(val);	
	}

	public static String time2str(final Integer val)
	{
		return ""+(int)(val/100)+":"+Utils.int2str(Math.abs(val)%100);
		
	}

	public static String calendar2str(final Calendar cal)
	{
		return int2str(cal.get(Calendar.DAY_OF_MONTH))+"/"
		+int2str(cal.get(Calendar.MONTH))+"/"
		+cal.get(Calendar.YEAR);
	}

	public static String calendar2str(final Date date)
	{
		return int2str(date.day)+"/"
		+int2str(date.month)+"/"
		+int2str(date.year);
	}

	public static double calendar2Julian(final Calendar cal)
	{
		return Utils.calendar2Julian(cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
	}
	
	public static double calendar2Julian(final int day,final int month,final int year) {
		final int GGG = (( year < 1582 )||( year <= 1582 && month < 10 )||( year <= 1582 && month == 10 && day < 5 )) ? 0 : 1;
		double JulianDay = -1 * Math.floor(7 * (Math.floor((month + 9) / 12) + year) / 4);
		final int S = ((month - 9)<0) ? -1 : 1;
		final int A = Math.abs(month - 9);
		double J1 = Math.floor(year + S * Math.floor(A / 7));
		J1 = -1 * Math.floor((Math.floor(J1 / 100) + 1) * 3 / 4);
		JulianDay = JulianDay + Math.floor(275 * month / 9) + day + (GGG * J1);
		JulianDay = JulianDay + 1721027 + 2 * GGG + 367 * year - 0.5;
		return JulianDay;
	}

	public static double hijir2Julian(final int day,final int month,final int year)
	{
	     return Math.floor((year * 10631 + 58442583)/30) + Math.floor((month * 325 - 320)/11) + (day - 1);
	     /*double KHS2 = ((JulianDayHijir + 1.5)/7);
	     double KHS3 = KHS2 - Math.floor(KHS2);
	     return Math.round(KHS3*7 + 0.000000000317) - 1;*/
	}

	public static Date julian2calendar(final double JulianDay)
	{
		final double Z = Math.floor(JulianDay+0.5);
		final double F = JulianDay+0.5 - Z;
		final double I = Math.floor((Z - 1867216.25)/36524.25);
		final double A = (Z < 2299161) ? Z : (Z + 1 + I - Math.floor(I/4));
		final double B = A + 1524 ;
		final double C = Math.floor((B - 122.1)/365.25);
		final double D = Math.floor(365.25 * C);
		final double T = Math.floor((B - D)/ 30.6001);
		final double RJ = B - D - Math.floor(30.6001 * T) + F;
		final int JJ = (int)Math.floor(RJ);
		final int MM = (int)((T < 13.5) ? T - 1 : T - 13 ) ;
		final int AA = (int)((MM > 2.5) ? C - 4716 : (C - 4715));
		return new Date(JJ,MM,AA);
	}
	
	public static int getDayOfWeek(final double JulianDay) {
		final double startDay = (JulianDay + 1.5); //on commence le dimanche
		final double week = (startDay/7);
		final double dayOfWeek = week - Math.floor(week); //un pseudo modulo
		return (int) Math.round(dayOfWeek*7 + 0.000000000317);
	}


	/**
	 * @param JulianDay
	 * @return
	 */
	public static Date julian2Hijir(final double JulianDay) {
		double Z = (JulianDay+0.5);
		int AH = (int) Math.floor((Z * 30 - 58442554)/10631);
		double R2 = Z - Math.floor((AH * 10631 + 58442583)/30);
		int M = (int) Math.floor((R2 * 11 + 330)/325);
		int J = (int)(R2 - Math.floor((M * 325 - 320)/11)) + 1 ;
		return new Date(J,M,AH);
	}

	
	public static BufferedReader getDoc(final String source,final String fileInZip)
	{
		boolean inZip = (fileInZip!=null) ;
		BufferedReader bread = null;
		
		try{
			InputStream is =  new FileInputStream(source);
			if (is != null)            	
			{
				

				if (inZip)
				{
					ByteArrayInputStream bais = null;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ZipInputStream zis = new ZipInputStream(is);
		        	ZipEntry ze;
		        	boolean exit = false;
		        	byte[] datax = new byte[1024];
		        	while ( (ze = zis.getNextEntry())!=null && !exit )
		        	{
		        		if (fileInZip.equalsIgnoreCase(ze.getName()))
		        		{
		        			exit = true;	        			
		        			int count;
		        			while ((count = zis.read(datax)) != -1) {
		        			baos.write(datax, 0, count);
		        			bais = new ByteArrayInputStream(baos.toByteArray());
		        			}
		        			bread =new BufferedReader(new InputStreamReader(bais));
		        		}
		        	}
		        	zis.close();
				} else
				{
					bread = new BufferedReader(new InputStreamReader(is));					
				}
				is.close(); 
			}
		

		}catch(Exception ex)
		{
			
		}

		return bread;
		
	}

    // constants
    private static final double SQ2P1 = 2.414213562373095048802e0;
    private static final double SQ2M1  = .414213562373095048802e0;
    private static final double p4  = .161536412982230228262e2;
    private static final double p3  = .26842548195503973794141e3;
    private static final double p2  = .11530293515404850115428136e4;
    private static final double p1  = .178040631643319697105464587e4;
    private static final double p0  = .89678597403663861959987488e3;
    private static final double q4  = .5895697050844462222791e2;
    private static final double q3  = .536265374031215315104235e3;
    private static final double q2  = .16667838148816337184521798e4;
    private static final double q1  = .207933497444540981287275926e4;
    private static final double q0  = .89678597403663861962481162e3;
    private static final double PIO2 = 1.5707963267948966135E0;
    private static final double nan = (0.0/0.0);
    // reduce
    private static double mxatan(final double arg)
    {
        double argsq, value;

        argsq = arg*arg;
        value = ((((p4*argsq + p3)*argsq + p2)*argsq + p1)*argsq + p0);
        value = value/(((((argsq + q4)*argsq + q3)*argsq + q2)*argsq + q1)*argsq + q0);
        return value*arg;
    }

    // reduce
    private static double msatan(double arg)
    {
        if(arg < SQ2M1)
            return mxatan(arg);
        if(arg > SQ2P1)
            return PIO2 - mxatan(1/arg);
            return PIO2/2 + mxatan((arg-1)/(arg+1));
    }

    // implementation of atan
    public static double atan(double arg)
    {
        if(arg > 0)
            return msatan(arg);
        return -msatan(-arg);
    }

    // implementation of atan2
    public static double atan2(double arg1, double arg2)
    {
        if(arg1+arg2 == arg1)
        {
            if(arg1 >= 0)
            return PIO2;
                return -PIO2;
        }
        arg1 = atan(arg1/arg2);
        if(arg2 < 0)
       {
            if(arg1 <= 0)
                return arg1 + Math.PI;
            return arg1 - Math.PI;
        }
        return arg1;
    
    }

    // implementation of asin
    public static double asin(double arg)
    {
        double temp;
        int sign;

        sign = 0;
        if(arg < 0)
        {
            arg = -arg;
            sign++;
        }
        if(arg > 1)
            return nan;
        temp = Math.sqrt(1 - arg*arg);
        if(arg > 0.7)
            temp = PIO2 - atan(temp/arg);
        else
            temp = atan(arg/temp);
        if(sign > 0)
            temp = -temp;
        return temp;
    }

    // implementation of acos
    public static double acos(double arg)
    {
        if(arg > 1 || arg < -1)
            return nan;
        return PIO2 - asin(arg);
    }
}
