package CustomComponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.surfcityapps.am.loseweight.Constants;
import com.surfcityapps.am.loseweight.HomeActivity;
import com.surfcityapps.am.loseweight.ListenView_Fragment;
import com.surfcityapps.am.loseweight.R;
import com.surfcityapps.am.loseweight.RewindSelectDialogFragment;
import com.surfcityapps.am.loseweight.RewindSelectDialogFragment.RewindSelectDialogListener;
import com.surfcityapps.am.loseweight.SettingsFragment;

import java.util.ArrayList;

import CustomComponents.MySwitch.OnChangeAttemptListener;
import CustomComponents.SlidingMenu.CanvasTransformer;
import CustomComponents.SlidingMenu.ContentSelectedListener;


public class CustomViewBehind extends ViewGroup implements RewindSelectDialogListener {

	private static final String TAG = "CustomViewBehind";

	private static final int MARGIN_THRESHOLD = 48; // dips
	private int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

	private CustomViewAbove mViewAbove;

	private View mContent;
	private View mSecondaryContent;
	private int mMarginThreshold;
	private int mWidthOffset;
	private CanvasTransformer mTransformer;
	private boolean mChildrenEnabled;
	private ContentSelectedListener mContentSelectedListener;	
	private SettingsFragment mFragment;
	private int currentType;
	private ArrayList<String> SoundDisplayNameAry;
	private ArrayList<String> mutarySongList;
	
	private ArrayList<Integer> backgroundExtentValuesArray;
	private ArrayList<String> backgroundExtentDisplayArray;

	private ArrayList<Integer> voiceLoopValuesArray;
	private int selectedPositionForChoice;
	
	public CustomViewBehind(Context context) {
		this(context, null);
	}

	public CustomViewBehind(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMarginThreshold = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				MARGIN_THRESHOLD, getResources().getDisplayMetrics());
	}

	public void setCustomViewAbove(CustomViewAbove customViewAbove) {
		mViewAbove = customViewAbove;
	}

	public void setCanvasTransformer(CanvasTransformer t) {
		mTransformer = t;
	}

	public void setWidthOffset(int i) {
		mWidthOffset = i;
		requestLayout();
	}
	
	public void setMarginThreshold(int marginThreshold) {
		mMarginThreshold = marginThreshold;
	}
	
	public int getMarginThreshold() {
		return mMarginThreshold;
	}

	public int getBehindWidth() {
		return mContent.getWidth();
	}

	

	public View getContent() {
		return mContent;
	}

	/**
	 * Sets the secondary (right) menu for use when setMode is called with SlidingMenu.LEFT_RIGHT.
	 * @param v the right menu
	 */
	public void setSecondaryContent(View v) {
		if (mSecondaryContent != null)
			removeView(mSecondaryContent);
		mSecondaryContent = v;
		addView(mSecondaryContent);
	}

	public View getSecondaryContent() {
		return mSecondaryContent;
	}

	public void setChildrenEnabled(boolean enabled) {
		mChildrenEnabled = enabled;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		if (mTransformer != null)
			invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		return !mChildrenEnabled;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return !mChildrenEnabled;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mTransformer != null) {
			canvas.save();
			mTransformer.transformCanvas(canvas, mViewAbove.getPercentOpen());
			super.dispatchDraw(canvas);
			canvas.restore();
		} else
			super.dispatchDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width-mWidthOffset, height);
		if (mSecondaryContent != null)
			mSecondaryContent.layout(0, 0, width-mWidthOffset, height);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width-mWidthOffset);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
		mContent.measure(contentWidth, contentHeight);
		if (mSecondaryContent != null)
			mSecondaryContent.measure(contentWidth, contentHeight);
	}

	private int mMode;
	private boolean mFadeEnabled;
	private final Paint mFadePaint = new Paint();
	private float mScrollScale;
	private Drawable mShadowDrawable;
	private Drawable mSecondaryShadowDrawable;
	private int mShadowWidth;
	private float mFadeDegree;

	public void setMode(int mode) {
		if (mode == SlidingMenu.LEFT || mode == SlidingMenu.RIGHT) {
			if (mContent != null)
				mContent.setVisibility(View.VISIBLE);
			if (mSecondaryContent != null)
				mSecondaryContent.setVisibility(View.INVISIBLE);
		}
		mMode = mode;
	}

	public int getMode() {
		return mMode;
	}

	public void setScrollScale(float scrollScale) {
		mScrollScale = scrollScale;
	}

	public float getScrollScale() {
		return mScrollScale;
	}

	public void setShadowDrawable(Drawable shadow) {
		mShadowDrawable = shadow;
		invalidate();
	}

	public void setSecondaryShadowDrawable(Drawable shadow) {
		mSecondaryShadowDrawable = shadow;
		invalidate();
	}

	public void setShadowWidth(int width) {
		mShadowWidth = width;
		invalidate();
	}

	public void setFadeEnabled(boolean b) {
		mFadeEnabled = b;
	}

	public void setFadeDegree(float degree) {
		if (degree > 1.0f || degree < 0.0f)
			throw new IllegalStateException("The BehindFadeDegree must be between 0.0f and 1.0f");
		mFadeDegree = degree;
	}

	public int getMenuPage(int page) {
		page = (page > 1) ? 2 : ((page < 1) ? 0 : page);
		if (mMode == SlidingMenu.LEFT && page > 1) {
			return 0;
		} else if (mMode == SlidingMenu.RIGHT && page < 1) {
			return 2;
		} else {
			return page;
		}
	}

	public void scrollBehindTo(View content, int x, int y) {
		int vis = View.VISIBLE;		
		if (mMode == SlidingMenu.LEFT) {
			if (x >= content.getLeft()) vis = View.INVISIBLE;
			scrollTo((int)((x + getBehindWidth())*mScrollScale), y);
		} else if (mMode == SlidingMenu.RIGHT) {
			if (x <= content.getLeft()) vis = View.INVISIBLE;
			scrollTo((int)(getBehindWidth() - getWidth() + 
					(x-getBehindWidth())*mScrollScale), y);
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			mContent.setVisibility(x >= content.getLeft() ? View.INVISIBLE : View.VISIBLE);
			mSecondaryContent.setVisibility(x <= content.getLeft() ? View.INVISIBLE : View.VISIBLE);
			vis = x == 0 ? View.INVISIBLE : View.VISIBLE;
			if (x <= content.getLeft()) {
				scrollTo((int)((x + getBehindWidth())*mScrollScale), y);				
			} else {
				scrollTo((int)(getBehindWidth() - getWidth() + 
						(x-getBehindWidth())*mScrollScale), y);				
			}
		}
		if (vis == View.INVISIBLE)
			Log.v(TAG, "behind INVISIBLE");
		setVisibility(vis);
	}

	public int getMenuLeft(View content, int page) {
		if (mMode == SlidingMenu.LEFT) {
			switch (page) {
			case 0:
				return content.getLeft() - getBehindWidth();
			case 2:
				return content.getLeft();
			}
		} else if (mMode == SlidingMenu.RIGHT) {
			switch (page) {
			case 0:
				return content.getLeft();
			case 2:
				return content.getLeft() + getBehindWidth();	
			}
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			switch (page) {
			case 0:
				return content.getLeft() - getBehindWidth();
			case 2:
				return content.getLeft() + getBehindWidth();
			}
		}
		return content.getLeft();
	}

	public int getAbsLeftBound(View content) {
		if (mMode == SlidingMenu.LEFT || mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() - getBehindWidth();
		} else if (mMode == SlidingMenu.RIGHT) {
			return content.getLeft();
		}
		return 0;
	}

	public int getAbsRightBound(View content) {
		if (mMode == SlidingMenu.LEFT) {
			return content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT || mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() + getBehindWidth();
		}
		return 0;
	}

	public boolean marginTouchAllowed(View content, int x) {
		int left = content.getLeft();
		int right = content.getRight();
		if (mMode == SlidingMenu.LEFT) {
			return (x >= left && x <= mMarginThreshold + left);
		} else if (mMode == SlidingMenu.RIGHT) {
			return (x <= right && x >= right - mMarginThreshold);
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return (x >= left && x <= mMarginThreshold + left) || 
					(x <= right && x >= right - mMarginThreshold);
		}
		return false;
	}

	public void setTouchMode(int i) {
		mTouchMode = i;
	}

	public boolean menuOpenTouchAllowed(View content, int currPage, float x) {
		switch (mTouchMode) {
		case SlidingMenu.TOUCHMODE_FULLSCREEN:
			return true;
		case SlidingMenu.TOUCHMODE_MARGIN:
			return menuTouchInQuickReturn(content, currPage, x);
		}
		return false;
	}

	public boolean menuTouchInQuickReturn(View content, int currPage, float x) {
		if (mMode == SlidingMenu.LEFT || (mMode == SlidingMenu.LEFT_RIGHT && currPage == 0)) {
			return x >= content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT || (mMode == SlidingMenu.LEFT_RIGHT && currPage == 2)) {
			return x <= content.getRight();
		}
		return false;
	}

	public boolean menuClosedSlideAllowed(float dx) {
		if (mMode == SlidingMenu.LEFT) {
			return dx > 0;
		} else if (mMode == SlidingMenu.RIGHT) {
			return dx < 0;
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	public boolean menuOpenSlideAllowed(float dx) {
		if (mMode == SlidingMenu.LEFT) {
			return dx < 0;
		} else if (mMode == SlidingMenu.RIGHT) {
			return dx > 0;
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	public void drawShadow(View content, Canvas canvas) {
		if (mShadowDrawable == null || mShadowWidth <= 0) return;
		int left = 0;
		if (mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - mShadowWidth;
		} else if (mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			if (mSecondaryShadowDrawable != null) {
				left = content.getRight();
				mSecondaryShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
				mSecondaryShadowDrawable.draw(canvas);
			}
			left = content.getLeft() - mShadowWidth;
		}
		mShadowDrawable.setBounds(left, 0, left + mShadowWidth, getHeight());
		mShadowDrawable.draw(canvas);
	}

	public void drawFade(View content, Canvas canvas, float openPercent) {
		if (!mFadeEnabled) return;
		final int alpha = (int) (mFadeDegree * 255 * Math.abs(1-openPercent));
		mFadePaint.setColor(Color.argb(alpha, 0, 0, 0));
		int left = 0;
		int right = 0;
		if (mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - getBehindWidth();
			right = content.getLeft();
		} else if (mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
			right = content.getRight() + getBehindWidth();			
		} else if (mMode == SlidingMenu.LEFT_RIGHT) {
			left = content.getLeft() - getBehindWidth();
			right = content.getLeft();
			canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
			left = content.getRight();
			right = content.getRight() + getBehindWidth();			
		}
		canvas.drawRect(left, 0, right, getHeight(), mFadePaint);
	}
	
	private boolean mSelectorEnabled = true;
	private Bitmap mSelectorDrawable;
	private View mSelectedView;

	private View footer;

	private ListAdapter adapter;

	private MySwitch repeatInductionSwitch;
	
	public void drawSelector(View content, Canvas canvas, float openPercent) {
		if (!mSelectorEnabled) return;
		if (mSelectorDrawable != null && mSelectedView != null) {
			String tag = (String) mSelectedView.getTag(R.id.selected_view);
			if (tag.equals(TAG+"SelectedView")) {
				canvas.save();
				int left, right, offset;
				offset = (int) (mSelectorDrawable.getWidth() * openPercent);
				if (mMode == SlidingMenu.LEFT) {
					right = content.getLeft();
					left = right - offset;
					canvas.clipRect(left, 0, right, getHeight());
					canvas.drawBitmap(mSelectorDrawable, left, getSelectorTop(), null);		
				} else if (mMode == SlidingMenu.RIGHT) {
					left = content.getRight();
					right = left + offset;
					canvas.clipRect(left, 0, right, getHeight());
					canvas.drawBitmap(mSelectorDrawable, right - mSelectorDrawable.getWidth(), getSelectorTop(), null);
				}
				canvas.restore();
			}
		}
	}
	
	public void setSelectorEnabled(boolean b) {
		mSelectorEnabled = b;
	}

	public void setSelectedView(View v) {
		if (mSelectedView != null) {
			mSelectedView.setTag(R.id.selected_view, null);
			mSelectedView = null;
		}
		if (v != null && v.getParent() != null) {
			mSelectedView = v;
			mSelectedView.setTag(R.id.selected_view, TAG+"SelectedView");
			invalidate();
		}
	}

	private int getSelectorTop() {
		int y = mSelectedView.getTop();
		y += (mSelectedView.getHeight() - mSelectorDrawable.getHeight()) / 2;
		return y;
	}

	public void setSelectorBitmap(Bitmap b) {
		mSelectorDrawable = b;
		refreshDrawableState();
	}

	public void setContent(View v,int type,ContentSelectedListener listener,SettingsFragment setFragment) {
		if (mContent != null)
			removeView(mContent);
		
		currentType = type;
		mFragment = setFragment;
		mContentSelectedListener = listener;
		mContent = v;
		addView(mContent);
		
		TextView title = (TextView) v.findViewById(R.id.side_menu_title_textView);
		if (title != null) {
			
			ListView list = (ListView) v.findViewById(R.id.side_screen_listView);
			list.addHeaderView(new View(setFragment.getActivity()));

			
			if (currentType == SlidingMenu.SLIDING_MUSIC) {
				title.setText("Background");
				SoundDisplayNameAry = new ArrayList<String>();
				SoundDisplayNameAry.add("Pure Embrace");
				SoundDisplayNameAry.add("Letting Go");
				SoundDisplayNameAry.add("Adrift");
				SoundDisplayNameAry.add("Floating");
				SoundDisplayNameAry.add("Brook");
				SoundDisplayNameAry.add("Beach");
				SoundDisplayNameAry.add("Rain");
				SoundDisplayNameAry.add("None");

				
				mutarySongList = new ArrayList<String>();
				mutarySongList.add("pure_embrace");
				mutarySongList.add("letting_go");
				mutarySongList.add("adrift"); 
				mutarySongList.add("floating");
				mutarySongList.add("brook");
				mutarySongList.add("beach");
				mutarySongList.add("rain");
				mutarySongList.add("None");

				list.addFooterView(new View(setFragment.getActivity()));

			}
			else {
				backgroundExtentDisplayArray = new ArrayList<String>();
				backgroundExtentDisplayArray.add("None");
				backgroundExtentDisplayArray.add("1 min");				
				backgroundExtentDisplayArray.add("5 minutes");
				backgroundExtentDisplayArray.add("10 minutes");
				backgroundExtentDisplayArray.add("20 minutes");
				backgroundExtentDisplayArray.add("30 minutes");				
				backgroundExtentDisplayArray.add("1 hour");
				backgroundExtentDisplayArray.add("8 hours");
				
				backgroundExtentValuesArray = new ArrayList<Integer>();
				backgroundExtentValuesArray.add(0);
				backgroundExtentValuesArray.add(60);				
				backgroundExtentValuesArray.add(5*60);
				backgroundExtentValuesArray.add(10*60);
				backgroundExtentValuesArray.add(20*60);
				backgroundExtentValuesArray.add(30*60);
				backgroundExtentValuesArray.add(60*60);
				backgroundExtentValuesArray.add(480*60);

				 if (currentType == SlidingMenu.SLIDING_DELAY_BACKGROUND) {
						title.setText("Delay Ending");			
						list.addFooterView(new View(setFragment.getActivity()));
					}
					else {
						voiceLoopValuesArray = new ArrayList<Integer>();
						voiceLoopValuesArray.add(1);
						voiceLoopValuesArray.add(2);				
						voiceLoopValuesArray.add(3);
						voiceLoopValuesArray.add(-1);
						
						title.setText("Play Count");
						LayoutInflater inflater = (LayoutInflater) mFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					    footer = inflater.inflate(R.layout.layout_side_menu_item_repeat_induction, null);
					    
						final SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);

					    repeatInductionSwitch = (MySwitch) footer.findViewById(R.id.sideMenu_repeatInductionToggleSwtich);
					    boolean currentValue = pref.getBoolean(Constants.PREF_REPEAT_INDUCTION,true);
					    repeatInductionSwitch.setChecked(currentValue);
						//Toast.makeText(mFragment.getActivity(), "Checked == " + String.valueOf(currentValue), Toast.LENGTH_SHORT).show();

					    repeatInductionSwitch.setOnChangeAttemptListener(new OnChangeAttemptListener() {
							@Override
							public void onChangeAttempted(boolean isChecked) {
								logicForRepeatInduction(isChecked);

							}
						});
						int value = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.PREF_VOICE_LOOP_VALUE, 1);					    
						footer.setVisibility((value > 1  || value == -1) ? View.VISIBLE : View.INVISIBLE);

						//Arif
						if(Constants.K_SHOW_INDUCTION == 0)
							footer.setVisibility(View.INVISIBLE);
						
						list.addFooterView(footer,null,false);
					}
			}
			
			Typeface helveticaBold = Typeface.createFromAsset(v.getResources().getAssets(),   "HelveticaNeueLTStd-Bd.otf");	
			title.setTypeface(helveticaBold);
			
			adapter = new ListAdapter();
			list.setAdapter(adapter);
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int index, long arg3) {
					index -= 1;
					selectedPositionForChoice = index;
					HomeActivity activity = (HomeActivity) mFragment.getActivity();
					ListenView_Fragment listenFragment = activity.listenFragment;
					
					SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);

					boolean iAP_purchased = pref.getBoolean(Constants.PREF_PAID_IAP, false);

					Editor prefEditor = pref.edit();
					if (currentType == SlidingMenu.SLIDING_MUSIC) {
						 Constants.logMessage("sound changing in music screen");
						 if (iAP_purchased) {
                                 // cases 1 -  voice playing - checked in -: is in background playing simply then
								 prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, SoundDisplayNameAry.get(index));
								 prefEditor.commit();

								 boolean isBackgroundPlaying = false;
								 
								 if (listenFragment.backgroundPlayer != null) {
									isBackgroundPlaying = listenFragment.backgroundPlayer.isPlaying();
									listenFragment.backgroundPlayer.stop();
									listenFragment.backgroundPlayer = null;
								}
								 
								 listenFragment.initializeBackgroundAudio();
								 boolean shouldPlayBgAudio = false;


								 try {
									 String currentAudioId = listenFragment.AllSoundsToPlay[listenFragment.nextSoundToPlayIndex - 1];
									 
									 boolean isVoicePlaying = false;
									 if (listenFragment.voicePlayer != null) {
										isVoicePlaying = listenFragment.voicePlayer.isPlaying();
									 }

									 if ((currentAudioId.equalsIgnoreCase(Constants.K_AUDIO_INDUCTION) || currentAudioId.equalsIgnoreCase(Constants.K_AUDIO_HYP_SESSION) || currentAudioId.equalsIgnoreCase(Constants.K_AUDIO_AWAKEN) ) && isVoicePlaying) {
									 //if (( currentAudioId == R.raw.induction_a || currentAudioId == R.raw.hypnotic_session || currentAudioId == R.raw.awaken) && isVoicePlaying) {
										 shouldPlayBgAudio = true;
									 }
                                     else if (listenFragment.hypnoticPlayer != null && listenFragment.hypnoticPlayer.isPlaying()) {
                                         shouldPlayBgAudio = true;
                                     }
								 }
                                 catch (Exception e) {

                                 }

                                 if (listenFragment.bgTimer != null) {
                                     shouldPlayBgAudio = true;
                                 }
						

								 if (shouldPlayBgAudio || isBackgroundPlaying) {
									listenFragment.playBackgroundAudioWithFade();
								}
						}
						 else {
							 showUnlockScreen(activity);
							 return;
						 }
					}
					else if (currentType == SlidingMenu.SLIDING_DELAY_BACKGROUND) {
						 if (iAP_purchased) {
										if (listenFragment.isAudioInitialized()) {
												if (mFragment.getFragmentManager().findFragmentByTag("100") == null) {
													new RewindSelectDialogFragment(CustomViewBehind.this,
															"Changing the Delay End during playback requires starting over.",
															Constants.AUDIO_BG_DELAY_TAG).show(mFragment.getFragmentManager(), "100");
													return;
												}
										}
										else {
											if (pref.getInt(Constants.PREF_VOICE_LOOP_VALUE, 1) == -1) {
												new RewindSelectDialogFragment(CustomViewBehind.this,
														"In order to use the Delay Ending feature, the Loop setting will be disabled.",
														Constants.AUDIO_LOOP_DISABLE_TAG).show(mFragment.getFragmentManager(), "100");
												return;
											}
											else {
													 prefEditor.putInt(Constants.PREF_BACKGROUND_DELAY_VALUE, backgroundExtentValuesArray.get(index));
													 prefEditor.putString(Constants.PREF_BACKGROUND_DELAY_NAME, backgroundExtentDisplayArray.get(index));											 
													prefEditor.commit();
													listenFragment.initializeAudio();
											}
										}
						 }
						 else {
							 showUnlockScreen(activity);
							 return;
						 }					 
					}
					else if (currentType == SlidingMenu.SLIDING_PLAY_COUNT) {
						 if (iAP_purchased) {
							 
							 if (listenFragment.isAudioInitialized()) {
								 new RewindSelectDialogFragment(CustomViewBehind.this,
											"Changing the Audio Loop during playback requires starting over.",
											Constants.AUDIO_PLAY_COUNT_TAG).show(mFragment.getFragmentManager(), "100");
								 return;
							}
							 
							 int currentValue = voiceLoopValuesArray.get(index);
							 if (currentValue == -1) {
								 loopSelectionLogic();
								 return;
							}
							 else {
									 if (listenFragment.isAudioInitialized()) {
										 new RewindSelectDialogFragment(CustomViewBehind.this,
													"Changing the Audio Loop during playback requires starting over.",
													Constants.AUDIO_PLAY_COUNT_TAG).show(mFragment.getFragmentManager(), "100");
										 return;
									}
									 else {
										 prefEditor.putInt(Constants.PREF_VOICE_LOOP_VALUE, currentValue);
										 prefEditor.commit();
										 listenFragment.voiceLoopLoaded = false;		
										 listenFragment.initializeAudio();
									 }
							 }
					
						}
						 else {
							 showUnlockScreen(activity);
							 return;							 
						 }
						 
					}

					 
					 // dismiss side menu
					if (mContentSelectedListener != null) {
						if (currentType != SlidingMenu.SLIDING_PLAY_COUNT) {
							mContentSelectedListener.contentSelected();
						}
						else {
							int value = voiceLoopValuesArray.get(index).intValue();
							footer.setVisibility( (value > 1 || value == -1)  ? View.VISIBLE : View.INVISIBLE);
							
							//Arif
							if(Constants.K_SHOW_INDUCTION == 0)
								footer.setVisibility(View.INVISIBLE);

							adapter.notifyDataSetChanged();
						}
					}
				}
			});
			
		} // title != null
	}
	
	private void showUnlockScreen(final HomeActivity activity) {
		if (mContentSelectedListener != null) {
			if (currentType != SlidingMenu.SLIDING_PLAY_COUNT) {
			}
            mContentSelectedListener.contentSelected();
        }

		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				activity.showUnlockScreen();				
			}
		}, 400);
	}
	
	private void logicForRepeatInduction(boolean checkedState) {
		HomeActivity activity = (HomeActivity) mFragment.getActivity();
		ListenView_Fragment listenFragment = activity.listenFragment;
		SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
		boolean iAP_purchased = pref.getBoolean(Constants.PREF_PAID_IAP, false);
		if (iAP_purchased) {
			if (listenFragment.isAudioInitialized()) {
				if (mFragment.getFragmentManager().findFragmentByTag("100") == null) {
					new RewindSelectDialogFragment(CustomViewBehind.this,
							"Changing the Repeat Induction during playback requires starting over.",
							Constants.AUDIO_REPEAT_INDUCTION_TAG).show(mFragment.getFragmentManager(), "100");
				}
				repeatInductionSwitch.setChecked(checkedState);
			}
			else {
				Editor prefEditor = pref.edit();
				prefEditor.putBoolean(Constants.PREF_REPEAT_INDUCTION, checkedState);	    
			    prefEditor.commit();					
				listenFragment.initializeAudio();
			}
		}
		else {
			repeatInductionSwitch.setChecked(!checkedState);
			showUnlockScreen(activity);
		}
	}
	
	private void loopSelectionLogic() {
		SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);

		Boolean awakenEndEnabled = pref.getBoolean(Constants.PREF_AWAKEN_END, true);
		Boolean delayEndEnabled = pref.getInt(Constants.PREF_BACKGROUND_DELAY_VALUE, 0) !=  0;

	    
	    if (awakenEndEnabled || delayEndEnabled) {
			new RewindSelectDialogFragment(CustomViewBehind.this,
					"In order to use the Loop feature, the background delay setting will be disabled.",
					Constants.AUDIO_DELAY_DISABLE_TAG).show(mFragment.getFragmentManager(), "100");
	    }
	    else {
	       setVoiceLoop();
	    }

	}
	
	private void setVoiceLoop() {
		int value =  voiceLoopValuesArray.get(selectedPositionForChoice);
		SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor prefEditor = pref.edit();
		prefEditor.putBoolean(Constants.PREF_AWAKEN_END, false);
		 prefEditor.putInt(Constants.PREF_BACKGROUND_DELAY_VALUE, backgroundExtentValuesArray.get(0));
		 prefEditor.putString(Constants.PREF_BACKGROUND_DELAY_NAME, backgroundExtentDisplayArray.get(0));
		 prefEditor.putInt(Constants.PREF_VOICE_LOOP_VALUE, value);

		footer.setVisibility((value > 1  || value == -1) ? View.VISIBLE : View.INVISIBLE);			
		 prefEditor.commit();
		 
		adapter.notifyDataSetChanged();
		HomeActivity activity = (HomeActivity) mFragment.getActivity();
		ListenView_Fragment listenFragment = activity.listenFragment;
			
		listenFragment.voiceLoopLoaded = false;
		listenFragment.initializeAudio();
	   
		if (mContentSelectedListener != null) {
			if (currentType != SlidingMenu.SLIDING_PLAY_COUNT) {
				mContentSelectedListener.contentSelected();
			}
		}

	}
/// ADAPTER FOR LIST
	
	private class ListAdapter extends BaseAdapter {

		Typeface helveticaRoman;
		
		@Override
		public int getCount() {
			if (currentType == SlidingMenu.SLIDING_MUSIC) {
				return SoundDisplayNameAry.size() ;	
			}
			else if (currentType == SlidingMenu.SLIDING_DELAY_BACKGROUND) {
				return backgroundExtentDisplayArray.size() ;	
			}
			else if (currentType == SlidingMenu.SLIDING_PLAY_COUNT) {
				return voiceLoopValuesArray.size() ;	
			}			
			return 0;
			
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup arg2) {
			View v = convertView;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.layout_side_menu_item, null);
				helveticaRoman = Typeface.createFromAsset(mFragment.getActivity().getAssets(),   "HelveticaNeueLTStd-Roman.otf");	

			}
			
			TextView titleTextView = (TextView) v.findViewById(R.id.side_menu_item_title);
			titleTextView.setTypeface(helveticaRoman);
			
			ImageView checkImageView = (ImageView) v.findViewById(R.id.side_menu_item_checkView);

			if (currentType == SlidingMenu.SLIDING_MUSIC) {
				String currentTitle = SoundDisplayNameAry.get(index);
				titleTextView.setText(currentTitle);
				String prefernceTitle = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE).getString(Constants.PREF_BACKGROUND_SOUND, "PURE EMBRACE");
				

				if (prefernceTitle.equalsIgnoreCase(currentTitle)) {
					v.setBackgroundColor(Color.parseColor("#25ffffff"));
					checkImageView.setVisibility(View.VISIBLE);
				}
				else {
					v.setBackgroundColor(Color.TRANSPARENT);
					checkImageView.setVisibility(View.INVISIBLE);					
				}
				
			}
			else if (currentType == SlidingMenu.SLIDING_DELAY_BACKGROUND) {
					String currentTitle = backgroundExtentDisplayArray.get(index);
					titleTextView.setText(currentTitle);
					
					Integer indexValue = backgroundExtentValuesArray.get(index);
					Integer preferenceValue = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.PREF_BACKGROUND_DELAY_VALUE, 0);
	
					if (indexValue.equals (preferenceValue)) {
							v.setBackgroundColor(Color.parseColor("#15ffffff"));
							checkImageView.setVisibility(View.VISIBLE);
					}
					else {
							v.setBackgroundColor(Color.TRANSPARENT);
							checkImageView.setVisibility(View.INVISIBLE);					
					}
			}
			else if (currentType == SlidingMenu.SLIDING_PLAY_COUNT) {
					Integer value = voiceLoopValuesArray.get(index);
					
					if (value == -1) {
							titleTextView.setText("Loop");
					}
					else {
							titleTextView.setText(String.valueOf(value));
					}
					Integer preferenceValue = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.PREF_VOICE_LOOP_VALUE, 1);
					if (value.equals (preferenceValue)) {
							v.setBackgroundColor(Color.parseColor("#15ffffff"));
							checkImageView.setVisibility(View.VISIBLE);
					}
					else {
							v.setBackgroundColor(Color.TRANSPARENT);
							checkImageView.setVisibility(View.INVISIBLE);					
					}			
			}
			
			return v;
		}
		
	}

	@Override
	public void rewindDismiss(boolean yesClicked, int TAG) {

		final HomeActivity activity = (HomeActivity) mFragment.getActivity();
		final ListenView_Fragment listenFragment = activity.listenFragment;
		
		if (TAG == Constants.AUDIO_BG_DELAY_TAG) {
			SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
			Editor prefEditor = pref.edit();
			prefEditor.putInt(Constants.PREF_BACKGROUND_DELAY_VALUE, backgroundExtentValuesArray.get(selectedPositionForChoice));
		    prefEditor.putString(Constants.PREF_BACKGROUND_DELAY_NAME, backgroundExtentDisplayArray.get(selectedPositionForChoice));
			if (pref.getInt(Constants.PREF_VOICE_LOOP_VALUE, 1) == -1) {
				listenFragment.voiceLoopLoaded = false;
				prefEditor.putInt(Constants.PREF_VOICE_LOOP_VALUE,1);
			}
			prefEditor.commit();
			listenFragment.initializeAudio();
		}
		else if (TAG == Constants.AUDIO_PLAY_COUNT_TAG)
		{
			SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
			Editor prefEditor = pref.edit();
			int value= voiceLoopValuesArray.get(selectedPositionForChoice);
			prefEditor.putInt(Constants.PREF_VOICE_LOOP_VALUE,value);
			footer.setVisibility((value > 1  || value == -1) ? View.VISIBLE : View.INVISIBLE);
			if (value == -1) {
			     prefEditor.putBoolean(Constants.PREF_AWAKEN_END, false);
				 prefEditor.putInt(Constants.PREF_BACKGROUND_DELAY_VALUE, backgroundExtentValuesArray.get(0));
				 prefEditor.putString(Constants.PREF_BACKGROUND_DELAY_NAME, backgroundExtentDisplayArray.get(0));
			}
			prefEditor.commit();
			listenFragment.voiceLoopLoaded = false;
			listenFragment.initializeAudio();
		}
		else if (TAG == Constants.AUDIO_DELAY_DISABLE_TAG) {
			setVoiceLoop();
			return;
		}
		else if (TAG == Constants.AUDIO_LOOP_DISABLE_TAG) {
			SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
			Editor prefEditor = pref.edit();
			prefEditor.putInt(Constants.PREF_BACKGROUND_DELAY_VALUE, backgroundExtentValuesArray.get(selectedPositionForChoice));
		    prefEditor.putString(Constants.PREF_BACKGROUND_DELAY_NAME, backgroundExtentDisplayArray.get(selectedPositionForChoice));
			listenFragment.voiceLoopLoaded = false;
			prefEditor.putInt(Constants.PREF_VOICE_LOOP_VALUE, 1);		    
			prefEditor.commit();
			listenFragment.initializeAudio();
		}
		else if (TAG == Constants.AUDIO_REPEAT_INDUCTION_TAG) {
			SharedPreferences pref = mFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
			Editor prefEditor = pref.edit();
			boolean currentValue = repeatInductionSwitch.isChecked();
			prefEditor.putBoolean(Constants.PREF_REPEAT_INDUCTION, currentValue);
			prefEditor.commit();
			listenFragment.initializeAudio();		
		
		}
		
		if (mContentSelectedListener != null) {
			if (currentType != SlidingMenu.SLIDING_PLAY_COUNT) {
				mContentSelectedListener.contentSelected();
			}
			else {
				adapter.notifyDataSetChanged();
			}
		}
		
		
	}// end method rewind dismiss
	
	
}// end class
