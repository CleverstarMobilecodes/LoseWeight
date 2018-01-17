package com.surfcityapps.am.loseweight;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.askingpoint.android.AskingPoint;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;

public class ShareActivity extends Activity {
		
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
		protected void onResume() {
			super.onResume();
		    //mPlusOneButton.initialize("http://play.google.com/store/apps/details?id=" + ShareActivity.this.getPackageName(), 1);
		} 
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layout_share);
			
			ImageButton crossButton = (ImageButton) findViewById(R.id.popup_crossButton);
			crossButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ShareActivity.this.finish();
					overridePendingTransition(R.anim.no_change,R.anim.slide_down);					
				}
			});
			
			Button fbButton = (Button) findViewById(R.id.shareScreen_facebook);
			fbButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					shareOnFacebook();
				}
			});
			
			Button twitterButton = (Button) findViewById(R.id.shareScreen_twitter);
			twitterButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					shareTwitter();
				}
			});
			
			
			//mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);
			
			
			Button googlePlusButton = (Button) findViewById(R.id.shareScreen_googlePlus);
			googlePlusButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
						File file = saveImage(bm);
						 String photoUri;
						try {
							photoUri = MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), null, null);
							 Intent shareIntent = new PlusShare.Builder(ShareActivity.this)
					            .setText(Constants.TWEET_TEXT)
					            .setType("image/jpeg")
					            .setStream(Uri.parse(photoUri))
					            .getIntent();
							    startActivity(shareIntent);
	
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}
			});
			
			Button emailButton = (Button) findViewById(R.id.shareScreen_emailApp);
			emailButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					shareEmail();
				}
			});
			
			Typeface helveticaMD = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Md.otf");	
			Typeface helveticaLt = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Lt.otf");	

			TextView titleTextView = (TextView) findViewById(R.id.popup_titleTextview);
			titleTextView.setTypeface(helveticaMD);
			
			TextView detailTextView = (TextView) findViewById(R.id.popup_detailTextview);
			detailTextView.setTypeface(helveticaLt);
			
			fbButton.setTypeface(helveticaLt);
			twitterButton.setTypeface(helveticaLt);
			googlePlusButton.setTypeface(helveticaLt);
			emailButton.setTypeface(helveticaLt);

			if(Constants.K_IS_AMAZON == 0) {
				try {
					getPackageManager().getApplicationInfo("com.facebook.katana", 0);
				} catch (PackageManager.NameNotFoundException e) {

				}
			}
			else
			{
				googlePlusButton.setVisibility(View.GONE);
				fbButton.setVisibility(View.GONE);
			}
			
			try{
			    getPackageManager().getApplicationInfo("com.twitter.android", 0 );
			} 
			catch( PackageManager.NameNotFoundException e ) {
				twitterButton.setVisibility(View.GONE);
			}
			
		}// end on Create
		

		private File saveImage(Bitmap finalBitmap) {

		    String root = Environment.getExternalStorageDirectory().toString();
		    File myDir = new File(root + "/saved_images");    
		    myDir.mkdirs();
		    Random generator = new Random();
		    int n = 10000;
		    n = generator.nextInt(n);
		    String fname = "Image-"+ n +".jpg";
		    File file = new File (myDir, fname);
		    if (file.exists ()) file.delete (); 
		    try {
		           FileOutputStream out = new FileOutputStream(file);
		           finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		           out.flush();
		           out.close();

		    } catch (Exception e) {
		           e.printStackTrace();
		    }
		    
		    return file;
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

				Toast.makeText(ShareActivity.this, Constants.K_FACEBOOK_ERROR, Toast.LENGTH_LONG).show();	
			}

			
		}
		
		
		public void shareEmail() {
			Constants.logMessage("sharing email");
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_SUBJECT, Constants.EMAIL_SUBJECT);
			i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml(Constants.EMAIL_TEXT));
			try {
			    startActivity(Intent.createChooser(i, Constants.K_SEND_EMAIL));
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
			

		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (Session.getActiveSession() != null) {
				 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

			}
			 
	
		}		
		
}// end class
