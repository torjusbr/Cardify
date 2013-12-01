package fr.eurecom.cardify;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.messaging.Client;
import fr.eurecom.messaging.ServerAsyncTask;
import fr.eurecom.messaging.WiFiDirectBroadcastReceiver;

public class Lobby extends Activity implements ConnectionInfoListener {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private WiFiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private String groupOwnerIp;
	private WifiP2pDeviceList peers;
	
	private Client client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		setUpWiFiDirect();
		peers = new WifiP2pDeviceList();
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

	
	//TODO: Stupid
	private void resetPeerList() {
		TextView tv = (TextView)findViewById(R.id.peerText);
		tv.setText("Players online: ");
		((LinearLayout) findViewById(R.id.peerButtons)).removeAllViews();
		((LinearLayout) findViewById(R.id.connectedDevicesLayout)).removeAllViews();
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
			printPeerButton(peer);
		}
		
		tv.setText(text);
	}
	
	private void printPeerButton(final WifiP2pDevice device) {
		ViewGroup vg;
		Button bt = new Button(this);
		if (device.status == WifiP2pDevice.CONNECTED) {
			bt.setText("Disconnect " + device.deviceName);
			vg = (ViewGroup) findViewById(R.id.connectedDevicesLayout);
			bt.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View view) {
		        	disconnectFromDevice(device);
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
	}
	
	private void connectToDevice(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		

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
	
	private void disconnectFromDevice(WifiP2pDevice device) {
		mManager.removeGroup(mChannel, new ActionListener() {

		    @Override
		    public void onSuccess() {
		        //success logic
		    	Log.d("WifiDirectBroadcastReciever.onRecieve()", "disconnected");
		    }

		    @Override
		    public void onFailure(int reason) {
		        //failure logic
		    	Log.d("WifiDirectBroadcastReciever.onRecieve()", "failed to disconnect " + reason);
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

	// Every time new connection is available
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		groupOwnerIp = info.groupOwnerAddress.getHostAddress();
		
		if (info.groupFormed) {
			if (info.isGroupOwner) {
				setUpHost(info);
			} else {
				setUpClient(info);
			}
		}
	}
	
	// Create host if not already done
	private void setUpHost(WifiP2pInfo info) {
		Toast.makeText(getApplicationContext(), "You are the group owner", Toast.LENGTH_LONG).show();
		
		if (this.client == null){
			this.client = new Client(info);
		}
		
		Button startButton = (Button) findViewById(R.id.startButton);
		startButton.setVisibility(Button.VISIBLE);
	}
	
	// Create client and register at host
	private void setUpClient(WifiP2pInfo info) {
		Toast.makeText(getApplicationContext(), "Group Owner IP - " + groupOwnerIp, Toast.LENGTH_LONG).show();
		this.client = new Client(info);
		this.client.addReceiver(info.groupOwnerAddress);
		this.client.registerAtHost();
	}

}
