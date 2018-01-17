package com.surfcityapps.am.loseweight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surfcityapps.am.loseweight.UnlockFeatureDialogFragment.UnlockFeatureDialogListener;

public class MusicListView_Fragment extends Fragment implements View.OnClickListener,  UnlockFeatureDialogListener {

	private RelativeLayout row1Layout;
	private RelativeLayout row2Layout;
	private RelativeLayout row3Layout;
	private RelativeLayout row4Layout;
	private RelativeLayout row5Layout;
	private RelativeLayout row6Layout;

	CheckBox row1CheckBox, row2CheckBox, row3CheckBox, row4CheckBox, row5CheckBox, row6CheckBox;
	
	String soundNames[] = { "None", "Pure Embrace" , "Resonance" , "Constellation" , "Brook" , "Beach" };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	      View v = inflater.inflate(R.layout.layout_side_menu_fragment, container, false);
	      
	    /*
	      
	      row1Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row1_RelativeLayout);
	      row1Layout.setClickable(true);
	      row1Layout.setOnClickListener(this);

	      row2Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row2_RelativeLayout);
	      row2Layout.setClickable(true);
	      row2Layout.setOnClickListener(this);

	      row3Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row3_RelativeLayout);
	      row3Layout.setClickable(true);
	      row3Layout.setOnClickListener(this);
	      
	      row4Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row4_RelativeLayout);
	      row4Layout.setClickable(true);
	      row4Layout.setOnClickListener(this);
	      
	      row5Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row5_RelativeLayout);
	      row5Layout.setClickable(true);
	      row5Layout.setOnClickListener(this);	         
	      
	      row6Layout = (RelativeLayout) v.findViewById(R.id.music_screen_row6_RelativeLayout);
	      row6Layout.setClickable(true);
	      row6Layout.setOnClickListener(this);		      
	      
		  row1CheckBox = (CheckBox) row1Layout.getChildAt(2);		 
		  row2CheckBox = (CheckBox) row2Layout.getChildAt(2);		 
		  row3CheckBox = (CheckBox) row3Layout.getChildAt(2);		 
		  row4CheckBox = (CheckBox) row4Layout.getChildAt(2);		 
		  row5CheckBox = (CheckBox) row5Layout.getChildAt(2);		 
		  row6CheckBox = (CheckBox) row6Layout.getChildAt(2);			      
	      
		  
		  TextView  row1TextView = (TextView) row1Layout.getChildAt(1);		 
		  TextView  row2TextView = (TextView) row2Layout.getChildAt(1);		 
		  TextView  row3TextView = (TextView) row3Layout.getChildAt(0);		 
		  TextView  row4TextView = (TextView) row4Layout.getChildAt(0);		 
		  TextView  row5TextView = (TextView) row5Layout.getChildAt(0);		 
		  TextView  row6TextView = (TextView) row6Layout.getChildAt(0);		 
		  
		  row1TextView.setText(soundNames[0]);
		  row2TextView.setText(soundNames[1]);
		  row3TextView.setText(soundNames[2]);
		  row4TextView.setText(soundNames[3]);
		  row5TextView.setText(soundNames[4]);
		  row6TextView.setText(soundNames[5]);
		  
		  disableAllCheckBox();
		  
		  String currentSelected = getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
		  
		  CheckBox checkBox  = null;
		  
		  if (currentSelected.equalsIgnoreCase(soundNames[0])) {
				 checkBox = (CheckBox) row1Layout.getChildAt(2);		 
		  }
		  else if (currentSelected.equalsIgnoreCase(soundNames[1])) {
				 checkBox = (CheckBox) row2Layout.getChildAt(2);		 
		  }
		  else if (currentSelected.equalsIgnoreCase(soundNames[2])) {
				 checkBox = (CheckBox) row3Layout.getChildAt(2);		 
		  }
		  else if (currentSelected.equalsIgnoreCase(soundNames[3])) {
				 checkBox = (CheckBox) row4Layout.getChildAt(2);		 
		  }
		  else if (currentSelected.equalsIgnoreCase(soundNames[4])) {
				 checkBox = (CheckBox) row5Layout.getChildAt(2);		 
		  }
		  else if (currentSelected.equalsIgnoreCase(soundNames[5])) {
				 checkBox = (CheckBox) row6Layout.getChildAt(2);		 
		  }
		  
		  checkBox.setVisibility(View.VISIBLE);
		  checkBox.setChecked(true);
		  */
	      
	      return v;
	}

	@Override
	public void onClick(View relativeLayout) {
		 disableAllCheckBox();
		
		 boolean iAP_purchased = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.PREF_PAID_IAP, false);
		 if (!iAP_purchased && relativeLayout != row2Layout) {
			new UnlockFeatureDialogFragment(this).show(getFragmentManager(), "dialog");
			 CheckBox checkBox = (CheckBox) row2Layout.getChildAt(2);		 
			 checkBox.setVisibility(View.VISIBLE);
			 checkBox.setChecked(true);
		}
		 else {		 
			 CheckBox checkBox = (CheckBox) ((ViewGroup) relativeLayout).getChildAt(2);		 
			 checkBox.setVisibility(View.VISIBLE);
			 checkBox.setChecked(true);
			 
			 ListenView_Fragment listenFragment = ((HomeActivity)getActivity()).listenFragment;

			 saveChoiceToSharedPref();
			 
			 listenFragment.initializeBackgroundAudio();
			 
			 if (listenFragment.shouldPlayBackgroundMusic() && listenFragment.backgroundPlayer != null && listenFragment.voicePlayer.isPlaying()) {
				listenFragment.playBackgroundAudioWithFade();
			}
			 
		 }

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void saveChoiceToSharedPref(){
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		 Editor prefEditor = pref.edit();
		
		 Constants.logMessage("sound changing in music screen");

		 CheckBox row1CheckBox = (CheckBox) row1Layout.getChildAt(2);		
		 
		 if (row1CheckBox.isChecked()) {
			 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[0]);			 
		}
		 else {
				 if (row2CheckBox.isChecked()) {
					 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[1]);
				 }
				 else if (row3CheckBox.isChecked()) {
					 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[2]);
				 }
				 else if (row4CheckBox.isChecked()) {
					 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[3]);
				 }
				 else if (row5CheckBox.isChecked()) {
					 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[4]);
				 }
				 else if (row6CheckBox.isChecked()) {
					 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, soundNames[5]);
				 }
				 
		 }

		 
		 
		 prefEditor.commit();
		 
		 
	}
	
	private void disableAllCheckBox() {
		
		 row1CheckBox.setVisibility(View.INVISIBLE);
		 row1CheckBox.setChecked(false);
		 
		 row2CheckBox.setVisibility(View.INVISIBLE);
		 row2CheckBox.setChecked(false);
		 
		 row3CheckBox.setVisibility(View.INVISIBLE);
		 row3CheckBox.setChecked(false);
		 
		 row4CheckBox.setVisibility(View.INVISIBLE);
		 row4CheckBox.setChecked(false);
		 
		 row5CheckBox.setVisibility(View.INVISIBLE);
		 row5CheckBox.setChecked(false);
		 
		 row6CheckBox.setVisibility(View.INVISIBLE);
		 row6CheckBox.setChecked(false);
	}

	@Override
	public void unlockFeatureDismiss(boolean yesClicked) {
		((HomeActivity) getActivity()).purchaseProduct();				
	}
	
	
	
}//end class
