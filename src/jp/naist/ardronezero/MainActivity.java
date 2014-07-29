package jp.naist.ardronezero;

import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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
	
	private GestureDetector gestDetect;

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
	
	ControllerThread ctrThread;

	static ARDrone drone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		state = (TextView) findViewById(R.id.textView1);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnTakeOffOrLand = (Button) findViewById(R.id.btnTakeOffOrLand);
		
		gestDetect = new GestureDetector(this, new MySimpleOnGestureListener() );
		
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
		//TODO Auto-generated method stub
		gestDetect.onTouchEvent(event);
		return false;
	}

	public void connect(View view) {

		WifiManager connManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		//(new DroneStarter()).execute(MainActivity.drone);//これ消すと飛ぶ、すぐ下で呼んでいるのになぜかここにある?

		if (connManager.isWifiEnabled()) {
			state.setTextColor(Color.RED);//ボタン色変更
			state.setText("Connecting..."
					+ connManager.getConnectionInfo().getSSID());//ボタンに文字列追加
			btnConnect.setEnabled(false);//ボタンを無効化
			(new DroneStarter()).execute(MainActivity.drone);//ドローンの始動準備
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
			btnTakeOffOrLand.setVisibility(View.VISIBLE);//ボタンを見えるように
			btnTakeOffOrLand.setClickable(true);//ボタンが押せる（押したときのボタンの状態が変わる,まだ押しても何も起きない)
			btnTakeOffOrLand.setEnabled(true);//ボタンが有効化,次のlistenerで、押した時にする動作を決定
			btnTakeOffOrLand.setOnClickListener(new View.OnClickListener() {//ボタンを押したときの動作を登録
				public void onClick(View v) {//ボタンを押したときに動作する関数

					if (null == drone || drone.getState() == State.DISCONNECTED) {//通信がされていない
						state.setText("Disconnected");
						state.setTextColor(Color.RED);
						btnConnect.setEnabled(true);//connectボタンを有効化
						return;
					}

					if (btnTakeOffOrLand.getText().equals(
							getString(R.string.land))) {//ボタンがland文字列だったら(ボタン文字列比較よりdrone.getState()のような状態を確かめるよウにした方がいいんじゃ?)
						try {
							drone.land();//land
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}

						btnTakeOffOrLand.setText(R.string.take_off);//文字列にtake_offを設定
					} else {
						try {
							drone.clearEmergencySignal();
							drone.trim();
							drone.takeOff();//飛翔
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}
						btnTakeOffOrLand.setText(R.string.land);//ボタン文字列にlandを設定
					}
				}
			});
		}
	}

	
	public void turnLeft(View view){ //Spin left
		try{
			state.setText("Turn Left");
			drone.move(0f,0f,0f,-10f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Turn Left command", e);
		}
		btnTurnLeft.setEnabled(true);
	}

	public void turnRight(View view){ //右に回転
		try{
			state.setText("Turn right");
			drone.move(0f,0f,0f,10f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Turn Right command", e);
		}
		btnTurnRight.setEnabled(true);
	}
	
	public void moveFront(View view){ //前進
		try{
			state.setText("Move Front");
			drone.move(0f,-10f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Front command", e);
		}
		btnMoveFront.setEnabled(true);
	}
	
	public void moveBack(View view){ //後退
		try{
			state.setText("Move Back");
			drone.move(0f,10f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Back command", e);
		}
		btnMoveBack.setEnabled(true);
	}

	public void moveRise(View view){//上昇
		try{
			state.setText("Move Rise");
			drone.move(0f,0f,10f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Rise command", e);
		}
		btnRise.setEnabled(true);
	}
	
	public void moveDown(View view){//低下
		try{
			state.setText("Move Down");
			drone.move(0f,0f,-10f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Down command", e);
		}
		btnDown.setEnabled(true);
	}
	
	public void moveLeft(View view){//左に
		try{
			state.setText("Move Left");
			drone.move(-10f,0f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Left command", e);
		}
		btnLeft.setEnabled(true);
	}
	
	public void moveRight(View view){//右に
		try{
			state.setText("Move Right");
			drone.move(10f,0f,0f,0f); 
		}catch(Exception e){
			Log.e(TAG, "Faliled to execute Move Right command", e);
		}
		btnRight.setEnabled(true);
	}
	

	
	
	
	private class DroneStarter extends AsyncTask<ARDrone, Integer, Boolean> {
		//飛ぶときの初期設定(connectionなど)
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
	
	//basic gesture class
	private class MySimpleOnGestureListener extends SimpleOnGestureListener{
		
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
		}
		

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			//TODO Auto-generated Method / stab
			//Log.v("onFling",  "X speed: " + (int)velocityX + ", Y speed: " + (int)velocityY);
			//return super.onFling(e1, e2, velocityX, velocityY);
			float threshold = 1500;
			if( Math.abs( (int)velocityX ) > threshold ){//if velocity of X-axis direction  is larger than threshold,  
				Log.v("onFling",  "X speed: " + (int)velocityX + ", Y speed: " + (int)velocityY);//recognized flip gesture.
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
}
