package edu.upenn.cis542;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MenuActivity extends Activity {
    private String name;
	/** Called when the activity is first created. */    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        Intent intent=getIntent();
        name=intent.getStringExtra("NAME");
        
        Toast.makeText(getApplicationContext(),
             		"Welcome, "+name+"!", Toast.LENGTH_LONG).show();          
	}
    
    public void onClickQuit(View v){    	
    	finish();
    }
    
    public void onClickPlay(View v){    	
    	Intent i = new Intent(this,TypingGameActivity.class);
    	
		i.putExtra("NAME",name);
		setResult(RESULT_OK, i);
    	startActivityForResult(i, MenuActivity.ACTIVITY_TypingGameActivity);    	
   	}  
    
    public void onClickShow(View v){    	
    	Intent i2 = new Intent(this,ShowActivity.class);
    	startActivityForResult(i2, MenuActivity.ACTIVITY_ShowActivity);    	
   	}  
   
    public static final int ACTIVITY_TypingGameActivity=5;
    public static final int ACTIVITY_ShowActivity=6;
   
}