package com.surfcityapps.am.loseweight;

import com.askingpoint.android.AskingPoint;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.plus.PlusOneButton;
import com.uservoice.uservoicesdk.UserVoice;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewActivity extends Activity {

		private PlusOneButton mPlusOneButton;

		@Override
		protected void onStart() {
			super.onStart();
			FlurryAgent.onStartSession(this, Constants.FLURRY_APP_ID);
	        AskingPoint.onStart(this, Constants.ASKING_POINT_APP_ID);	
		} 
		
		@Override
		protected void onStop() {
			super.onStop();
			FlurryAgent.onEndSession(this);
			AskingPoint.onStop(this);
		}

		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layout_activity_review);
			
			ImageButton crossButton = (ImageButton) findViewById(R.id.popup_crossButton);
			crossButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ReviewActivity.this.finish();
					overridePendingTransition(R.anim.no_change,R.anim.slide_down);					
				}
			});
			
			
			Typeface helveticaMD = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Md.otf");	
			Typeface helveticaLt = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Lt.otf");	

			TextView titleTextView = (TextView) findViewById(R.id.popup_titleTextview);
			titleTextView.setTypeface(helveticaMD);
			
			TextView detailTextView = (TextView) findViewById(R.id.popup_detailTextview);
			detailTextView.setTypeface(helveticaLt);
			
			Button rateButton = (Button) findViewById(R.id.rate_screen_reviewButton);
			rateButton.setTypeface(helveticaLt);
			
			TextView supportTextView = (TextView) findViewById(R.id.rate_screen_supportTextView);
			supportTextView.setTypeface(helveticaLt);
			
			mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);

			if(Constants.K_IS_AMAZON == 1) {
				RelativeLayout googlePlusLayout = (RelativeLayout) findViewById(R.id.plusButtonRelLayout);
				googlePlusLayout.setVisibility(View.GONE);
			}

			rateButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String storeText = "market://details?id=";

					if(Constants.K_IS_AMAZON == 1)
						storeText = "amzn://apps/android?p=";

					Uri uri = Uri.parse(storeText + ReviewActivity.this.getPackageName());
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					try {
					  startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {

						storeText = "http://play.google.com/store/apps/details?id=";

						if(Constants.K_IS_AMAZON == 1)
							storeText = "http://www.amazon.com/gp/mas/dl/android?p=";

					  		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(storeText + ReviewActivity.this.getPackageName())));
					}
				}
			});
			
			RelativeLayout supportLayout = (RelativeLayout) findViewById(R.id.rate_screen_supportLayout);
			supportLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UserVoice.launchUserVoice(ReviewActivity.this);
					
					//Arif
					ReviewActivity.this.finish();
					overridePendingTransition(R.anim.no_change,R.anim.slide_down);
				}
			});
			
			supportTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					UserVoice.launchUserVoice(ReviewActivity.this);

					//Arif
					ReviewActivity.this.finish();
					overridePendingTransition(R.anim.no_change,R.anim.slide_down);

				}
			});
			
		} // end onCreate
		
		@Override
		protected void onResume() {
			super.onResume();
		    mPlusOneButton.initialize("http://play.google.com/store/apps/details?id=" + ReviewActivity.this.getPackageName(), 1);
		}
}// end class
