package kosta.iot.mammoth.stickclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kosta.iot.mammoth.stickclient.data.P;

public class ClientActivity extends Activity {
    public static boolean connectedTarget;

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private BroadcastReceiver wifiClientReceiver;

    private IntentFilter wifiClientReceiverIntentFilter;
    private WifiP2pDevice targetDevice;
    private WifiManager wifi;
    private WifiP2pInfo wifiInfo;

    private StickVIew stickVIew1, stickVIew2;
    private static TextView textView = null;
    private Button findBtn, connectBtn, socketBtn, socketDisBtn;

    //info pannel
    private TextView infoView;
    private Button infoUpBtn, infoDownBtn, infoOffBtn;
    public static int th = 0;

    private TextView pitch;
    private Button pitch_bt1, pitch_bt2;
    public static int th0 = 0;

    private TextView infoView1;
    private Button infoUpBtn1, infoDownBtn1;
    public static int th1 = 0;
    private TextView infoView2;
    private Button infoUpBtn2, infoDownBtn2;
    public static int th2 = 0;

    private TextView infoView3;
    private Button infoUpBtn3, infoDownBtn3;
    public static int th3 = 0;

    private TextView infoView4;
    private Button infoUpBtn4, infoDownBtn4;
    public static int th4 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        //infoview
        createInfoView();

        wifiInit();
        createView();
    }

    private void wifiInit() {
        connectedTarget = false;

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
                wifi.setWifiEnabled(true);
        }

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifichannel = wifiManager.initialize(this, getMainLooper(), null);
        wifiClientReceiver = new WiFiClientBroadcastReceiver(wifiManager, wifichannel, this);
        wifiClientReceiverIntentFilter = new IntentFilter();
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(wifiClientReceiver, wifiClientReceiverIntentFilter);
    }

    private void createView() {
        this.stickVIew1 = (StickVIew) this.findViewById(R.id.stick_view1);
        this.stickVIew2 = (StickVIew) this.findViewById(R.id.stick_view2);
        textView = (TextView) this.findViewById(R.id.text_view);
        this.findBtn = (Button) this.findViewById(R.id.find_btn);
        this.connectBtn = (Button) this.findViewById(R.id.connect_btn);
        this.socketBtn = (Button) this.findViewById(R.id.socket_btn);
        this.socketDisBtn = (Button) this.findViewById(R.id.socket_disconnect_btn);

        //peer 검색 요청하기
        this.findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("피어 검색...");
                wifiManager.stopPeerDiscovery(wifichannel, null);
                wifiManager.discoverPeers(wifichannel, null);
            }
        });

        //타겟에 연결
        this.connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetDevice != null) {
                    setText(targetDevice.deviceName + "에 연결 시도...");
                    connectToPeer(targetDevice);
                }
            }
        });

        //소켓 활성화
        this.socketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("소켓 접속 요청...");
                new SocketThread(stickVIew1, stickVIew2, P.PORT, wifiInfo).start();
            }
        });

        this.socketDisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("소켓 해제...");
                if(SocketThread.prevSender!=null) {
                    SocketThread.prevSender.stopThread();
                    SocketThread.prevSender = null;
                }
            }
        });
    }

    private void createInfoView(){
        this.infoView = (TextView)this.findViewById(R.id.infoview);
        infoView.setText("throttle : " + th);
        this.infoUpBtn = (Button)this.findViewById(R.id.up_button);
        infoUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView.setText("throttle : " + ++th);
            }
        });
        this.infoDownBtn = (Button)this.findViewById(R.id.down_button);
        infoDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView.setText("throttle : " + --th);
            }
        });
        this.infoOffBtn = (Button)this.findViewById(R.id.off_button);
        infoOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                th = 0;
                infoView.setText("throttle : " + th);
            }
        });

        this.pitch = (TextView)this.findViewById(R.id.pitch);
        pitch.setText("m0 : " + th0);
        this.pitch_bt1 = (Button)this.findViewById(R.id.pitch_bt1);
        pitch_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitch.setText("m0 : " + ++th0);
            }
        });
        this.pitch_bt2 = (Button)this.findViewById(R.id.pitch_bt2);
        pitch_bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitch.setText("m0 : " + --th0);
            }
        });

        this.infoView1 = (TextView)this.findViewById(R.id.infoview1);
        infoView1.setText("m1 : " + th1);
        this.infoUpBtn1 = (Button)this.findViewById(R.id.up_button1);
        infoUpBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView1.setText("m1 : " + ++th1);
            }
        });
        this.infoDownBtn1 = (Button)this.findViewById(R.id.down_button1);
        infoDownBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView1.setText("m1 : " + --th1);
            }
        });


        this.infoView2 = (TextView)this.findViewById(R.id.infoview2);
        this.infoUpBtn2 = (Button)this.findViewById(R.id.up_button2);
        infoUpBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView2.setText("m2: " + ++th2);
            }
        });
        this.infoDownBtn2 = (Button)this.findViewById(R.id.down_button2);
        infoView2.setText("m2 : " + th2);
        infoDownBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView2.setText("m2: " + --th2);
            }
        });

        this.infoView3 = (TextView)this.findViewById(R.id.infoview3);
        infoView3.setText("m3 : " + th3);
        this.infoUpBtn3 = (Button)this.findViewById(R.id.up_button3);
        infoUpBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView3.setText("m3 : " + ++th3);
            }
        });
        this.infoDownBtn3 = (Button)this.findViewById(R.id.down_button3);
        infoDownBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView3.setText("m3 : " + --th3);
            }
        });

        this.infoView4 = (TextView)this.findViewById(R.id.infoview4);
        infoView4.setText("m4 : " + th4);
        this.infoUpBtn4 = (Button)this.findViewById(R.id.up_button4);
        infoUpBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView4.setText("m4 : " + ++th4);
            }
        });
        this.infoDownBtn4 = (Button)this.findViewById(R.id.down_button4);
        infoDownBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView4.setText("m4 : " + --th4);
            }
        });
    }

    @Override
    protected void onDestroy() {
        /*if (wifi.isWifiEnabled()) {
            if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
                wifi.setWifiEnabled(false);
        }*/

        clearWifi();

        if (wifiClientReceiver != null)
            unregisterReceiver(wifiClientReceiver);

        if(SocketThread.prevSender!=null) {
            SocketThread.prevSender.stopThread();
        }

        super.onDestroy();
    }

    private void clearWifi(){
        wifiManager.cancelConnect(wifichannel, null);
        wifiManager.clearServiceRequests(wifichannel, null);
        wifiManager.clearLocalServices(wifichannel, null);
        wifiManager.removeGroup(wifichannel, null);
        wifiManager.stopPeerDiscovery(wifichannel, null);
    }

    public void setTargetDevice(WifiP2pInfo wifiInfo, WifiP2pDevice device) {
        if (device != null)
            this.setText(device.deviceName);
        this.wifiInfo = wifiInfo;
        this.targetDevice = device;
    }

    public static void setText(String str) {
        if(textView != null)
            textView.setText("Target Device : " + str);
    }

    private void connectToPeer(final WifiP2pDevice wifiPeer) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiPeer.deviceAddress;
        wifiManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                showMessage(getApplicationContext(), wifiPeer.deviceName + " : 와이파이 연결됨...");
            }

            public void onFailure(int reason) {
                showMessage(getApplicationContext(), wifiPeer.deviceName + " : 와이파이 연결해제...");
            }
        });
    }

    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}