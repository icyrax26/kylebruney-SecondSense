package kylebruney.salford.secondsense.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kylebruney.salford.secondsense.R;
import kylebruney.salford.secondsense.sensors.AccelerometerResultReceiver;
import kylebruney.salford.secondsense.sensors.AccelerometerService;
import kylebruney.salford.secondsense.sensors.GyroscopeResultReceiver;
import kylebruney.salford.secondsense.sensors.GyroscopeService;
import kylebruney.salford.secondsense.sensors.PedometerResultReceiver;
import kylebruney.salford.secondsense.sensors.PedometerService;
import kylebruney.salford.secondsense.util.Constants;
import kylebruney.salford.secondsense.util.PreferencesData;

public class RecordPage extends Activity {

	AccelerometerResultReceiver accelerometerResultReceiver;
	PedometerResultReceiver pedometerResultReceiver;
	GyroscopeResultReceiver gyroscopeResultReceiver;	
	private static String FILE_PREFIX;
	private static final String FILE_SUFFIX = ".csv";
	private final String SENSOR_NOT_SUPPORTED = "Not supported";	
    File root = Environment.getExternalStorageDirectory();
    ArrayList<String> xEvent = new ArrayList();
    Button btnWrite;
    DecimalFormat df = new DecimalFormat("0.00");
    private TextView accelerometer;
    private TextView gyroscope;
    Button btnReset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordpage);

		setupSensors();
		
		//Sensor TextViews
		accelerometer = ((TextView)findViewById(R.id.tv_accel_text));
		gyroscope = ((TextView)findViewById(R.id.tv_gyro_text));
		
		//Create File Directory
		File localFile = new File("/sdcard/SecondSense/");
        if (!localFile.exists()){
        	localFile.mkdir();
        }
		
        //Stop and Save Button
		btnWrite = (Button) findViewById(R.id.btnStop);
        btnWrite.setOnClickListener(new OnClickListener() {
        	
        File root = Environment.getExternalStorageDirectory();        
        

		public void onClick(View v) {
			
			//Save to SD Card
			try {
				File myFile = new File(this.root, "/SecondSense/" + FILE_PREFIX + ".csv");
	            myFile.createNewFile();
	            FileOutputStream fOut = new FileOutputStream(myFile);
	            OutputStreamWriter myOutWriter = 
	                                    new OutputStreamWriter(fOut);
	            myOutWriter.append("Time is displayed Hours:Minutes:Seconds:Milliseconds" + "\n");
	            myOutWriter.append(xEvent.toString());
	            myOutWriter.close();
	            fOut.close();
	            Toast.makeText(getBaseContext(),
	                    "Successfully saved to SD card'",
	                    Toast.LENGTH_SHORT).show();
	        } catch (Exception e) {
	            Toast.makeText(getBaseContext(), e.getMessage(),
	                    Toast.LENGTH_SHORT).show();
	        }
		
		//Share Data
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("plain/text");
        localIntent.putExtra("android.intent.extra.SUBJECT", FILE_PREFIX);
        localIntent.putExtra("android.intent.extra.TEXT", 
        		"Time is displayed in Hours:Minutes:Seconds:Milliseconds" + "\n" 
        				+ xEvent.toString());
        startActivity(Intent.createChooser(localIntent, "Share Via"));
        return;
        
		}});
        
        btnReset = (Button)findViewById(R.id.button3);
        btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RecordPage.this, HomePage.class);
				startActivity(i);
				finish();
			}
        	
        });

	}

	//Setup Sensors
	private void setupSensors() {

		Handler handler = new Handler();

		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
		for (int i = 0; i < sensorList.size(); i++) {
			int type = sensorList.get(i).getType();
			if (type == Sensor.TYPE_ACCELEROMETER) {
				accelerometerResultReceiver = new AccelerometerResultReceiver(
						handler);
				accelerometerResultReceiver
						.setReceiver(new AccelerometerReceiver());
			}
			if (type == Sensor.TYPE_STEP_COUNTER) {
				pedometerResultReceiver = new PedometerResultReceiver(handler);
				pedometerResultReceiver.setReceiver(new PedometerReceiver());

			}
			if (type == Sensor.TYPE_GYROSCOPE) {
				gyroscopeResultReceiver = new GyroscopeResultReceiver(handler);
				gyroscopeResultReceiver.setReceiver(new GyroscopeReceiver());
			}
			 
		}
		
		//Update Sensor TextViews
		if (accelerometerResultReceiver == null) {
			new UpdateUI(R.id.tv_accel_result, SENSOR_NOT_SUPPORTED);
		}
		if (pedometerResultReceiver == null) {
			new UpdateUI(R.id.tv_ped_result, SENSOR_NOT_SUPPORTED);
		}
		if (gyroscopeResultReceiver == null) {
			new UpdateUI(R.id.tv_gyro_result, SENSOR_NOT_SUPPORTED);
		}

	}


	//Drop down menu allowing the user to name the file
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	//Textbox for the file name
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_file_prefix) {
			LayoutInflater factory = LayoutInflater.from(RecordPage.this);
			final View textEntryView = factory.inflate(
					R.layout.dialog_stringinput, null);
			AlertDialog.Builder alert = new AlertDialog.Builder(
					RecordPage.this);
			alert.setTitle("Set file prefix");
			alert.setView(textEntryView);

			final EditText mUserText = (EditText) textEntryView
					.findViewById(R.id.value);
			mUserText.setText(FILE_PREFIX);
			
			alert.setPositiveButton(getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String prefixString = mUserText.getText()
									.toString();

							FILE_PREFIX = prefixString;

							PreferencesData.saveString(RecordPage.this,
									PreferencesData.PREFERENCE_FILE_PREFIX,
									prefixString);

							

							return;
						}
					});

			alert.setNegativeButton(getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							return;
						}
					});
			alert.show();

		}
		return super.onOptionsItemSelected(item);
	}

	// Start Button
	public void start(View v) {
		
		
		// start accelerator service
		if (accelerometerResultReceiver != null) {
			Intent accelerometerIntent = new Intent(RecordPage.this, AccelerometerService.class);
			accelerometerIntent.putExtra(Constants.EXTRA_RECEIVER,
					accelerometerResultReceiver);
			startService(accelerometerIntent);
		}
		
		//start pedometer service
		if (pedometerResultReceiver != null) {
			Intent pedometerIntent = new Intent(RecordPage.this, PedometerService.class);
			pedometerIntent.putExtra(Constants.EXTRA_RECEIVER,
					pedometerResultReceiver);
			startService(pedometerIntent);
		}

		// start gyroscope service
		if (gyroscopeResultReceiver != null) {
			Intent gyroscopeIntent = new Intent(RecordPage.this, GyroscopeService.class);
			gyroscopeIntent.putExtra(Constants.EXTRA_RECEIVER,
					gyroscopeResultReceiver);
			startService(gyroscopeIntent);
		}
		
		
	}
	
	//Pause Button
	public void stop(View v) {
		if (accelerometerResultReceiver != null)
			stopService(new Intent(this, AccelerometerService.class));
		if (pedometerResultReceiver != null)
			stopService(new Intent(this, PedometerService.class));
		if (gyroscopeResultReceiver != null)
			stopService(new Intent(this, GyroscopeService.class));
		
	}

	private class UpdateUI implements Runnable {

		private String mText;
		private TextView mTv;

		public UpdateUI(int textviewid, String text) {
			this.mTv = (TextView) findViewById(textviewid);
			this.mText = text;
		}

		@Override
		public void run() {
			mTv.setText(mText);
		}

	}
	
	//Accelerometer Class
	private class AccelerometerReceiver implements
			AccelerometerResultReceiver.Receiver {

		private int resultTextViewID;
		private float x, y, z;

		public AccelerometerReceiver() {
			resultTextViewID = R.id.tv_accel_result;
		}

		private void sendLocationToUI() {
			double roundx = Math.round(x);
			double roundy = Math.round(y);
			double roundz = Math.round(z);
			
			runOnUiThread(new UpdateUI(resultTextViewID, "x: " + roundx
					+ ", y: " + roundy + ", z: " + roundz));
		}

		@Override
		public void newEvent(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
			sendLocationToUI();			
			//Record the Current Time
			Calendar localCalendar = Calendar.getInstance();
		    localCalendar.getTime();
		    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm:ss:S");
		    xEvent.add("Time: " + localSimpleDateFormat.format(localCalendar.getTime()) + " ");
			
		    //Record Accelerometer Values
			xEvent.add(getAccDegrees(x, y, z) + " ");
			
			//Display Accelerometer X, Y and Z Values
			accelerometer.setText(getAccDegrees(x, y, z));

		}

		@Override
		public void error(String error) {
			runOnUiThread(new UpdateUI(resultTextViewID, error));
		}

	}
	
	//Pedometer Class
	private class PedometerReceiver implements
			PedometerResultReceiver.Receiver {

		private int resultTextViewID;
		private float x;

		public PedometerReceiver() {
			resultTextViewID = R.id.tv_ped_result;
		}

		private void sendLocationToUI() {
			double roundx = Math.round(x);

			runOnUiThread(new UpdateUI(resultTextViewID, "Footsteps: " + roundx));
		}

		@Override
		public void newEvent(float x){
				this.x = x;
				sendLocationToUI();
				
				//Convert Step Counter to a String
				String str1 = df.format(x);
			
				//Record Step Counter Value
				xEvent.add("Footstep Counter: " + str1 + " ");
				
		}

		@Override
		public void error(String error) {
			runOnUiThread(new UpdateUI(resultTextViewID, error));
		}

	}

	//GyroScope Class
	private class GyroscopeReceiver implements GyroscopeResultReceiver.Receiver {

		private int resultTextViewID;
		private float x, y, z;

		public GyroscopeReceiver() {

			resultTextViewID = R.id.tv_gyro_result;
		}

		private void sendLocationToUI() {
			double roundx = Math.round(x);
			double roundy = Math.round(y);
			double roundz = Math.round(z);
			runOnUiThread(new UpdateUI(resultTextViewID, "x: " + roundx
					+ ", y: " + roundy + ", z: " + roundz));
		}

		@Override
		public void newEvent(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
			sendLocationToUI();
			
			//Record Gyroscope values
			xEvent.add(getGyroDegrees(x, y, z) + "\n");
			
			//Display Gyroscope X, Y and Z Values
			gyroscope.setText(getGyroDegrees(x, y, z));
			
		}

		@Override
		public void error(String error) {
			runOnUiThread(new UpdateUI(resultTextViewID, error));
		}

	}
	
	//Convert Accelerometer Values into a String
	public String getAccDegrees(float accx, float accy, float accz) {
		if((accy >= -5 && accy < 11) && (accz >= -7 && accz < 11)) { return "Correct Orientation"; }
		else{return "Error";}
	}
	
	//Convert Gyroscope Values into a String
	public String getGyroDegrees(float gyrox, float gyroy, float gyroz){
		if((gyroy >= 1) || (gyroz >= 1)) { return "Direction: Left"; }
		if((gyroy <= -1) || (gyroz <= -1)) { return "Direction: Right"; }
		else {return "Direction: Straight";}
	}
	
}