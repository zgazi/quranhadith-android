package com.greenledge.quran;

//import com.greenledge.common.Settings;
import com.greenledge.common.MainApplication;
import com.greenledge.quran.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class CompassView extends View implements SensorEventListener
{
   private Bitmap mBitmap,mBitmap2;
   private int angle,angleNord,boussole;
   private final Matrix mMatrix = new Matrix(),mMatrix2 = new Matrix();
   private final int x,y,x2,y2;
   //private Settings setting; 
   private Context mContext;
   private boolean freeze;
   private SensorManager sm;
   // define the display assembly compass picture
   private ImageView image;

   // record the compass picture angle turned
   private float currentDegree = 0f;
   public CompassView(Context context)
	{
		this(context,null);
		// TODO Auto-generated constructor stub
	}


	public CompassView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public CompassView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs);
		this.mContext = context;
		if (this.mContext==null) this.mContext = MainApplication.getContext();
		mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.compass_arrow);
		mBitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.compass_base);
		angle = 0;
		boussole = 0;
		freeze = false;
		x = mBitmap.getWidth()/2;
		y = mBitmap.getHeight()/2;
		x2 = mBitmap2.getWidth()/2;
		y2 = mBitmap2.getHeight()/2;
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
	 if (!freeze)
	  {
      //int angleFinal = (int) (boussole + setting.getDegQibla());
	  int angleFinal = (int) (boussole + MainApplication.getAngle(21.423333,39.823333));
      angleFinal = (angleFinal+360)%360;

      int angleNordFinal = ((int) (boussole)+360)%360;
      
      angle = (angle+360)%360;
      int d = (int)(angle - angleFinal);
      d=(d+360)%360;
      
      angleNord = (angleNord+360)%360;
      int d2 = (int)(angleNord - angleNordFinal);
      d2=(d2+360)%360;
      
      if (d*d>=6)
     	 {
     	 if ((((angle - angleFinal)+360)%360)>=180)
     		 {
     		 angle += 2;
     		 } else
     		 {
     			 angle -=2;
     		 }
     	 }
      
     if (d2*d2>=6)
   	 {
   	 if ((((angleNord - angleNordFinal)+360)%360)>=180)
   		 {
   		 angleNord += 2;
   		 } else
   		 {
   			 angleNord -=2;
   		 }
   	 }
      
      mMatrix.setRotate((angle+360)%360, x, y);
      mMatrix2.setRotate((angleNord+360)%360, x2, y2);
      invalidate();
      //int tx = (canvas.getWidth() - mBitmap.getWidth()) / 2;
      //int ty = 100;
      //canvas.translate(tx, ty);
      canvas.drawBitmap(mBitmap2, mMatrix2, null);
      canvas.drawBitmap(mBitmap, mMatrix, null);
		
	  }
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub
		return;
		
	}

	public void setFreeze(boolean freeze)
	{
		this.freeze = freeze;
		SensorManager m = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if (freeze)
		{
			m.registerListener(this,m.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);			
		} else
		{
			m.unregisterListener(this);						
		}
		invalidate();
		this.refreshDrawableState();
	}
	
	public void onSensorChanged(SensorEvent event)
	{
	   float values[] = event.values;
	   switch (event.sensor.getType()) {
	   case Sensor.TYPE_ORIENTATION :
	      //if (currTime - lastOrientationUpdate < SENSOR_REFRESH_MS)    break;
	      //lastOrientationUpdate = System.currentTimeMillis();
	      boussole = -(int)values[0];
	      break;
	   }
/*
       // get the angle around the z-axis rotated
       float degree = Math.round(event.values[0]);

       tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

       // create a rotation animation (reverse turn degree degrees)
       RotateAnimation ra = new RotateAnimation(
               currentDegree,
               -degree,
               Animation.RELATIVE_TO_SELF, 0.5f,
               Animation.RELATIVE_TO_SELF,
               0.5f);

       // how long the animation will take place
       ra.setDuration(210);

       // set the animation after the end of the reservation status
       ra.setFillAfter(true);

       // Start the animation
       image.startAnimation(ra);
       currentDegree = -degree;
*/	
	}

}
