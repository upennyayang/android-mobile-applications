package edu.upenn.cis.cis542;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MenuActivity extends Activity {
	private static final int ACTIVITY_AddPage = 2;
	// private int current_mode;
	private LinkedList<String> namelist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		// current_mode = getIntent().getIntExtra("id", 0);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			initialization();
		}
	}

	public void initialization() {
		ListView listview = (ListView) findViewById(R.id.listView1);
		SharedPreferences prefTable = this.getSharedPreferences("FANCY", 0);

		// get data from database
		namelist = new LinkedList<String>();
		String currentname = prefTable.getString("current_mode_name", "none")
				.trim();
		int currentid = prefTable.getInt("current_mode_id", 0);

		PlanDB db = new PlanDB(this);
		Cursor cursor = db.getAllPlan();
		if (cursor != null) {
			startManagingCursor(cursor);
			while (cursor.moveToNext()) {
				String name = cursor.getString(1).trim();
				namelist.add(name);
			}
			cursor.close();
		}
		db.close();

		ArrayList<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
		for (String name : namelist) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (name.equals(currentname))
				map.put("CHECK", R.drawable.accept);
			else
				map.put("CHECK", 0);
			map.put("NAME", name);
			contents.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, contents,
				R.layout.listitem, new String[] { "CHECK", "NAME" }, new int[] {
						R.id.currentCheck, R.id.title });

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				pickMode(arg2);
				initialization();
			}
		});
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// pop up an option list for managing and deletion
				showOption(arg2);
				initialization();
				return false;
			}
		});
	}

	protected void pickMode(int curItem) {
		String pickedName = namelist.get(curItem);
		// stop any current mode.

		// switch to the selected mode.
		SharedPreferences prefTable = this.getSharedPreferences("FANCY", 0);
		PlanDB plandb = new PlanDB(getApplicationContext());
		Integer id = plandb.getIdByName(pickedName);
		plandb.close();
		if (id != null) {
			SharedPreferences.Editor prefTableEditor = prefTable.edit();
			prefTableEditor.putInt("current_mode_id", id);
			prefTableEditor.putString("current_mode_name", pickedName);
			prefTableEditor.commit();
			// Toast.makeText(getApplicationContext(), pickedName + " picked",
			// Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Operation invalid",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void showOption(int curItem) {
		// LinearLayout listOfOption = (LinearLayout)
		// findViewById(R.id.optionlinearlayout);
		String name = namelist.get(curItem);
		PlanDB db = new PlanDB(this);
		Integer id = db.getIdByName(name);
		db.close();
		if (id != null) {
			OptionlistDialog dialog = new OptionlistDialog(this, id);
			dialog.show();
		} else {
			Toast.makeText(this, "Mannual Mode", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this, ProjectActivity.class);
			i.putExtra("id", 0);
			startActivityForResult(i, ACTIVITY_AddPage);
		}
	}

	public void onaddMode(View view) {
		Intent i = new Intent(this, ProjectActivity.class);
		i.putExtra("id", -1);
		startActivityForResult(i, ACTIVITY_AddPage);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case ACTIVITY_AddPage:

			break;
		default:
			break;
		}

	}

	public void onBackToMain(View view) {
		onBackPressed();
	}

	
	public void onBackPressed() {
		super.onBackPressed();
	}
}
