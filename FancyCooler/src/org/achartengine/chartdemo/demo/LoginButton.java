/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.achartengine.chartdemo.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import edu.upenn.cis.cis542.R;

import org.achartengine.chartdemo.demo.SessionEvents.AuthListener;
import org.achartengine.chartdemo.demo.SessionEvents.LogoutListener;


public class LoginButton extends ImageView {

    private Facebook mFb;
    private Handler mHandler;
    private SessionListener mSessionListener = new SessionListener();
    private String[] mPermissions;
    private Activity mActivity;
    private int mActivityCode;

    public LoginButton(Context context) {
        super(context);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(final Activity activity, final int activityCode, final Facebook fb) {
        init(activity, activityCode, fb, new String[] {});
    }

    public void init(final Activity activity, final int activityCode, final Facebook fb,
            final String[] permissions) {
        mActivity = activity;
        mActivityCode = activityCode;
        mFb = fb;
        mPermissions = permissions;
        mHandler = new Handler();


        drawableStateChanged();

        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
        setOnClickListener(new ButtonOnClickListener());
        
        
    }

    private final class ButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        
        public void onClick(View arg0) {
        	System.out.println("Click");
            if (mFb.isSessionValid()) {
//                SessionEvents.onLogoutBegin();
//                AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
//                asyncRunner.logout(getContext(), new LogoutRequestListener());
            } else {
            	mFb.authorize(mActivity, mPermissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
            }
        }
    }

    private final class LoginDialogListener implements DialogListener {
        
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
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

    private class SessionListener implements AuthListener, LogoutListener {

        
        public void onAuthSucceed() {
//            setImageResource(R.drawable.logout);
            SessionStore.save(mFb, getContext());
        }

        
        public void onAuthFail(String error) {
        }

        
        public void onLogoutBegin() {
        }

        
        public void onLogoutFinish() {
            SessionStore.clear(getContext());
        }
    }

}