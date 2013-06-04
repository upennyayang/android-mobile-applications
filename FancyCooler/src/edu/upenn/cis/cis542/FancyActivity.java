package edu.upenn.cis.cis542;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chartdemo.demo.BaseRequestListener;
import org.achartengine.chartdemo.demo.LoginButton;
import org.achartengine.chartdemo.demo.SessionEvents;
import org.achartengine.chartdemo.demo.Utility;
import org.achartengine.chartdemo.demo.SessionEvents.AuthListener;
import org.achartengine.chartdemo.demo.SessionEvents.LogoutListener;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FancyActivity extends Activity {
	/** Called when the activity is first created. */
	private static final int ACTIVITY_MainPage = 1;
	private int current_mode = 0;
	private final int PLAN_NOT_FOUND = 100, PLAN_FOUND = 101, INET_ADDRESS = 102;
	private boolean isRunning = false, isPause = false, isShakePressed = false,
			connected = false;
	private double curTemp = 0;

	// speak
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	String voicename = null;
	Integer voiceid = null;

	private static TimeSeries timeSeries;
	private static XYMultipleSeriesDataset dataset;
	private static XYMultipleSeriesRenderer renderer;
	private static XYSeriesRenderer rendererSeries;
	private static GraphicalView view;
	private ShakeListener mShaker;
	InputStream in;
	String receive;
	BufferedReader read;
	PrintWriter write;
	Socket s;
	TextView tv;
	private Handler mHandler;
	public static final String APP_ID = "194553427322886";
	String[] permissions = { "offline_access", "publish_stream", "user_photos",
			"publish_checkins", "photo_upload" };
	ProgressDialog dialog;

	public double getCurTemp() {
		return curTemp;
	}

	public void setCurTemp(double curTemp) {
		this.curTemp = curTemp;
	}

	public int getCurTime() {
		return curTime;
	}

	public void setCurTime(int curTime) {
		this.curTime = curTime;
	}

	private int curTime = 0;

	// private long startTime = 0;

	// class Timer extends AsyncTask<Void, Double, Void> {
	//
	// protected Void doInBackground(Void... params) {
	// boolean isNetworkAvailable = true;
	// while (isNetworkAvailable) {
	// Double tempvalue = 90.2222;
	// Double timevalue = 100.0;
	// publishProgress(tempvalue, timevalue);
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// return null;
	// }
	// }
	// return null;
	// }
	//
	// protected void onProgressUpdate(Double... temp) {
	// curTemp = temp[0];
	// curTime = (int) (double) temp[1];
	// DecimalFormat df = new DecimalFormat("#.#");
	// TextView instanceTemp = (TextView) findViewById(R.id.instantTemperature);
	// instanceTemp.setText(df.format(curTemp).toString() + "°C");
	// getGoal();
	// }
	// }

	public void statusUpdate(double temperature, int time, boolean isExist) {
		curTemp = temperature;
		curTime = time;
		ImageView imageView = (ImageView) findViewById(R.id.control);
		if (isExist) {
			imageView.setAlpha(50);
			imageView.setEnabled(true);
		} else {
			imageView.setAlpha(100);
			imageView.setEnabled(false);
		}

		DecimalFormat df = new DecimalFormat("#.###");
		TextView instanceTemp = (TextView) findViewById(R.id.instantTemperature);
		instanceTemp.setText(df.format(curTemp).toString() + "°C");
		timeSeries.add(new Date(), curTemp);
		view.repaint();
		getGoal();
	}

	public void adjustSpeed(float shakeSpeed) {
		// int fanSpeed=0;//adjust here
		write.println("f100");
		write.flush();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainpage);
		initialization();
	}

	private void initialization() {

		SharedPreferences preferences = getSharedPreferences("FANCY", 0);
		Editor edit = preferences.edit();
		String ipAddr = preferences.getString("IP", "");
		String port = preferences.getString("PORT", "");
		if (ipAddr.isEmpty() || port.isEmpty()) {
			// ask the user to put in the ip addr
			removeDialog(PLAN_NOT_FOUND);
			showDialog(PLAN_NOT_FOUND);
		}else{
			connectToServer();
			System.err.println("we have ip addr");
		}
		// initialize events
		eventInit();

		// voice control
		speakerInit();
		readingInit();
		// initialize the database
		dbInit();

		// get current state
		setState(0, 0, 0, 0, 0);

		// get instant temperature
		getInstantTemp();

		// diagram
		LinearLayout diagramLayout = (LinearLayout) findViewById(R.id.diagramLayout);
		getChart();
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
		diagramLayout.addView(view);
		// shaker

//		mShaker = new ShakeListener(this);
//		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
//			public void onShake() {
//				if (isShakePressed) {
//					adjustSpeed(mShaker.getSpeed());
//					new AlertDialog.Builder(getApplicationContext())
//							.setPositiveButton(android.R.string.ok, null)
//							.setMessage("Shooken!").show();
//				}
//			}
//		});

		// MainPanel mp = new MainPanel();
		// Graphicchart);
		// mp.initialize();
		// mp.getChart(diagram);
		// Toast toast = Toast.makeText(getApplicationContext(),
		// "Connecting to Arduino", Toast.LENGTH_LONG);
		// toast.setDuration(Toast.LENGTH_LONG);
		// toast.show();

		// control
		getControl();
		updateControl();

		// goal
		getGoal();
	}

	private void dbInit() {
		PlanDB plandb = new PlanDB(getApplicationContext());
		plandb.init();
		plandb.close();
	}

	private void eventInit() {
		ImageView voiceCtrlView = (ImageView) findViewById(R.id.voicecontrol);
		final ImageView shakeCtrlView = (ImageView) findViewById(R.id.shakeshake);
		voiceCtrlView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO

				return false;
			}
		});
		shakeCtrlView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					shakeCtrlView.setAlpha(50);
					isShakePressed = true;
					break;
				case MotionEvent.ACTION_UP:
					shakeCtrlView.setAlpha(255);
					isShakePressed = false;
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	private void setState(int sche_state, int sche_id, double sche_temp,
			int sche_time, int sche_fan) {
		// PlanDB plandb = new PlanDB(getApplicationContext());
		// String name = plandb.getNameById(0);
		// plandb.close();

		// TODO: network communication

		// Integer sche_state = 0;
		// Integer sche_id = 2;
		// Integer sche_temp = 20;
		// Integer sche_time = 500;
		// Integer sche_fan = 90;

		PlanDB db = new PlanDB(getApplicationContext());
		String name = db.getNameById(sche_id);
		Integer temp = db.getTemperatureById(sche_id);
		Integer time = db.getTimeById(sche_id);
		Integer fan = db.getFanById(sche_id);

		SharedPreferences pref = getSharedPreferences("FANCY", 0);
		SharedPreferences.Editor prefTableEditor = pref.edit();
		if (name != null && temp == sche_temp && time == sche_time
				&& fan == sche_fan) {
			prefTableEditor.putInt("current_mode_id", sche_id);
			prefTableEditor.putString("current_mode_name", name);
		} else {
			String manualname = db.getNameById(0);
			prefTableEditor.putInt("current_mode_id", 0);
			prefTableEditor.putString("current_mode_name", manualname);
		}
		prefTableEditor.commit();
		db.close();

		if (sche_state == 1)
			isRunning = true;
		else
			isRunning = false;

		updateControl();
	}

	private void getInstantTemp() {
		// new Timer().execute();
		double tempvalue = 0.0;
		TextView instanceTemp = (TextView) findViewById(R.id.instantTemperature);
		instanceTemp.setText(tempvalue + "°C");
	}

	private void getControl() {
		isRunning = false;
	}

	private void getGoal() {
		SharedPreferences pref = getSharedPreferences("FANCY", 0);
		int curID = pref.getInt("current_mode_id", 0);
		PlanDB db = new PlanDB(getApplicationContext());
		Integer curTempValue = db.getTemperatureById(curID);
		Integer curTimeValue = db.getTimeById(curID);
		Integer curSpeedValue = db.getFanById(curID);
		TextView alarmSpec = (TextView) findViewById(R.id.alarm_spec);
		TextView speedSpec = (TextView) findViewById(R.id.speed_spec);
		speedSpec.setText("Fan Speed: " + curSpeedValue + "%");
		if (curTempValue != null && curTimeValue != null) {
			double remaintemp = curTemp - curTempValue;
			DecimalFormat df = new DecimalFormat("#.###");
			if (curTime <= 0)
				alarm();
			String remaintime = convert(curTime);
			alarmSpec.setText("Remaining: " + df.format(remaintemp).toString()
					+ "°C, " + remaintime);
		} else {
			if (curTempValue != null) {
				double remaintemp = curTemp - curTempValue;
				alarmSpec.setText("Remaining: " + remaintemp + "°C");
			} else if (curTimeValue != null) {
				String remaintime = convert(curTimeValue - curTime);
				alarmSpec.setText("Remaining: " + remaintime);
			} else {
				alarmSpec.setText("Illegal");
			}
		}
		db.close();
	}

	private String convert(long time) {
		long second = time / 1000;
		String min = Long.toString(second / 60);
		String sec = Long.toString(second % 60);
		sec = sec.length() <= 1 ? "0" + sec : sec;
		return min + ":" + sec;
	}

	public void onMenuSwitchImageView(View view) {
		Intent i = new Intent(this, MenuActivity.class);
		i.putExtra("id", current_mode);
		startActivityForResult(i, ACTIVITY_MainPage);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case ACTIVITY_MainPage:

			break;

		case VOICE_RECOGNITION_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = intent
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				PlanDB pldb = new PlanDB(this);
				for (String name : matches) {
					voicename = name;
					// System.out.println("the name is:  "+name);
					voiceid = pldb.getIdByName(voicename);
					if (voiceid != null) {
						// Toast.makeText(getApplicationContext(), voicename,
						// Toast.LENGTH_SHORT).show();
						break;
					}
				}
				pldb.close();
				if (voiceid == null) {
					removeDialog(PLAN_NOT_FOUND);
					showDialog(PLAN_NOT_FOUND);
				} else {
					removeDialog(PLAN_FOUND);
					showDialog(PLAN_FOUND);
				}
			}
		default:
			break;
		}

	}

	public void onControlClick(View view) {
		// getControl();
		switchControl();
		updateControl();
	}

	private void updateControl() {
		alarm();
		if (!connected)
			return;
		ImageView control = (ImageView) findViewById(R.id.control);
		if (isRunning == false) {
//			// stop
//			write.println("a");
//			write.flush();
//			control.setImageResource(R.drawable.stop);
//			isPause = true;
		} else {
			// start
			SharedPreferences preferences = getSharedPreferences("FANCY", 0);
			int currentModeId = preferences.getInt("current_mode_id", 0);
			PlanDB db = new PlanDB(getApplicationContext());
			Integer temperature = db.getTemperatureById(currentModeId);
			Integer time = db.getTimeById(currentModeId);
			Integer fan = db.getFanById(currentModeId);
			if (temperature == null || time == null || fan == null)
				System.err.println("Error");
			else {

				if (isPause) {
					write.println("s" + String.valueOf(currentModeId) + " "
							+ String.valueOf(temperature) + " "
							+ String.valueOf(time - curTime) + " "
							+ String.valueOf(fan));
				} else {
					write.println("s" + String.valueOf(currentModeId) + " "
							+ String.valueOf(temperature) + " "
							+ String.valueOf(time) + " " + String.valueOf(fan));
				}
				write.flush();
				control.setImageResource(R.drawable.play);
			}
		}

	}

	private void switchControl() {
		if (isRunning == false) {
			isRunning = true;
		} else {
			isRunning = false;
		}
	}

	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void getChart() {

		dataset = new XYMultipleSeriesDataset();

		renderer = new XYMultipleSeriesRenderer();
		renderer.setAxesColor(Color.BLUE);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitle("Time");
		renderer.setChartTitleTextSize(15);
		renderer.setFitLegend(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setPointSize(5);
		renderer.setXTitle("Time");
		renderer.setYTitle("Tempeture");
		renderer.setMargins(new int[] { 10, 10, 10, 10 });
		renderer.setZoomButtonsVisible(true);
		renderer.setBarSpacing(5);
		renderer.setShowGrid(true);
		renderer.setZoomEnabled(true);

		rendererSeries = new XYSeriesRenderer();
		rendererSeries.setColor(Color.RED);
		renderer.addSeriesRenderer(rendererSeries);
		rendererSeries.setFillPoints(true);
		rendererSeries.setPointStyle(PointStyle.CIRCLE);

		timeSeries = new TimeSeries("Temperature");

		SharedPreferences preferences = getSharedPreferences("FANCY", 0);
		String ipAddr = preferences.getString("IP", "");
		String port = preferences.getString("PORT", "");
		// if (ipAddr.isEmpty() || port.isEmpty()) {
		connectToServer();
		// ask the user to put in the ip addr
		removeDialog(PLAN_NOT_FOUND);
		showDialog(PLAN_NOT_FOUND);
		// }

		// connectToServer();
		// try {
		// s = new Socket("158.130.63.12", 12345);
		// if(s.isConnected())
		// connected=true;
		// else
		// connected=false;
		//
		// read = new BufferedReader(new InputStreamReader(s.getInputStream()));
		// write = new PrintWriter(s.getOutputStream(), true);
		// write.println("q");
		// write.flush();
		//
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//

		Thread thr = new Thread(mThread);
		thr.start();

		mHandler = new Handler();

		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
		LoginButton btn = (LoginButton) findViewById(R.id.facebookbutton);
		btn.init(this, 20, Utility.mFacebook, permissions);

		dataset.addSeries(timeSeries);
		view = ChartFactory.getTimeChartView(this, dataset, renderer, "Test");
		// ll.addView(view);

		view.setDrawingCacheEnabled(true);
		view.refreshDrawableState();
		view.repaint();

	}

	private void connectToServer() {

//		try {

			try {
				s = new Socket("158.130.102.79",8080);
				SharedPreferences preferences = getSharedPreferences("FANCY", 0);
				String ipAddr = preferences.getString("IP", "");
				String port = preferences.getString("PORT", "");
//				SocketAddress remoteAddr = new InetSocketAddress(ipAddr,
//						Integer.valueOf(port));
//				SocketAddress remoteAddr = new InetSocketAddress("158.130.63.12",
//						12345);
//				s.connect(remoteAddr, 10000);
			} catch (Exception e) {
				System.err.println("Can not connect to server");
				Toast.makeText(getApplicationContext(),
						"Can not connect to server", Toast.LENGTH_SHORT).show();
				removeDialog(this.PLAN_NOT_FOUND);
				showDialog(this.PLAN_NOT_FOUND);
				return;
			}
			System.err.println("we are here continuing");
			if (s.isConnected()) {
				
				connected = true;
				System.err.println("We are connected");
			} else {
				connected = false;
				System.err.println("We fuck Can not connect to server");
			}

			
			try {
				
				write = new PrintWriter(s.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			write.println("q");
			write.flush();
			
			try {
				in = s.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(true){
			byte[] tmp=new byte[1024];
			try {
				while(in.available()>0){
			        int i=in.read(tmp, 0, 1024);
			        System.err.println(new String(tmp, 0, i));
			     }
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
			
			
			
//			try {
//				read = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			try {
//				while((receive = read.readLine())!=null){
//					System.err.println(receive);
//				}
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.err.println("we are leaving");

//		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	Runnable done = new Runnable() {
		public void run() {
			/*
			 * Random rand = new Random(); int a = rand.nextInt(100);
			 * timeSeries.add(new Date(), a); view.repaint();
			 */
			System.err.println("inside run");
//		    byte[] tmp=new byte[1024];
			
//			try {
//				while(in.available()>0){
//			        int i=in.read(tmp, 0, 1024);
//			        System.out.print(new String(tmp, 0, i));
//			     }
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
			
			
//			try {
//				receive = read.readLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.err.println(receive);
			if (receive != null) {
				// message.setText(receive);
				System.err.println(receive);
				Messages m = new Messages();
				int msgType = m.AnalyseMgetChargetCharsg(receive);
				switch (msgType) {
				case 0:
					return;
				case 1:
				case 2:
					SharedPreferences preferences = getSharedPreferences(
							"FANCY", 0);
					Editor editor = preferences.edit();
					editor.putInt("current_state", m.Getrun());
					editor.commit();
					if (m.Getrun() == 0)
						isRunning = false;
					else
						isRunning = true;
					updateControl();
					break;
				case 3:
					int settingId = m.Getid();
					double temperature = m.Gettem();
					int totalRunTime = m.Gettime();
					int fanSpeed = m.Getfan();
					setState(m.Getrun(), settingId, temperature, totalRunTime,
							fanSpeed);
					break;
				case 4:
					temperature = m.Getcurtem();
					int currentRunTime = m.Getcurtime();
					int exist = m.Getexist();
					statusUpdate(temperature, currentRunTime,
							(exist == 0) ? false : true);
					break;
				default:
					break;
				}
				Pattern p = Pattern.compile("\\s(\\d+\\.*\\d*)\\s");
				Matcher matcher = p.matcher(receive);
				String result = null;
				if (matcher.find()) {
					result = matcher.group(1);
					System.out.println(result);
				}
				if (result != null) {
					timeSeries.add(new Date(), Double.valueOf(result));
					view.repaint();
				}
			}

		}
	};

	Runnable mThread = new Runnable() {
		public void run() {
			// just sleep for 30 seconds.
			while (true) {
				try {
					
					Thread.sleep(500L);
					// Thread.sleep(30);
					runOnUiThread(done);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// switch (requestCode) {
	// case AUTHORIZE_ACTIVITY_RESULT_CODE: {
	// Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
	// break;
	// }
	//
	// }
	// }

	/*
	 * public void ipInput(){ AlertDialog.Builder builder; AlertDialog
	 * alertDialog;
	 * 
	 * Context mContext = getApplicationContext(); LinearLayout ll = new
	 * LinearLayout(this); ll.setOrientation(LinearLayout.VERTICAL); TextView
	 * text = new TextView(this);
	 * text.setText("Hello, this is a custom dialog!"); EditText textInput = new
	 * EditText(this); ll.addView(text); ll.addView(textInput); builder = new
	 * AlertDialog.Builder(mContext); builder.setView(ll); alertDialog =
	 * builder.create();
	 * 
	 * 
	 * alertDialog.show();
	 * 
	 * }
	 */
	/*
	 * callback for the photo upload
	 */
	public class PhotoUploadListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			mHandler.post(new Runnable() {

				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Photo successfully updated!", Toast.LENGTH_SHORT);
					toast.show();
				}

			});
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	public class FbAPIsAuthListener implements AuthListener {

		public void onAuthSucceed() {
			Toast successToast = Toast.makeText(getApplicationContext(),
					"Login Successfully! Uploading...", Toast.LENGTH_LONG);
			// logoutBtn.setVisibility(View.VISIBLE);
			successToast.show();
			successToast.setDuration(1000);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uploadPhoto();
		}

		public void onAuthFail(String error) {

			if (error.equals("Already Login")) {
				Toast successToast = Toast.makeText(getApplicationContext(),
						"Uploading...", Toast.LENGTH_LONG);
				successToast.show();
				successToast.setDuration(1000);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				uploadPhoto();
			} else {
				Toast.makeText(getApplicationContext(), error,
						Toast.LENGTH_LONG).show();
			}

		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */

	public class FbAPIsLogoutListener implements LogoutListener {

		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {
			Toast.makeText(getApplicationContext(), "Logout Successfully! ",
					Toast.LENGTH_LONG).show();
		}
	}

	public void uploadPhoto() {
		Bitmap img = view.getDrawingCache(true); // gets NULL !!!
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] bMapArray = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle params = new Bundle();
		params.putByteArray("photo", bMapArray);
		params.putString("caption", "CIS542 App photo upload");
		Utility.mAsyncRunner.request("me/photos", params, "POST",
				new PhotoUploadListener(), null);
	}

	// ------------------voice control

	private void speakerInit() {
		ImageView voicecontrolview = (ImageView) findViewById(R.id.voicecontrol);

		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			voicecontrolview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			voicecontrolview.setAlpha(50);
			voicecontrolview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(),
							"Voice Control Not Supported", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}

	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case 100:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// this is the message to display
			builder.setMessage("Sorry! No plan is found!");
			// this is the button to display
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						// this is the method to call when the button is clicked
						public void onClick(DialogInterface dialog, int id) {
							// this will hide the dialog

							dialog.cancel();
						}
					});
			return builder.create();
		case 101:
			builder = new AlertDialog.Builder(this);
			// this is the message to display
			String me = "Do you want to start plan " + voicename + "?";
			builder.setMessage(me);
			// this is the button to display
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						// this is the method to call when the button is clicked
						public void onClick(DialogInterface dialog, int id) {
							// this will hide the dialog

							SharedPreferences preferences = getSharedPreferences(
									"FANCY", 0);
							SharedPreferences.Editor editor = preferences
									.edit();
							editor.putInt("current_mode_id", voiceid);
							editor.putString("current_mode_name", voicename);
							editor.commit();

							dialog.cancel();
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						// this is the method to call when the button is clicked
						public void onClick(DialogInterface dialog, int id) {
							// this will hide the dialog

							dialog.cancel();
						}
					});
			return builder.create();
		case INET_ADDRESS:
			System.err.println("fuck we are in the diaglog");
			
			
		
			
			
			
			
			
			
			
			
			
//			builder = new AlertDialog.Builder(this);
//			// this is the message to display
//			builder.setMessage("Arduino's IP Adress:Port");
//			final EditText address = new EditText(getApplicationContext());
//			builder.setView(address);
//			// this is the button to display
//			builder.setPositiveButton("OK",
//					new DialogInterface.OnClickListener() {
//						// this is the method to call when the button is clicked
//						public void onClick(DialogInterface dialog, int id) {
//							// this will hide the dialog
//							String ipPort = address.getText().toString();
//							int index = ipPort.indexOf(":");
//							String ip = ipPort.substring(0, index);
//							String port = ipPort.substring(index + 1);
//							SharedPreferences preferences = getSharedPreferences(
//									"FANCY", 0);
//							Editor editor = preferences.edit();
//							editor.putString("IP", ip);
//							editor.putString("PORT", port);
//							editor.commit();
//							dialog.cancel();
//							connectToServer();
//						}
//					});
//			builder.setNegativeButton("Quit",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							finish();
//						}
//					});
//			return builder.create();
		default:
			return null;
		}
		/*
		 * if (id == 100) { AlertDialog.Builder builder = new
		 * AlertDialog.Builder(this); // this is the message to display
		 * builder.setMessage("Sorry! No plan is found!"); // this is the button
		 * to display builder.setPositiveButton("OK", new
		 * DialogInterface.OnClickListener() { // this is the method to call
		 * when the button is clicked public void onClick(DialogInterface
		 * dialog, int id) { // this will hide the dialog
		 * 
		 * dialog.cancel(); } }); return builder.create(); } else if (id == 101)
		 * { AlertDialog.Builder builder = new AlertDialog.Builder(this); //
		 * this is the message to display String me =
		 * "Do you want to start plan " + voicename + "?";
		 * builder.setMessage(me); // this is the button to display
		 * builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		 * { // this is the method to call when the button is clicked public
		 * void onClick(DialogInterface dialog, int id) { // this will hide the
		 * dialog
		 * 
		 * SharedPreferences preferences = getSharedPreferences( "FANCY", 0);
		 * SharedPreferences.Editor editor = preferences .edit();
		 * editor.putInt("current_mode_id", voiceid);
		 * editor.putString("current_mode_name", voicename); editor.commit();
		 * 
		 * dialog.cancel(); } }); builder.setNegativeButton("Cancel", new
		 * DialogInterface.OnClickListener() { // this is the method to call
		 * when the button is clicked public void onClick(DialogInterface
		 * dialog, int id) { // this will hide the dialog
		 * 
		 * dialog.cancel(); } }); return builder.create(); }
		 */
	}

	// ====NOTIFICATION===================

	private static final int NOTICE_ID = 191713;
	private TextToSpeech texttoread = null;

	private void readingInit() {
		texttoread = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int supported = texttoread.setLanguage(Locale.US);
					if ((supported != TextToSpeech.LANG_AVAILABLE)
							&& (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
					}
				}
			}
		});
	}

	public void read(String text) {
		texttoread.speak("Hello! " + text, TextToSpeech.QUEUE_FLUSH, null);
	}

	private void alarm() {
		SharedPreferences preference = getSharedPreferences("FANCY", 0);
		String planName = preference.getString("current_mode_name", "manual");
		String noticeString = "Your " + planName + " is ready!";
		setupNotification(noticeString);
		read(noticeString);
	}

	private void cancelNotification() {
		final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(NOTICE_ID);
	}

	private void setupNotification(String noticeString) {
		Notification notification = new Notification(R.drawable.ic_launcher,
				noticeString, System.currentTimeMillis());
		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
				this, FancyActivity.class), 0);

		notification.setLatestEventInfo(this, "Fancy Report", noticeString,
				intent);

		notification.defaults |= Notification.DEFAULT_SOUND;

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		long[] vibrate = { 100, 200, 100, 300 };
		notification.vibrate = vibrate;
		// <uses-permission android:name="android.permission.VIBRATE" />
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(NOTICE_ID, notification);
	}
	// =======================

}
