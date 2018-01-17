package com.surfcityapps.am.loseweight;

import CustomComponents.MySwitch;
import CustomComponents.MySwitch.OnChangeAttemptListener;
import CustomComponents.SlidingMenu;
import CustomComponents.SlidingMenu.ContentSelectedListener;
import CustomComponents.SlidingMenu.OnClosedListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.surfcityapps.am.loseweight.RewindSelectDialogFragment.RewindSelectDialogListener;
import com.surfcityapps.am.loseweight.UnlockFeatureDialogFragment.UnlockFeatureDialogListener;

public class SettingsFragment extends Fragment implements UnlockFeatureDialogListener, RewindSelectDialogListener {

	private ListView listView;
	private CustomListAdapter listAdapter;
	
	private SharedPreferences pref;
	private Editor prefEditor;

	public boolean hypnoticBoosterPurchase;
	private SlidingMenu slidingMenu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	      View v = inflater.inflate(R.layout.layout_settings, container, false);
	      
	      
	      TextView title = (TextView) v.findViewById(R.id.title_settings);
		  title.setTypeface(((HomeActivity) getActivity()).helveticaTh);
	      
	      listView = (ListView) v.findViewById(R.id.settings_listView);
	      
	      HomeActivity activity = (HomeActivity) getActivity();
	      
	      View emptySpaceView = new View(getActivity());
	      emptySpaceView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) Constants.pxFromDp(getActivity(), 0)));
	      
	      //20dp list divider height header footer added by default
	      listView.addHeaderView(emptySpaceView);
	      //listView.addFooterView(emptySpaceView);
	      
	      AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) Constants.pxFromDp(getActivity(), 40));
	      FrameLayout frameLayout = new FrameLayout(getActivity());
 	      frameLayout.setPadding(0, (int) Constants.pxFromDp(getActivity(), 20), 0, (int) Constants.pxFromDp(getActivity(), 90));
	      
	      FrameLayout.LayoutParams layoutParamsButton = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) Constants.pxFromDp(getActivity(), 43));
	      layoutParamsButton.gravity = Gravity.CENTER_HORIZONTAL;
	      
	      Button button = new Button(getActivity());
	      button.setBackgroundResource(R.drawable.white_button_bg);
	      button.setTextColor(getResources().getColor(R.color.LIGHT_BLUE_TEXT));
	      button.setLayoutParams(layoutParamsButton);
	      button.setText(Constants.K_SETTING_RESTORE);
	      button.setTypeface(activity.helveticalLT);
	     
	      frameLayout.addView(button);
	      
	      button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((HomeActivity) getActivity()).restoreProduct();
			}
		});
	      
	      listView.addFooterView(frameLayout);

	      pref = getActivity().getPreferences(Context.MODE_PRIVATE);
	      prefEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();

	      //Arif
			if (Constants.K_IS_PRO == 1) 
				button.setVisibility(View.INVISIBLE);
			
	      listAdapter = new CustomListAdapter();
		  listView.setAdapter(listAdapter);

		  FrameLayout fL = (FrameLayout) v.findViewById(R.id.settings_frameLayout);
	      slidingMenu = new SlidingMenu(this.getActivity());
	      slidingMenu.setMode(SlidingMenu.RIGHT);
		  slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	      //menu.setShadowWidthRes(R.dimen.shadow_width);
		  //menu.setShadowDrawable(R.drawable.shadow);
		  slidingMenu.setBehindOffsetRes(R.dimen.SlideMenuWidth);
		  slidingMenu.backgroundResource = R.drawable.plain_background;
		  slidingMenu.setFadeDegree(0.35f);
		  slidingMenu.attachToFragment(this, SlidingMenu.SLIDING_CONTENT,fL,false);
		  slidingMenu.setMenu(R.layout.layout_side_menu_fragment,-1,this);
		  
		  slidingMenu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
			  	listAdapter.notifyDataSetChanged();			
			}
		});
		  
		  slidingMenu.setOnContentClickListener(new ContentSelectedListener() {
			
			  @Override
				public void contentSelected() {
				  	slidingMenu.toggle();
				  	new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							
						}
					}, 400);
				}
		  });
		  
		  
		  initializeUnlockAndShareButtons(v);
	      return v;
	}
	
	public void dismissingSettings() {
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.toggle();
		}
	}

	public void initializeUnlockAndShareButtons(View v) {
		final HomeActivity activity = (HomeActivity) getActivity();
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
	public void onResume() {
		super.onResume();
	}
	
	public void userPurchased(){
		if (hypnoticBoosterPurchase)    {
			AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
			boolean wiredHeadsetOn = audioManager.isWiredHeadsetOn();
			boolean bluetoothHeadsetOn = audioManager.isBluetoothA2dpOn();

			if (wiredHeadsetOn == true || bluetoothHeadsetOn == true  || Constants.K_IS_HEADPHONE_SKIP == 1) {
				prefEditor.putBoolean(Constants.PREF_HYPNOTIC_BOOSTER, true);
				prefEditor.commit();
				 ListenView_Fragment listenFragment = ((HomeActivity)getActivity()).listenFragment;

				listenFragment.initializeHypnoticAudio();
				if (listenFragment.shouldPlayBackgroundMusic() && listenFragment.isAudioInitialized() && listenFragment.voicePlayer.isPlaying() ) {
					listenFragment.playHypnoticAudioWithFade();
				}
				
			}
			else {
				Toast.makeText(getActivity(), Constants.K_LISTEN_HEADPHONES_MSG, Toast.LENGTH_LONG).show();				
			}
		}
		
		hypnoticBoosterPurchase = false;
		reloadData();
	}
	
	public void reloadData() {
	      pref = getActivity().getPreferences(Context.MODE_PRIVATE);
	      prefEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
	      listAdapter = new CustomListAdapter();
		  listView.setAdapter(listAdapter);
	}
	
	private class CustomListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public void showMenuWithDelay() {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					slidingMenu.toggle();			
				}
			}, 300);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			
			View v = convertView;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) SettingsFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.layout_settings_list_item, null);
			}
			
			RelativeLayout cellLayout = (RelativeLayout) v.findViewById(R.id.settings_screen_item_cell_relativeLayout);
			cellLayout.setOnClickListener(null);
			
			TextView primaryTextView = (TextView) v.findViewById(R.id.settings_screen_item_primaryTextView);
			HomeActivity activity = 	(HomeActivity) getActivity();
			primaryTextView.setTypeface(activity.helveticaMD);
			

			TextView detailTextView = (TextView) v.findViewById(R.id.settings_screen_item_detail_textView);
			detailTextView.setVisibility(View.GONE);

			detailTextView.setTypeface(activity.helveticaRoman);

			ImageButton moreButton = (ImageButton) v.findViewById(R.id.settings_screen_item_more_Button);
			moreButton.setVisibility(View.GONE);
			moreButton.setOnClickListener(null);
			
			TextView rightTextView = (TextView) v.findViewById(R.id.settings_screen_item_value_textView);
			rightTextView.setVisibility(View.INVISIBLE);

			rightTextView.setTypeface(activity.helveticaMD);

			ImageView arrow = (ImageView) v.findViewById(R.id.settings_screen_item_arrow_imageView);
			arrow.setVisibility(View.INVISIBLE);

			final MySwitch toggleSwitch = (MySwitch) v.findViewById(R.id.settings_screen_item_toggleSwtich);
			toggleSwitch.setSwitchTypeface(activity.helveticaMD);
			toggleSwitch.setVisibility(View.VISIBLE);
			
			if (position == 0) {
				primaryTextView.setText(Constants.K_SETTING_AUDIOINST);				
				toggleSwitch.setChecked(pref.getBoolean(Constants.PREF_INSTRUCTIONS_ON, true));
			}
			else if (position == 1) {
				primaryTextView.setText(Constants.K_SETTING_BOOSTER);
				toggleSwitch.setChecked(pref.getBoolean(Constants.PREF_HYPNOTIC_BOOSTER, true));
				detailTextView.setVisibility(View.VISIBLE);
				detailTextView.setText(Constants.K_SETTING_BOOSTER_DESC);
				moreButton.setVisibility(View.VISIBLE);
				moreButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						 HomeActivity activity =  (HomeActivity) getActivity();
						 activity.imageV_instructions.performClick();
					}
				});				
			
			}
			else if (position == 2) {
				primaryTextView.setText(Constants.K_SETTING_AWAKEN);
				toggleSwitch.setChecked(pref.getBoolean(Constants.PREF_AWAKEN_END, true));
			}			
			else if (position == 3) {
				primaryTextView.setText(Constants.K_SETTING_BACKGROUND);
				toggleSwitch.setVisibility(View.INVISIBLE);
				rightTextView.setVisibility(View.VISIBLE);
				rightTextView.setText(getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_SOUND, "PURE EMBRACE"));
				rightTextView.setText(rightTextView.getText().toString().toUpperCase());
				arrow.setVisibility(View.VISIBLE);
				
				cellLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						slidingMenu.setMenu(R.layout.layout_side_menu_fragment, SlidingMenu.SLIDING_MUSIC,SettingsFragment.this);
						showMenuWithDelay();					
						//((SettingsBaseFragment)getParentFragment()).pushMusicFragment();
					}
				});
				
			}
			else if (position == 4) {
				primaryTextView.setText(Constants.K_SETTING_DELAY);
				toggleSwitch.setVisibility(View.INVISIBLE);
				rightTextView.setVisibility(View.VISIBLE);
				rightTextView.setText(getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_DELAY_NAME, "None"));
				rightTextView.setText(rightTextView.getText().toString().toUpperCase());
				arrow.setVisibility(View.VISIBLE);
				cellLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						slidingMenu.setMenu(R.layout.layout_side_menu_fragment, SlidingMenu.SLIDING_DELAY_BACKGROUND,SettingsFragment.this);
						showMenuWithDelay();
					}
				});
			}
			else if (position == 5) {
				primaryTextView.setText(Constants.K_SETTING_PLAYCOUNT);
				toggleSwitch.setVisibility(View.INVISIBLE);
				rightTextView.setVisibility(View.VISIBLE);
				int value = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.PREF_VOICE_LOOP_VALUE, 1);
				if (value == -1) 
					rightTextView.setText(Constants.K_SETTING_LOOP);
				else
					rightTextView.setText(String.valueOf(value));
				
				//rightTextView.setText(rightTextView.getText().toString().toUpperCase());
				arrow.setVisibility(View.VISIBLE);				
				cellLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						slidingMenu.setMenu(R.layout.layout_side_menu_fragment, SlidingMenu.SLIDING_PLAY_COUNT,SettingsFragment.this);
						showMenuWithDelay();
					}
				});
				
				/*
				primaryTextView.setText("Restore In-App Purchase");
				toggleSwitch.setVisibility(View.INVISIBLE);
				cellLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						((HomeActivity)getActivity()).restoreProduct();
					}
				});
				*/
			}
			
			toggleSwitch.setOnCheckedChangeListener(null);
			
			toggleSwitch.setOnChangeAttemptListener(new OnChangeAttemptListener() {
				
				@Override
				public void onChangeAttempted(boolean isChecked) {
					/*
					
				}
			});
			toggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			//toggleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				*/
					Constants.logMessage(" checked change " + String.valueOf(isChecked));
					 ListenView_Fragment listenFragment = ((HomeActivity)getActivity()).listenFragment;

					boolean iAP_purchased = pref.getBoolean(Constants.PREF_PAID_IAP, false);
					
					if (position == 0) {
						if (iAP_purchased == false)  {
							toggleThisSwitchBack(toggleSwitch, Constants.PREF_INSTRUCTIONS_ON, true);
							hypnoticBoosterPurchase = false;
							showUnlockDialog();
						}
						else {
							if (listenFragment.isAudioInitialized()) {
								if (getFragmentManager().findFragmentByTag("100") == null) {
									new RewindSelectDialogFragment(SettingsFragment.this,
											Constants.K_SETTING_STARTOVER,
											Constants.AUDIO_INSTRUCTIONS_REWIND_TAG).show(getFragmentManager(), "100");
									
									toggleThisSwitchBack(toggleSwitch, Constants.PREF_INSTRUCTIONS_ON, false);
								}

							}
							else {
								prefEditor.putBoolean(Constants.PREF_INSTRUCTIONS_ON, isChecked);
								prefEditor.commit();
								listenFragment.initializeAudio();
							}
						}
											}
					else if ( position == 2) {
						if (iAP_purchased == false)  {
								toggleThisSwitchBack(toggleSwitch, Constants.PREF_AWAKEN_END, true);
								hypnoticBoosterPurchase = false;
								showUnlockDialog();
							}
							else {
										if (listenFragment.isAudioInitialized()) {
											if (getFragmentManager().findFragmentByTag("100") == null) {
												new RewindSelectDialogFragment(SettingsFragment.this,
														Constants.K_SETTING_STARTOVER1,
														Constants.AUDIO_AWAKEN_END_TAG).show(getFragmentManager(), "100");
												
												toggleThisSwitchBack(toggleSwitch, Constants.PREF_AWAKEN_END, true);
											}
										}
										else {
											prefEditor.putBoolean(Constants.PREF_AWAKEN_END, isChecked);
											prefEditor.commit();
											listenFragment.initializeAudio();
										}
							}						
					}
					else if ( position == 2) { //background cell

					}
					else if ( position == 1) {
						if (iAP_purchased == false)  {
							toggleThisSwitchBack(toggleSwitch, Constants.PREF_HYPNOTIC_BOOSTER, false);
							hypnoticBoosterPurchase = true;
							showUnlockDialog();
						}
						else {
							AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
							boolean wiredHeadsetOn = audioManager.isWiredHeadsetOn();
							boolean bluetoothHeadsetOn = audioManager.isBluetoothA2dpOn();

							if (wiredHeadsetOn == true || bluetoothHeadsetOn == true || isChecked == false || Constants.K_IS_HEADPHONE_SKIP == 1) {
								prefEditor.putBoolean(Constants.PREF_HYPNOTIC_BOOSTER, isChecked);
								prefEditor.commit();

								if (isChecked == false) {
									if (listenFragment.hypnoticPlayer != null) {
										listenFragment.hypnoticPlayer.stop();
									}
								}
								else {
									listenFragment.initializeHypnoticAudio();
									if (listenFragment.shouldPlayBackgroundMusic() && listenFragment.isAudioInitialized() && (listenFragment.voicePlayer.isPlaying() || listenFragment.backgroundPlayer.isPlaying()) ) {
										listenFragment.playHypnoticAudioWithFade();
									}
								}
							}
							else {
								Toast.makeText(getActivity(), Constants.K_LISTEN_HEADPHONES_MSG, Toast.LENGTH_LONG).show();
								toggleThisSwitchBack(toggleSwitch, Constants.PREF_HYPNOTIC_BOOSTER, false);
							}
						}								
					}
					else if (position == 4) {
						if (iAP_purchased == false)  {
							toggleThisSwitchBack(toggleSwitch, Constants.PREF_SHARING_PROMPT, true);
							hypnoticBoosterPurchase = false;							
							showUnlockDialog();
						}
						else {
							prefEditor.putBoolean(Constants.PREF_SHARING_PROMPT, isChecked);
						}								
					}

					prefEditor.commit();
				}
			});
			
			
			return v;
		}
		
	}
	
	private void toggleThisSwitchBack(final MySwitch toggleSwitch, final String key , final boolean defaultVal) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					toggleSwitch.setChecked(pref.getBoolean(key, defaultVal));									
				}
			}, 400);
	}
	
	private void showUnlockDialog() {
		
		((HomeActivity) getActivity()).showUnlockScreen();
		/*
		if (getFragmentManager().findFragmentByTag("dialog") == null) {
			 new UnlockFeatureDialogFragment(this).show(getFragmentManager(), "dialog");
		}
		*/
	}

	@Override
	public void unlockFeatureDismiss(boolean yesClicked) {
		((HomeActivity) getActivity()).purchaseProduct();
	}

	@Override
	public void rewindDismiss(boolean yesClicked, int TAG) {
		 ListenView_Fragment listenFragment = ((HomeActivity)getActivity()).listenFragment;

		if (TAG == Constants.AUDIO_INSTRUCTIONS_REWIND_TAG) {
			 prefEditor.putBoolean(Constants.PREF_INSTRUCTIONS_ON, !pref.getBoolean(Constants.PREF_INSTRUCTIONS_ON, false)); //reverse the value
			 prefEditor.commit();
		}
		else if (TAG == Constants.AUDIO_AWAKEN_END_TAG) {
			 prefEditor.putBoolean(Constants.PREF_AWAKEN_END, !pref.getBoolean(Constants.PREF_AWAKEN_END, true)); //reverse the value
			 prefEditor.commit();
		}
		 listenFragment.initializeAudio();
		 listAdapter.notifyDataSetChanged();
	}
	
	
}//end class
