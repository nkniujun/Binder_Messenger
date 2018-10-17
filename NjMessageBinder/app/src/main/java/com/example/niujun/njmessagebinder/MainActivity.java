package com.example.niujun.njmessagebinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Messenger mClientMessenger;
    private Messenger mServerMessenger;
    boolean mBound;
    String TAG = "Nj";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, " client  handle  Message.");
        }
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClientMessenger = new Messenger(new ClientHandler());
            mServerMessenger = new Messenger(service);//基于服务端返回的IBinder创建一个Messenger
            mBound = true;
            /**
             * Messenger 主要作用： client 和 service 连接后，client向service发生消息，然后service收到消息后做相应的处理
             */
            sendMessage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mServerMessenger = null;
            mClientMessenger = null;
        }
    };

    public void sendMessage() {
        if (!mBound) return;
        Message message = Message.obtain(null, MessengerService.MSG_CLIENT_TO_SERVICE, 0, 0);
        message.replyTo = mClientMessenger; //指定回信人是客户端定义的
        try {
            Log.d(TAG, "客户端->服务端写信。");
            mServerMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MessengerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }
}
