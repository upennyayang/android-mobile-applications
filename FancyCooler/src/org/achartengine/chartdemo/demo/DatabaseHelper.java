package org.achartengine.chartdemo.demo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "CIS542";
    private SQLiteDatabase db; 
    // column names in the table
    private static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_TEMPETURE = "tempeture";
    public static final String KEY_SPEED = "speed";
    public static final String SETTING_TABLE_NAME = "setting";
    public static final String SETTING_TABLE_CREATE =
                "CREATE TABLE " + SETTING_TABLE_NAME+ " (" +
                KEY_ROWID + " integer primary key autoincrement," +
                KEY_NAME + " TEXT NOT NULL," +
                KEY_TIME + " DOUBLE NOT NULL," +
                KEY_TEMPETURE+ " DOUBLE NOT NULL," +
                KEY_SPEED + " INTEGER NOT NULL)"; 
                
	
	public DatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase(); 
        if  (db == null) Log.v("PersonDbAdapter", "db is null!");
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL(SETTING_TABLE_CREATE);
	}

	public void onOpen(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		super.onOpen(arg0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void insertSetting(UserSetting setting){
		String stmt = "INSERT INTO "+SETTING_TABLE_NAME+" ("+KEY_NAME+", "+KEY_TIME+", "+KEY_TEMPETURE+", "+KEY_SPEED+") " +
					  "VALUES ('"+setting.getKEY_NAME()+"',"+setting.getKEY_TIME()+","+setting.getKEY_TEMPETURE()+","+setting.getKEY_SPEED()+")";
		db.execSQL(stmt);
	}
	public Cursor querySettingInfo(String name){
		String[] columns = { KEY_TIME,KEY_TEMPETURE,KEY_SPEED};
		String[] selectionArgs = { name };

		return db.query(SETTING_TABLE_NAME, 	// table to select from
			columns,							// fields (columns) to select
			KEY_NAME + "=?", 					// WHERE clause
			selectionArgs,						// arguments to WHERE clause
			null,								// GROUP BY clause
			null,								// HAVING clause
			null								// ORDER BY clause
			);	
	}
	public Cursor queryAllNames(){
		String[] columns = { KEY_NAME};

		return db.query(SETTING_TABLE_NAME, 	// table to select from
			columns,							// fields (columns) to select
			null,								// WHERE clause
			null,								// arguments to WHERE clause
			null,								// GROUP BY clause
			null,								// HAVING clause
			null								// ORDER BY clause
			);	
	}

}
