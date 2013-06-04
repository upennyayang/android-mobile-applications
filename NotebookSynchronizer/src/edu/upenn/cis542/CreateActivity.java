package edu.upenn.cis542;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;


public class CreateActivity extends Activity {
    /** Called when the activity is first created. */    
	static private String name;	
	
@Override
protected void onPause() {
    
	// TODO Auto-generated method stub
	super.onPause();
}

@Override
protected void onRestart() {	
	// TODO Auto-generated method stub
	super.onRestart();	
}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);                 
        
        EditText filenameArea=(EditText)findViewById(R.id.filenameArea);           
    	filenameArea.setText("");    	
        EditText tagArea=(EditText)findViewById(R.id.contentArea);      
        tagArea.setText("");    	
        EditText contentArea=(EditText)findViewById(R.id.contentArea);      
        contentArea.setText("");    	       
       
       
        
    }
    
   
    
    public void onClickQuit(View v){    	
    	finish();
    }
    
    public void onClickSubmit(View v){    	    	
    	removeDialog(SUBMIT_DIALOG);
   		showDialog(SUBMIT_DIALOG);    	   	  	      
    }
   
    
    
    
    protected Dialog onCreateDialog(int id){
    	if (id==SUBMIT_DIALOG){    		
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage("Create a New File?");
    		builder.setPositiveButton("OK", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){    			
    		
    				//Change the file name
    				SharedPreferences filenameMap = getSharedPreferences("FILENAME",MODE_PRIVATE);	
    				SharedPreferences tagMap = getSharedPreferences("TAG",MODE_PRIVATE);	
    				SharedPreferences contentMap = getSharedPreferences("CONTENT",MODE_PRIVATE);	
    				
    				SharedPreferences.Editor filenameEditor=filenameMap.edit();    
    				SharedPreferences.Editor tagEditor=tagMap.edit(); 
    				SharedPreferences.Editor contentEditor=contentMap.edit(); 
    				
    				EditText filenameArea=(EditText)findViewById(R.id.filenameArea);  
    				EditText tagArea=(EditText)findViewById(R.id.tagArea);
    				EditText contentArea=(EditText)findViewById(R.id.contentArea);
    				
    				
    				int tempFileId= Integer.parseInt(MenuActivity.fileId);
    				String fileid= String.valueOf(tempFileId+1);    				
    				String filename=filenameArea.getText().toString();
    				
    			    /*
    				List<String>planets= new ArrayList<String>();
    		    	for(int i=0; i<files.length;i++){
    		    		planets.add(files[i]);
    		    	}
    		    	*/
    				/*
    				String[] spinnerStr=getResources().getStringArray(R.id.spinner);
    			    List<String>planets= new ArrayList<String>();
    			    for (int i=0; i<spinnerStr.length; i++){
    			    	planets.add(spinnerStr[i]);
    			    }
    			    planets.add(fileid);
    			    
    			    Spinner s = (Spinner) findViewById(R.id.spinner);  
    			    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    			            android.R.layout.simple_spinner_item, planets);
    			    s.setAdapter(adapter);      
    			    */
    			    
    			    
    				MenuActivity.fileList.add(fileid);
    		       	filenameEditor.putString(fileid, filename);
    		      	filenameEditor.commit();  
    		     	tagEditor.putString(fileid, tagArea.getText().toString());
    		      	tagEditor.commit();  
    		     	contentEditor.putString(fileid, contentArea.getText().toString());
    		      	contentEditor.commit();  
    		        dialog.cancel();
    			}    			
    		});    
    		return builder.create();
    	}    	
    	else return null;

    }
    
    	
    private static final int SUBMIT_DIALOG=4; 

  

    
    public static String[] sentCollection={
    		"CIS542 is an interesting course!","Professor Chris is nice!", "TAs are awesome!",
    		"Arduino is cute!","Android is interesting!", "So I strongly recommend CIS542!",		
    		"In our project, we have enhanced our back-end and front-end.", "We use a lot of sensors in our projects.",
    		"We can even post graph on Facebook.","If you want to go fast, go alone. If you want to go far, go together."};

    
    
}