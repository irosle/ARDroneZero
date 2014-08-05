package jp.naist.ardronezero;

import java.io.IOException;

import com.codeminders.ardrone.ARDrone;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

//
//This class is basic gesture class
//This class is used only operating drone.
//Don't use this class for other things(for example, detect gesture in other activity),because this class has DroneMove object,
//and if any gesture is detected, run a method in DroneMove. In other words, if you don't want to move drone, don't use this class.
//2014/8/5 K.S
public class ARDroneZeroGestureListener extends SimpleOnGestureListener{
		//private float vecX = 0.0f;//swipe speed(X axis)// maybe not used
		//private float vecY = 0.0f;//swipe speed(Y axis)
		static ARDroneZeroDroneMove dronemove;
		
		public ARDroneZeroGestureListener() {
			dronemove = new ARDroneZeroDroneMove();//get DroneMove object
		}

		/*
		public float getVecX(){
			return vecX;
		}
		
		public float getVecY(){
			return vecY;
		}
		*/
	
		@Override
		public boolean onDoubleTap(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onDoubleTap",  "onDoubleTap");
			return super.onDoubleTap(e);
		}
		
		@Override
		public boolean onDoubleTapEvent(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onDoubleTapEvent",  "onDoubleTapEvent");
			return super.onDoubleTapEvent(e);
		}
		
		@Override
		public boolean onDown(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onDown",  "onDown");
			return super.onDown(e);
			//return true;
		}
		

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			//TODO Auto-generated Method / stab
			//Log.v("onFling",  "X speed: " + (int)velocityX + ", Y speed: " + (int)velocityY);
			//return super.onFling(e1, e2, velocityX, velocityY);
			float threshold = 1500;
			if( Math.abs( (int)velocityX ) > threshold ){//if velocity of X-axis direction  is larger than threshold,  
				Log.v("onFling",  "X speed: " + (int)velocityX + ", Y speed: " + (int)velocityY);//recognized flip gesture.
				
				dronemove.spin( velocityX/*( (velocityX > 0f) ? 10f: -10f)*/ );
				return super.onFling(e1, e2, velocityX, velocityY);
			}else{
				
				return false;
			}
		}

		@Override
		public void onLongPress(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onLongPress",  "onLongPress");
			super.onLongPress(e);
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
			//TODO Auto-generated Method / stab
			Log.v("onScroll",  "X scroll: " + (int)distanceX + "Y scroll: " + (int)distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
		
		@Override
		public void onShowPress(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onShowPress",  "onShowPress");
			super.onShowPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onSingleTapConfirmed",  "onSingleTapConfirmed");
			return super.onSingleTapConfirmed(e);
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e){
			//TODO Auto-generated Method / stab
			Log.v("onSingleTapUp",  "onSingleTapUp");
			return super.onSingleTapUp(e);
		}
	}