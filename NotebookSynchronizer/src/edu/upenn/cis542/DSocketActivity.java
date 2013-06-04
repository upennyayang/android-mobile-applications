package edu.upenn.cis542;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DSocketActivity extends Activity {
	    private String serverIpAddress = "158.130.62.251"; //"209.20.78.93";
	    private int serverPort = 3001;
	    private boolean connected = false;
	    private Handler handler = new Handler();
// move cThread out here for scope purposes


	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	    }

	    public void Connect () {
	            if (!connected) {
	                if (!serverIpAddress.equals("")) {
	                    Thread cThread = new Thread(new ClientThread());
	                    cThread.start();
	                }
	            }
	    }

	    public class ClientThread implements Runnable {

	        public void run() {
	            try {
	                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
	                Log.d("ClientActivity", "C: Connecting...");
	                Socket socket = new Socket(serverAddr, serverPort); //ServerActivity.SERVERPORT);
	                connected = true;
	                while (connected) {
	                    try {
	                        Log.d("ClientActivity", "C: Sending command.");
	                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
	                                    .getOutputStream())), true);
	                            // where you issue the commands
	                            out.println("Hey Server!");
	                            Log.d("ClientActivity", "C: Sent.");
	                    } catch (Exception e) {
	                        Log.e("ClientActivity", "S: Error", e);
	                    }
	                }
	                socket.close();
	                Log.d("ClientActivity", "C: Closed.");
	            } catch (Exception e) {
	                Log.e("ClientActivity", "C: Error", e);
	                connected = false;
	            }
	        }
	    }

	    public void onButtonClick(View v){
	    	 Toast.makeText(getApplicationContext(),
	             		"Start Connecting.. ", Toast.LENGTH_LONG).show();
	    	DSocketActivity NMS = new DSocketActivity();
	        NMS.Connect();

	    }


}
