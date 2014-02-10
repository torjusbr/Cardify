package fr.eurecom.messaging;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class PeerListener implements PeerListListener{
	private Client client;
	
	public PeerListener(Client client) {
		this.client = client;
	}
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(WifiP2pDevice device : peers.getDeviceList()) {
			sb.append(prefix);
			prefix = ";";
			sb.append(device.deviceName);
		}
		this.client.registerAtHost(sb.toString());
	}
	
}
