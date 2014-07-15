package jp.naist.ardronezero;

import java.net.InetAddress;

/*import jp.naist.ardronelabyrinth.FullscreenActivity;
import jp.naist.ardronelabyrinth.FullscreenActivity.DroneStarter;
*/
import com.codeminders.ardrone.*;
import com.codeminders.ardrone.ARDrone.State;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final long CONNECTION_TIMEOUT = 10000;
	private static final String TAG = "AR.Drone";

	TextView state;
	Button btnConnect;
	Button btnTakeOffOrLand;

	ControllerThread ctrThread;

	static ARDrone drone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		state = (TextView) findViewById(R.id.textView1);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnTakeOffOrLand = (Button) findViewById(R.id.btnTakeOffOrLand);
	}

	public void connect(View view) {

		WifiManager connManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		(new DroneStarter()).execute(MainActivity.drone);

		if (connManager.isWifiEnabled()) {
			state.setTextColor(Color.RED);
			state.setText("Connecting..."
					+ connManager.getConnectionInfo().getSSID());
			btnConnect.setEnabled(false);
			(new DroneStarter()).execute(MainActivity.drone);
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
			btnTakeOffOrLand.setClickable(true);
			btnTakeOffOrLand.setEnabled(true);
			btnTakeOffOrLand.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					if (null == drone || drone.getState() == State.DISCONNECTED) {
						state.setText("Disconnected");
						state.setTextColor(Color.RED);
						btnConnect.setEnabled(true);
						return;
					}

					if (btnTakeOffOrLand.getText().equals(
							getString(R.string.land))) {
						try {
							drone.land();
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}

						btnTakeOffOrLand.setText(R.string.take_off);
					} else {
						try {
							drone.clearEmergencySignal();
							drone.trim();
							drone.takeOff();
						} catch (Throwable e) {
							Log.e(TAG, "Faliled to execute take off command", e);
						}
						btnTakeOffOrLand.setText(R.string.land);
					}
				}
			});
		}
	}

	private class DroneStarter extends AsyncTask<ARDrone, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(ARDrone... drones) {
			ARDrone drone = drones[0];
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
