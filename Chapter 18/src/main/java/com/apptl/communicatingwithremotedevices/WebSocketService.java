package com.apptl.communicatingwithremotedevices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Erik Hellman
 */
public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    private Set<WebSocket> mClients;
    private MessageListener mMessageListener;
    private MyWebSocketServer mServer;
    private LocalBinder mLocalBinder = new LocalBinder();

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mClients = Collections.synchronizedSet(new HashSet<WebSocket>());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWebSocketServer();
    }

    public void startWebSocketServer() {
        if (mServer == null) {
            InetSocketAddress serverAddress = new InetSocketAddress(8081);
            mServer = new MyWebSocketServer(serverAddress);
            mServer.start();
        }
    }

    public void stopWebSocketServer() {
        if (mServer != null) {
            try {
                mServer.stop();
            } catch (IOException e) {
                Log.e(TAG, "Error stopping server.", e);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error stopping server.", e);
            }
        }
    }

    public void sendBroadcast(String message) {
        for (WebSocket client : mClients) {
            client.send(message);
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        mMessageListener = messageListener;
    }

    public interface MessageListener {
        void onMessage(WebSocket client, String message);
    }

    class MyWebSocketServer extends WebSocketServer {

        public MyWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket webSocket,
                           ClientHandshake clientHandshake) {
            mClients.add(webSocket);
        }

        @Override
        public void onClose(WebSocket webSocket,
                            int code,
                            String reason,
                            boolean remote) {
            mClients.remove(webSocket);
        }

        @Override
        public void onMessage(WebSocket webSocket,
                              String message) {
            if(mMessageListener != null) {
                mMessageListener.onMessage(webSocket, message);
            }
        }

        @Override
        public void onError(WebSocket webSocket,
                            Exception e) {
            webSocket.close();
            mClients.remove(webSocket);
        }
    }

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }
}
