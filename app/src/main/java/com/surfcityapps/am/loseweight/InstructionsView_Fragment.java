package com.surfcityapps.am.loseweight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class InstructionsView_Fragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.layout_instructions_fragment, container, false);
			 
			TextView title = (TextView) v.findViewById(R.id.title_instructions);
		    title.setTypeface(((HomeActivity) getActivity()).helveticaTh);
			
			WebView webView =  (WebView) v.findViewById(R.id.instructions_screen_webView);
			webView.setBackgroundColor(0x00000000);
			boolean tablet = (getActivity().getResources().getConfiguration().screenLayout  & Configuration.SCREENLAYOUT_SIZE_MASK)    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
			
			if (tablet) {
				webView.loadUrl("file:///android_asset/hypnoticInstruction-ipad.html");
			}
			else {
				webView.loadUrl("file:///android_asset/hypnoticInstruction.html");
			}
			webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

			initializeUnlockAndShareButtons(v);
			
			return v;
	}
	
	public void initializeUnlockAndShareButtons(View v) {
		final HomeActivity activity = (HomeActivity) getActivity();
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		final boolean iAP_purchased = pref.getBoolean(Constants.PREF_PAID_IAP, false);
		ImageButton unlockButton = (ImageButton) v.findViewById(R.id.unlockImageButton);
		
		if (iAP_purchased) {
			unlockButton.setImageResource(R.drawable.star_symbol);
		}
		
		unlockButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (iAP_purchased) {
					activity.showRateScreen();	
				}
				else {
					activity.showUnlockScreen();	
				}
				
			}
		});
		
		ImageButton shareButton = (ImageButton) v.findViewById(R.id.shareImageButton);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				activity.showShareScreen();
			}
		});
		
	}
	
}
