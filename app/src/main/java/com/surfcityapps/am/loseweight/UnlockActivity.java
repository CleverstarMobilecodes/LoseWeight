package com.surfcityapps.am.loseweight;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserData;
import com.android.vending.billing.IInAppBillingService;
import com.askingpoint.android.AskingPoint;
import com.flurry.android.FlurryAgent;
import com.apsalar.sdk.Apsalar;
import com.surfcityapps.am.loseweight.purchase.MySku;
import com.surfcityapps.am.loseweight.purchase.SamplePurchasingListener;

public class UnlockActivity extends Activity {
	
	private boolean isPurchasing,isRestoring,isCheckingPrice;
	private int buyIntentBundleResponse;
	private int skuDetailsResponse;
	IInAppBillingService inAppBilling_Service;
	private int PURCHASE_INTENT_CODE = 122;
	private int ITEM_PURCHASED_CODE = 100;
	private int ERROR_RESPONSE_UNAVAILABLE	 = 2;
	private int ERROR_PRODUCT_NOT_PURCHASED	 = 3;
	private int ERROR_UNKNOWN	 = 4;
	private boolean isServiceBound = false; //arif

	Button upgradeButton;

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
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.layout_activity_unlock);
			
			upgradeButton = (Button) findViewById(R.id.unlock_upgradeButton);
			upgradeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					purchaseProduct();					
				}
			});
			
			ImageButton crossButton = (ImageButton) findViewById(R.id.popup_crossButton);
			crossButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					UnlockActivity.this.setResult(RESULT_CANCELED);
					dismissActivity();
				}
			});

			PackageManager pkgManager = getPackageManager();

			String installerPackageName = pkgManager.getInstallerPackageName(getPackageName());
			if(installerPackageName == null){
			}else if(installerPackageName.startsWith("com.amazon")) {
				// Amazon
				setupAmazonIAPOnCreate();
			} else if ("com.android.vending".equals(installerPackageName)) {
			}

			isCheckingPrice = true;
			//purchaseProduct();
			
			TextView restore = (TextView) findViewById(R.id.unlock_restoreTextView);
			restore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					restoreProduct();
				}
			});
			
			Typeface helveticaMD = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Md.otf");	
			Typeface helveticaLt = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Lt.otf");	
			Typeface helveticaRoman = Typeface.createFromAsset(getAssets(),   "HelveticaNeueLTStd-Roman.otf");	

			TextView titleTextView = (TextView) findViewById(R.id.popup_titleTextview);
			titleTextView.setTypeface(helveticaMD);
			
			TextView detailTextView = (TextView) findViewById(R.id.unlock_detailTextView);
			detailTextView.setTypeface(helveticaMD);

			upgradeButton.setTypeface(helveticaMD);
			restore.setTypeface(helveticaLt);
			
			setLtTypeFace(R.id.unlock_point1, helveticaRoman);
			setLtTypeFace(R.id.unlock_point2, helveticaRoman);
			setLtTypeFace(R.id.unlock_point3, helveticaRoman);
			setLtTypeFace(R.id.unlock_point4, helveticaRoman);
			setLtTypeFace(R.id.unlock_point5, helveticaRoman);
			setLtTypeFace(R.id.unlock_point6, helveticaRoman);
		}

		private void setupAmazonIAPOnCreate() {
			final SamplePurchasingListener purchasingListener = new SamplePurchasingListener(this);
			PurchasingService.registerListener(this.getApplicationContext(), purchasingListener);
		}
		private void setLtTypeFace(int resourceId, Typeface typeface) {
			TextView textView = (TextView) findViewById(resourceId);
			textView.setTypeface(typeface);
		}

	@Override
	protected void onResume() {
		super.onResume();
		PackageManager pkgManager = getPackageManager();

		String installerPackageName = pkgManager.getInstallerPackageName(getPackageName());
		if(installerPackageName == null){
		}else if(installerPackageName.startsWith("com.amazon")) {
			// Amazon
			PurchasingService.getUserData();
			PurchasingService.getPurchaseUpdates(false);

			final Set<String> productSkus = new HashSet<String>();
			for (final MySku mySku : MySku.values()) {
				productSkus.add(mySku.getSku());
			}
			PurchasingService.getProductData(productSkus);
		} else if ("com.android.vending".equals(installerPackageName)) {
		}
	}

	private void dismissActivity() {
			UnlockActivity.this.finish();
			overridePendingTransition(R.anim.no_change,R.anim.slide_down);					
		}
		
		// purchase code copied from home activity

		private class PurchaseProductAsync extends AsyncTask<Void, Void, Object> {

			@Override
			protected Object doInBackground(Void... arg0) {
				
				ArrayList<String> skuList = new ArrayList<String>();
				skuList.add(Constants.IAP_IDENTIFIER);
				Bundle querySkus = new Bundle();
				querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
				try {
					Bundle skuDetails = inAppBilling_Service.getSkuDetails(3, getPackageName(), "inapp", querySkus);
					skuDetailsResponse = skuDetails.getInt("RESPONSE_CODE");
					 ArrayList<String> responseList
				      = skuDetails.getStringArrayList("DETAILS_LIST");
				   
				      JSONObject object = new JSONObject(responseList.get(0));
				      String price = object.getString("price");
				      if (isCheckingPrice) {
						return price;
					}
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
			protected void onPostExecute(Object finalObject) {
				super.onPostExecute(finalObject);
				isPurchasing = false;
				if (isCheckingPrice) {
					isCheckingPrice = false;
                    if (finalObject != null) {
                        upgradeButton.setText(Constants.K_UPGRADE_TEXT + finalObject);
                    }
					return;
				}
				
				String skuResponse = String.valueOf(skuDetailsResponse);
				String buyIntentResponse = String.valueOf(buyIntentBundleResponse);
				//Toast.makeText(HomeActivity.this, "SkuDetails response code : " + skuResponse  + "Buy Intent Response : " + buyIntentResponse  , Toast.LENGTH_LONG).show();
				
				if (buyIntentBundleResponse == 7) {
					UnlockActivity.this.restoreProduct();
					return;
				}
				
				if (finalObject != null) {
					PendingIntent pendingIntent = ((Bundle) finalObject).getParcelable("BUY_INTENT");
					try {
						startIntentSenderForResult(pendingIntent.getIntentSender(), PURCHASE_INTENT_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
					} catch (SendIntentException e) {
						e.printStackTrace();
					}
				}
				else {
					Toast.makeText(UnlockActivity.this, Constants.IAP_CONNECTION_ERROR_TEXT, Toast.LENGTH_SHORT).show();
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
					UnlockActivity.this.setResult(RESULT_OK);
						dismissActivity();
						Toast.makeText(UnlockActivity.this,  Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();

					Apsalar.event("iap_restore");
				}
				else if (result == ERROR_RESPONSE_UNAVAILABLE) {
		            Toast.makeText(UnlockActivity.this, Constants.IAP_CONNECTION_ERROR_TEXT, Toast.LENGTH_SHORT).show();
				}
				else if (result == ERROR_PRODUCT_NOT_PURCHASED) {
		            Toast.makeText(UnlockActivity.this, Constants.IAP_NOT_OWNED_TEXT, Toast.LENGTH_SHORT).show();
				}
				else {
		            Toast.makeText(UnlockActivity.this, Constants.K_RESTORE_ERROR, Toast.LENGTH_SHORT).show();
				}
			}
		} // end restoreAsync
		
		
		public void purchaseProduct(){
			PackageManager pkgManager = getPackageManager();

			String installerPackageName = pkgManager.getInstallerPackageName(getPackageName());
			if(installerPackageName == null){
				// Google Play
				if (inAppBilling_Service == null) {
					isPurchasing = true;
					bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), billingConnection, Context.BIND_AUTO_CREATE);
					return;
				}

				// settingsBaseFragment.settings.userPurchased();
				new PurchaseProductAsync().execute((Void) null);
			}else if(installerPackageName.startsWith("com.amazon")) {
				// Amazon
				final RequestId requestId = PurchasingService.purchase(MySku.UPDATEAPP.getSku());
			} else if ("com.android.vending".equals(installerPackageName)) {
				// Google Play
				if (inAppBilling_Service == null) {
					isPurchasing = true;
					bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), billingConnection, Context.BIND_AUTO_CREATE);
					return;
				}

				// settingsBaseFragment.settings.userPurchased();
				new PurchaseProductAsync().execute((Void) null);
			}
		}
		
		public void restoreProduct() {
			PackageManager pkgManager = getPackageManager();

			String installerPackageName = pkgManager.getInstallerPackageName(getPackageName());
			if(installerPackageName == null){
				// Google Play
				if (inAppBilling_Service == null) {
					isRestoring = true;
					bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), billingConnection, Context.BIND_AUTO_CREATE);
					return;
				}
				new RestoreProductAsync().execute((Void) null);
			}else if(installerPackageName.startsWith("com.amazon")) {
				// Amazon
				PurchasingService.getPurchaseUpdates(false);
			} else if ("com.android.vending".equals(installerPackageName)) {
				// Google Play
				if (inAppBilling_Service == null) {
					isRestoring = true;
					bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), billingConnection, Context.BIND_AUTO_CREATE);
					return;
				}
				new RestoreProductAsync().execute((Void) null);
			}
		}
		
		ServiceConnection billingConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			       inAppBilling_Service = null;			
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				isServiceBound = true;	//arif

				inAppBilling_Service = IInAppBillingService.Stub.asInterface(service);	
				if (isPurchasing)
					purchaseProduct();
				else
					restoreProduct();
			}
		};

		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			 
			if (requestCode == PURCHASE_INTENT_CODE) {
				if (resultCode == Activity.RESULT_OK) {
				      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
				      try {
				            JSONObject jo = new JSONObject(purchaseData);
				            String sku = jo.getString("productId");
				            if (sku.equalsIgnoreCase(Constants.IAP_IDENTIFIER)) {
								Editor prefEditor = getSharedPreferences("HomeActivity",Context.MODE_PRIVATE).edit();
								prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
								prefEditor.commit();
								UnlockActivity.this.setResult(Activity.RESULT_OK);
								dismissActivity();
					            Toast.makeText(this,  Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();
							
							}
				      	 }
				          catch (JSONException e) {
				             e.printStackTrace();
				          }
				}
				
			}
		}		
	
		@Override
		protected void onDestroy() {
			super.onDestroy();
			PackageManager pkgManager = getPackageManager();

			if(isServiceBound) {	//arif
				String installerPackageName = pkgManager.getInstallerPackageName(getPackageName());
				if (installerPackageName == null) {
					// Google Play
					unbindService(billingConnection);
				} else if ("com.android.vending".equals(installerPackageName)) {
					// Google Play
					unbindService(billingConnection);
				}
			}
		}

	public void showMessage(final String message) {
		Toast.makeText(UnlockActivity.this, message, Toast.LENGTH_LONG).show();
	}

	public void handleEntitlementPurchase(final Receipt receipt, final UserData userData) {
		try {
			if (receipt.isCanceled()) {

			} else {
				grantEntitlementPurchase(receipt, userData);
			}
			return;
		} catch (final Throwable e) {
			showMessage("Purchase cannot be completed, please retry");
		}

	}

	private void grantEntitlementPurchase(final Receipt receipt, final UserData userData) {
		if(receipt.getSku() == MySku.UPDATEAPP.getSku()){
			PurchasingService.notifyFulfillment(receipt.getReceiptId(), FulfillmentResult.FULFILLED);
			EndAmazonPurchase();
		}else{
			PurchasingService.notifyFulfillment(receipt.getReceiptId(), FulfillmentResult.UNAVAILABLE);
		}
	}

	public void EndAmazonPurchase()
	{
		try {
			Editor prefEditor = getSharedPreferences("HomeActivity",Context.MODE_PRIVATE).edit();
			prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
			prefEditor.commit();
			UnlockActivity.this.setResult(Activity.RESULT_OK);
			dismissActivity();
			Toast.makeText(this,  Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void RestoreAmazonSuccess()
	{
		try {
			Editor prefEditor = getSharedPreferences("HomeActivity",Context.MODE_PRIVATE).edit();
			prefEditor.putBoolean(Constants.PREF_PAID_IAP, true);
			prefEditor.commit();
			UnlockActivity.this.setResult(Activity.RESULT_OK);
			dismissActivity();
			Toast.makeText(this,  Constants.IAP_PURCHASED_TEXT, Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}// end class
