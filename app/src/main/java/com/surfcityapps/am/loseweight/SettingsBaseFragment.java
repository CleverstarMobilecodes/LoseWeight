package com.surfcityapps.am.loseweight;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SettingsBaseFragment extends Fragment {

	private int FRAME_LAYOUT_ID = 120;
	private String SETTINGS_FRAGMENT_TAG = "Settings";

	SettingsFragment settings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		 FrameLayout wrapper = new FrameLayout(getActivity());
		 wrapper.setId(FRAME_LAYOUT_ID);
		 
		    // SETTINGS_FRAGMENT_TAG add it
		    if (getChildFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG) == null) {
		        settings = new SettingsFragment();

		        getChildFragmentManager().beginTransaction()
		                .add(FRAME_LAYOUT_ID, settings, SETTINGS_FRAGMENT_TAG).commit();
		    }
		    
		    return wrapper;
	}
	
	public void pushMusicFragment() {
		   FragmentTransaction fragmentTransaction = getChildFragmentManager()
		            .beginTransaction();
		    MusicListView_Fragment musicFragment = new MusicListView_Fragment();
		    //fragmentTransaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left, 0 , R.anim.move_out_right);
		    fragmentTransaction.add(FRAME_LAYOUT_ID, musicFragment, "Music");
		    fragmentTransaction.addToBackStack(null);
		    fragmentTransaction.commit();
	}
	
	public boolean hasFragmentToPop() {
		int count = getChildFragmentManager().getBackStackEntryCount();
		
		if (count == 0) {
			return false;
		}
		
		MusicListView_Fragment music = (MusicListView_Fragment) getChildFragmentManager().findFragmentByTag("Music");

		music.saveChoiceToSharedPref();

		getChildFragmentManager().popBackStack();

		SettingsFragment settings = (SettingsFragment) getChildFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG);
		settings.reloadData();	
		
	
		
		return true;
	}
	
	
}//end class
