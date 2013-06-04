package org.achartengine.chartdemo.demo;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LogoutButon extends Button {
	private Handler mHandler;
	private Facebook mFb;	
	public LogoutButon(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setText("Snapshot to Facebook");
        setBackgroundColor(Color.BLUE);
        mHandler = new Handler();
        setOnClickListener(new ButtonOnClickListener());
	}
	public void init(final Facebook fb){
		this.mFb = fb;
	}
	
	private final class ButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        
        public void onClick(View arg0) {
            if (mFb.isSessionValid()) {
                SessionEvents.onLogoutBegin();
                AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
                asyncRunner.logout(getContext(), new LogoutRequestListener());
            } 
        }
    }
	private class LogoutRequestListener extends BaseRequestListener {
		
        public void onComplete(String response, final Object state) {
            /*
             * callback should be run in the original thread, not the background
             * thread
             */
            mHandler.post(new Runnable() {
                
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
		}
	}

}
