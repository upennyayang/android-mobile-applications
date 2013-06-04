package edu.upenn.cis.cis542;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ProjectActivity extends Activity {
    /** Called when the activity is first created. */
    private SeekBar seek1, seek2, seek3;
    private TextView temtv;
    private TextView timetv;
    private TextView fantv;
    private TextView nametv;
    private String name;
    private ImageView bsave, bsaveas, bapply, bquit;
    private int ori_id, ori_time, ori_temp, ori_fan;
    private String ori_name;
    private int time;
    private int fan;
    private int temp;
    private int id;
    private int min, sec;
    private CheckBox cb1, cb2;
    private boolean temselected, timeselected;
    private ImageView btemp,btemm, btimep, btimem;
    private PlanDB plandb;
    private boolean apply;
    private Intent back;
    private Bundle backb;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createdialog);
        nametv=(TextView)findViewById(R.id.name);
        seek1=(SeekBar)findViewById(R.id.seek1);
        seek1.setOnSeekBarChangeListener(seek1lis);
        temtv=(TextView)findViewById(R.id.temperature);
        seek2=(SeekBar)findViewById(R.id.seek2);
        seek3=(SeekBar)findViewById(R.id.seek3);
        seek2.setOnSeekBarChangeListener(seek2lis);
        seek3.setOnSeekBarChangeListener(seek3lis);
        timetv=(TextView)findViewById(R.id.time);
        fantv=(TextView)findViewById(R.id.fan);
        cb1=(CheckBox)findViewById(R.id.checkbox1);
        cb2=(CheckBox)findViewById(R.id.checkbox2);
        btemp=(ImageView)findViewById(R.id.tempplus);
        btemm=(ImageView)findViewById(R.id.tempminus);
        btimep=(ImageView)findViewById(R.id.timeplus);
        btimem=(ImageView)findViewById(R.id.timeminus);
        bsave=(ImageView)findViewById(R.id.save);
      //  bsaveas=(Button)findViewById(R.id.saveas);
        bapply=(ImageView)findViewById(R.id.apply);
        bquit=(ImageView)findViewById(R.id.quit);
        time=1500;
        fan=50;
        temp=50;
        temselected=true;
        timeselected=false;
        Bundle gb=getIntent().getExtras();
      //  ori_id=gb.getInt("id");
        ori_id=0;
        plandb=new PlanDB(this);
        if(ori_id!=-1){
        	Cursor c=plandb.selectPlan(ori_id);
        	if(c!=null){
        		startManagingCursor(c);
        		
        	ori_name=c.getString(1);
        	
        	ori_time=c.getInt(3);
        	ori_temp=c.getInt(2);
        	ori_fan=c.getInt(4);}
        	if(ori_time!=-1){
        		
            	timetv.setText(Integer.toString(ori_time));
            	seek2.setProgress(ori_time);
            }
        	else if(ori_time==-1){
        		timeselected=false;
    			btimep.setEnabled(false);
    			btimem.setEnabled(false);
    			seek2.setEnabled(false);
    			cb2.setChecked(false);
        	}
            if(ori_temp!=-1){
            	temtv.setText(Integer.toString(ori_temp));
            	seek1.setProgress(ori_temp);
            }
        	else if(ori_time==-1){
        		temselected=false;
    			btemp.setEnabled(false);
    			btemm.setEnabled(false);
    			seek1.setEnabled(false);
    			cb1.setChecked(false);
        	}
            fantv.setText(Integer.toString(ori_fan));
            seek3.setProgress(ori_fan);
            nametv.setText(ori_name);
            time=ori_time;
            temp=ori_temp;
            fan=ori_fan;
        }
        else if(ori_id==-1){
        //	ori_id=0;
        	nametv.setText("New Plan");
        	ori_time=time;
        	ori_temp=temp;
        	ori_fan=fan;
  //      	bsaveas.setEnabled(false);
        }
       
       
    }
	
	public void onButtonTempplus(View view){
		temp=temp+1;
		temtv.setText(String.valueOf(temp));
		seek1.setProgress(temp);
	}
	
	
	public void onButtonTempminus(View view){
		temp=temp-1;
		temtv.setText(String.valueOf(temp));
		seek1.setProgress(temp);
	}
	
	
	public void onButtonTimeplus(View view){
		time=time+1;
		min=time / 60;
		sec=time % 60;
		timetv.setText(min+" minutes "+sec+" seconds");
		seek2.setProgress(time);
	}
	
	
	public void onButtonTimeminus(View view){
		time=time-1;
		min=time / 60;
		sec=time % 60;
		timetv.setText(min+" minutes "+sec+" seconds");
		seek2.setProgress(time);
	}
	
	
	public void onButtonFanplus(View view){
		fan=fan+1;
		fantv.setText(String.valueOf(fan)+"%");
		seek3.setProgress(fan);
	}
	
	
	public void onButtonFanminus(View view){
		fan=fan-1;
		fantv.setText(String.valueOf(fan)+"%");
		seek3.setProgress(fan);
	}
	

	
	
	public void onCheckBox1(View view){
		if(cb1.isChecked()==true){
			temselected=true;
			temp=seek1.getProgress();
			btemp.setEnabled(true);
			btemm.setEnabled(true);
			seek1.setEnabled(true);
		}
		else{
			if(cb2.isChecked()==false){
				removeDialog(1);
	    		showDialog(1); 
	    		
				cb1.setChecked(true);
				temselected=true;
				return;
			}
			temp=-1;
			temselected=false;
			btemp.setEnabled(false);
			btemm.setEnabled(false);
			seek1.setEnabled(false);
		}
	}
	
	
	public void onCheckBox2(View view){
		if(cb2.isChecked()==true){
			time=seek2.getProgress();
			timeselected=true;
			btimep.setEnabled(true);
			btimem.setEnabled(true);
			seek2.setEnabled(true);
			
		}
		else{
			if(cb1.isChecked()==false){
				removeDialog(1);
	    		showDialog(1); 
			
				cb2.setChecked(true);
				temselected=true;
				return;
			}
			time=-1;
			timeselected=false;
			btimep.setEnabled(false);
			btimem.setEnabled(false);
			seek2.setEnabled(false);
		}
	}
	
	public void onButtonSave(View view){
		if(ori_id==0 || ori_id==-1){
			removeDialog(2);
    		showDialog(2); 
		}
		else{
			removeDialog(3);
    		showDialog(3); 
		}
	}
	
	/*public void onButtonSaveAs(View view){
		
			removeDialog(4);
    		showDialog(4); 
		
	}*/
	
	public void onButtonApply(View view){
		if(ori_id==-1){
			plandb.planUpdateFan(0, fan);
			plandb.planUpdateTemp(0, temp);
			plandb.planUpdateTime(0, time);
			apply=true;
			back=new Intent();
			backb=new Bundle();
			backb.putBoolean("apply", apply);
			backb.putInt("id", 0);
			back.putExtras(backb);
			plandb.close();
			setResult(RESULT_OK, back);
			finish();
		}
		else {
			plandb.planUpdateFan(ori_id, fan);
			plandb.planUpdateTemp(ori_id, temp);
			plandb.planUpdateTime(ori_id, time);
			apply=true;
			back=new Intent();
			backb=new Bundle();
			backb.putBoolean("apply", apply);
			backb.putInt("id", ori_id);
			back.putExtras(backb);
			plandb.close();
			setResult(RESULT_OK, back);
			finish();
		}
		
	}
	public void onButtonQuit(View view){
		apply=false;
		back=new Intent();
		backb=new Bundle();
		backb.putBoolean("apply", false);
		backb.putInt("id", ori_id);
		back.putExtras(backb);
		plandb.close();
		setResult(RESULT_OK, back);
		finish();
	}
	
	private OnSeekBarChangeListener seek1lis=new OnSeekBarChangeListener(){

		
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			temp=seek1.getProgress();
			temtv.setText(String.valueOf(seek1.getProgress()));
			
			
		}

		
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}};
		private OnSeekBarChangeListener seek2lis=new OnSeekBarChangeListener(){

			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				time=seek2.getProgress();
				min=time / 60;
				sec=time % 60;
				timetv.setText(min+" minutes "+sec+" seconds");
				
				
			}

			
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}};
			private OnSeekBarChangeListener seek3lis=new OnSeekBarChangeListener(){

				
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					fan=seek3.getProgress();
					fantv.setText(String.valueOf(seek3.getProgress())+"%");
					
					
				}

				
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}

				
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					
				}};
				 protected Dialog onCreateDialog(int id) {
				    	if (id == 1) {
					    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
				                // this is the message to display
					    	builder.setMessage("You must at least select one of the tempearture and time!"); 
				                // this is the button to display
					    	builder.setPositiveButton("OK",
					    		new DialogInterface.OnClickListener() {
				                           // this is the method to call when the button is clicked 
					    	           public void onClick(DialogInterface dialog, int id) {
				                                   // this will hide the dialog
					    	        	  
					    	        	   dialog.cancel();
					    	           }
					    	         });
				    		return builder.create();
				    	}
				    	if(id==2){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		LayoutInflater factory = LayoutInflater.from(this); 
				    		final View textEntryView = factory.inflate(R.layout.save_dialog, null); 
				    		builder.setView(textEntryView);
				    		builder.setPositiveButton("OK",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   ori_temp=temp;
						    	        	   ori_time=time;
						    	        	   ori_fan=fan;
						    	        	   EditText et=(EditText)textEntryView.findViewById(R.id.saveinput);
						    	        	   ori_name=et.getText().toString();
						    	        	   
						    	        	   ori_id=plandb.getLastId()+1;
						    	        	   plandb.insertPlan(ori_id, ori_name, ori_temp,ori_time, ori_fan);
						    	        	   nametv.setText(ori_name);
						    	        	   dialog.cancel();
						    	           }
				    		});
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	  
						    	        	   dialog.cancel();
						    	           }
						    	         });
					    	return builder.create();
				    		
				    	}
				    	if(id==3){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		builder.setMessage("Do you want to save with the same name, or save as another plan?");
				    		builder.setPositiveButton("Save",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   ori_temp=temp;
						    	        	   ori_time=time;
						    	        	   ori_fan=fan;
						    	        	   plandb.planUpdateFan(ori_id, ori_fan);
						    	        	   plandb.planUpdateTemp(ori_id, ori_temp);
						    	        	   plandb.planUpdateTime(ori_id, ori_time);
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNeutralButton("Save As", new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   removeDialog(4);
					    	           		   showDialog(4); 
						    	        	   dialog.cancel();
						    	           }
						    	         });
					    	return builder.create();
				    		
				    	}				    	
				    	if(id==4){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		LayoutInflater factory = LayoutInflater.from(this); 
				    		final View textEntryView = factory.inflate(R.layout.saveas_dialog, null); 
				    		builder.setView(textEntryView);
				    		builder.setPositiveButton("OK",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   ori_temp=temp;
						    	        	   ori_time=time;
						    	        	   ori_fan=fan;
						    	        	   EditText et=(EditText)textEntryView.findViewById(R.id.saveasinput);
						    	        	   ori_name=et.getText().toString();
						    	        	   
						    	        	   ori_id=plandb.getLastId()+1;
						    	        	   plandb.insertPlan(ori_id, ori_name, ori_temp,ori_time, ori_fan);
						    	        	   nametv.setText(ori_name);
						    	        	   dialog.cancel();
						    	        	   
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
					    	return builder.create();
				    		
				    	}
				    	if(id==5){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		builder.setMessage("Are you sure to apply without saving?");
				    		builder.setPositiveButton("Save",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog

						    	        	   
						    	        		   ori_temp=temp;
							    	        	   ori_time=time;
							    	        	   ori_fan=fan;
							    	        	   plandb.planUpdateFan(ori_id, ori_fan);
							    	        	   plandb.planUpdateTemp(ori_id, ori_temp);
							    	        	   plandb.planUpdateTime(ori_id, ori_time);
							    	        	   apply=true;
							    	        	   back=new Intent();
							    	   			backb=new Bundle();
							    	   			backb.putBoolean("apply", apply);
							    	   			backb.putInt("id", ori_id);
							    	   			back.putExtras(backb);
							    	   			setResult(RESULT_OK, back);
							    	   			plandb.close();
							    	        	   finish();
							    	        	//   finish();
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNeutralButton("Apply", new DialogInterface.OnClickListener() {
		                           // this is the method to call when the button is clicked 
				    	           public void onClick(DialogInterface dialog, int id) {
			                                   // this will hide the dialog
				    	        	   apply=true;
				    	        	   back=new Intent();
				    	   			backb=new Bundle();
				    	   			
				    	   			backb.putBoolean("apply", apply);
				    	   			backb.putInt("id", ori_id);
				    	   			back.putExtras(backb);
				    	   			setResult(RESULT_OK, back);
				    	        	plandb.close();   
				    	   			finish();
				    	        	   dialog.cancel();
				    	           }
				    	         });
				    		
					    	return builder.create();
				    		
				    	}
				       	if(id==6){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		builder.setMessage("Are you sure to quit without saving?");
				    		builder.setPositiveButton("Save",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	
						    	        		   ori_temp=temp;
							    	        	   ori_time=time;
							    	        	   ori_fan=fan;
							    	        	   plandb.planUpdateFan(ori_id, ori_fan);
							    	        	   plandb.planUpdateTemp(ori_id, ori_temp);
							    	        	   plandb.planUpdateTime(ori_id, ori_time);
							    	        	   apply=false;
							    	        	   back=new Intent();
							    	   			backb=new Bundle();
							    	   			backb.putBoolean("apply", apply);
							    	   			backb.putInt("id", ori_id);
							    	   			back.putExtras(backb);
							    	   			setResult(RESULT_OK, back);
							    	        	plandb.close();   
							    	   			finish();
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
		                           // this is the method to call when the button is clicked 
				    	           public void onClick(DialogInterface dialog, int id) {
			                                   // this will hide the dialog
				    	        	   apply=false;
				    	        	   back=new Intent();
				    	   			backb=new Bundle();
				    	   			backb.putBoolean("apply", apply);
				    	   			backb.putInt("id", ori_id);
				    	   			back.putExtras(backb);
				    	   			setResult(RESULT_OK, back);
				    	   			plandb.close();
				    	        	   finish();
				    	        	   dialog.cancel();
				    	           }
				    	         });
				    		
					    	return builder.create();
				    		
				    	}	
				    	if(id==7){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		LayoutInflater factory = LayoutInflater.from(this); 
				    		final View textEntryView = factory.inflate(R.layout.save_dialog, null); 
				    		builder.setView(textEntryView);
				    		builder.setPositiveButton("OK",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   ori_temp=temp;
						    	        	   ori_time=time;
						    	        	   ori_fan=fan;
						    	        	   EditText et=(EditText)textEntryView.findViewById(R.id.saveinput);
						    	        	   ori_name=et.getText().toString();
						    	        	   
						    	        	   ori_id=plandb.getLastId()+1;
						    	        	   plandb.insertPlan(ori_id, ori_name, ori_temp,ori_time, ori_fan);
						    	        	   nametv.setText(ori_name);
						    	        	   apply=true;
						    	        	   plandb.close();
						    	        	   back=new Intent();
						    	   			backb=new Bundle();
						    	   			backb.putBoolean("apply", apply);
						    	   			backb.putInt("id", ori_id);
						    	   			back.putExtras(backb);
						    	   			setResult(RESULT_OK, back);
						    	        	   finish();
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
					    	return builder.create();
				    		
				    	}
				    	if(id==8){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		LayoutInflater factory = LayoutInflater.from(this); 
				    		final View textEntryView = factory.inflate(R.layout.save_dialog, null); 
				    		builder.setView(textEntryView);
				    		builder.setPositiveButton("OK",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   ori_temp=temp;
						    	        	   ori_time=time;
						    	        	   ori_fan=fan;
						    	        	   EditText et=(EditText)textEntryView.findViewById(R.id.saveinput);
						    	        	   ori_name=et.getText().toString();
						    	        	   ori_id=plandb.getLastId()+1;
						    	        	   plandb.insertPlan(ori_id, ori_name, ori_temp,ori_time, ori_fan);
						    	        	   nametv.setText(ori_name);
						    	        	   apply=false;
						    	        	   plandb.close();
						    	        	   back=new Intent();
						    	   			backb=new Bundle();
						    	   			backb.putBoolean("apply", apply);
						    	   			backb.putInt("id", ori_id);
						    	   			back.putExtras(backb);
						    	   			setResult(RESULT_OK, back);
						    	        	   finish();
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
					    	return builder.create();
				    		
				    	}
				    	if(id==9){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		builder.setMessage("Are you sure to apply this new plan without saving?");
				    		builder.setPositiveButton("Save",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	
						    	        		 removeDialog(7);
						    	        		 showDialog(7);
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNeutralButton("Apply", new DialogInterface.OnClickListener() {
		                           // this is the method to call when the button is clicked 
				    	           public void onClick(DialogInterface dialog, int id) {
			                                   // this will hide the dialog
				    	        	   apply=true;
				    	        	   ori_id=0;
				    	        	   plandb.planUpdateFan(ori_id, ori_fan);
				    	        	   plandb.planUpdateTemp(ori_id, ori_temp);
				    	        	   plandb.planUpdateTime(ori_id, ori_time);
				    	        	   plandb.close();
				    	        	   back=new Intent();
				    	   			backb=new Bundle();
				    	   			backb.putBoolean("apply", apply);
				    	   			backb.putInt("id", ori_id);
				    	   			back.putExtras(backb);
				    	   			setResult(RESULT_OK, back);
				    	        	   finish();
				    	        	   dialog.cancel();
				    	           }
				    	         });
				    		
					    	return builder.create();
				    		
				    	}	
				    	if(id==10){
				    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
				    		builder.setMessage("Are you sure to quit this new plan without saving?");
				    		builder.setPositiveButton("Save",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	
						    	        		 removeDialog(8);
						    	        		 showDialog(8);
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNegativeButton("Cancel",
						    		new DialogInterface.OnClickListener() {
					                           // this is the method to call when the button is clicked 
						    	           public void onClick(DialogInterface dialog, int id) {
					                                   // this will hide the dialog
						    	        	   
						    	        	   dialog.cancel();
						    	           }
						    	         });
				    		builder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
		                           // this is the method to call when the button is clicked 
				    	           public void onClick(DialogInterface dialog, int id) {
			                                   // this will hide the dialog
				    	        	   apply=false;
				    	        	   plandb.close();
				    	        	   back=new Intent();
				    	   			backb=new Bundle();
				    	   			backb.putBoolean("apply", apply);
				    	   			backb.putInt("id", ori_id);
				    	   			back.putExtras(backb);
				    	   			setResult(RESULT_OK, back);
				    	   			
				    	        	   finish();
				    	        	   dialog.cancel();
				    	           }
				    	         });
				    		
					    	return builder.create();
				    		
				    	}	
				    	return null;
				 }
		public boolean Changed(){
			if(time==ori_time){
				if(temp==ori_temp){
					if(fan==ori_fan){
						return false;
					}
				}
			}
			return true;
		}
		
		
}