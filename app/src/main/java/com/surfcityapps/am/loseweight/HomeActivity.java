package com.surfcityapps.am.loseweight;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.askingpoint.android.AskingPoint;
import com.askingpoint.android.AskingPoint.OnTagCommandListener;
import com.askingpoint.android.Command;
import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;
import com.apsalar.sdk.Apsalar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class HomeActivity extends FragmentActivity implements StatusCallback {

	private UiLifecycleHelper facebookUIHelper;
	
	FragmentTabHost tabHost = null;
	
	ImageView imageV_listenTab, imageV_settings,imageV_instructions , imageV_interact , imageV_more;
	
	Typeface helveticalLT,helveticaMD,helveticaRoman,helveticaLtCtn,helveticaBD,helveticaTh;
	
	public ListenView_Fragment listenFragment;
	public SettingsBaseFragment settingsBaseFragment;
	private InstructionsView_Fragment instructionsFragment;
	private InteractView_Fragment interactFragment;
	private MoreView_Fragment moreViewFragment;
	
	IInAppBillingService inAppBilling_Service;
	private int PURCHASE_INTENT_CODE = 122;
	private int UNLOCK_INTENT_CODE = 123;
	private int ITEM_PURCHASED_CODE = 100;
	private int ERROR_RESPONSE_UNAVAILABLE	 = 2;
	private int ERROR_PRODUCT_NOT_PURCHASED	 = 3;
	private int ERROR_UNKNOWN	 = 4;

	private boolean isRestoring,isPurchasing;

	private int skuDetailsResponse;
	private int buyIntentBundleResponse;
	
	public boolean isBluetoothHeadSetPlugged;
	
	boolean AskingPointTagRequested = false;
	private ArrayList<String> tagsRequested = new ArrayList<String>(0);
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this);
        AskingPoint.onStart(this, Constants.ASKING_POINT_APP_ID);	

        if (!AskingPointTagRequested) {
			AskingPointTagRequested = true;
		
			
			AskingPoint.requestCommandsWithTag(this, "SessionStart", new OnTagCommandListener() {
				
				@Override
				public boolean onTagCommand(String tag, Command<?> command) {

					 if(command != null) {

							if (command.type ==  Command.Type.PAYLOAD) {
							        try {
										JSONArray array =  command.data.getJSONArray("allTags");
										showAskingPointRandomTag(array);
									} catch (JSONException e) {
										e.printStackTrace();
									}
							}
		                }
					 return false;
				}
			});
			
			//AskingPoint 
			AskingPoint.requestCommandsWithTag(this, "ShowAd");

		}
	}
	
	private void showAskingPointRandomTag(final JSONArray tagsArray) throws JSONException {
		if (tagsArray.length() == tagsRequested.size()) {
			return;
		}
		
		String tag = null;
		do {
			tag = tagsArray.getString(new Random().nextInt(tagsArray.length()));
		} while (tagsRequested.indexOf(tag) != -1 );
		
		tagsRequested.add(tag);
		
		//Toast.makeText(HomeActivity.this, "Requested " + tag, Toast.LENGTH_SHORT).show();
		AskingPoint.requestCommandsWithTag(this, tag, new OnTagCommandListener() {
			
			@Override
			public boolean onTagCommand(String tag, Command<?> command) {

				 if(command != null) {
					 
				 }
				 else {
					 try {
						showAskingPointRandomTag(tagsArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				 }
				 return false;
			}
		});

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	     telephonyManager.listen(new  PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
	      	     
		setContentView(R.layout.layout_activity_home);
		
		//set default for settings
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		Editor prefEditor = pref.edit();
		
		//set true for Pro and false fro Free version
	    if(Constants.K_IS_PRO == 1)
	    	prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
		
		Config config = new Config(Constants.USER_VOICE_APP_ID);
		
		Map<String, String> customFields = new HashMap<String, String>();
		customFields.put("IAP Purchased", (pref.getBoolean(Constants.PREF_PAID_IAP, false)) ? "Yes" : "No");
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pInfo.versionName;
			String OsVersion = android.os.Build.VERSION.RELEASE;
			String device = android.os.Build.MODEL;
			customFields.put("Device Name", device);
			customFields.put("App Name", Constants.APP_TITLE);
			customFields.put("App Version", version);
			customFields.put("Device OS", OsVersion);

			if(Constants.K_IS_AMAZON == 1)
				customFields.put("Amazon Appstore", "Yes");

			config.setCustomFields(customFields);
			config.setShowForum(false);
			config.setShowPostIdea(false);
		} catch (NameNotFoundException e) {
		
		}
		
		UserVoice.init(config, this);
		
		genHashKey();
		
		initializeFonts();
		//ApSalar
		Apsalar.setFBAppId(Constants.FB_ID);
		Apsalar.startSession(this,Constants.AS_ID,Constants.AS_KEY);
		Apsalar.event("app_start");

		//Crittercism.initialize(getApplicationContext(), "55dcc1f2cb114f1000e34bea");//Constants.CRITTERCISM_APP_ID
		FlurryAgent.init(this, Constants.FLURRY_APP_ID);
		
		boolean instructionsOn = pref.getBoolean(Constants.PREF_INSTRUCTIONS_ON, true);
		Constants.logMessage("instructions On == " + String.valueOf(instructionsOn));
		prefEditor.putBoolean(Constants.PREF_INSTRUCTIONS_ON, instructionsOn);
		
		boolean awakenEnd = pref.getBoolean(Constants.PREF_AWAKEN_END, true);
		Constants.logMessage("awaken end == " + String.valueOf(awakenEnd));		
		prefEditor.putBoolean(Constants.PREF_AWAKEN_END, awakenEnd);
		
		String backgroundSound = pref.getString(Constants.PREF_BACKGROUND_SOUND, "Pure Embrace");
		Constants.logMessage("background sound == " + backgroundSound);		
		prefEditor.putString(Constants.PREF_BACKGROUND_SOUND, backgroundSound);
		
		boolean hypnoticEnabled = pref.getBoolean(Constants.PREF_HYPNOTIC_BOOSTER, false);
		Constants.logMessage("hypnotic enabled == " + String.valueOf(hypnoticEnabled));		
		prefEditor.putBoolean(Constants.PREF_HYPNOTIC_BOOSTER, hypnoticEnabled);
		
		boolean sharingPrompt = pref.getBoolean(Constants.PREF_SHARING_PROMPT, true);
		Constants.logMessage("sharing prompt == " + String.valueOf(sharingPrompt));		
		prefEditor.putBoolean(Constants.PREF_SHARING_PROMPT, sharingPrompt);

		//Arif 
		if(pref.getInt("justInstalled", -1) == -1) //Running for first time
		{
			prefEditor.putInt("justInstalled", 1);
			
			if(Constants.K_AWAKEN_SWITCH == 0)
				prefEditor.putBoolean(Constants.PREF_AWAKEN_END, false);
		}

		prefEditor.commit();

		listenFragment = new ListenView_Fragment();
		settingsBaseFragment = new SettingsBaseFragment();
		instructionsFragment = new InstructionsView_Fragment();
		interactFragment = new InteractView_Fragment();
		moreViewFragment = new MoreView_Fragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.frameLayout, listenFragment);
		transaction.add(R.id.frameLayout, settingsBaseFragment);
		transaction.add(R.id.frameLayout, instructionsFragment);
		transaction.add(R.id.frameLayout, interactFragment);
		transaction.add(R.id.frameLayout, moreViewFragment);

		transaction.hide(settingsBaseFragment);
		transaction.hide(instructionsFragment);
		transaction.hide(interactFragment);
		transaction.hide(moreViewFragment);
		
		transaction.commit();
		
		
		//Arif - Read config file

		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		    		XMLParser parser = new XMLParser();
		    		String xml = parser.getXmlFromUrl(Constants.K_CONFIG_URL); // getting XML
		    		
//		    		Constants.logMessage("XML is:" + xml);
		    		
		    		Document doc = parser.getDomElement(xml); // getting DOM element
		    		NodeList nl = doc.getElementsByTagName("item");
		    		
//		    		Constants.logMessage("Node is:" + nl.getLength());
		    		
		    		if(nl.getLength() > 0)
		    		{
		    			Element e = (Element) nl.item(0);
		    			String kiipCount = parser.getValue(e, "sessionendkiipcount");
		    			String sessionEndCount = parser.getValue(e, "sessionendcount"); // cost child value
		    			
//		    			Constants.logMessage("KiipCount is:" + kiipCount);
		    			
		    			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		    			Editor prefEditor = pref.edit();
		    			prefEditor.putInt(Constants.PREF_KIIPCOUNT_VALUE, Integer.parseInt(kiipCount));
		    			prefEditor.putInt(Constants.PREF_SESSIONEND_SERVER_VALUE, Integer.parseInt(sessionEndCount));
		    			prefEditor.commit();
		    		}

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		});

		thread.start(); 

		///////////////////////////
		
		facebookUIHelper = new UiLifecycleHelper(this, this);

		imageV_listenTab = (ImageView) findViewById(R.id.ListenTabButton);

		imageV_listenTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					unSelectAllTabs();
					setCurrentVisibleFragment(listenFragment);
					imageV_listenTab.setImageDrawable(getResources().getDrawable(R.drawable.tab_listen_selected));;
					createdSelectedTabHeight(imageV_listenTab);
			}
		});
		
		//getResources().getDimensionPixelSize(R.dimen.typo14)
		
		imageV_settings = (ImageView) findViewById(R.id.SettingTabLayout);
		imageV_settings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					unSelectAllTabs();
					setCurrentVisibleFragment(settingsBaseFragment);
					imageV_settings.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_selected));;
					createdSelectedTabHeight(imageV_settings);					
			}
		});
		
		
		imageV_instructions = (ImageView) findViewById(R.id.InstructionsTabLayout);
		imageV_instructions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					unSelectAllTabs();
					setCurrentVisibleFragment(instructionsFragment);
					imageV_instructions.setImageDrawable(getResources().getDrawable(R.drawable.tab_instructions_selected));;
					createdSelectedTabHeight(imageV_instructions);										
			}
		});
		
		
		imageV_interact = (ImageView) findViewById(R.id.InteractTabLayout);
		imageV_interact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					unSelectAllTabs();
					setCurrentVisibleFragment(interactFragment);
					imageV_interact.setImageDrawable(getResources().getDrawable(R.drawable.tab_interact_selected));;
					createdSelectedTabHeight(imageV_interact);															
			}
		});
		
		imageV_more = (ImageView) findViewById(R.id.MoreTabLayout);
		imageV_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					unSelectAllTabs();
					setCurrentVisibleFragment(moreViewFragment);
					imageV_more.setImageDrawable(getResources().getDrawable(R.drawable.tab_more_selected));;
					createdSelectedTabHeight(imageV_more);															
			}
		});
		
		unSelectAllTabs();
		
		imageV_listenTab.performClick();
		
		
	}

	private void initializeFonts() {
		helveticalLT = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Lt.otf");	
		helveticaMD = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Md.otf");	
		helveticaBD = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Bd.otf");	
		helveticaRoman = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Roman.otf");	
		helveticaLtCtn = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-LtCn.otf");	
		helveticaTh = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Th.otf");	

	}
	
	@SuppressWarnings("unused")
	private void genHashKey() {
		try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;

                    md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String something = new String(Base64.encode(md.digest(), 0));
                    Constants.logMessage(something);
                    //Log.e("hash key", something);
        } 
        }
        catch (NameNotFoundException e1) {
            // TODO Auto-generated catch block
            //Log.e("name not found", e1.toString());
        }

             catch (NoSuchAlgorithmException e) {
                 //Log.e("no such an algorithm", e.toString());
            }
             catch (Exception e){
                 //Log.e("exception", e.toString());
             }
	}
	
	private void unSelectAllTabs() {
		imageV_listenTab.setImageDrawable(getResources().getDrawable(R.drawable.tab_listen_unselected));
		imageV_settings.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_unselected));;
		imageV_instructions.setImageDrawable(getResources().getDrawable(R.drawable.tab_instructions_unselected));;
		imageV_interact.setImageDrawable(getResources().getDrawable(R.drawable.tab_interact_unselected));;
		imageV_more.setImageDrawable(getResources().getDrawable(R.drawable.tab_more_unselected));;
	
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(0,getResources().getDimensionPixelSize(R.dimen.tabBarNonSelected));		
		params.weight = 1;
		imageV_listenTab.setLayoutParams(params);
		imageV_settings.setLayoutParams(params);
		imageV_instructions.setLayoutParams(params);
		imageV_interact.setLayoutParams(params);
		imageV_more.setLayoutParams(params);
	}

	private void createdSelectedTabHeight (ImageView tabImageView) {
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(0,getResources().getDimensionPixelSize(R.dimen.tabBarHeight));		
		params.weight = 1;
		tabImageView.setLayoutParams(params);
	}
	private void setCurrentVisibleFragment(Fragment toMakeVisibleFragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.hide(listenFragment);
		transaction.hide(settingsBaseFragment);
		transaction.hide(instructionsFragment);
		transaction.hide(interactFragment);
		transaction.hide(moreViewFragment);

		if (toMakeVisibleFragment != settingsBaseFragment) {
			if (settingsBaseFragment.settings != null) {
				settingsBaseFragment.settings.dismissingSettings();
			}
		}
		
		toMakeVisibleFragment.setRetainInstance(true);
		if (toMakeVisibleFragment.isDetached()) {
			 transaction.add(R.id.frameLayout, toMakeVisibleFragment);
		}
		transaction.show(toMakeVisibleFragment);
		transaction.commitAllowingStateLoss();
	}
	
	@Override
	public void onBackPressed() {
		if (settingsBaseFragment.hasFragmentToPop()) {
			Constants.logMessage("popping Settings Fragment");
			return;
		}
		
		Constants.logMessage("going home");

		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(i);
		
		/*
		if (listenFragment.voicePlayer != null && listenFragment.voicePlayer.isPlaying()) {
			listenFragment.onClick(listenFragment.playPauseButton);
		}
		super.onBackPressed();
		*/
	}
	
	private class PurchaseProductAsync extends AsyncTask<Void, Void, Bundle> {


		@Override
		protected Bundle doInBackground(Void... arg0) {
			
			ArrayList<String> skuList = new ArrayList<String>();
			skuList.add(Constants.IAP_IDENTIFIER);
			Bundle querySkus = new Bundle();
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
			try {
				Bundle skuDetails = inAppBilling_Service.getSkuDetails(3, getPackageName(), "inapp", querySkus);
				skuDetailsResponse = skuDetails.getInt("RESPONSE_CODE");
				//Constants.logMessage(skuDetails.toString());
				int response = skuDetails.getInt("RESPONSE_CODE");
				if (response == 0) {
				   //ArrayList responseList    = skuDetails.getStringArrayList("DETAILS_LIST");
					//Constants.logMessage(responseList.toString());
					Bundle buyIntentBundle = inAppBilling_Service.getBuyIntent(3, getPackageName(),
							   Constants.IAP_IDENTIFIER, "inapp", null);
					response = buyIntentBundle.getInt("RESPONSE_CODE");
					buyIntentBundleResponse = buyIntentBundle.getInt("RESPONSE_CODE");
					if (response == 0) {
						return buyIntentBundle;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Bundle buyIntentBundle) {
			super.onPostExecute(buyIntentBundle);
			isPurchasing = false;
			String skuResponse = String.valueOf(skuDetailsResponse);
			String buyIntentResponse = String.valueOf(buyIntentBundleResponse);
			//Toast.makeText(HomeActivity.this, "SkuDetails response code : " + skuResponse  + "Buy Intent Response : " + buyIntentResponse  , Toast.LENGTH_LONG).show();
			
			if (buyIntentBundleResponse == 7) {
				HomeActivity.this.restoreProduct();
				return;
			}
			
			if (buyIntentBundle != null) {
				PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
				try {
					startIntentSenderForResult(pendingIntent.getIntentSender(),
							   PURCHASE_INTENT_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
							   Integer.valueOf(0));
				} catch (SendIntentException e) {
					e.printStackTrace();
				}
			}
			else {
				Toast.makeText(HomeActivity.this, Constants.IAP_CONNECTION_ERROR_TEXT, Toast.LENGTH_SHORT).show();
			}
		}// end on post execute
		
	}
	
	private class RestoreProductAsync extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			Bundle ownedItems;
			try {
				ownedItems = inAppBilling_Service.getPurchases(3, getPackageName(), "inapp", null);
				int response = ownedItems.getInt("RESPONSE_CODE");
				Constants.logMessage(" response for restore " + String.valueOf(response));
				if (response == 0 ) {
				   ArrayList<String> ownedSkus =   ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				   for (String sku : ownedSkus) {
					   	if (sku.equalsIgnoreCase(Constants.IAP_IDENTIFIER)) {
							return ITEM_PURCHASED_CODE;
						}
				   }
				   
					return ERROR_PRODUCT_NOT_PURCHASED;

				}
				else if ( response == 3) {
					return ERROR_RESPONSE_UNAVAILABLE;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			
			return ERROR_UNKNOWN;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			isRestoring = false;
			
			if (result == ITEM_PURCHASED_CODE) {
				Editor prefEditor = getSharedPreferences("HomeActivity",Context.MODE_PRIVATE).edit();
					prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
					prefEditor.commit();
					Toast.makeText(HomeActivity.this,  Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();

					updatePurchaseStatus();
				Apsalar.event("iap_restore");
			}
			else if (result == ERROR_RESPONSE_UNAVAILABLE) {
	            Toast.makeText(HomeActivity.this,  Constants.IAP_CONNECTION_ERROR_TEXT, Toast.LENGTH_SHORT).show();
			}
			else if (result == ERROR_PRODUCT_NOT_PURCHASED) {
	            Toast.makeText(HomeActivity.this, Constants.IAP_NOT_OWNED_TEXT, Toast.LENGTH_SHORT).show();
			}
			else {
	            Toast.makeText(HomeActivity.this, Constants.K_RESTORE_ERROR, Toast.LENGTH_SHORT).show();
			}
		}
		
	} // end restoreAsync
	
	
	public void purchaseProduct(){
		if (inAppBilling_Service == null) {
				isPurchasing = true;
			   bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),   billingConnection , Context.BIND_AUTO_CREATE);
			   return;
		}
		
        settingsBaseFragment.settings.userPurchased();
        
		new PurchaseProductAsync().execute((Void)null);
	}
	
	public void restoreProduct() {
		if (inAppBilling_Service == null) {
		   isRestoring = true;
		   bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),   billingConnection , Context.BIND_AUTO_CREATE);
		   return;
		}
	
		new RestoreProductAsync().execute((Void)null);
	}
	
	ServiceConnection billingConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		       inAppBilling_Service = null;			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			inAppBilling_Service = IInAppBillingService.Stub.asInterface(service);	
			if (isPurchasing)
				purchaseProduct();
			else
				restoreProduct();
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Session.getActiveSession() != null) {
			 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

		}
		 
		    facebookUIHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		            //Log.e("Activity", String.format("Error: %s", error.toString()));
		        		Constants.logMessage("error in sharing");
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		        		Constants.logMessage("shared");
		        	}
		    });
		 
		if (requestCode == PURCHASE_INTENT_CODE) {
				
				if (resultCode == Activity.RESULT_OK) {
				      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
				      try {
				            JSONObject jo = new JSONObject(purchaseData);
				            String sku = jo.getString("productId");
				            if (sku.equalsIgnoreCase(Constants.IAP_IDENTIFIER)) {
								Editor prefEditor = getPreferences(Context.MODE_PRIVATE).edit();
								prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
								prefEditor.commit();
					            Toast.makeText(this, Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();
						
					            updatePurchaseStatus();
								Apsalar.event("iap_success");
					            
					            //Arif FB IAP Event
						        AppEventsLogger logger = AppEventsLogger.newLogger(this);
						        logger.logEvent("IAP Purchased");
					            					            
					            Constants.logMessage("iap_success");
							}
				      	 }
				          catch (JSONException e) {
				             e.printStackTrace();
				          }
				}
		}
		else if ( requestCode == UNLOCK_INTENT_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				updatePurchaseStatus();
			}
		}
	}// end method
	
	private void updatePurchaseStatus() {
		listenFragment.initializeUnlockAndShareButtons(listenFragment.getView());
		moreViewFragment.initializeUnlockAndShareButtons(moreViewFragment.getView());
		interactFragment.initializeUnlockAndShareButtons(interactFragment.getView());
		instructionsFragment.initializeUnlockAndShareButtons(instructionsFragment.getView());
		settingsBaseFragment.settings.initializeUnlockAndShareButtons(settingsBaseFragment.settings.getView());
		settingsBaseFragment.settings.userPurchased();
		listenFragment.updateHypnoticSeekbar();	
	}
	
	public void shareEmail() {
		Constants.logMessage("sharing email");
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, Constants.EMAIL_SUBJECT);
		i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml(Constants.EMAIL_TEXT));
		try {
		    startActivity(Intent.createChooser(i, Constants.K_SEND_EMAIL ));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, Constants.K_EMAIL_ERROR, Toast.LENGTH_SHORT).show();
		}				
	}
	
	public void shareTwitter() {
		Constants.logMessage("Sharing twitter");
		try{
		    Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.putExtra(Intent.EXTRA_TEXT, Constants.TWEET_TEXT);
		    intent.setType("text/plain");
		    final PackageManager pm = getPackageManager();
		    final List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
		      int len =  activityList.size();
		      
		
		     boolean foundTwitter = false; 
		      
		    for (int i = 0; i < len; i++) {
		        final ResolveInfo app = (ResolveInfo) activityList.get(i);
                if (app.activityInfo.packageName.toLowerCase().contains("twitter")) {
		       // if ("com.twitter.android.PostActivity".equals(app.activityInfo.name) || "com.twitter.applib.PostActivity".equals(app.activityInfo.name)) {
		            final ActivityInfo activity=app.activityInfo;
		            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
		            intent=new Intent(Intent.ACTION_SEND);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		            intent.setComponent(name);
		            intent.putExtra(Intent.EXTRA_TEXT, Constants.TWEET_TEXT);
		            foundTwitter = true;
		            startActivity(intent);
		            break;
		        }
		    }
		    
		      if (foundTwitter == false) {
				    Toast.makeText(this, Constants.K_TWITTER_ERROR, Toast.LENGTH_SHORT).show();
		      }
		}
		catch(final ActivityNotFoundException e) {
		    Toast.makeText(this, Constants.K_TWITTER_ERROR, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void shareOnFacebook() {
		Constants.logMessage("sharing facebook");
		try {
			
			if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
				Bundle params = new Bundle();
				params.putString("name", Constants.FACEBOOK_TEXT_NAME);
				params.putString("caption", Constants.FACEBOOK_TEXT_CAPTION);
				params.putString("description", Constants.FACEBOOK_TEXT_DESCRIPTION);
				params.putString("link", Constants.FACEBOOK_SHARE_URL);
				params.putString("picture", Constants.FACEBOOK_SHARE_ICON);
				WebDialog feedDialog = (
				        new WebDialog.FeedDialogBuilder(this,
				            Session.getActiveSession(),
				            params))
				        .setOnCompleteListener(new OnCompleteListener() {
							
							@Override
							public void onComplete(Bundle values, FacebookException error) {
			    				//Toast.makeText(HomeActivity.this, "Exception occured : " + error.getMessage(), Toast.LENGTH_LONG).show();

							}
						})
				        .build();
				    feedDialog.show();
			}
			else {
				  // start Facebook Login
				  Session.openActiveSession(this, true, new Session.StatusCallback() {

				    // callback when session changes state
				    @Override
				    public void call(Session session, SessionState state, Exception exception) {
				    			if (session.isOpened()) {
				    				//Toast.makeText(HomeActivity.this, "Session opened", Toast.LENGTH_LONG).show();	
				    				shareOnFacebook();
							}
				    			else if (exception != null) {
				    				//Toast.makeText(HomeActivity.this, "Exception occured : " + exception.getMessage(), Toast.LENGTH_LONG).show();
				    			}
				    }
				  });
			}
			
		
		}
		catch (Exception e) {
			//Toast.makeText(HomeActivity.this, "Exception occured : " + e.getMessage(), Toast.LENGTH_LONG).show();

			Toast.makeText(HomeActivity.this, Constants.K_FACEBOOK_ERROR, Toast.LENGTH_LONG).show();	
		}

		
	}
	
	public void showShareScreen() {
		Intent intent = new Intent(this, ShareActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up,R.anim.no_change);
	}
	
	public void showUnlockScreen() {
		Intent intent = new Intent(this, UnlockActivity.class);
		startActivityForResult(intent, UNLOCK_INTENT_CODE);
		overridePendingTransition(R.anim.slide_up,R.anim.no_change);
	}
		
	public void showRateScreen() {
		Intent intent = new Intent(this, ReviewActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up,R.anim.no_change);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        Constants.logMessage("activity resume..app coming to front");
		facebookUIHelper.onResume();
		//cancel if it is there
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(Constants.ON_GOING_NOTIFICATION_ID);
	}
	
	@Override
	public void onPause() {
			super.onPause();
            Constants.logMessage("pausing activity.. app going in background");
            facebookUIHelper.onPause();


			if (listenFragment.voicePlayer != null && listenFragment.voicePlayer.isPlaying() || (listenFragment.backgroundPlayer != null && listenFragment.backgroundPlayer.isPlaying())) {
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.icon)
				        .setContentTitle(Constants.APP_TITLE);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, HomeActivity.class);
				//resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

				mBuilder.setContentIntent(contentIntent);

				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				Notification notif = mBuilder.build();
				notif.flags = Notification.FLAG_ONGOING_EVENT;
				mNotificationManager.notify(Constants.ON_GOING_NOTIFICATION_ID, notif);
			}
	}


	@Override
	public void call(Session session, SessionState state, Exception exception) {
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Constants.logMessage("on stop activity");
		FlurryAgent.onEndSession(this);
		AskingPoint.onStop(this);
	}
	
	@Override
	protected void onDestroy() {
		Constants.logMessage("activity destroy");
		try {
			//cancel if it is there
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(Constants.ON_GOING_NOTIFICATION_ID);
			super.onDestroy();
			facebookUIHelper.onDestroy();
		}
		catch ( Exception e ) {
			
		}
	}
	
	private class PhoneListener extends PhoneStateListener {
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			 if (state == TelephonyManager.CALL_STATE_RINGING) {
				 Constants.logMessage("call ringinig");
					if (listenFragment.voicePlayer != null && listenFragment.voicePlayer.isPlaying()) {
						listenFragment.onClick(listenFragment.playPauseButton);
					}
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Constants.logMessage("user increased volume");
                //return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Constants.logMessage("user decreased volume");
                //return true;
            default:
                //return false;
        }

        return false;
    }


}//end class
