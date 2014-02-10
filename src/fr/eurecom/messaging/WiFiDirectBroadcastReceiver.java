package fr.eurecom.messaging;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import fr.eurecom.cardify.Lobby;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private Lobby lobby;

	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Lobby activity) {
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.lobby = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		CardPeerListListener cardPeerListListener = new CardPeerListListener();
		String action = intent.getAction();
		
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	        } else {
	        	new AlertDialog.Builder(context)
				.setTitle("WiFi direct is not enabled")
				.setMessage("Enable WiFi Direct to play multiplayer game")
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						lobby.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
					}
				}).create().show();
	        }
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
		    if (mManager != null) {
		        mManager.requestPeers(mChannel, cardPeerListListener);
		    }
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, lobby);
            } else {
            	lobby.deviceDisconnected();
            }
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
		} else {
		}
	}
	
	private class CardPeerListListener implements PeerListListener {
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			lobby.setPeers(peers);
		}		
	}
	
}