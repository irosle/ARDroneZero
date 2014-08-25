package jp.naist.ardronezero;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ARDroneZeroDynamicObject {
	private static final String TAG = "AR.DroneZero DynamicObject";
	static int mIDMaster = 0;
	static private  Bitmap mImageContainer[];
	private Bitmap mObjImage;
	public int mID;
	private int mWinWidth, mWinHeight;
	private int mObjectWidth, mObjectHeight;
	private int mX, mY;//position(upper left of image

	
	private int mSpeedX, mSpeedY;
	private int dirX, dirY;//direction
	private int mRateOfChangingDirection;//rate of changing the object direction(0 - 100, 0 : not changing, 100: always changing)
	private Random rnd;
	public ARDroneZeroDynamicObject(Context c, final int objImageNo){//constructor
		rnd = new Random();
		mImageContainer = new Bitmap[10];//âÊëúÇÃñáêî
		if( mImageContainer[objImageNo] == null){//this image has not loaded yet.
			switch(objImageNo){
				case 0	: mImageContainer[0] 	= BitmapFactory.decodeResource(c.getResources(), R.drawable.test_enemy0); break;
				case 1	: mImageContainer[1] 	= BitmapFactory.decodeResource(c.getResources(), R.drawable.test_enemy1); break;
				default	: mImageContainer[0] 	= BitmapFactory.decodeResource(c.getResources(), R.drawable.test_enemy0); break; 
			}
		}
		mObjImage = mImageContainer[objImageNo];
		

	    mID = mIDMaster;//give id
	    ++mIDMaster;
	    
	    mWinWidth = 0;
	    mWinHeight = 0;
	    mX = 0;//init position
	    mY = 0;
	    mSpeedX = 50;
	    mSpeedY = 50;
	    dirX = dirY = 1;
	    mRateOfChangingDirection = 20;
	}
	
	public void setWindowSize(final int width, final int height){
		mWinWidth = width;
		mWinHeight = height;
	}


	public Bitmap getImage(){
		return mObjImage;
	}
	
	public int getPosX(){
		return mX;
	}
	public int getPosY(){
		return mY;
	}
	
	public boolean isInsideTheWindow(){
		return ( 0 <= mX && mX < mWinWidth && 0 <= mY && mY < mWinHeight) ? true : false; 
	}
	
	public void move() {
		if( rnd.nextInt(100) < mRateOfChangingDirection){
			switch( rnd.nextInt(9)){
				case 0: dirX =  0; dirY =  0; break;//stay
				case 1: dirX = -1; dirY =  0; break;//left
				case 2: dirX = -1; dirY =  1; break;//left and down
				case 3: dirX =  0; dirY =  1; break;//down
				case 4: dirX =  1; dirY =  1; break;//right and down
				case 5: dirX =  1; dirY =  0; break;//right
				case 6: dirX =  1; dirY = -1; break;//right and up
				case 7: dirX =  0; dirY = -1; break;//up
				case 8: dirX = -1; dirY = -1; break;//left and up
				default:
					dirX = dirY = 0; break;
			}
		}
		mX += mSpeedX * dirX;
		mY += mSpeedY * dirY;
		//if enemy alive out of the window, the following sentence should be comment-out;
		mX = ( mX < 0 ) ? 0 : ( (mWinWidth  <= mX + mObjImage.getWidth() ) ? mWinWidth  - 1  - mObjImage.getWidth(): mX); 
		mY = ( mY < 0 ) ? 0 : ( (mWinHeight <= mY + mObjImage.getHeight() ) ? mWinHeight - 1 - mObjImage.getHeight() : mY); 
	}

	
	//destroy the object 
	public void destroy(){
		;
	}
}

