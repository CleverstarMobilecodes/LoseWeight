package com.surfcityapps.am.loseweight;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class RewindSelectDialogFragment extends DialogFragment {
	
	private RewindSelectDialogListener listener;
	private String title;
	private int TAG;
	
	public interface RewindSelectDialogListener {
		public void rewindDismiss(boolean yesClicked , int TAG);
	}
	
	public RewindSelectDialogFragment() {
	
	}
	
	public RewindSelectDialogFragment(RewindSelectDialogListener object,String title , int TAG) {
		super();
		this.TAG = TAG;
		this.title = title;
		this.listener = object;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        TextView textView = new TextView(getActivity());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.TEXT_BLUE));
        textView.setPadding(20, 10, 20,10);
        builder.setCustomTitle(textView);

        String items[] = new String[] { Constants.K_LISTEN_REWIND , Constants.K_CANCEL };
        
        builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							if (listener != null) {
								listener.rewindDismiss(true,TAG);
							}
						}
				
			}
		});
        
	     return builder.create();
	}

}
