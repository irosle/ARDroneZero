package jp.naist.ardronezero;

import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.ARDrone.State;
//import jp.naist.ardronelabyrinth.FullscreenActivity;
//import jp.naist.ardronelabyrinth.FullscreenActivity.DroneStarter;
import com.codeminders.ardrone.ControllerThread;

public class MainActivity extends Activity {

	private static final long CONNECTION_TIMEOUT = 10000;
	private static final String TAG = "AR.Drone";
	
	private GestureDetectorCompat mDetector;


	TextView state;
	Button btnConnect;
	Button btnTakeOffOrLand;

	Button btnTurnLeft; //++++++++++++++
	Button btnTurnRight;
	Button btnMoveFront;
	Button btnMoveBack;
	Button btnRise;
	Button btnDown;
	Button btnLeft;
	Button btnRight;	
	
	
	float speedX = 0.0f;
	float speedY = 0.0f;
	
	ControllerThread ctrThread;

	static ARDrone drone;
	static ARDroneZeroDroneMove dronemove;//when and where this instance is created? I'll check this after.(2014/8/5 K.S) 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		state = (TextView) findViewById(R.id.textView1);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnTakeOffOrLand = (Button) findViewById(R.id.btnTakeOffOrLand);
		
		mDetector = new GestureDetectorCompat(this, new ARDroneZeroGestureListener() );
		
		btnTurnLeft = (Button) findViewById(R.id.btnTurnLeft); 
		btnTurnRight= (Button) findViewById(R.id.btnTurnRight);
		btnMoveFront= (Button) findViewById(R.id.btnMoveFront);
		btnMoveBack = (Button) findViewById(R.id.btnMoveBack);
		btnRise		= (Button) findViewById(R.id.btnRise);
		btnDown		= (Button) findViewById(R.id.btnDown);
		btnLeft		= (Button) findViewById(R.id.btnMoveLeft);
		btnRight	= (Button) findViewById(R.id.btnMoveRight);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){//register touch detection
		//This function starts if user touches display.
		//TODO Auto-generated method stub
		this.mDetector.onTouchEvent(event);

		return true;//Go to gesture Listener Method
	}
	
	public static ARDrone getARDroneInstance(){
		return drone;//( drone != null) ? drone : null;
	}

	public void connect(View view) {

		WifiManager connManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		//if comment out this line, drone fly.(The same statement is just following if(). Why this statement is here?)
		//(new DroneStarter()).execute(MainActivity.drone);

		if (connManager.isWifiEnabled()) {
			state.setTextColor(Color.RED);//button color change 
			state.setText("Connecting..."
					+ connManager.getConnectionInfo().getSSID());//add string the button
			btnConnect.setEnabled(false);//disable the button
			(new DroneStarter()).execute(MainActivity.drone);//drone setup
		} else {
			//turnOnWifiDialog.show();
		}
	}

	private void droneOnConnected() {

		state.setTextColor(Color.GREEN);
		state.setText("Connected");
		// loadDroneSettingsFromPref();
		// btnConnect.setEnabled(false);
		// drone.addImageListener(this);

		if (null != ctrThread) {
			ctrThread.setDrone(drone);
		}

		if (btnTakeOffOrLand != null) {
			btnTakeOffOrLand.setVisibility(View.VISIBLE);
			btnTakeOffOrLand.setClickable(true);//enable to push the button(but nothing happened when the button was pushed
			btnTakeOffOrLand.setEnabled(true);//button is enable, and decide operation on next lister()
			btnTakeOffOrLand.setOnClickListener(new View.OnClickListener() {//register operation when user push the button
				public void onClick(View v) {//the function when pushed the button

					if (null == drone || drone.getState() == State.DISCONNECTED) {//if drone has disconnection
						state.setText("Disconnected");
						state.setTextColor(Color.RED);
						btnConnect.setEnabled(true);//we can use connect button
						return;
					}

					if (btnTakeOffOrLand.getText().equals(
							getString(R.string.land))) {//if button string is "land" (I think comparing drone.state() is more better than comparing string.2014/8/5 K.S
						try {
							drone.land();//land
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}

						btnTakeOffOrLand.setText(R.string.take_off);//set "takeoff" on button string
					} else {
						try {
							drone.clearEmergencySignal();
							drone.trim();
							drone.takeOff();//fly
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}
						btnTakeOffOrLand.setText(R.string.land);//set "land" on button string
					}
				}
			});
		}
	}

	public void turnLeft(View view){ //Spin left
		try{
			//MySimpleOnGestureListener.this.onFling(e1, e2, velocityX, velocityY);
			state.setText("Turn Left");
			//float tmp = (vecX > 0) ? -10f : 10f;
			drone.move(0f,0f,0f,-10f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Turn Left command", e);
		}
		btnTurnLeft.setEnabled(true);
	}

	public void turnRight(View view){ //right spin
		try{
			state.setText("Turn right");
			drone.move(0f,0f,0f,10f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Turn Right command", e);
		}
		btnTurnRight.setEnabled(true);
	}
	
	public void moveFront(View view){ //go ahead
		try{
			state.setText("Move Front");
			drone.move(0f,-10f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Front command", e);
		}
		btnMoveFront.setEnabled(true);
	}
	
	public void moveBack(View view){ //back
		try{
			state.setText("Move Back");
			drone.move(0f,10f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Back command", e);
		}
		btnMoveBack.setEnabled(true);
	}

	public void moveRise(View view){//move upper direction
		try{
			state.setText("Move Rise");
			drone.move(0f,0f,10f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Rise command", e);
		}
		btnRise.setEnabled(true);
	}
	
	public void moveDown(View view){//move under direction
		try{
			state.setText("Move Down");
			drone.move(0f,0f,-10f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Down command", e);
		}
		btnDown.setEnabled(true);
	}
	
	public void moveLeft(View view){//go to left
		try{
			state.setText("Move Left");
			drone.move(-10f,0f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Left command", e);
		}
		btnLeft.setEnabled(true);
	}
	
	public void moveRight(View view){//go to right
		try{
			state.setText("Move Right");
			drone.move(10f,0f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Right command", e);
		}
		btnRight.setEnabled(true);
	}
	

	
	
	
	private class DroneStarter extends AsyncTask<ARDrone, Integer, Boolean> {
		//Initialize if drone fly  (For example, connection)
		@Override
		protected Boolean doInBackground(ARDrone... drones) {
			ARDrone drone = drones[0];
			//final byte[] DEFAULT_DRONE_IP  = { (byte) 192, (byte) 168, (byte) 1, (byte) 1 };
			try {
				drone = new ARDrone(
						InetAddress.getByAddress(ARDrone.DEFAULT_DRONE_IP),
						10000, 60000);

				MainActivity.drone = drone;
				drone.connect();
				drone.clearEmergencySignal();
				drone.trim();
				// drone.waitForReady(CONNECTION_TIMEOUT);
				drone.playLED(1, 10, 4);
				drone.selectVideoChannel(ARDrone.VideoChannel.HORIZONTAL_ONLY);
				drone.setCombinedYawMode(true);
				return true;
			} catch (Exception e) {
				Log.e(TAG, "Failed to connect to drone", e);
				try {
					drone.clearEmergencySignal();
					drone.clearImageListeners();
					drone.clearNavDataListeners();
					drone.clearStatusChangeListeners();
					drone.disconnect();
				} catch (Exception ex) {
					Log.e(TAG, "Failed to clear drone state", ex);
				}

			}
			return false;
		}

		protected void onPostExecute(Boolean success) {
			if (success.booleanValue()) {
				droneOnConnected();
			} else {
				Log.e(TAG, "Error on Post Execute");
				/*
				 * state.setTextColor(Color.RED); state.setText("Error");
				 * btnConnect.setEnabled(true);
				 */
			}
		}
	}
}
