package com.test.customtabs;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CustomTabsClientExample";
    String chromePackageName = "com.android.chrome";
    private EditText url;
    private Button open;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession session;
    private String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (EditText) findViewById(R.id.url);
        open = (Button) findViewById(R.id.open);

        urlString = url.getText().toString();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlString = url.getText().toString();
                openCustomTabs();
                open.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = session.requestPostMessageChannel(Uri.parse("http://tuvvut.com/"));
                        int i = session.postMessage("123123", null);
                        Log.d(TAG, "run " + b + " , " + i);
                    }
                }, 3000);
            }
        });

        CustomTabsClient.bindCustomTabsService(this, chromePackageName, getCustomTabsServiceConnection());
    }

    private CustomTabsServiceConnection getCustomTabsServiceConnection() {
        return new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mCustomTabsClient = client;
                mCustomTabsClient.warmup(0L);
                session = mCustomTabsClient.newSession(getCustomTabsCallback());
                session.mayLaunchUrl(Uri.parse(urlString), null, null);
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
                Log.d(TAG, "onMessageChannelReady ");
            }

            @Override
            public void onPostMessage(String message, Bundle extras) {
                super.onPostMessage(message, extras);
                Log.d(TAG, "onPostMessage ");
            }
        };
    }

    private void openCustomTabs() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        customTabsIntent.intent.setPackage(chromePackageName);
        customTabsIntent.launchUrl(this, Uri.parse(urlString));
    }
}
