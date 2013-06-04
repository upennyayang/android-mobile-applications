package edu.upenn.cis542;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MenuActivity extends Activity {
	private String fileid="No File";
    private String name;
	/** Called when the activity is first created. */   
    
   
    @Override
    protected void onPause() {
        
    	// TODO Auto-generated method stub
    	super.onPause();
    }

    @Override
    protected void onResume() {	
    	// TODO Auto-generated method stub
    	super.onResume();    	        
        
    	
        Spinner s = (Spinner) findViewById(R.id.spinner);       
    	//String[] files=fileList.toArray(new String[fileList.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, files);
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);      
        
        s.setOnItemSelectedListener(new MyOnItemSelectedListener());   
    	
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        Intent intent=getIntent();
        name=intent.getStringExtra("NAME");        
        Toast.makeText(getApplicationContext(),
             		"Welcome, "+name+"!", Toast.LENGTH_LONG).show();
        
        //Initial Five Files
        SharedPreferences filenameMap = this.getSharedPreferences("FILENAME",
				MODE_PRIVATE);
        SharedPreferences tagMap = this.getSharedPreferences("TAG",
				MODE_PRIVATE);
        SharedPreferences contentMap = this.getSharedPreferences("CONTENT",
				MODE_PRIVATE);
				      
        SharedPreferences.Editor filenameEditor=filenameMap.edit();    
        SharedPreferences.Editor tagEditor=tagMap.edit();   
       	SharedPreferences.Editor contentEditor=contentMap.edit();       		
       	    
        filenameEditor.putString("File 1", "Finance Filename");
        filenameEditor.commit();   
        filenameEditor.putString("File 2", "Health Filename");
        filenameEditor.commit();   
        filenameEditor.putString("File 3", "Economics Filename");
        filenameEditor.commit();   
        filenameEditor.putString("File 4", "Technology! Filename");
        filenameEditor.commit();   
        filenameEditor.putString("File 5", "Sports Filename");
       	filenameEditor.commit();       
       	
        tagEditor.putString("File 1", "Finance");
        tagEditor.commit();   
        tagEditor.putString("File 2", "Health");
        tagEditor.commit();   
        tagEditor.putString("File 3", "Economics");
        tagEditor.commit();   
        tagEditor.putString("File 4", "Technology!");
        tagEditor.commit();   
        tagEditor.putString("File 5", "Sports");
       	tagEditor.commit();        
       	
       	contentEditor.putString("File 1", "All we want is Dollars $$$$$");
       	contentEditor.commit();          	
      	contentEditor.putString("File 2", "I am the strongest person in the world");
      	contentEditor.commit();   
      	contentEditor.putString("File 3", "We are in an economic crisis with unemployment at an all time high");
      	contentEditor.commit();   
      	contentEditor.putString("File 4", "This is Wireless Technology ");
      	contentEditor.commit();   
      	contentEditor.putString("File 5", "I am Michael Jordan");
        contentEditor.commit();       	
          	
        //Combobox
        
        //List<String> fileList=new ArrayList<String>();
        fileList.add("File 1");
        fileList.add("File 2");
        fileList.add("File 3");
        fileList.add("File 4");
        fileList.add("File 5");        
        
       
        Spinner s = (Spinner) findViewById(R.id.spinner);        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, fileList);        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);      
        
        s.setOnItemSelectedListener(new MyOnItemSelectedListener());        
         
	}
    
	//OnItemSelected
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {	      
	    	fileid=parent.getItemAtPosition(pos).toString();
	    	Toast.makeText(parent.getContext(), "The planet is " +
		  	          fileid, Toast.LENGTH_LONG).show();	 
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
	
	public void onClickCreate(View v){    
		Intent i = new Intent(this,CreateActivity.class);  
    	startActivityForResult(i, MenuActivity.ACTIVITY_CreateActivity);    	
   	}     
	
    public void onClickModify(View v){    	
    	Intent i = new Intent(this,ModifyActivity.class);    	
		i.putExtra("FILEID",fileid);
		setResult(RESULT_OK, i);
    	startActivityForResult(i, MenuActivity.ACTIVITY_ModifyActivity);    	
   	}           
   
    
    public void onClickQuit(View v){    	
    	finish();
    }
   
    public static final String fileId="0"; 
    public static final int ACTIVITY_CreateActivity=7; 
    public static final int ACTIVITY_ModifyActivity=5;
    public static final List<String> fileList=new ArrayList<String>();
    public static final String files[] = {"File 1","File 2","File 3","File 4","File 5"};    
   
}