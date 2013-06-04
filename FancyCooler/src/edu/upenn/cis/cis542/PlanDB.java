package edu.upenn.cis.cis542;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlanDB extends SQLiteOpenHelper{
	private SQLiteDatabase db; 
	private static final String TABLE_CREATE = "create table plans (" +
			"id integer primary key, name text not null, temperature integer not null, time integer not null, fan integer not null);";
	public PlanDB(Context context) {
		super(context, "PlanDB", null, 1);
		db = getWritableDatabase(); 
		if  (db == null) Log.v("PlanDB", "db is null!");

		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase d) {
		// TODO Auto-generated method stub
		d.execSQL(TABLE_CREATE);
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase d, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		super.onOpen(d);
		db = getWritableDatabase();
	}	
	public void init(){
		String stmt="select * from plans";
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			String stmt1="insert into plans ( id, name, temperature, time, fan) values (0, 'Manual', 50, 1500, 50)";
			db.execSQL(stmt1);
		}
		
	}
	public void planDelete(int num){
		String stmt = "delete from plans where id = "+num;
		   db.execSQL(stmt);
		
	}
	public void planUpdateName(int num, String name){
		String stmt = "update plans set name ='"+name+"' where id = "+num;
		   db.execSQL(stmt);
	}
	public void planUpdateTemp(int num, int temp){
		String stmt = "update plans set temperature ="+temp+" where id = "+num;
		   db.execSQL(stmt);
	}
	public void planUpdateTime(int num, int time){
		String stmt = "update plans set time ="+time+" where id = "+num;
		   db.execSQL(stmt);
	}
	public void planUpdateFan(int num, int fan){
		String stmt = "update plans set fan ="+fan+" where id = "+num;
		   db.execSQL(stmt);
	}
	public void insertPlan(int id, String name, int temp, int time, int fan){
		String stmt = "insert into plans ( id, name, temperature, time, fan) values ("+id+",'"+name+"',"+temp+","+time+","+fan+")";
		   db.execSQL(stmt);
	}
	public Cursor selectPlan(int num){
		String stmt = "select * from plans where id="+num;
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c;
		
	}
	public Cursor getAllPlan(){
		String stmt = "select * from plans";
		return db.rawQuery(stmt, null);
		
	}
	public Integer getLastId(){
		String stmt = "select id from plans ORDER BY id DESC";
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getInt(0);
	
	}
	public Integer getIdByName(String name){
		String stmt = "select * from plans where name ='"+name+"'";
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getInt(0);
	}
	public String getNameById(int id){
		String stmt = "select * from plans where id ="+id;
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getString(1);
	}
	public Integer getTemperatureById(int id){
		String stmt = "select * from plans where id ="+id;
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getInt(2);
	}
	public Integer getTimeById(int id){
		String stmt = "select * from plans where id ="+id;
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getInt(3);
	}
	public Integer getFanById(int id){
		String stmt = "select * from plans where id ="+id;
		Cursor c= db.rawQuery(stmt, null);
		if(c.getCount()==0){
			return null;
		}
		c.moveToFirst();
		return c.getInt(4);
	}
}
