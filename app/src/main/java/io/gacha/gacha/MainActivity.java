package io.gacha.gacha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private LoginButton loginButton;

    private CallbackManager callbackManager;

    private static AsyncHttpClient client = new AsyncHttpClient();
    private AsyncHttpResponseHandler responseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.text);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                RequestParams params = new RequestParams();
                params.put("userName", loginResult.getAccessToken().getUserId());
                params.put("accessToken", loginResult.getAccessToken().getToken());

                client.post("http://api.gacha.co.kr:8080/authenticate", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String statusCodeStr = String.valueOf(statusCode);
                        textView.setText(statusCodeStr);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        textView.setText(statusCode);
                    }
                });

            }

            @Override
            public void onCancel() {
                textView.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                textView.setText("Login attempt failed.");
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
