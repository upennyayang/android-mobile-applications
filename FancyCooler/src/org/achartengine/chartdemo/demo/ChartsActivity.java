package org.achartengine.chartdemo.demo;

import android.app.Activity;

import android.app.ProgressDialog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import org.achartengine.chartdemo.demo.SessionEvents.AuthListener;
import org.achartengine.chartdemo.demo.SessionEvents.LogoutListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ChartsActivity extends Activity {
	public static final String APP_ID = "194553427322886";
	String[] permissions = { "offline_access", "publish_stream", "user_photos",
			"publish_checkins", "photo_upload" };

	ProgressDialog dialog;
	private Handler mHandler;
	private LinearLayout ll;
	private LinearLayout newBtnPanel;
	private static Random random = new Random();

	private static TimeSeries timeSeries;
	private static XYMultipleSeriesDataset dataset;
	private static XYMultipleSeriesRenderer renderer;
	private static XYSeriesRenderer rendererSeries;
	private static GraphicalView view;
	// private static Thread mThread;
	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;
	LoginButton loginBtn;
	LogoutButon logoutBtn;
	EditText cmdTxView;
	TextView message;
	// --------------
	String receive;
	BufferedReader read;
	PrintWriter write;
	Socket s;
	TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		timeSeries = new TimeSeries("Random");
		// mThread = new Thread() {
		// public void run() {
		// while (true) {
		// try {
		// Thread.sleep(2000L);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// timeSeries.add(new Date(), random.nextInt(10));
		// view.repaint();
		// }
		// }
		// };

		// mThread.start();
		try {
			s = new Socket("158.130.63.16", 12345);
			read = new BufferedReader(new InputStreamReader(s.getInputStream()));
			write = new PrintWriter(s.getOutputStream(), true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread thr = new Thread(mThread);
		thr.start();

		mHandler = new Handler();

		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		loginBtn = new LoginButton(getApplicationContext());
		loginBtn.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook,
				permissions);

		
		LinearLayout msgLayout = new LinearLayout(this);
		msgLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView txv = new TextView(getApplicationContext());
		txv.setText("Arduino:");
		message = new TextView(getApplicationContext());
		message.setText("");
		msgLayout.addView(txv);
		msgLayout.addView(message);
		
		cmdTxView = new EditText(getApplicationContext());
		LayoutParams params = new LinearLayout.LayoutParams(
			    LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
			params.weight = 1.0f;
		cmdTxView.setLayoutParams(params);
		cmdTxView.setText("");
		
		Button sendCmd = new Button(getApplicationContext());
		sendCmd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	String str=cmdTxView.getText().toString();
		        write.println(str);
		        write.flush();  
		        cmdTxView.setText("");
            }
        });			    	
		sendCmd.setText("Send Command");
		
		LinearLayout ctlLayout = new LinearLayout(this);
		ctlLayout.setOrientation(LinearLayout.HORIZONTAL);
		ctlLayout.addView(cmdTxView);
		ctlLayout.addView(sendCmd);

		// layout
		ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(msgLayout);
		ll.addView(ctlLayout);
		ll.addView(loginBtn);


		dataset.addSeries(timeSeries);
		view = ChartFactory.getTimeChartView(this, dataset, renderer, "Test");
		ll.addView(view);

		view.setDrawingCacheEnabled(true);
		view.refreshDrawableState();
		view.repaint();

		setContentView(ll);

	}

	Runnable done = new Runnable() {
		public void run() {
			try {
				receive = read.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println(receive);
			if (receive != null) {
				message.setText(receive);

				Pattern p = Pattern.compile("\\s(\\d+\\.*\\d*)\\s");
				Matcher matcher = p.matcher(receive);
				String result = null;
				if (matcher.find()) {
					result = matcher.group(1);
					System.out.println(result);
				}
				// timeSeries.add(new Date(), random.nextInt(10));
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
					Thread.sleep(2000L);
//					Thread.sleep(30);
					runOnUiThread(done);

				} catch (InterruptedException e) {
//					 TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			break;
		}

		}
	}

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
}
