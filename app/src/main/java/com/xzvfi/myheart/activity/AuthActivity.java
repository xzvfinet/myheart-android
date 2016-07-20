package com.xzvfi.myheart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = "AuthActivity";

    private CallbackManager callbackManager;

    private TextView mTextView;

    @Override
    protected void onResume() {
        super.onResume();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            String token = accessToken.getToken();
            Call<User> call = Singleton.getNetworkService().getUser(token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        // 기존 유저
                        Toast.makeText(getApplicationContext(), "기존 유저", Toast.LENGTH_SHORT).show();
                        moveToMain(response.body());
                    } else {
                        // 새로운 유저
                        moveToRegister();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "유저 정보 가져오기 실패".concat(t.toString()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFacebook();

        setContentView(R.layout.activity_auth);

        mTextView = (TextView) findViewById(R.id.text);

//        Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Call<User> call = Singleton.getNetworkService().getUser(AccessToken.getCurrentAccessToken().getToken());
//                call.enqueue(new Callback<User>() {
//                    @Override
//                    public void onResponse(Call<User> call, Response<User> response) {
//                        if (response.body() != null) {
//                            // 기존 유저
//                            Toast.makeText(getApplicationContext(), "기존 유저", Toast.LENGTH_SHORT).show();
//                            moveToMain(response.body());
//                        } else {
//                            // 새로운 유저
//                            moveToRegister();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<User> call, Throwable t) {
//                        t.printStackTrace();
//                        Toast.makeText(getApplicationContext(), "유저 정보 가져오기 실패".concat(t.toString()), Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });

        // login button
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        if (loginButton != null) {
//            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
            loginButton.registerCallback(callbackManager, loginResultFacebookCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
    }

    private void moveToRegister() {
        Toast.makeText(this, "페이스북 인증 성공. 정보를 추가로 입력해주세요!", Toast.LENGTH_SHORT).show();
        Intent registerIntent = new Intent(AuthActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void moveToMain(User user) {
        Toast.makeText(this, "기존 유저입니다. 오신 것을 환영합니다", Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(AuthActivity.this, MainActivity.class);
        mainIntent.putExtra("user", user);
        startActivity(mainIntent);
        finish();
    }

    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            Toast.makeText(getApplicationContext(), "페이스북 로그인 토큰을 받아오는데 성공했습니다. 서버로 보냅니다.", Toast.LENGTH_SHORT).show();

            String token = loginResult.getAccessToken().getToken();

            Call<User> call = Singleton.getNetworkService().getUser(token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null) {
                        // 기존 유저
                        Toast.makeText(getApplicationContext(), "기존 유저", Toast.LENGTH_SHORT).show();
                        moveToMain(response.body());
                    } else {
                        // 새로운 유저
                        moveToRegister();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "유저 정보 가져오기 실패".concat(t.toString()), Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getApplicationContext(), "페이스북 인증 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, error.toString());
        }
    };
}