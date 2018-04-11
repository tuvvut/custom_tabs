package com.test.customtabs;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.customtabs.CustomTabsSessionToken;
import android.support.customtabs.PostMessageServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CustomTabsClientExample";
    String chromePackageName = "com.android.chrome";
    private EditText url;
    private Button open;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession session;
    private String urlString;
    private PostMessageServiceConnection postMessageServiceConnection;
    private CustomTabsIntent customTabsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (EditText) findViewById(R.id.url);
        open = (Button) findViewById(R.id.open);

        urlString = url.getText().toString();
        open.setOnClickListener(this);
        CustomTabsClient.bindCustomTabsService(this, chromePackageName, getCustomTabsServiceConnection());
    }

    private CustomTabsServiceConnection getCustomTabsServiceConnection() {
        return new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mCustomTabsClient = client;
                mCustomTabsClient.warmup(0L);
                session = mCustomTabsClient.newSession(getCustomTabsCallback());
                setCustomTabsIntent();
                Log.d(TAG, "onCustomTabsServiceConnected ");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "onServiceDisconnected ");
            }
        };
    }

    private CustomTabsCallback getCustomTabsCallback() {
        return new CustomTabsCallback() {
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
                Log.d(TAG, "onNavigationEvent ");
            }

            @Override
            public void extraCallback(String callbackName, Bundle args) {
                super.extraCallback(callbackName, args);
                Log.d(TAG, "extraCallback ");
            }

            @Override
            public void onMessageChannelReady(Bundle extras) {
                super.onMessageChannelReady(extras);
                boolean i = postMessageServiceConnection.postMessage("123123", null);
                Log.d(TAG, "onMessageChannelReady " + i);
            }

            @Override
            public void onPostMessage(String message, Bundle extras) {
                super.onPostMessage(message, extras);
                Log.d(TAG, "onPostMessage " + message);
            }
        };
    }

    private void setCustomTabsIntent() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
        customTabsIntent = builder.build();

        CustomTabsSessionToken token = CustomTabsSessionToken.getSessionTokenFromIntent(customTabsIntent.intent);
        postMessageServiceConnection = new PostMessageServiceConnection(token) {
            @Override
            public void onBindingDied(ComponentName name) {
                Log.d(TAG, "onBindingDied");
            }

            @Override
            public void onPostMessageServiceDisconnected() {
                Log.d(TAG, "onPostMessageServiceDisconnected");
                super.onPostMessageServiceDisconnected();
            }

            @Override
            public void onPostMessageServiceConnected() {
                Log.d(TAG, "onPostMessageServiceConnected");

                boolean b = session.requestPostMessageChannel(Uri.parse("http://tuvvut.com"));
                Log.d(TAG, "requestPostMessageChannel " + b);

                super.onPostMessageServiceConnected();
            }

            @Override
            public boolean bindSessionToPostMessageService(Context context, String packageName) {
                Log.d(TAG, "bindSessionToPostMessageService");
                return super.bindSessionToPostMessageService(context, packageName);
            }
        };

        postMessageServiceConnection.bindSessionToPostMessageService(this, "com.test.customtabs");
        bindService(customTabsIntent.intent, postMessageServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        urlString = url.getText().toString();
        customTabsIntent.launchUrl(this, Uri.parse(urlString));

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                postMessageServiceConnection.notifyMessageChannelReady(null);
            }
        }, 2000);
    }
}
