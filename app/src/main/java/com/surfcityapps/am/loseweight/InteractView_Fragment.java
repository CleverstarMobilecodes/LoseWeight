package com.surfcityapps.am.loseweight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusShare;
import com.uservoice.uservoicesdk.UserVoice;

public class InteractView_Fragment extends Fragment {

	private RelativeLayout twitterLayout;
	private RelativeLayout facebookLayout;
	private RelativeLayout googlePlusShare;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_interact_fragment, container, false);

		HomeActivity activity = (HomeActivity) getActivity();

		TextView title = (TextView) v.findViewById(R.id.title_interact);
		title.setTypeface(((HomeActivity) getActivity()).helveticaTh);

		Button moreAppsLayout = (Button) v.findViewById(R.id.interact_screen_moreAppsButtons);
		moreAppsLayout.setTypeface(activity.helveticalLT);
		moreAppsLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent launchPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MORE_APPS_URL));
					startActivity(launchPageIntent);
				}
			});
			
		initializeEmailLayout(v);
		initializeTwitterLayout(v);

		initializeGooglePlusLayout(v);

		initializeFacebookLayout(v);
		initializeFeedbackLayout(v);
		initializeLikeUsFacebook(v);
		initializeFollowTwitterLayout(v);
		initializeSuggestTopicLayout(v);
		initializeSendLogsLayout(v);
		initializeFollowOnGooglePlus(v);

			//			primaryTextView.setTypeface(activity.helveticaMD);
			
			
			TextView t_getSupport = (TextView) v.findViewById(R.id.interact_getSupport);
			t_getSupport.setTypeface(activity.helveticaMD);
			
			TextView t_email = (TextView) v.findViewById(R.id.interact_email_title);
			t_email.setTypeface(activity.helveticaMD);
			
			TextView t_facebook = (TextView) v.findViewById(R.id.interact_facebook_title);
			t_facebook.setTypeface(activity.helveticaMD);

			TextView t_gplusShare = (TextView) v.findViewById(R.id.interact_googlePlusShare_title);
			t_gplusShare.setTypeface(activity.helveticaMD);

			TextView t_twitter = (TextView) v.findViewById(R.id.interact_twitter_title);
			t_twitter.setTypeface(activity.helveticaMD);
			
		
			TextView t_weWantToHear = (TextView) v.findViewById(R.id.interact_weWantToHear);
			t_weWantToHear.setTypeface(activity.helveticaMD);

			TextView t_likeUs = (TextView) v.findViewById(R.id.interact_likeUs_facebookTitle);
			t_likeUs.setTypeface(activity.helveticaMD);

			TextView t_follow_us = (TextView) v.findViewById(R.id.interact_screen_followTwitter_textView);
			t_follow_us.setTypeface(activity.helveticaMD);
			 
			TextView t_follow_us_google = (TextView) v.findViewById(R.id.interact_screen_followGoogle_textView);
			t_follow_us_google.setTypeface(activity.helveticaMD);
			
			
			TextView t_support = (TextView) v.findViewById(R.id.interact_screen_feedbackTextView);
			t_support.setTypeface(activity.helveticaMD);
	
			TextView t_suggest = (TextView) v.findViewById(R.id.interact_screen_supportTextView);
			t_suggest.setTypeface(activity.helveticaMD);

            TextView t_sendLogs = (TextView) v.findViewById(R.id.interact_screen_sessionLogsTextView);
            t_sendLogs.setTypeface(activity.helveticaMD);

			initializeUnlockAndShareButtons(v);
			
			
			boolean FbAvailable = true;
			boolean TwitterAvailable  = true;

		if(Constants.K_IS_AMAZON == 0) {
			try {
				getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
			} catch (PackageManager.NameNotFoundException e) {
				FbAvailable = false;
				facebookLayout.setVisibility(View.GONE);
			}
		}
		else
		{
			FbAvailable = false;
			facebookLayout.setVisibility(View.GONE);
			googlePlusShare.setVisibility(View.GONE);
		}

			try{
			    getActivity().getPackageManager().getApplicationInfo("com.twitter.android", 0 );
			} 
			catch( PackageManager.NameNotFoundException e ) {
				TwitterAvailable = false;
				twitterLayout.setVisibility(View.GONE);
			}

			if (!FbAvailable && !TwitterAvailable ) {
				View GplusBottomLine = v.findViewById(R.id.interact_googlePlus_bottomSeparatorLine);
				GplusBottomLine.setVisibility(View.VISIBLE);
			}
			else if (!FbAvailable) {
				View twitterBottomLine = v.findViewById(R.id.interact_twitter_bottomSeparatorLine);
				twitterBottomLine.setVisibility(View.VISIBLE);
			}
			
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
	
	private void initializeEmailLayout(View v) {
		RelativeLayout email =  (RelativeLayout) v.findViewById(R.id.interact_screen_emailRelativeLayout);

		email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HomeActivity homeActivity = (HomeActivity) getActivity();
				homeActivity.shareEmail();
			}
		});
		
	}
	
	private void initializeTwitterLayout(View v) {
		twitterLayout = (RelativeLayout) v.findViewById(R.id.interact_screen_twitterRelativeLayout);
		twitterLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HomeActivity homeActivity = (HomeActivity) getActivity();
				homeActivity.shareTwitter();
			}
		});
	}
	
	private void initializeGooglePlusLayout(View v ) {
		googlePlusShare =  (RelativeLayout) v.findViewById(R.id.interact_screen_GooglePlusShareRelativeLayout);
		googlePlusShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
				File file = saveImage(bm);
				 String photoUri;
				try {
					photoUri = MediaStore.Images.Media.insertImage(
					         getActivity().getContentResolver(), file.getAbsolutePath(), null, null);
					 Intent shareIntent = new PlusShare.Builder(InteractView_Fragment.this.getActivity())
			            .setText(Constants.TWEET_TEXT)
			            .setType("image/jpeg")
			            .setStream(Uri.parse(photoUri))
			            .getIntent();
					    startActivity(shareIntent);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			}
		});
	
	}
	
	private void initializeFacebookLayout(View v) {
		facebookLayout = (RelativeLayout) v.findViewById(R.id.interact_screen_facebookRelativeLayout);
		facebookLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					HomeActivity homeActivity = (HomeActivity) getActivity();
					homeActivity.shareOnFacebook();
			}
		});
	}
	
	private void initializeFeedbackLayout(View v) {
		RelativeLayout feedback =  (RelativeLayout) v.findViewById(R.id.interact_screen_sendFeedback_RelativeLayout);

		feedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				UserVoice.launchUserVoice(InteractView_Fragment.this.getActivity());

		
			}
		});
		
	}
	

	
	private void initializeLikeUsFacebook(View v) {
		RelativeLayout likeUs =  (RelativeLayout) v.findViewById(R.id.interact_screen_likeUs_RelativeLayout);

		likeUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			      final String urlFb = "fb://page/"+"521338244625559"; //id
			        Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setData(Uri.parse(urlFb));

			        // If a Facebook app is installed, use it. Otherwise, launch
			        // a browser
			        final PackageManager packageManager = getActivity().getPackageManager();
			        List<ResolveInfo> list =
			            packageManager.queryIntentActivities(intent,
			            PackageManager.MATCH_DEFAULT_ONLY);
			        
			        int size  = list.size();
			        
			        if (size == 0) {
			            final String urlBrowser = "https://www.facebook.com/"+"surfcityapps";
			            intent.setData(Uri.parse(urlBrowser));
			        }
			        
			        try {
				        startActivity(intent);				
			        }
			        catch (Exception e) {
					    Toast.makeText(InteractView_Fragment.this.getActivity(), Constants.K_LIKEUS_ERROR, Toast.LENGTH_SHORT).show();
					}
			        
			}
		});
	}
	
	private void initializeFollowTwitterLayout(View v) {
		RelativeLayout follow =  (RelativeLayout) v.findViewById(R.id.interact_screen_followUs_RelativeLayout);

		follow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				try {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://twitter.com/surfcityapps"));
					getActivity().startActivity(intent);
				} catch (Exception e) {
				    Toast.makeText(InteractView_Fragment.this.getActivity(), Constants.K_EMAIL_ERROR, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}




    public File  extractLog() {
        //set a file
        Date datum = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        String fullName = df.format(datum)+"systemLog.txt";
        File file = new File (Environment.getExternalStorageDirectory(), fullName);

        //clears a file
        if(file.exists()){
            file.delete();
        }

        //write log to file
        int pid = android.os.Process.myPid();
        try {
            //String command = String.format("logcat -d -v threadtime *:*");
            String command = String.format("logcat -d -v threadtime *:*");
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine = null;

            PackageInfo pInfo;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                String version = pInfo.versionName;
                String OsVersion = android.os.Build.VERSION.RELEASE;
                String device = android.os.Build.MODEL;
                result.append( "for Android : " + version + "\n" + device + " " + OsVersion);
                result.append("\n");
            }
            catch (Exception e) {

            }


            while ((currentLine = reader.readLine()) != null) {
                if (currentLine != null && currentLine.contains(String.valueOf(pid))) {
                    result.append(currentLine);
                    result.append("\n");
                }
            }

            //return result.toString();
            FileWriter out = new FileWriter(file);
            out.write(result.toString());
            out.close();

            //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }


        //clear the log
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        //return "Cant Find Log Info";
        return file;

    }

	
	private void initializeSuggestTopicLayout(View v) {
		RelativeLayout email =  (RelativeLayout) v.findViewById(R.id.interact_screen_suggestTopic_RelativeLayout);

		email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HomeActivity homeActivity = (HomeActivity) getActivity();
				UserVoice.launchPostIdea(homeActivity);
			}
		});
	}

    private void initializeSendLogsLayout(View v) {
        RelativeLayout sendLogs =  (RelativeLayout) v.findViewById(R.id.interact_screen_sessionLogs_RelativeLayout);

        sendLogs.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"feedback@surfcityapps.com"});
                PackageInfo pInfo;
                try {

                    i.putExtra(Intent.EXTRA_SUBJECT, "Logs: " + Constants.FACEBOOK_TEXT_NAME );

                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    uris.add(Uri.fromFile(extractLog()));

                    File root = new File(Environment.getExternalStorageDirectory(), "AttractWealth");
                    if (!root.exists()) {
                        root.mkdirs();
                    }

					//arif - The system log contains all the necessary logs so we don't want this file. Also this file doesn't exist in Marshmallow
//                    File applog = new File(root, "appLog.txt");
//
//					if(applog.exists())
//                    	uris.add(Uri.fromFile(applog));

                    i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(i, Constants.K_SEND_EMAIL));

                } catch (Exception e) {
                    Toast.makeText(InteractView_Fragment.this.getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

	
	public void openGPlus(String profile) {
	    try {
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setClassName("com.google.android.apps.plus",
	          "com.google.android.apps.plus.phone.UrlGatewayActivity");
	        intent.putExtra("customAppUri", profile);
	        startActivity(intent);
	    } catch(ActivityNotFoundException e) {
	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+profile+"/posts")));
	    }
	}

	private void initializeFollowOnGooglePlus(View v) {
		RelativeLayout followUs =  (RelativeLayout) v.findViewById(R.id.interact_screen_followUsGoogle_RelativeLayout);

		followUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					openGPlus(Constants.GOOGLE_PLUS_PAGE_ID);
			}
		});

	}
}//end class
