package edu.upenn.cis.cis542;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionlistDialog extends AlertDialog {
	final private int curID;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.optionlist);

		ImageView settingsview = (ImageView) findViewById(R.id.settingsview);
		ImageView removeview = (ImageView) findViewById(R.id.removeview);
		TextView removeviewtext = (TextView) findViewById(R.id.removeviewtext);
		ImageView checkview = (ImageView) findViewById(R.id.checkview);
		ImageView cancelview = (ImageView) findViewById(R.id.cancelview);

		if (curID == 0) {
			removeview.setAlpha(50);
			removeview.setEnabled(false);
			removeviewtext.setTextColor(Color.GRAY);
			removeviewtext.setEnabled(false);
		}

		settingsview.setOnClickListener(new View.OnClickListener() {

			
			public void onClick(View v) {
				// managing mode
				Intent i = new Intent(getContext(), ProjectActivity.class);
				i.putExtra("id", curID);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				dismiss();
				getContext().startActivity(i);
			}
		});
		removeview.setOnClickListener(new View.OnClickListener() {

			
			public void onClick(View v) {
				// remove the selected mode
				PlanDB db = new PlanDB(getContext());
				String name = db.getNameById(0);
				db.planDelete(curID);
				db.close();
				// change the current mode to manual
				SharedPreferences pref = getContext().getSharedPreferences(
						"FANCY", 0);
				SharedPreferences.Editor prefTableEditor = pref.edit();
				prefTableEditor.putInt("current_mode_id", 0);
				prefTableEditor.putString("current_mode_name", name);
				prefTableEditor.commit();

				onBackPressed();
			}
		});
		checkview.setOnClickListener(new View.OnClickListener() {

			
			public void onClick(View v) {
				// choose the current mode
				
				PlanDB db = new PlanDB(getContext());
				String name = db.getNameById(curID);
				SharedPreferences preferences = getContext().getSharedPreferences("FANCY", 0);
				Editor edit = preferences.edit();
				edit.putString("current_mode_name", name);
				edit.putInt("current_mode_id", curID);
				edit.commit();
				db.close();
				onBackPressed();
			}
		});
		cancelview.setOnClickListener(new View.OnClickListener() {

			
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	public OptionlistDialog(Context context, int curID) {
		super(context, android.R.style.Theme_Dialog);
		this.curID = curID;
	}
}
