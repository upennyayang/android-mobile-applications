package edu.upenn.cis542;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowActivity extends Activity {
	/** Called when the activity is first created. */
	// public static ArrayList<String> recordList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		showRecord();
		// showDialog(READY_DIALOG);

	}

	public void onClickQuit(View v) {
		finish();
	}

	public void showRecord() {
		TextView tv = ((TextView) findViewById(R.id.Text1));
		tv.setText("No Record!");
		String oneRecord = "";
		String viewContent = "";

		SharedPreferences timemap = this.getSharedPreferences("time",
				MODE_PRIVATE);
		SharedPreferences namemap = this.getSharedPreferences("name",
				MODE_PRIVATE);

		Map<String, ?> all = timemap.getAll();

		Object[] array = all.keySet().toArray();
		int size = array.length;

		for (int i = 0; i < size; i++) {
			String shortestTime = timemap.getString((String) array[i],
					"No One Challenged:)");
			String bestPlayer = namemap.getString((String) array[i],
					"No One Challenged:)");
			// if(bestPlayer!=null){
			oneRecord = (String) array[i] + "\nBest Player:" + bestPlayer
					+ "\nShortest Time:" + shortestTime;
			// }
			viewContent = viewContent + oneRecord + "\r\n\r\n";
		}

		tv.setText(viewContent);

	}

}