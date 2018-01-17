package com.surfcityapps.am.loseweight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class MoreView_Fragment extends Fragment {

	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.layout_more_fragment, container, false);
			
		    TextView title = (TextView) v.findViewById(R.id.title_more);
		    title.setTypeface(((HomeActivity) getActivity()).helveticaTh);
		      
			webView = (WebView) v.findViewById(R.id.more_screen_webView);
			webView.loadUrl(Constants.MORE_TAB_URL);
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webView.setWebViewClient(new WebViewClient(){
				@Override
			    public boolean shouldOverrideUrlLoading(WebView  view, String  url){
			        if( url.equals(Constants.MORE_TAB_URL) ){
			        	return true;
			        }
			        
			        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			        
		        	return true;
			       // return false;
			    }
			});
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
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden == false) {
			webView.loadUrl(Constants.MORE_TAB_URL);
		}
	}

}
