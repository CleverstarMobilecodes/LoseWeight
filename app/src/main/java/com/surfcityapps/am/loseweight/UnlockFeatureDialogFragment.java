package com.surfcityapps.am.loseweight;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@SuppressLint("ValidFragment")
public class UnlockFeatureDialogFragment extends DialogFragment {

		private UnlockFeatureDialogListener listener;
		
		interface UnlockFeatureDialogListener {
			public void unlockFeatureDismiss(boolean yesClicked);
		}
		
		public UnlockFeatureDialogFragment() {
		
		}
		
		public UnlockFeatureDialogFragment(UnlockFeatureDialogListener object) {
			super();
			this.listener = object;
		}
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        builder.setTitle(Constants.IAP_TITLE);

	        builder.setMessage(Constants.IAP_MSG);
	        
	        builder.setNegativeButton("No", null);
		    builder.setPositiveButton("Yes",  new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listener.unlockFeatureDismiss(true);
					
				}
			});
	        
		     return builder.create();
		}

} // end class

