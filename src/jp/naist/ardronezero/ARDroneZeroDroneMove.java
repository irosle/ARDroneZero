package jp.naist.ardronezero;

import android.util.Log;
import android.view.View;
import com.codeminders.ardrone.ARDrone;


public class ARDroneZeroDroneMove {
	static ARDrone drone;
	private static final String TAG = "AR.DroneZero Move";
	ARDroneZeroDroneMove(){//constructor
		drone = MainActivity.getARDroneInstance();
	}
	
	//A positive value makes the drone spin right; 
	//A negative value makes it spin left.
	public void spin(float x){ //Spin Right or Left
		drone = MainActivity.getARDroneInstance();
		if(drone != null){
			try{
				Log.e(TAG, "x = " + x);
				x *= -1f;//change spin direction
				drone.move(0f,0f,0f,x); 
			}catch(Exception e){
				Log.e(TAG, "Faliled to execute Spin command", e);
			}
		}else{
			Log.e(TAG, "ARDroneMove has not instanve of ARDrone!");
		}
	}

}
