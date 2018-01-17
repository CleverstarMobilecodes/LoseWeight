package com.surfcityapps.am.loseweight;

import android.content.Context;
import android.util.Log;

public class Constants {

	private static boolean DEBUG = false;

	//Amazon Build
	public static int  K_IS_AMAZON = 1;

	//Pro Switch
	public static int  K_IS_PRO = 0;	 // 1== Yes , 0 == No

	//Repeat Induction Settings
	public static int  K_SHOW_INDUCTION = 1; // Set 0 for Self-Esteem App only.

	//Awaken at End Settings
	public static int K_AWAKEN_SWITCH = 1; // Set 0 for Sleep Well App only.

	public static String CRITTERCISM_APP_ID 			= "undefined";
	public static String FLURRY_APP_ID 			= "QC9NWYXV3N33Y4JWDZDF";
	public static String ASKING_POINT_APP_ID 		= "WwCbAHMEVgSK89qrr77_Jb-C7rNfqfBpwuY6yB_e028";
	public static String IAP_IDENTIFIER				= "com.surfcityapps.am.loseweight.first_iap";
	public static String MORE_TAB_URL 			= "http://surfcityapps.com/moream2/loseweight.html";

	public static String K_LISTEN_HEADPHONES_MSG	= "To obtain the benefits of the Hypnotic Booster, you must listen with headphones.";
	public static String K_SETTING_BOOSTER_DESC 	= "The Hypnotic Booster adjusts your brainwave frequency to make you more receptive to hypnotic suggestions. This is a powerful feature that helps you achieve your goals even faster.";

	public static String TWEET_TEXT				= "I'm getting fit with a great app from @SurfCityApps! Check it out! http://srfcty.co/18f3";
	public static String EMAIL_TEXT				= "I'm getting fit with a great hypnosis app from Surf City Apps! Check it out! <a href =http://srfcty.co/18f3 > http://srfcty.co/18f3 </a>";
	public static String EMAIL_SUBJECT			= "Check out this great hypnosis app!";
	public static String FEEDBACK_TEXT			= "Feedback: Weight Loss Hypnosis ";
	public static String K_LISTEN_BOOSTER 			= "HYPNOTIC BOOSTER";
	public static String K_SETTING_BOOSTER 		= "Hypnotic Booster";

	//Facebook Text
public static String FB_ID 					= "557472347656858";
	public static String FACEBOOK_TEXT_NAME		= "Weight Loss Hypnosis";
	public static String FACEBOOK_TEXT_CAPTION 		= "By Surf City Apps";
	public static String FACEBOOK_TEXT_DESCRIPTION 	= "Lose weight automatically by changing the subconscious thoughts that control your behavior. Overcome cravings, increase motivation to exercise, and lose weight with ease.";
	public static String FACEBOOK_SHARE_ICON		= "http://surfcityapps.com/moreicon/loseweight.png";
	public static String APP_TITLE 				= "Lose Weight";
	public static String IAP_TITLE 				= "Lose Weight Even Faster!";
	public static String FACEBOOK_SHARE_URL		= "http://surfcityapps.com/app/weight-loss-hypnosis/";

	//Constants////////////////////////

	public static String AS_ID 						= "surfcity";
	public static String AS_KEY 					= "VFOquG7j";

	public static int AUDIO_INSTRUCTIONS_REWIND_TAG = 84;
	public static int AUDIO_AWAKEN_END_TAG 			= 85;
	public static int AUDIO_BG_DELAY_TAG 			= 86;
	public static int AUDIO_PLAY_COUNT_TAG 			= 87;
	public static int AUDIO_LOOP_DISABLE_TAG 		= 88;
	public static int AUDIO_DELAY_DISABLE_TAG 		= 89;
	public static int AUDIO_REPEAT_INDUCTION_TAG 	= 90;

	//Fade Settings
	public static float K_FADE_IN_SECONDS = 8.0f; // 3 seconds duration
	public static float K_FADE_IN_STEPS = 0.1f;
	public static long K_FADE_INTERVAL = 100;
	public static float K_FADE_OUT_SECONDS = 5.0f; // 12.1 seconds
	public static float K_FADE_OUT_STEPS = 10f; // 0.1 sec // total steps = 12.1/0.1


	public static String PREF_PAID_IAP 				= "Purchased_Pro";
	public static String PREF_INSTRUCTIONS_ON 		= "Instructions_On";
	public static String PREF_AWAKEN_END 			= "Awaken_On";
	public static String PREF_BACKGROUND_SOUND		= "Background_Music";
	public static String PREF_HYPNOTIC_BOOSTER    	= "Hypnotic_Enabled";
	public static String PREF_SHARING_PROMPT	    = "Sharing_Prompt";
	public static String PREF_SESSION_COUNT		    = "Session Count";
	public static String PREF_BACKGROUND_DELAY_VALUE= "Background_Delay";
	public static String PREF_BACKGROUND_DELAY_NAME	= "Background_Delay_Name";
	public static String PREF_VOICE_LOOP_VALUE		= "Voice_Loop";
	public static String PREF_REPEAT_INDUCTION		= "Repeat_Induction";
	public static String PREF_KIIPCOUNT_VALUE		= "KiipCount";
	public static String PREF_SESSIONEND_SERVER_VALUE		= "SessionEnd";
	public static String PREF_VOLUME_VOICE			= "Voice_volume";
	public static String PREF_VOLUME_BACKGROUND		= "Background_volume";
	public static String PREF_VOLUME_HYPNOTIC		= "Hypnotic_volume";
	public static String K_CANCEL 					= "Cancel";
	public static String GOOGLE_PLUS_PAGE_ID		= "118097143902620747133";
	public static String USER_VOICE_APP_ID			= "surfcityapps.uservoice.com";
	public static int    ON_GOING_NOTIFICATION_ID 	= 444;

	public static String K_RESTORE_ERROR			= "Error occured in restoring.";
	public static String K_EMAIL_ERROR				= "There are no email clients installed.";
	public static String K_TWITTER_ERROR			= "Twitter App not found";
	public static String K_FACEBOOK_ERROR			= "Error occured.. Please Try Later";
	public static String K_LIKEUS_ERROR				= "Error in launching page";
	public static String K_LISTEN_LOOP 				= "VOICE IS SET TO LOOP";
	public static String K_LISTEN_BACKGROUND 		= "BACKGROUND";
	public static String K_LISTEN_DISABLED 			= "DISABLED";
	public static String K_LISTEN_ENABLED 			= "ENABLED";
	public static String K_LISTEN_VOICE 			= "VOICE";
	public static String K_LISTEN_REWIND 			= "Rewind to Beginning";
	public static String K_SETTING_RESTORE 			= "Restore In-App Purchase";
	public static String K_SETTING_AUDIOINST 		= "Audio Instructions";
	public static String K_SETTING_AWAKEN 			= "Awaken at End";
	public static String K_SETTING_BACKGROUND 		= "Background";
	public static String K_SETTING_DELAY 			= "Delay Ending";
	public static String K_SETTING_PLAYCOUNT 		= "Play Count";
	public static String K_SETTING_LOOP 			= "LOOP";
	public static String K_UPGRADE_TEXT 			= "Upgrade now for ";
	public static String K_CONFIG_URL 				= "http://surfcityapps.com/appsettings/config_android.xml";

	public static String MORE_APPS_URL 				= "amzn://apps/android?asin=B00HKL0XL8&showAll=1";
	public static String IAP_PURCHASED_TEXT			= "The advanced features are now available!";
	public static String IAP_NOT_OWNED_TEXT			= "Oops, you have not purchased this item in the past.";
	public static String IAP_CONNECTION_ERROR_TEXT	= "Your device is having trouble connecting.";
	public static String IAP_MSG					= "You are using the free version of this app. Would you like to unlock all the advanced features and settings?";
	public static String K_SETTING_STARTOVER 		= "Changing the Audio Instructions setting during playback requires starting over.";
	public static String K_SETTING_STARTOVER1 		= "Changing the Awaken End during playback requires starting over.";
	public static String K_SEND_EMAIL				= "Send mail...";

	//Headphone Skip settings
	public static int  K_IS_HEADPHONE_SKIP = 0;	 // 1== Yes , 0 == No. Should always be set as 0


	//Sound File names
	public static String K_AUDIO_INSTRUCTIONS		= "instructions.m4a";
	public static String K_AUDIO_INDUCTION			= "induction_a.m4a";
	public static String K_AUDIO_HYP_SESSION		= "hypnotic_session.m4a";
	public static String K_AUDIO_AWAKEN				= "awaken.m4a";
	public static String K_AUDIO_PURE				= "pure_embrace.m4a";
	public static String K_AUDIO_FLOATING			= "floating.m4a";
	public static String K_AUDIO_ADRIFT				= "adrift.m4a";
	public static String K_AUDIO_LETTING_GO			= "letting_go.m4a";
	public static String K_AUDIO_BROOK				= "brook.m4a";
	public static String K_AUDIO_BEACH				= "beach.m4a";
	public static String K_AUDIO_RAIN				= "rain.m4a";
	public static String K_AUDIO_HYP_BOOSTER		= "hypnotic_booster.m4a";


	public static void logMessage(String msg) {
//		if (DEBUG) {
			Log.w(Constants.APP_TITLE, msg);
//		}
	}

	public static float pxFromDp(Context context,float dp)
	{
		return dp * context.getResources().getDisplayMetrics().density;
	}
}

