package com.apptl.networking;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * @author Erik Hellman
 */
public class ChatService extends Service {
    private static final String TAG = "ChatService";
    private ChatWebSocketClient mChatWebSocketClient;
    private ChatClient mChatClient;
    private LocalBinder mLocalBinder = new LocalBinder();

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void connectToChatServer(URI serverUri) {
        new ChatWebSocketClient(serverUri).connect();
    }

    public void disconnect() {
        if (mChatWebSocketClient != null) {
            mChatWebSocketClient.close();
        }
    }

    public void setChatClient(ChatClient chatClient) {
        mChatClient = chatClient;
    }

    public void sendMessage(String message) {
        if (mChatWebSocketClient != null) {
            mChatWebSocketClient.send(message);
        }
    }

    public boolean isConnected() {
        return mChatWebSocketClient != null;
    }

    public interface ChatClient {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String from, String body, Date timestamp);
    }

    private class ChatWebSocketClient extends WebSocketClient {

        public ChatWebSocketClient(URI serverURI) {
            super(serverURI);
        }



        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            // Called when the Web Socket is connected
            mChatWebSocketClient = this;
            if (mChatClient != null) {
                mChatClient.onConnected();
            }

            Notification notification = buildNotification();
            startForeground(1001, notification);
        }

        @Override
        public void onMessage(String message) {
            // Called when a text message is received
            if (mChatClient != null) {
                try {
                    JSONObject chatMessage = new JSONObject(message);
                    String from = chatMessage.getString("from");
                    String body = chatMessage.getString("body");
                    Date timestamp =
                            new Date(chatMessage.getLong("timestamp"));
                    mChatClient.onMessageReceived(from, body, timestamp);
                } catch (JSONException e) {
                    Log.e(TAG, "Malformed message!", e);
                }
            }
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            // Called when a binary message is received
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            // Called when the connection is closed
            mChatWebSocketClient = null;
            if (mChatClient != null) {
                mChatClient.onDisconnected();
            }

            stopForeground(true);
        }

        @Override
        public void onError(Exception e) {
            // Called on in case of communication error
        }
    }

    private Notification buildNotification() {
        return null;
    }

    private class LocalBinder extends Binder {

        public ChatService getService() {
            return ChatService.this;
        }
    }
}


