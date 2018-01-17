package com.surfcityapps.am.loseweight;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.askingpoint.android.AskingPoint;
import com.facebook.AppEventsLogger;
import com.surfcityapps.am.loseweight.RewindSelectDialogFragment.RewindSelectDialogListener;
import com.surfcityapps.am.loseweight.UnlockFeatureDialogFragment.UnlockFeatureDialogListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import CustomComponents.LetterSpacingTextView;
import CustomComponents.ProgressWheel;


public class ListenView_Fragment extends Fragment implements OnClickListener, OnCompletionListener,  OnSeekBarChangeListener, UnlockFeatureDialogListener, RewindSelectDialogListener {
	
    public MediaPlayer voicePlayer;
	public MediaPlayer backgroundPlayer;
	public MediaPlayer hypnoticPlayer;
    int timerVar = 0;

	OnAudioFocusChangeListener audioFocusListener;
	
	ImageButton playPauseButton,rewindButton;
	private TextView timerTextView,loopTextView;
	LetterSpacingTextView backgroundMusicTextView,hypnoticEnabledTextView;

	private boolean shouldPlayVoice,shouldPlayBackgroundExtend;
	
	private Timer timer;
	private CustomTimerTask customTimerTask;

	public Timer bgTimer;
	private BgTimerTask bgTimerTask;

    Timer fadeinBGTimer,fadeinHypTimer;

    private SeekBar voiceSeekBar,backgroundSeekBar,hypnoticSeekBar;
	private ImageView hypnoticSpeakerImageView;
	
	private ProgressWheel progressWheel,innerProgressWheel,animateWheel;

	private int totalTime,currentTime,backgroundTime,voiceLoopCounter;
	public boolean voiceLoopLoaded;

	public String AllSoundsToPlay[];
	public int nextSoundToPlayIndex;
	
	private double settingBackgroundPlayerVolume;
	private double settingHypnoticPlayerVolume;
	
	private boolean isFadingBackground,isFadingHypnotic;
	private boolean detectedDisabledHypnoticTouch;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	      View v = inflater.inflate(R.layout.layout_listen_fragment, container, false);

	      Constants.logMessage("creating listen view fragment");
	      
	      audioFocusListener = new OnAudioFocusChangeListener() {
	  		AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

	  	    public void onAudioFocusChange(int focusChange) {
	  	    	Constants.logMessage("audio focus changed");
	  	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	  	            // Pause playback
	  	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	  	            // Resume playback 
	  	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
	  	            // am.unregisterMediaButtonEventReceiver(audioFocusListener);
	  	        	Constants.logMessage("audio focus loss");
	  	            am.abandonAudioFocus(audioFocusListener);
	  	            // Stop playback
	  	            try {
		  	            ListenView_Fragment.this.onClick(playPauseButton);						
					} catch (NullPointerException e) {
						//failed to pause music
						Constants.logMessage("npe audio focus");
					}

	  	        }
	  	    }};
	  	    
	      progressWheel = (ProgressWheel) v.findViewById(R.id.listen_screen_spinner);
	      innerProgressWheel = (ProgressWheel) v.findViewById(R.id.listen_screen_inner_spinner);
	      animateWheel = (ProgressWheel) v.findViewById(R.id.listen_screen_animate_spinner);	      
	      
	      backgroundMusicTextView = (LetterSpacingTextView) v.findViewById(R.id.listen_screen_backgroundVoiceSelected_textView);
	      hypnoticEnabledTextView = (LetterSpacingTextView) v.findViewById(R.id.listen_screen_hypnoticEnabled_textView);
	      
	      Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "232MKSD-RoundMedium.TTF");  
	      backgroundMusicTextView.setTypeface(font);
	      hypnoticEnabledTextView.setTypeface(font);

	      HomeActivity activity = (HomeActivity) getActivity();
	      LetterSpacingTextView voiceTitleTextView = (LetterSpacingTextView) v.findViewById(R.id.listen_screen_voice_textView);
	      voiceTitleTextView.setTypeface(activity.helveticaLtCtn);
	      voiceTitleTextView.setText(Constants.K_LISTEN_VOICE,BufferType.NORMAL);
	      voiceTitleTextView.setLetterSpacing(1);

	      LetterSpacingTextView backgroundTitleTextView = (LetterSpacingTextView) v.findViewById(R.id.listen_screen_background_textView);
	     // backgrondTitleTextView.setTypeface(font);

	      LetterSpacingTextView hypnoticTitleTextView = (LetterSpacingTextView) v.findViewById(R.id.listen_screen_hypnotic_textView);
	      //hypnoticTitleTextView.setTypeface(font);
	      
	      backgroundTitleTextView.setText(Constants.K_LISTEN_BACKGROUND,BufferType.NORMAL);
	      backgroundTitleTextView.setLetterSpacing(1);
	      
	      hypnoticTitleTextView.setText(Constants.K_LISTEN_BOOSTER,BufferType.NORMAL);
	      hypnoticTitleTextView.setLetterSpacing(1);
	      
	      hypnoticTitleTextView.setTypeface(activity.helveticaLtCtn);
	      backgroundTitleTextView.setTypeface(activity.helveticaLtCtn);
	      hypnoticEnabledTextView.setTypeface(activity.helveticaLtCtn);
	      backgroundMusicTextView.setTypeface(activity.helveticaLtCtn);

	      hypnoticEnabledTextView.setText(Constants.K_LISTEN_DISABLED,BufferType.NORMAL);
	      hypnoticEnabledTextView.setLetterSpacing(1);
	      
	      backgroundMusicTextView.setText("",BufferType.NORMAL);
	      backgroundMusicTextView.setLetterSpacing(1);
	      
	      playPauseButton = (ImageButton) v.findViewById(R.id.listen_screen_play_pause_button);
	      playPauseButton.setOnClickListener(this);
	      
	      rewindButton = (ImageButton) v.findViewById(R.id.listen_screen_rewind_button);
	      rewindButton.setOnClickListener(this);

	      timerTextView = (TextView) v.findViewById(R.id.listen_screen_timer_textView);
	      //timerTextView.setTypeface(font);
	      timerTextView.setTypeface(activity.helveticaRoman);
	      
	      loopTextView = (TextView) v.findViewById(R.id.listen_screen_loop_textView);
	      loopTextView.setTypeface(activity.helveticaBD);

	      voiceSeekBar = (SeekBar) v.findViewById(R.id.listen_screen_voice_seekbar);
	      voiceSeekBar.setOnSeekBarChangeListener(this);
	      
	      backgroundSeekBar = (SeekBar) v.findViewById(R.id.listen_screen_background_seekbar);
	      backgroundSeekBar.setOnSeekBarChangeListener(this);
	      
	      hypnoticSeekBar = (SeekBar) v.findViewById(R.id.listen_screen_hypnotic_seekbar);
	      hypnoticSeekBar.setOnSeekBarChangeListener(this);
	      	      
	      hypnoticSpeakerImageView = (ImageView) v.findViewById(R.id.listen_screen_hypnotic_imageView);

	      SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

	      voiceSeekBar.setProgress(pref.getInt(Constants.PREF_VOLUME_VOICE, 45));
	      backgroundSeekBar.setProgress(pref.getInt(Constants.PREF_VOLUME_BACKGROUND, 45));
	      hypnoticSeekBar.setProgress(pref.getInt(Constants.PREF_VOLUME_HYPNOTIC, 45));

	      initializeAudio();
	      
	      initializeUnlockAndShareButtons(v);
	      return v;
	}

	public void initializeUnlockAndShareButtons(View v) {
		final HomeActivity activity = (HomeActivity) getActivity();
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		final boolean iAP_purchased = pref.getBoolean(Constants.PREF_PAID_IAP, false);
		
		ImageButton unlockButton = (ImageButton) v.findViewById(R.id.unlockImageButton);
		
		if (iAP_purchased) {
			unlockButton.setImageResource(R.drawable.listen_star_symbol);
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
	

	public void updateHypnoticSeekbar() 
	{
		String soundSelected = getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
		backgroundMusicTextView.setText(soundSelected.toUpperCase());
		
		 boolean iAP_purchased = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.PREF_PAID_IAP, false);
		 
		 //hypnoticSeekBar.setEnabled(true);
		 
		 if (iAP_purchased) {
			 boolean hypnoticEnabled = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.PREF_HYPNOTIC_BOOSTER, false);
			 hypnoticSeekBar.setOnTouchListener(null);
			 
			 if (hypnoticEnabled) {
				 hypnoticEnabledTextView.setText(Constants.K_LISTEN_ENABLED);
				 hypnoticSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress));
				 hypnoticSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_thumb));
				 hypnoticSpeakerImageView.setImageResource(R.drawable.sound_icon);
			 }
			 else {
				 hypnoticEnabledTextView.setText(Constants.K_LISTEN_DISABLED);
				   hypnoticSeekBar.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent arg1) {
							
							return true;
						}
					}); 
				 hypnoticSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.disabled_seekbar_progress));
				 hypnoticSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_thumb_disabled));
				 hypnoticSpeakerImageView.setImageResource(R.drawable.sound_icon_disabled);				 
			 }

		}
		else {
		      hypnoticSeekBar.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						if (!detectedDisabledHypnoticTouch) {
							detectedDisabledHypnoticTouch = true;
							((HomeActivity) getActivity()).showUnlockScreen();							
							
							new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									detectedDisabledHypnoticTouch = false;
								}
							}, 1500);
						}
						
						return true;
					}
				});
			      
				hypnoticSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.disabled_seekbar_progress));
				hypnoticSeekBar.setThumb(getResources().getDrawable(R.drawable.slider_thumb_disabled));
				 hypnoticSpeakerImageView.setImageResource(R.drawable.sound_icon_disabled);				 				
		}
		 			
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden == false) {
			updateHypnoticSeekbar();
		}
	}
	
	public void initializeAudio() {
		Constants.logMessage("initializing audio");

		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (voicePlayer != null) {
					Constants.logMessage("stopping voice player");
					voicePlayer.stop();
					voicePlayer.reset(); //arif
					voicePlayer.release();
					voicePlayer = null;
				}				
			}
		});

		AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		am.abandonAudioFocus(audioFocusListener);
		
		if (timer != null)
			timer.cancel();
		
		if (bgTimer != null)
			bgTimer.cancel();
		
		timer = null;
		bgTimer = null;
		
		if (customTimerTask != null)
			customTimerTask.cancel();

		if (bgTimerTask != null)
			bgTimerTask.cancel();

		customTimerTask = null;
		bgTimerTask = null;

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

		playPauseButton.setBackgroundResource(R.drawable.play_button_state);
		//playPauseButton.setImageResource(R.drawable.listen_screen_play_track);
		rewindButton.setBackgroundResource(R.drawable.rewind_button_state);
		rewindButton.setEnabled(false);

		shouldPlayVoice = true;
		//rewindButton.setImageResource(R.drawable.listen_screen_rewind_disabled);
		totalTime =  currentTime = 0;
		backgroundTime = pref.getInt(Constants.PREF_BACKGROUND_DELAY_VALUE, 0) * 1000;
	    
		  if (voiceLoopLoaded == false) {
			   voiceLoopLoaded = true;
		       voiceLoopCounter = pref.getInt(Constants.PREF_VOICE_LOOP_VALUE, 1);
		  }

		updateRepeatCount();
		
		Constants.logMessage("resetting time : total-time == " + String.valueOf(totalTime));

	    progressWheel.setProgress(0);
	    innerProgressWheel.setProgress(0);

	
		ArrayList<String> soundsToPlay =  new ArrayList<String>();
		
		if (pref.getBoolean(Constants.PREF_INSTRUCTIONS_ON, true) == true ) {
			soundsToPlay.add(Constants.K_AUDIO_INSTRUCTIONS);
		}
		
		  int count = voiceLoopCounter;
		  if (voiceLoopCounter == -1) {
		        count = 65;
		  }
		
		  boolean repeatInduction =pref.getBoolean(Constants.PREF_REPEAT_INDUCTION, true) ;
		    for (int i = 0; i < count; i++) {

		        if (i == 0 || repeatInduction) {
		        	soundsToPlay.add(Constants.K_AUDIO_INDUCTION);
		        }
		        soundsToPlay.add(Constants.K_AUDIO_HYP_SESSION);
		    }

		    voicePlayer = new MediaPlayer();
		    AssetFileDescriptor descriptor;
			try {
				descriptor = getActivity().getAssets().openFd(soundsToPlay.get(0));
				voicePlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
				descriptor.close();
				voicePlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
	   // voicePlayer = MediaPlayer.create(getActivity(),soundsToPlay.get(0));
		voicePlayer.setVolume((float) (voiceSeekBar.getProgress() * 0.01), (float) (voiceSeekBar.getProgress() * 0.01));
	    voicePlayer.setOnCompletionListener(this);
	    
		if (pref.getBoolean(Constants.PREF_AWAKEN_END, true) == true ) {
			soundsToPlay.add(Constants.K_AUDIO_AWAKEN);
		}
	    
		AllSoundsToPlay = new String[soundsToPlay.size()];
		
		for (int i = 0; i < soundsToPlay.size(); i++) {
			String soundName = soundsToPlay.get(i);
			AllSoundsToPlay[i] = soundName;
		}

		calculateTotalAudioTime();
		initializeBackgroundAudio();

		initializeHypnoticAudio();
		
	    nextSoundToPlayIndex = 1;
	}
	
	
	
	public void initializeHypnoticAudio() {
		Constants.logMessage("initialized hypnotic audio");

		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (hypnoticPlayer != null) {
					hypnoticPlayer.stop();
					hypnoticPlayer.reset(); //arif
					hypnoticPlayer.release();
					hypnoticPlayer = null;
				}
			
				hypnoticPlayer = new MediaPlayer();
					AssetFileDescriptor descriptor;
					try {
						descriptor = getActivity().getAssets().openFd(Constants.K_AUDIO_HYP_BOOSTER);
						hypnoticPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
						descriptor.close();
						hypnoticPlayer.prepare();
					} catch (Exception e) {
						e.printStackTrace();
					}
				hypnoticPlayer.setVolume((float) ((hypnoticSeekBar.getProgress() * 0.01) * 0.15), (float) ((hypnoticSeekBar.getProgress() * 0.01) * 0.15));
				hypnoticPlayer.setLooping(true);

			}
		});
	}
	
	public void initializeBackgroundAudio() {
		Constants.logMessage("initializing background audio");

		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (backgroundPlayer != null) {
					backgroundPlayer.stop();
					backgroundPlayer.reset(); //arif
					backgroundPlayer.release();
					backgroundPlayer = null;
				}
			
				backgroundPlayer = new MediaPlayer();
				String backgroundSoundSelected  = getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
				String initializeSoundName = null;
				if (backgroundSoundSelected.equalsIgnoreCase("None")) {
				    backgroundPlayer = null;
				    return;
				}
				else if (backgroundSoundSelected.equalsIgnoreCase("Pure Embrace")) {
				   // backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.pure_embrace);
					initializeSoundName = Constants.K_AUDIO_PURE;
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Resonance")) {
					SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
					Editor edit = pref.edit();
					edit.putString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
					edit.commit();
					initializeSoundName = Constants.K_AUDIO_PURE;
				   // backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.pure_embrace);
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Constellation")) {
					SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
					Editor edit = pref.edit();
					edit.putString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
					edit.commit();
					initializeSoundName = Constants.K_AUDIO_PURE;
				    //backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.pure_embrace);
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Floating")) {
				    //backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.floating);
					initializeSoundName = Constants.K_AUDIO_FLOATING;
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Adrift")) {
				    //backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.adrift);
					initializeSoundName = Constants.K_AUDIO_ADRIFT;
				}				
				else if(backgroundSoundSelected.equalsIgnoreCase("Letting Go")) {
				    //backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.letting_go);
					initializeSoundName = Constants.K_AUDIO_LETTING_GO;
				}				
				else if(backgroundSoundSelected.equalsIgnoreCase("Brook")) {
				   // backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.brook);
					initializeSoundName = Constants.K_AUDIO_BROOK;
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Beach")) {
				    //backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.beach);
					initializeSoundName = Constants.K_AUDIO_BEACH;
				}
				else if(backgroundSoundSelected.equalsIgnoreCase("Rain")) {
					//backgroundPlayer = MediaPlayer.create(getActivity(), R.raw.beach);
					initializeSoundName = Constants.K_AUDIO_RAIN;
				}

				AssetFileDescriptor descriptor;
				try {
					descriptor = getActivity().getAssets().openFd(initializeSoundName);
					backgroundPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
					backgroundPlayer.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (backgroundPlayer != null) {
					Constants.logMessage("background player initialized with voice == " + backgroundSoundSelected);

				    backgroundPlayer.setVolume((float) ( (backgroundSeekBar.getProgress() * 0.01) * 0.2), (float)((backgroundSeekBar.getProgress() * 0.01) * 0.2));
					backgroundPlayer.setLooping(true);
				}
			}
		});

	}
	
	public boolean shouldPlayBackgroundMusic(){
		String currentVoiceSound = AllSoundsToPlay[nextSoundToPlayIndex-1];
		
		if (currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_INDUCTION) || currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_HYP_SESSION) || currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_AWAKEN)) {
			Constants.logMessage("should play background sound");
			return true;
		}
		
		Constants.logMessage("should not play background sound yet");

		return false;
	}
	
	
	
	public boolean isAudioInitialized(){
		if (currentTime >= 500) {
			return true;
		}
		
		return false;
	}
	
	private void calculateTotalAudioTime() {
	
		if (voiceLoopCounter == -1) {
			totalTime = 8000000;
			timerTextView.setText(Constants.K_LISTEN_LOOP);
			return;
		}
		else {
			  for (String soundId : AllSoundsToPlay) {
				  MediaPlayer player = new MediaPlayer();
					AssetFileDescriptor descriptor;
					try {
						descriptor = getActivity().getAssets().openFd(soundId);
						player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
						descriptor.close();
						player.prepare();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
					totalTime += player.getDuration();
					player.reset();
					player.release();
					player = null;
			} 
		}
		
		 /*
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		if (pref.getBoolean(Constants.PREF_INSTRUCTIONS_ON, true) == true ) {
			Constants.logMessage("instructions counted in total audio length");
			MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.instructions);
		}
		
		
		MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.induction_a);
		totalTime += player.getDuration();

		player = MediaPlayer.create(getActivity(), R.raw.hypnotic_session);
		totalTime += player.getDuration();
		
		if (pref.getBoolean(Constants.PREF_AWAKEN_END, true) == true ) {
			Constants.logMessage("awaken counted in total audio length");
			player = MediaPlayer.create(getActivity(), R.raw.awaken);
			totalTime += player.getDuration();
		}
		*/
		
	    Constants.logMessage("duration == " + String.valueOf(totalTime));
		int min = totalTime/(60 * 1000);
		int sec = (totalTime%(60 * 1000))/1000;
		
		final int bgSoundRemainMin = backgroundTime/(60 * 1000);
		final int bgSoundRemainSec = (backgroundTime%(60 * 1000))/1000;
		
		Constants.logMessage("total audio length == " + String.valueOf(totalTime));

		timerTextView.setText(String.format("%02d", min) + ":" + String.format("%02d", sec)  + " + " + String.format("%02d", bgSoundRemainMin) + ":" + String.format("%02d", bgSoundRemainSec)  + " Background");
		Constants.logMessage("audio length readable == " + timerTextView.getText().toString());
	}
	
	private class  CustomTimerTask extends TimerTask {

		@Override
		public void run() {

			//Constants.logMessage("timer tick");

			if (getActivity() == null) {
				Constants.logMessage("activity null returning from timer");
				return;
			}	
			
			//update UI
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					if (voicePlayer.isPlaying() == false) {
						Constants.logMessage("voice player not playing returning from timer task");
						return;
					}
					
					currentTime = currentTime + 500;
					//int timeLeft = voicePlayer.getDuration() - voicePlayer.getCurrentPosition();
					int timeLeft = totalTime - currentTime;
				    int min = timeLeft/(60 * 1000);;
					int sec =  (timeLeft%(60 * 1000))/1000;
					   
					if (min <= 0 &&sec <= 12 && backgroundTime == 0) {
						if (isFadingBackground == false) {
							if (backgroundPlayer != null && backgroundPlayer.isPlaying()) {
								isFadingBackground = true;
								stopBackgroundAudioWithFade();
							}
						}

						if (isFadingHypnotic == false) {
							if (hypnoticPlayer != null && hypnoticPlayer.isPlaying()) {
								isFadingHypnotic = true;
								stopHypnoticAudioWithFade();
							}							
						}

					}
					
					final int bgSoundRemainMin = backgroundTime/(60 * 1000);
					final int bgSoundRemainSec = (backgroundTime%(60 * 1000))/1000;
							
					final float currentProgressPercentage = currentTime/(float)totalTime;
					
					if (voiceLoopCounter != -1) {
						progressWheel.setProgress((int) (currentProgressPercentage*360));
						innerProgressWheel.setProgress((int) (currentProgressPercentage*360) );
					}
					
					//timerTextView.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
					if (voiceLoopCounter ==  -1) {
						timerTextView.setText(Constants.K_LISTEN_LOOP);
					}
					else {
						timerTextView.setText(String.format("%02d", min) + ":" + String.format("%02d", sec)  + " + " + String.format("%02d", bgSoundRemainMin) + ":" + String.format("%02d", bgSoundRemainSec)  + " Background");
					}

					String backgroundPlay = "";
					if (backgroundPlayer != null) {
						backgroundPlay = " background is playing == " + String.valueOf(backgroundPlayer.isPlaying()); 
					}
					
					String hypnoticPlay = "";
					if (hypnoticPlayer != null) {
						hypnoticPlay = " hypnotic is playing == " + String.valueOf(hypnoticPlayer.isPlaying()); 
						//Constants.logMessage();	
					}

                    timerVar++;
                    //Constants.logMessage("timer Val " + String.valueOf(timerVar));
                    if (timerVar %2 == 0) {
                        AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

                        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                        Constants.logMessage("system volume == " + String.valueOf(currentVolume));

                        if (backgroundPlayer != null) {
                            Constants.logMessage("background is playing == " + String.valueOf(backgroundPlayer.isPlaying()) );
                        }

                        if (hypnoticPlayer != null) {
                            Constants.logMessage("hypnotic is playing == " + String.valueOf(hypnoticPlayer.isPlaying()));
                        }
                        Constants.logMessage("audio length readable == " + timerTextView.getText().toString());
                    }
				}
			});
			
		}
		
	}
		

	private class BgTimerTask extends TimerTask {

		@Override
		public void run() {
			if (getActivity() == null) {
				Constants.logMessage("activity null returning from timer");
				return;
			}	
			
			//update UI
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (backgroundPlayer == null && hypnoticPlayer == null ) {
						Constants.logMessage("background player null returning from timer task");
						backgroundTime = 0;
						updateSessionCount();
						initializeAudio();
						rewindButton.setEnabled(false);
						return;
					}
					
					backgroundTime = backgroundTime - 500;
					
					if (backgroundTime <= 0) {
						backgroundTime = 0;
						updateSessionCount();
						initializeAudio();
						rewindButton.setEnabled(false);
					}

				   int timeLeft = totalTime - currentTime;

			       int min = timeLeft/(60 * 1000);;
				   int sec =  (timeLeft%(60 * 1000))/1000;
				   
					final int bgSoundRemainMin = backgroundTime/(60 * 1000);
					final int bgSoundRemainSec = (backgroundTime%(60 * 1000))/1000;
					
					if (bgSoundRemainMin <= 0 && bgSoundRemainSec == 12) {
						if (isFadingBackground == false) {
							if (backgroundPlayer != null && backgroundPlayer.isPlaying()) {
								isFadingBackground = true;
								stopBackgroundAudioWithFade();
							}
						}

						if (isFadingHypnotic == false) {
							if (hypnoticPlayer != null && hypnoticPlayer.isPlaying()) {
								isFadingHypnotic = true;
								stopHypnoticAudioWithFade();
							}							
						}

					}
										
					if (voiceLoopCounter == -1) {
				        min = sec = 0;
				    }					
					timerTextView.setText(String.format("%02d", min) + ":" + String.format("%02d", sec)  + " + " + String.format("%02d", bgSoundRemainMin) + ":" + String.format("%02d", bgSoundRemainSec)  + " Background");

                    String backgroundPlay = "";
                    if (backgroundPlayer != null) {
                        backgroundPlay = " background is playing == " + String.valueOf(backgroundPlayer.isPlaying());
                    }

                    String hypnoticPlay = "";
                    if (hypnoticPlayer != null) {
                        hypnoticPlay = " hypnotic is playing == " + String.valueOf(hypnoticPlayer.isPlaying());
                        //Constants.logMessage();
                    }

                    Constants.logMessage("timer tick- audio len == " + timerTextView.getText().toString() + backgroundPlay + hypnoticPlay);
				}
			});
			
		}
		
	}
	
	private void updateRepeatCount() {
		if (voiceLoopCounter == -1) {
			loopTextView.setVisibility(View.INVISIBLE);
			return;
		}
		loopTextView.setVisibility(View.VISIBLE);
		loopTextView.setText(String.valueOf(voiceLoopCounter));
	}

	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Constants.logMessage("listern fragment onDestroy");
	}

	@Override
	public void onClick(View buttonClicked) {
		Constants.logMessage("play/pause clicked");

			if (buttonClicked == playPauseButton) {
				if (shouldPlayVoice) {
					if (voicePlayer.isPlaying()) {
						Constants.logMessage("voice player playing .. will be paused");

						playPauseButton.setBackgroundResource(R.drawable.play_button_state);
						//playPauseButton.setImageResource(R.drawable.listen_screen_play_track);
						rewindButton.setEnabled(true);	
						killVoiceTimer();
						pausePlayer(voicePlayer);
						pausePlayer(backgroundPlayer);
						pausePlayer(hypnoticPlayer);
					}
					else {
						Constants.logMessage("Voice player paused.. will start playback");
						playPauseButton.setBackgroundResource(R.drawable.pause_button_state);
						//playPauseButton.setImageResource(R.drawable.listen_screen_pause_track);
						//rewindButton.setImageResource(R.drawable.listen_screen_rewind_active);
						rewindButton.setEnabled(true);
			
						AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
						// Request audio focus for playback
						int result = am.requestAudioFocus(audioFocusListener,
						                                 // Use the music stream.
						                                 AudioManager.STREAM_MUSIC,
						                                 // Request permanent focus.
						                                 AudioManager.AUDIOFOCUS_GAIN);
						   
						if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						    //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
						    // Start playback.
						    
							voicePlayer.start();
							Constants.logMessage("voice player resumed");

							String currentVoiceSound = AllSoundsToPlay[nextSoundToPlayIndex-1];
							
							if (currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_INDUCTION) || currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_HYP_SESSION) || currentVoiceSound.equalsIgnoreCase(Constants.K_AUDIO_AWAKEN)) {
								if (backgroundPlayer != null) {
									playBackgroundAudioWithFade();
								}
								
								SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

								if (pref.getBoolean(Constants.PREF_HYPNOTIC_BOOSTER, true) == true ) {
									if (hypnoticPlayer != null && hypnoticPlayer.isPlaying() == false) {
										playHypnoticAudioWithFade();
									}
								}
								
							}
							
							timer = new Timer();					
							customTimerTask = new CustomTimerTask();
							
							timer.scheduleAtFixedRate(customTimerTask, 1000, 500);
						}

					}
				}
				else {
					 // should play background music
					if ((backgroundPlayer != null && backgroundPlayer.isPlaying()) || (hypnoticPlayer != null && hypnoticPlayer.isPlaying()) ) {
						pausePlayer(backgroundPlayer);
						pausePlayer(hypnoticPlayer);
						killBgTimer();
						playPauseButton.setBackgroundResource(R.drawable.play_button_state);
						rewindButton.setEnabled(true);
					}
					else {
						playPauseButton.setBackgroundResource(R.drawable.pause_button_state);
						rewindButton.setEnabled(true);
			
						AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
						// Request audio focus for playback
						int result = am.requestAudioFocus(audioFocusListener,
						                                 // Use the music stream.
						                                 AudioManager.STREAM_MUSIC,
						                                 // Request permanent focus.
						                                 AudioManager.AUDIOFOCUS_GAIN);
						   
						if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						    //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
						    // Start playback.
						    
							playBackgroundAudioWithFade();
							if (hypnoticPlayer != null) {
								playHypnoticAudioWithFade();
							}

							bgTimer = new Timer();					
							bgTimerTask = new BgTimerTask();
							bgTimer.scheduleAtFixedRate(bgTimerTask, 1000, 500);
						}
 					}
				}
		
			}// end play pause clicked 
			else if (buttonClicked == rewindButton) {
				Constants.logMessage("rewind selected");

				new RewindSelectDialogFragment(ListenView_Fragment.this,Constants.APP_TITLE,-1).show(getFragmentManager(), "100");
			}
		
	}

    
	private void killBgTimer() {
		if (bgTimer != null) {
			bgTimer.cancel();
			bgTimer.purge();
		}

		bgTimer = null;
		
		if (bgTimerTask != null) {
			bgTimerTask.cancel();
		}
		bgTimerTask = null;
	}
	
	private void killVoiceTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}

		timer = null;
		
		if (customTimerTask != null) {
			customTimerTask.cancel();
		}
		customTimerTask = null;
	}
	
	private void pausePlayer (MediaPlayer player) {
		if (player != null && player.isPlaying()) //arif
		{
			player.pause();
		}
	}
	

	@Override
	public void onCompletion(MediaPlayer arg0) {
		Constants.logMessage("on completion of audio called");

		if (getActivity() == null) {
			return;
		}	
		
		voicePlayer = new MediaPlayer();
		
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

		AssetFileDescriptor descriptor = null ;

		if (nextSoundToPlayIndex  + 1 <= AllSoundsToPlay.length) {
			Constants.logMessage("loading next audio");
			
			try {
				if (AllSoundsToPlay[nextSoundToPlayIndex].equalsIgnoreCase(Constants.K_AUDIO_INDUCTION)) {
					Constants.logMessage("loading induction");
					descriptor = getActivity().getAssets().openFd(Constants.K_AUDIO_INDUCTION);
					voicePlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
					voicePlayer.prepare();

				}
				else if (AllSoundsToPlay[nextSoundToPlayIndex] .equalsIgnoreCase(Constants.K_AUDIO_HYP_SESSION)) {
					Constants.logMessage("loading hypnotic session");
					descriptor = getActivity().getAssets().openFd(Constants.K_AUDIO_HYP_SESSION);
					voicePlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
					voicePlayer.prepare();
				}
				else {
					Constants.logMessage("loading awaken");
					descriptor = getActivity().getAssets().openFd(Constants.K_AUDIO_AWAKEN);
					voicePlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
					voicePlayer.prepare();

				}
			}
			catch (Exception e) {
				
			}
			//voicePlayer = MediaPlayer.create(getActivity(), AllSoundsToPlay[nextSoundToPlayIndex]);
					
			
			float vol = (float) (voiceSeekBar.getProgress() * 0.01);
			voicePlayer.setVolume((float) (voiceSeekBar.getProgress() * 0.01), (float) (voiceSeekBar.getProgress() * 0.01));
			voicePlayer.start();
			voicePlayer.setOnCompletionListener(this);
			nextSoundToPlayIndex++;
			
			if (shouldPlayBackgroundMusic() ) {
				if (backgroundPlayer != null && backgroundPlayer.isPlaying() == false)  {
						playBackgroundAudioWithFade();
				}
				
				
				if (pref.getBoolean(Constants.PREF_HYPNOTIC_BOOSTER, true) == true ) {
					if (hypnoticPlayer != null && hypnoticPlayer.isPlaying() == false) {
						playHypnoticAudioWithFade();
					}
				}

			}
			
			return;
		}
		
		Constants.logMessage("session ended");
		
		if (backgroundTime > 0) {
			shouldPlayVoice = false;
			shouldPlayBackgroundExtend = true;
			currentTime = totalTime;
		    progressWheel.setProgress(360);
			innerProgressWheel.setProgress(360);
			bgTimer = new Timer();					
			bgTimerTask = new BgTimerTask();
			bgTimer.scheduleAtFixedRate(bgTimerTask, 1000, 500);
			return;
		}


		
		if (backgroundPlayer != null) {
			backgroundPlayer.stop();
		}
		
		if (hypnoticPlayer != null) {
			hypnoticPlayer.stop();
		}

        if (timer != null) {
            timer.cancel();
        }

		timer = null;

        if (customTimerTask != null) {
            customTimerTask.cancel();
        }

		customTimerTask = null;
		
		AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		am.abandonAudioFocus(audioFocusListener);
		
		//cancel any existing notification
		NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(Constants.ON_GOING_NOTIFICATION_ID);
		updateSessionCount();
	
		initializeAudio();

	}

   private void updateSessionCount () {
	   animateWheel.setVisibility(View.VISIBLE);
	   	AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
	    animation1.setDuration(1000);
	    animateWheel.startAnimation(animation1);
	    //animation1.setStartOffset(5000);s
	    animation1.setAnimationListener(new AnimationListener(){

	        @Override
	        public void onAnimationEnd(Animation arg0) {
	            animateWheel.setAlpha(1.0f);
	            animateWheel.setVisibility(View.INVISIBLE);
	        }

	        @Override
	        public void onAnimationRepeat(Animation arg0) {

	        }

	        @Override
	        public void onAnimationStart(Animation arg0) {

	        }

	    });
	    
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

		Editor prefEditor = pref.edit();
		int currentSessionCount = pref.getInt(Constants.PREF_SESSION_COUNT, 0);
		
		currentSessionCount++;
		prefEditor.putInt(Constants.PREF_SESSION_COUNT, currentSessionCount);
		prefEditor.commit();
		
		Constants.logMessage("Session Count" + currentSessionCount);
		Constants.logMessage("Server Session Count" + pref.getInt(Constants.PREF_SESSIONEND_SERVER_VALUE, -1));
		
		boolean iAP_purchased = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(Constants.PREF_PAID_IAP, false);
		
		if (!iAP_purchased) {
			if (currentSessionCount  >= pref.getInt(Constants.PREF_SESSIONEND_SERVER_VALUE, -1)){
				 try {
					 	prefEditor.putInt(Constants.PREF_SESSION_COUNT, 0);
						prefEditor.commit();
						
					 HomeActivity activity = (HomeActivity) getActivity();
					 activity.showUnlockScreen();
				} catch (Exception e) {
					Constants.logMessage("exception in share prompt");
					//e.printStackTrace();
				}
			}
		}
		
		int value = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.PREF_VOICE_LOOP_VALUE, 1);
		if (value == 3) 
		{
			//Toast.makeText(getActivity(), "It's working", Toast.LENGTH_LONG).show();
		}
		
		try {
			
			 //Arif FB IAP Event
	        AppEventsLogger logger = AppEventsLogger.newLogger(this.getActivity());
	        logger.logEvent("SessionEnd");

	        //AskingPoint 
			AskingPoint.requestCommandsWithTag(getActivity(), "SessionEnd");
		} catch (Exception e) {
			// asking point internal crash
		}
   }


    public void playBackgroundAudioWithFade() {
		if (backgroundPlayer == null) {
			return;
		}
		Constants.logMessage("playing background audio with fade");

		backgroundPlayer.setVolume(0, 0);
		Constants.logMessage("background player volume set to 0");

		backgroundPlayer.start();

		final float currentVolume = (float) ((backgroundSeekBar.getProgress() * 0.01) *0.2);
		
		settingBackgroundPlayerVolume = 0;

        if (fadeinBGTimer != null) {
            fadeinBGTimer.cancel();
            fadeinBGTimer = null;
        }

		fadeinBGTimer = new Timer();
        fadeinBGTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
					
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							try {
								//Constants.logMessage("new vol == " + String.valueOf(settingBackgroundPlayerVolume + 0.1));
								//settingBackgroundPlayerVolume = (float) (settingBackgroundPlayerVolume + (currentVolume/(12.0)*0.1));
								settingBackgroundPlayerVolume = (float) (settingBackgroundPlayerVolume + (currentVolume/(Constants.K_FADE_IN_SECONDS)*Constants.K_FADE_IN_STEPS));
								if(settingBackgroundPlayerVolume >= currentVolume) {
									Constants.logMessage("background player volume set to " + String.valueOf(currentVolume));
									backgroundPlayer.setVolume(currentVolume, currentVolume);
                                    fadeinBGTimer.cancel();
									//this.cancel();
								}
								else {
									Constants.logMessage("background player volume set to " + String.valueOf(settingBackgroundPlayerVolume));
									backgroundPlayer.setVolume((float)settingBackgroundPlayerVolume, (float)settingBackgroundPlayerVolume);
								}
							} catch (Exception e) {
								Constants.logMessage("exception in setting volume for background player");
								try {
									
									if (backgroundPlayer ==  null) {
										initializeBackgroundAudio();
									}
									backgroundPlayer.setVolume(currentVolume, currentVolume);
                                    fadeinBGTimer.cancel();
								} catch (Exception e2) {
								
								}

							}

						}
					});
				

			}
		}, 0, Constants.K_FADE_INTERVAL);			
		
		
	}

	public void playHypnoticAudioWithFade() {
			Constants.logMessage("playing hypnotic with fade");

			//if no headset cancel and set flag in settings switch
			AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
			boolean wiredHeadsetOn = audioManager.isWiredHeadsetOn();
			boolean bluetoothHeadsetOn = audioManager.isBluetoothA2dpOn();

			if (wiredHeadsetOn == false && bluetoothHeadsetOn == false && Constants.K_IS_HEADPHONE_SKIP == 0) {
				Editor prefEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
				prefEditor.putBoolean(Constants.PREF_HYPNOTIC_BOOSTER, false);
				prefEditor.commit();
				Constants.logMessage("earphones not being used.. disabling hypnotic");

				initializeHypnoticAudio();
				return;
			}
			
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					hypnoticPlayer.setVolume(0, 0);
					hypnoticPlayer.start();					
				}
			});

			final float currentVolume = (float) ((hypnoticSeekBar.getProgress() * 0.01) *0.15);
			
			settingHypnoticPlayerVolume = 0;


            if (fadeinHypTimer != null) {
                fadeinHypTimer.cancel();
                fadeinHypTimer = null;
            }

            fadeinHypTimer = new Timer();

			fadeinHypTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								try {
									//Constants.logMessage("new vol == " + String.valueOf(settingBackgroundPlayerVolume + 0.1));
									settingHypnoticPlayerVolume = (float) (settingHypnoticPlayerVolume + (currentVolume/(Constants.K_FADE_IN_SECONDS)*Constants.K_FADE_IN_STEPS));
									
									if(settingHypnoticPlayerVolume >= currentVolume) {
										Constants.logMessage("hypnotic player volume set to " + String.valueOf(currentVolume));

										hypnoticPlayer.setVolume(currentVolume, currentVolume);
										fadeinHypTimer.cancel();
									}
									
									Constants.logMessage("hypnotic player volume set to " + String.valueOf(settingHypnoticPlayerVolume));
									hypnoticPlayer.setVolume((float)settingHypnoticPlayerVolume, (float)settingHypnoticPlayerVolume);
								} catch (Exception e) {
									Constants.logMessage("exception in setting volume for hypnotic player");
									fadeinHypTimer.cancel();
									if (hypnoticPlayer ==  null) {
										initializeHypnoticAudio();
									}
									hypnoticPlayer.setVolume(currentVolume, currentVolume);
								}
								
							}
						});
				}
			}, 0, Constants.K_FADE_INTERVAL);
							
	}	
	
	public void stopBackgroundAudioWithFade() {
		Constants.logMessage("stop background audio with fade");

		float currentVolume = (float) ((backgroundSeekBar.getProgress() * 0.01)  *0.2);
		
		settingBackgroundPlayerVolume = currentVolume;
		
		final float decrementStep = currentVolume/(Constants.K_FADE_OUT_SECONDS*Constants.K_FADE_OUT_STEPS);
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			TimerTask t = this;

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (backgroundPlayer != null) {
							//Constants.logMessage("new vol == " + String.valueOf(settingBackgroundPlayerVolume + 0.1));
							try {
								settingBackgroundPlayerVolume = (float) (settingBackgroundPlayerVolume - decrementStep);
								if(settingBackgroundPlayerVolume <= 0.0f) {
									Constants.logMessage("backgound player volume set to 0 in fading");
									isFadingBackground = false;
									backgroundPlayer.stop();
									backgroundPlayer.setVolume(0.0f, 0.0f);
									t.cancel();
								}
								else {
									backgroundPlayer.setVolume((float)settingBackgroundPlayerVolume, (float)settingBackgroundPlayerVolume);
								}
							} catch (IllegalStateException e) {

							}
						}
						else {
							isFadingBackground = false;
							t.cancel();
						}
					}
				});
				
			}
		}, 0, Constants.K_FADE_INTERVAL);
		
	}

	public void stopHypnoticAudioWithFade() {
		Constants.logMessage("stop hypnotic audio with fade");

		float currentVolume = (float)((hypnoticSeekBar.getProgress() * 0.01) *0.15);
		
		settingHypnoticPlayerVolume = currentVolume;
		final float decrementStep = currentVolume/(Constants.K_FADE_OUT_SECONDS*Constants.K_FADE_OUT_STEPS);

		new Timer().scheduleAtFixedRate(new TimerTask() {
			TimerTask t = this;

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (hypnoticPlayer != null) {
							//Constants.logMessage("new vol == " + String.valueOf(settingBackgroundPlayerVolume + 0.1));
						settingHypnoticPlayerVolume = (float) (settingHypnoticPlayerVolume - decrementStep);
							if(settingHypnoticPlayerVolume <= 0) {
								Constants.logMessage("hypnotic player volume set to 0 in fade");
								hypnoticPlayer.stop();
								hypnoticPlayer.setVolume(0, 0);
								isFadingHypnotic = false;
								t.cancel();
							}
							else {
								try {
									hypnoticPlayer.setVolume((float)settingHypnoticPlayerVolume, (float)settingHypnoticPlayerVolume);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						else {
							isFadingHypnotic = false;
							t.cancel();
						}
					}
				});

			}
		}, 0, Constants.K_FADE_INTERVAL);
	}		
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		//Constants.logMessage(" slider value " + String.valueOf(seekBar.getProgress()));
	    SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
	    Editor edit = pref.edit();
		if (seekBar == voiceSeekBar) {
            Constants.logMessage(" voice slider value changed = " + String.valueOf(seekBar.getProgress()));
			//Constants.logMessage("slider == voice");
			if (voicePlayer != null) {
				edit.putInt(Constants.PREF_VOLUME_VOICE, progress);
				voicePlayer.setVolume((float) (seekBar.getProgress() * 0.01), (float) (seekBar.getProgress() * 0.01));
			}
		}
		else if (seekBar == backgroundSeekBar) {
            Constants.logMessage(" background slider value changed = " + String.valueOf(seekBar.getProgress()));
            if (fadeinBGTimer != null) {
                fadeinBGTimer.cancel();
                fadeinBGTimer = null;
            }

			edit.putInt(Constants.PREF_VOLUME_BACKGROUND, progress);			
			if (backgroundPlayer != null) {
			    backgroundPlayer.setVolume((float) ( (backgroundSeekBar.getProgress() * 0.01) * 0.2), (float)((backgroundSeekBar.getProgress() * 0.01) * 0.2));
			}
		}		
		else if (seekBar == hypnoticSeekBar) {
            Constants.logMessage(" hypnotic slider value changed = " + String.valueOf(seekBar.getProgress()));			edit.putInt(Constants.PREF_VOLUME_HYPNOTIC, progress);
			if (hypnoticPlayer != null) {
                if (fadeinHypTimer != null) {
                    fadeinHypTimer.cancel();
                    fadeinHypTimer = null;
                }
				float newVolume = (float) ((seekBar.getProgress() * 0.01) * 0.15);
				//Constants.logMessage(" new vol == "  + String.valueOf(newVolume));
				hypnoticPlayer.setVolume((float) ((seekBar.getProgress() * 0.01) * 0.15), (float) ((seekBar.getProgress() * 0.01) * 0.15));
			}
		}	
		
		edit.commit();
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void unlockFeatureDismiss(boolean yesClicked) {
		((HomeActivity) getActivity()).settingsBaseFragment.settings.hypnoticBoosterPurchase = true;
		((HomeActivity) getActivity()).purchaseProduct();				
	}

	@Override
	public void rewindDismiss(boolean yesClicked, int TAG) {
		initializeAudio();
	}

	
}//end class
