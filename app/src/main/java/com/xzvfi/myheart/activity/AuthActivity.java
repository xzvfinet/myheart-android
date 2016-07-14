package com.xzvfi.myheart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.xzvfi.myheart.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = "AuthActivity";

    private final String authUrl = "http://172.16.101.69:3000/auth";

    private CallbackManager callbackManager;
    private Intent registerIntent;

    private TextView mTextView;
    private RequestQueue queue;

    @Override
    protected void onResume() {
        super.onResume();

        if (isLoggedIn()) {
            moveToRegister();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_auth);

        //
        registerIntent = new Intent(AuthActivity.this, RegisterActivity.class);
        mTextView = (TextView) findViewById(R.id.text);
        queue = Volley.newRequestQueue(this);

        // test request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, authUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        String result = "Response is: " + response;
                        mTextView.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String result = "That  didn't work! " + error.toString();
                mTextView.setText(result);
            }
        });

        // test button
        final Button button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queue.add(stringRequest);
                    Log.i(TAG, "clicked");
                }
            });
        }

        // login button
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
            loginButton.registerCallback(callbackManager, loginResultFacebookCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void moveToRegister() {
        Toast.makeText(this, "페이스북 인증 성공. 정보를 추가로 입력해주세요!", Toast.LENGTH_LONG).show();
        startActivity(registerIntent);
        finish();
    }

    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            ProfileTracker p = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                }
            };
            p.startTracking();

            Toast.makeText(getApplicationContext(), "페이스북 로그인 토큰을 받아오는데 성공했습니다.", Toast.LENGTH_SHORT).show();
            StringRequest loginRequest = new StringRequest(Request.Method.POST, authUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (mTextView != null)
                                mTextView.setText("Response is: ".concat(response));

                            if (response.equals("merong")) {
                                moveToRegister();
                            } else {
                                Toast.makeText(getApplicationContext(), "인증 실패 response: ".concat(response), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mTextView != null)
                        mTextView.setText("That didn't work! ".concat(error.toString()));
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("login_token", loginResult.getAccessToken().getToken());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };

            queue.add(loginRequest);
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, error.toString());
        }
    };

}