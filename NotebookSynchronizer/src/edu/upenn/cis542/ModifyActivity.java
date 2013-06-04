package edu.upenn.cis542;

import java.util.ArrayList;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyActivity extends Activity {
    /** Called when the activity is first created. */    
	private String name;
	private String fileid;
	public static SharedPreferences filenameMap;
	public static SharedPreferences tagMap;
	public static SharedPreferences contentMap;
	
@Override
protected void onPause() {
    
	// TODO Auto-generated method stub
	super.onPause();
}

@Override
protected void onRestart() {	
	// TODO Auto-generated method stub
	super.onRestart();	
	
   /*
	Map<String, ?> allContent = contentMap.getAll();
	Object[] array = allContent.keySet().toArray();
	int size = array.length;
   */	
	
}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify);      
        //Get the selected Filename
	    Intent intent=getIntent();
	    fileid=intent.getStringExtra("FILEID");        
	    Toast.makeText(getApplicationContext(),
         		"The fileid is, "+fileid+" !", Toast.LENGTH_LONG).show();  
	    
	    
	    //Show the content of this file
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
	    
	    
	    
	    
	    String filename=filenameMap.getString(fileid,  null);
   		String tag=tagMap.getString(fileid,  null);
        String content=contentMap.getString(fileid,  null);               
        
        //debug
        Toast.makeText(getApplicationContext(),
         		"The filename is, "+filename+" !", Toast.LENGTH_LONG).show();  
        Toast.makeText(getApplicationContext(),
         		"The tag is, "+tag+" !", Toast.LENGTH_LONG).show();  
        Toast.makeText(getApplicationContext(),
         		"The content is, "+content+" !", Toast.LENGTH_LONG).show();  
        
        
        EditText filenameArea=(EditText)findViewById(R.id.filenameArea);           
    	filenameArea.setText(filename);    	
        EditText tagArea=(EditText)findViewById(R.id.tagArea);      
        tagArea.setText(tag);    	
        EditText contentArea=(EditText)findViewById(R.id.contentArea);      
        contentArea.setText(content);    	       
       
        
    }
    
   
    
    public void onClickQuit(View v){    	
    	finish();
    }
    
    public void onClickModifyTitle(View v){    	    	
    	removeDialog(MODIFY_TITLE_DIALOG);
   		showDialog(MODIFY_TITLE_DIALOG);    	   	  	      
    }
    
	public void onClickModifyTag(View v){    	
		removeDialog(MODIFY_TAG_DIALOG);
   		showDialog(MODIFY_TAG_DIALOG);    	   	 	
   	}      

	public void onClickModifyContent(View v){    	
		removeDialog(MODIFY_CONTENT_DIALOG);
   		showDialog(MODIFY_CONTENT_DIALOG);    	   	 	
   	}      
    
    
    
    protected Dialog onCreateDialog(int id){
    	if (id==MODIFY_TITLE_DIALOG){    		
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure to change the title name?");
    		builder.setPositiveButton("Yes", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){    			
    		
    				//Change the file name
    				SharedPreferences filenameMap = getSharedPreferences("FILENAME",MODE_PRIVATE);	
    				SharedPreferences.Editor filenameEditor=filenameMap.edit();        				
    				EditText filenameArea=(EditText)findViewById(R.id.filenameArea);  
    				 Toast.makeText(getApplicationContext(),
    			         		"The filename "+filenameArea.getText().toString()+" !", Toast.LENGTH_LONG).show();  
    				filenameEditor.remove(fileid);
    			  	filenameEditor.commit(); 
    		       	filenameEditor.putString(fileid, filenameArea.getText().toString());
    		      	filenameEditor.commit();       	
    		        dialog.cancel();
    			}    			
    		});    
    		return builder.create();
    	}
    	else if (id==MODIFY_TAG_DIALOG){    		
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure to change the tag name?");
    		builder.setPositiveButton("Yes", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){    			
    		
    				//Change the file name
    				
    				SharedPreferences tagMap = getSharedPreferences("TAG",MODE_PRIVATE);    				
    				SharedPreferences.Editor tagEditor=tagMap.edit(); 
    		       	EditText tagArea=(EditText)findViewById(R.id.tagArea);    		    
    		    	tagEditor.remove(fileid);
    			  	tagEditor.commit(); 
    		       	tagEditor.putString(fileid, tagArea.getText().toString());
    		      	tagEditor.commit();       	
    		        dialog.cancel();
    			}    			
    		});    
    		return builder.create();
    	}
    	else if (id==MODIFY_CONTENT_DIALOG){    		
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure to change the content?");
    		builder.setPositiveButton("Yes", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){    			
    		
    				//Change the file name    				
    				SharedPreferences contentMap = getSharedPreferences("CONTENT",MODE_PRIVATE);
    			    SharedPreferences.Editor contentEditor=contentMap.edit();   	   
    			  	EditText contentArea=(EditText)findViewById(R.id.contentArea);  
    		       	contentEditor.remove(fileid);
    			  	contentEditor.commit(); 
    		       	contentEditor.putString(fileid, contentArea.getText().toString());
    		      	contentEditor.commit();       	
    		        dialog.cancel();
    			}    			
    		});    
    		return builder.create();
    	}
    	else return null;

    }
    	
    
    private static final int MODIFY_TITLE_DIALOG=4; 
    private static final int MODIFY_TAG_DIALOG=5; 
    private static final int MODIFY_CONTENT_DIALOG=6; 
  
    public String fileId;    
    
}