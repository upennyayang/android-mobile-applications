package edu.upenn.cis542;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TypingGameActivity extends Activity {
    /** Called when the activity is first created. */    
	private String name;
	public static SharedPreferences timeMap;
	public static SharedPreferences nameMap;
@Override
protected void onPause() {
    pause_begin_time=System.currentTimeMillis();    
	// TODO Auto-generated method stub
	super.onPause();
}

@Override
protected void onRestart() {
	pause_end_time=System.currentTimeMillis();    
    //time1=pause_end_time- pause_begin_time;
	// TODO Auto-generated method stub
	super.onRestart();
}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        TextView sent=(TextView)findViewById(R.id.sentences);    			  
	    sent.setText(sentCollection[sentId]);
	    //Get the login name from login activity!
	    Intent intent=getIntent();
        name=intent.getStringExtra("NAME");
        Toast.makeText(getApplicationContext(),
             		"Let's go, "+name+" !", Toast.LENGTH_LONG).show();       
	    timeMap=getSharedPreferences("time", MODE_PRIVATE);
	    nameMap=getSharedPreferences("name", MODE_PRIVATE);
        showDialog(READY_DIALOG);
        
    }
    
   
    
    public void onClickQuit(View v){    	
    	finish();
    }
    
    public void onClickSubmit(View v){    	
    	EditText typed=(EditText)findViewById(R.id.typingArea);    	
    	String typedText=typed.getText().toString();
    	String rightText=sentCollection[sentId];    	
       	//String rightText=getResources().getString(R.string.sentence);
       	if(typedText.equals(rightText)){        		
       		removeDialog(CORRECT_DIALOG);
       		showDialog(CORRECT_DIALOG);
       	}
       	else{      		
       		removeDialog(INCORRECT_DIALOG);
       		showDialog(INCORRECT_DIALOG);
       		Toast.makeText(getApplicationContext(),
       		R.string.wrong_msg, Toast.LENGTH_LONG).show();
       	}   	   	
    }
    
    protected Dialog onCreateDialog(int id){
    	if (id==READY_DIALOG){    		
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage(R.string.ready);
    		builder.setPositiveButton(R.string.YES, 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){
    				time1=System.currentTimeMillis();
    				time2=-1;
    				EditText typed=(EditText)findViewById(R.id.typingArea);    	
    	       		typed.setText("");
    				//Toast.makeText(getApplicationContext(),Integer.toString((int)time1), Toast.LENGTH_LONG).show();
    				dialog.cancel();
    			}    			
    		});    
    		return builder.create();
    	}
    	else if (id==CORRECT_DIALOG){    
    		time2=System.currentTimeMillis();
    		//Toast.makeText(getApplicationContext(),Integer.toString((int)time2), Toast.LENGTH_LONG).show();
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);   
    		double temp_diff= (double) (time2-time1-(pause_end_time-pause_begin_time))/1000;
    		double diff=(double) (Math.round(temp_diff)/1.0);    		
       		String timeTaken=Double.toString(diff);
       		//Put Records in the Database API
       		SharedPreferences.Editor timeEditor=timeMap.edit();
       		SharedPreferences.Editor nameEditor=nameMap.edit();
       		String containsName=nameMap.getString(sentCollection[sentId], null);
            String shortestTimeStr=null;
            double shortestTime=10000000000.0;
            String bestPlayer="No One";
       		//If not contains this player, write into database
       		if(containsName==null){
       		    shortestTime=diff;
       		    bestPlayer=name;
       			timeEditor.putString(sentCollection[sentId],Double.toString(diff));
       			timeEditor.commit();
       		    nameEditor.putString(sentCollection[sentId], name);       		    
       		    nameEditor.commit();
       		    
       		}       	     		
       		//If contains just update the quickest time
       		else{
       			
       			shortestTimeStr=timeMap.getString(sentCollection[sentId], null);    
       			shortestTime=Double.parseDouble(shortestTimeStr);
        		if(diff<shortestTime){
        			Toast.makeText(getApplicationContext(),
        	       				"Congradulations!New Record!", Toast.LENGTH_LONG).show();       		
           			shortestTime=diff;  
           			bestPlayer=name;
        			timeEditor.putString(sentCollection[sentId], String.valueOf(shortestTime));
           			timeEditor.commit();
           			nameEditor.putString(sentCollection[sentId], bestPlayer);
           			nameEditor.commit();
           		}       			
       		}
       		//Get the best player for this specific sentence
       		bestPlayer=nameMap.getString(sentCollection[sentId],null);
       		builder.setMessage("That's right! "+"That took you "+timeTaken+"seconds!"
       		+" The top score is "+shortestTime+" held by "+bestPlayer+ ".  Try the same or different sentences?");    
       		builder.setPositiveButton("Same", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){      				
    				dialog.cancel();   		
    				showDialog(READY_DIALOG);   	       		
    			}    			
    		});
    		
    		builder.setNegativeButton("Different", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){  
    				sentId=sentId+1;
    				TextView sent=(TextView)findViewById(R.id.sentences);    			  
    			    sent.setText(sentCollection[sentId%10]);
    				dialog.cancel();   		
    				showDialog(READY_DIALOG);   	       		
    			}    			
    		});
    		return builder.create();
    	}
    	else if (id==INCORRECT_DIALOG){
    		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    		builder.setMessage("No, it's not correct!");    		
    		builder.setPositiveButton("OK", 
    				new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){      				
    				dialog.cancel();
    				showDialog(READY_DIALOG);
    			}    			
    		});
    		return builder.create();
    	}
		else return null;
    }
    
   
    private static final int READY_DIALOG=1; 
    private static final int CORRECT_DIALOG=2; 
    private static final int INCORRECT_DIALOG=3; 
    private long time1=-1;
    private long time2=-1;
    private long pause_begin_time=-1;
    private long pause_end_time=-1;
    public int sentId=0;
    public static String[] sentCollection={
    		"CIS542 is an interesting course!","Professor Chris is nice!", "TAs are awesome!",
    		"Arduino is cute!","Android is interesting!", "So I strongly recommend CIS542!",		
    		"In our project, we have enhanced our back-end and front-end.", "We use a lot of sensors in our projects.",
    		"We can even post graph on Facebook.","If you want to go fast, go alone. If you want to go far, go together."};

    
    
}