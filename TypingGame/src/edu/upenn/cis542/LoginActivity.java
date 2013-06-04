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

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //showDialog(READY_DIALOG);
    }  
    
    public void onClickQuit(View v){    	
    	finish();
    }
    
    public void onClickLogin(View v){    	
    	Intent i = new Intent(this,MenuActivity.class);    	
    	EditText typed=(EditText)findViewById(R.id.nameArea);    	
    	name=typed.getText().toString();
    	//Toast.makeText(getApplicationContext(),
     	//			typedText, Toast.LENGTH_LONG).show();     	
    	i.putExtra("NAME", name);
    	setResult(RESULT_OK, i);
        startActivityForResult(i, LoginActivity.ACTIVITY_MenuActivity);
        finish();
    }   	   	 
   
    
    public static final int ACTIVITY_MenuActivity=4;
    public String name;
   
}