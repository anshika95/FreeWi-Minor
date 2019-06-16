package com.example.kamal.minor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


@SuppressLint("ValidFragment")
public class BridgingFragment extends DialogFragment {

	private WiFiDirectActivity activty;
	private View mContentView;
	private String s;

	public BridgingFragment() {
	}

	public BridgingFragment(WiFiDirectActivity activty, String ssid) {
		this.activty = activty;
		this.s = ssid;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//
		LayoutInflater inflater = getActivity().getLayoutInflater();

		mContentView = inflater.inflate(R.layout.prompt_password, null);
		((TextView) mContentView.findViewById(R.id.ssid)).setText("Enter password for (" + this.s + ")");


		builder.setView(mContentView)

				.setPositiveButton(R.string.label_connect, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						String ssid = BridgingFragment.this.s;
						String password = ((TextView) BridgingFragment.this.mContentView
								.findViewById(R.id.password)).getText().toString();

						BridgingFragment.this.activty.connectToAccessPoint(ssid, password);

					}
				}).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						BridgingFragment.this.getDialog().cancel();
					}
				});
		return builder.create();
	}
}
