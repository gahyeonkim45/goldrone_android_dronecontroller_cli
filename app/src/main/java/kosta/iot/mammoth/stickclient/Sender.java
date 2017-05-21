package kosta.iot.mammoth.stickclient;

import android.graphics.Point;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import kosta.iot.mammoth.stickclient.data.P;

/**
 * Created by kosta on 2016-06-13.
 */
public class Sender extends Thread {
    private Socket socket;
    private boolean isRun = true;
    private StickVIew stickVIew1, stickVIew2;

    public Sender(Socket socket, StickVIew stickVIew1, StickVIew stickVIew2) {
        this.socket = socket;
        this.stickVIew1 = stickVIew1;
        this.stickVIew2 = stickVIew2;
    }

    @Override
    public void run() {
        BufferedWriter networkWriter = null;
        PrintWriter out = null;
        Point p1, p2;

        try {
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out = new PrintWriter(networkWriter, true);

            int x = 0, y = 0;
            while (isRun) {
                p1 = this.stickVIew1.getPoints();
                //p2 = this.stickVIew2.getPoints();
                //jjout.println(p1.x + "," + p1.y + "," + p2.x + "," + p2.y);
                out.println(p1.x + "," + p1.y + "," + String.valueOf(ClientActivity.th)  + "," + String.valueOf(ClientActivity.th1) + "," + String.valueOf(ClientActivity.th2)
                + "," + String.valueOf(ClientActivity.th3)+ "," + String.valueOf(ClientActivity.th0)+","+String.valueOf(ClientActivity.th4));
                Thread.sleep(25);
                //  Log.d(P.TAG, "전송완료 : " + x + "," + y);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(P.TAG, "IOException : " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(P.TAG, "InterruptedException : " + e.getMessage());
        } finally {
            if (networkWriter != null)
                try {
                    networkWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (socket != null) {
                try {
                    socket.shutdownOutput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void stopThread(){
        if(this.socket != null)
            isRun = false;
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
