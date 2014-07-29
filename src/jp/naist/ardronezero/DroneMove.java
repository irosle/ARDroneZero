package jp.naist.ardronezero;

import android.util.Log;
import android.view.View;
import com.codeminders.ardrone.ARDrone;


public class DroneMove {
	static ARDrone drone;
	private static final String TAG = "AR.Drone";
	
	
/*	A positive value makes the drone spin right; 
 *  A negative value makes it spin left.
 */
	public void Spin(float x){ //Spin Right or Left   
		try{
			drone.move(0f,0f,0f,x); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Spin Left command", e);
		}
	}

}
