package fr.eurecom.cardify;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import fr.eurecom.util.WiFiDirectBroadcastReceiver;

public class Lobby extends Activity {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private WiFiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		handler = new Handler();
		setUpWiFiDirect();
		handler.postDelayed(runnable, 60000);
	}

	private void setUpWiFiDirect() {
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			/* do what you need to do */
			resetPeerList();
			findPeers(mManager);
			
			/* and here comes the "trick" */
			handler.postDelayed(this, 10000);
		}
	};
	
	//TODO: Dum måte å gjøre det på
	private void resetPeerList() {
		TextView tv = (TextView)findViewById(R.id.peerText);
		tv.setText("Players online: ");
	}
	
	public void printPeers(WifiP2pDeviceList peerList) {
		Log.d(getLocalClassName(), "printPeers()");
		Log.d(getLocalClassName(), "printPeers(): " + peerList.getDeviceList().size());
		TextView tv = (TextView)findViewById(R.id.peerText);
		StringBuilder text = new StringBuilder("Players online: ");
		
		for (WifiP2pDevice peer : peerList.getDeviceList()) {
			Log.d(getLocalClassName(), "Peer from printPeers(): " + peer.deviceName);
			text.append(peer.deviceName + "\n");
		}
		tv.setText(text);
	}

	private void findPeers(WifiP2pManager mManager) {
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(getLocalClassName(), "Done searching for peers :)");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.d(getLocalClassName(),
						"Couldn't search for peers. Fucking error!!! :(");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
	}

	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	public void refreshPeers(View view) {
		resetPeerList();
		findPeers(mManager);
	}

}
