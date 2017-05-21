package kosta.iot.mammoth.stickclient;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import kosta.iot.mammoth.stickclient.data.P;

/**
 * Created by kosta on 2016-06-08.
 */
public class SocketThread extends Thread {
    private int port;
    private WifiP2pInfo wifiInfo;
    private StickVIew stickVIew1, stickVIew2;
    public static Sender prevSender = null;

    public SocketThread(StickVIew stickVIew1, StickVIew stickVIew2, int port, WifiP2pInfo wifiInfo) {
        this.stickVIew1 = stickVIew1;
        this.stickVIew2 = stickVIew2;
        this.port = port;
        this.wifiInfo = wifiInfo;
    }

    @Override
    public void run() {
        InetAddress targetIP = null;//wifiInfo.groupOwnerAddress;  //client 상태에서만 server ip를 볼 수 있다.
        Socket socket = null;
        Sender sender = null;

        try {
            targetIP = wifiInfo.groupOwnerAddress;;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           if (targetIP == null) {
                Log.d(P.TAG, "wifiinfo is null...");
                return;
            }
        }

        try {
            Log.d(P.TAG, "소켓 연결 요청...");
            socket = new Socket(targetIP, port);
            if (socket.isConnected()) {
                Log.d(P.TAG, "소켓 연결 완료...");
                if(prevSender != null) {
                    prevSender.stopThread();
                }

                sender = new Sender(socket, stickVIew1, stickVIew2);
                sender.start();
                prevSender = sender;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(P.TAG, "connet fail : " + e.getMessage());
        }
    }
}
