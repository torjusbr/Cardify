package fr.eurecom.cardify;

import java.util.ArrayList;
import java.util.Collection;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.util.ClientSender;
import fr.eurecom.util.ServerAsyncTask;
import fr.eurecom.util.WiFiDirectBroadcastReceiver;

public class Lobby extends Activity implements ConnectionInfoListener {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private WiFiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private static ArrayList<String> unconnectedDevices;
	private String groupOwnerIp;
	private WifiP2pInfo info;
	private WifiP2pDeviceList peers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		unconnectedDevices = new ArrayList<String>();
		setContentView(R.layout.activity_lobby);
		setUpWiFiDirect();
		peers = new WifiP2pDeviceList();
//		handler = new Handler();
//		setUpWiFiDirect();
//		handler.postDelayed(runnable, 2000);
	}

	protected void setUpWiFiDirect() {
		
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

	}

//	private Runnable runnable = new Runnable() {
//		@Override
//		public void run() {
//			/* do what you need to do */
//			resetPeerList();
//			findPeers(mManager);
//			
//			/* and here comes the "trick" */
//			handler.postDelayed(this, 10000);
//		}
//	};
	
	//TODO: Dum måte å gjøre det på
	private void resetPeerList() {
		TextView tv = (TextView)findViewById(R.id.peerText);
		tv.setText("Players online: ");
		((LinearLayout) findViewById(R.id.peerButtons)).removeAllViews();
		((LinearLayout) findViewById(R.id.connectedDevicesLayout)).removeAllViews();
		unconnectedDevices.clear();
	}
	
	public void setPeers(WifiP2pDeviceList peers) {
		this.peers = peers;
		resetPeerList();
		printPeers();
	}
	
	private void printPeers() {
		Log.d(getLocalClassName(), "printPeers()");
		Log.d(getLocalClassName(), "printPeers(): " + peers.getDeviceList().size());
		TextView tv = (TextView)findViewById(R.id.peerText);
		StringBuilder text = new StringBuilder("Players online: ");
		
		for (WifiP2pDevice peer : peers.getDeviceList()) {
			Log.d(getLocalClassName(), "Peer from printPeers(): " + peer.deviceName);
			text.append("\n" + peer.deviceName);
			printPeerButton(peer);
		}
		
		tv.setText(text);
	}
	
	private void printPeerButton(final WifiP2pDevice device) {
//		if (unconnectedDevices.contains(device.deviceName)) 
//			return;
		ViewGroup vg;
		Button bt = new Button(this);
		if (device.status == WifiP2pDevice.CONNECTED) {
			bt.setText("Disconnect " + device.deviceName);
			vg = (ViewGroup) findViewById(R.id.connectedDevicesLayout);
			bt.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View view) {
//		        	Disconnect!!
		        }
		    });
		} else {
			bt.setText("Connect " + device.deviceName);
			vg = (ViewGroup) findViewById(R.id.peerButtons);
			bt.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View view) {
		        	connectToDevice(device);
		        }
		    });
		}
		
		bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		vg.addView(bt);
//		unconnectedDevices.add(device.deviceName);
	}
	
	private void connectToDevice(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		new ServerAsyncTask(getApplicationContext(), (TextView) findViewById(R.id.peerText)).execute();
		
		config.groupOwnerIntent = 15; //15 Gjør denne personen til groupOwner (host).  
		mManager.connect(mChannel, config, new ActionListener() {

		    @Override
		    public void onSuccess() {
		        //success logic
		    	Log.d("WifiDirectBroadcastReciever.onRecieve()", "connected to peer");
		    }

		    @Override
		    public void onFailure(int reason) {
		        //failure logic
		    	Log.d("WifiDirectBroadcastReciever.onRecieve()", "not connected to peer " + reason);
		    }
		});
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
						"Couldn't search for peers. Fucking error!!! :( " + reasonCode);
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

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		this.info = info;
		groupOwnerIp = info.groupOwnerAddress.getHostAddress();
		
		if (info.isGroupOwner) {
			Toast.makeText(getApplicationContext(), "You are the group owner", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Group Owner IP - " + groupOwnerIp, Toast.LENGTH_LONG).show();
			Button bt = new Button(this);
			bt.setText("Send test shit");
			bt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			bt.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View view) {
		        	ClientSender.send("This is a test message", groupOwnerIp, 6969);
		        }
		    });
			ViewGroup vg = (ViewGroup) findViewById(R.id.connectedDevicesLayout);
			vg.addView(bt);
		}
	}

}
