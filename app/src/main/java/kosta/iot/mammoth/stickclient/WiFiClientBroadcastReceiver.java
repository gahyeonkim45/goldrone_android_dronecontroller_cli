/*
 WiFi Direct File Transfer is an open source application that will enable sharing 
 of data between Android devices running Android 4.0 or higher using a WiFi direct
 connection without the use of a separate WiFi access point.This will enable data 
 transfer between devices without relying on any existing network infrastructure. 
 This application is intended to provide a much higher speed alternative to Bluetooth
 file transfers. 

 Copyright (C) 2012  Teja R. Pitla
 Contact: teja.pitla@gmail.com

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>
 */


package kosta.iot.mammoth.stickclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import kosta.iot.mammoth.stickclient.data.P;

/*
 Some of this code is developed from samples from the Google WiFi Direct API Guide 
 http://developer.android.com/guide/topics/connectivity/wifip2p.html
 */

public class WiFiClientBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private Channel channel;
    private ClientActivity act;

    public WiFiClientBroadcastReceiver(WifiP2pManager manager, Channel channel, ClientActivity act) {
        super();
        this.act = act;
        this.manager = manager;
        this.channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                ClientActivity.showMessage(act, "Wifi Direct is enabled");
            } else {
                ClientActivity.showMessage(act, "Wifi Direct is not enabled");
                ClientActivity.connectedTarget = false;
            }
            
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	//This broadcast is sent when status of in range peers changes. Attempt to get current list of peers.
          //  final WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

        	manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
				public void onPeersAvailable(WifiP2pDeviceList peers) {
                        if (!ClientActivity.connectedTarget) {
                            for (WifiP2pDevice wd : peers.getDeviceList()) {
                                if (wd.deviceName.equals(P.TARGET_DEVICE_NAME)) {
                                    act.setTargetDevice(null, wd);
                                    break;
                                }
                            }
                        }
                    }
			});

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        	WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
        	WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

        	if(networkState.isConnected()){
                ClientActivity.connectedTarget = true;
                Log.d("test", "-> " + device);
                act.setTargetDevice(wifiInfo, device);
                ClientActivity.setText("(" + wifiInfo.groupOwnerAddress + ")에 연결되었습니다...");
        	}
        	else{
                ClientActivity.connectedTarget = false;
                ClientActivity.setText("연결이 해제 되었습니다...");
        		manager.cancelConnect(channel, null);
        	}

            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}