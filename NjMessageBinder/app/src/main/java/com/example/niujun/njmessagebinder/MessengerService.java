package com.example.niujun.njmessagebinder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MessengerService extends Service {

    String TAG = "Nj";

    /** Command to the service to display a message */
    static final int MSG_CLIENT_TO_SERVICE = 1;
    static final int MSG_SERVICE_TO_CLIENT = 2;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: 服务端返回binder");
        return mMessenger.getBinder();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, " service  handle  Message.");
            //Service接收客户端发来的Message，基于what成员，决定下一步的处理
            switch (msg.what) {
                case MSG_CLIENT_TO_SERVICE:
                    //给客户端回信
                    Message replyMsg = Message.obtain(null, MessengerService.MSG_SERVICE_TO_CLIENT, 0, 0);
                    try {
                        Log.d(TAG, "handleMessage: 服务端->客户端回信。");
                        msg.replyTo.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
