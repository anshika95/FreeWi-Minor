package com.example.kamal.minor;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kamal.minor.DeviceListFragment.DeviceActionListener;


public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	private static View mContentView = null;
	private WifiP2pDevice device;
	ProgressDialog progressDialog = null;


	public static void updateGroupMessage() {
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		if (view != null) {
			String s = "Currently in the network chatting: \n";
			for (MultipleClients c : RouterManager.routingTable.values()) {
				s += c.getMac() + "\n";
			}
			view.setText(s);
		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "Connecting to :"
						+ device.deviceAddress, true, true);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((DeviceActionListener) getActivity()).disconnect();
			}
		});

		return mContentView;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		Uri uri = data.getData();
		TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
		statusText.setText("Sending: " + uri);
		Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
	}


	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.getView().setVisibility(View.VISIBLE);

		if (!info.isGroupOwner) {
			Sender.queuePacket(new Packet(Packet.TYPE.HELLO, new byte[0], null, WiFiDirectBroadcastReceiver.MAC));
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}


	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		String s = "Currently in the network chatting: \n";
		for (MultipleClients c : RouterManager.routingTable.values()) {
			s += c.getMac() + "\n";
		}
		view.setText(s);
	}

	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		this.getView().setVisibility(View.GONE);
	}

}
