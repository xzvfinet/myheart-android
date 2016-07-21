package com.xzvfi.myheart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = "AuthActivity";

    private CallbackManager callbackManager;

    @Override
    protected void onResume() {
        super.onResume();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        access(accessToken);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFacebook();

        setContentView(R.layout.activity_auth);

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
        String token = AccessToken.getCurrentAccessToken().getToken();
        Toast.makeText(this, "페이스북 인증 성공. 정보를 추가로 입력해주세요! " + token, Toast.LENGTH_SHORT).show();
        Intent registerIntent = new Intent(AuthActivity.this, RegisterActivity.class);
        registerIntent.putExtra("token", token);
        startActivity(registerIntent);
        finish();
    }

    private void moveToMain(User user, Group group) {
        Toast.makeText(this, "기존 유저입니다. 오신 것을 환영합니다", Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(AuthActivity.this, MainActivity.class);
        mainIntent.putExtra("user", user);
        mainIntent.putExtra("group", group);
        startActivity(mainIntent);
        finish();
    }

    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            Toast.makeText(AuthActivity.this, "User Id: " + loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
//            access(loginResult.getAccessToken());
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

    private void access(AccessToken accessToken) {
        if (accessToken == null) {
            Toast.makeText(AuthActivity.this, "페이스북 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (accessToken.isExpired()) {
            Toast.makeText(AuthActivity.this, "Token 이 만료되었습니다. 재 인증 해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "페이스북 로그인 토큰을 받아오는데 성공했습니다. 서버로 보냅니다.", Toast.LENGTH_SHORT).show();

            String token = accessToken.getToken();
            Call<User> call = Singleton.getNetworkService().getUser(token);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        // 기존 유저
                        final User user = response.body();
                        Toast.makeText(getApplicationContext(), "기존 유저:" + user.getUserName(), Toast.LENGTH_SHORT).show();

                        Call<Group> groupCall = Singleton.getNetworkService().getGroup(user.getUserGroup());
                        groupCall.enqueue(new Callback<Group>() {
                            @Override
                            public void onResponse(Call<Group> call, Response<Group> response) {
                                if (response.isSuccessful()) {
                                    Group group = response.body();
                                    moveToMain(user, group);
                                } else {
                                    Toast.makeText(getApplicationContext(), "그룹 정보 가져오기 실패. 어플을 재시작해주세요!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Group> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(getApplicationContext(), "그룹 정보 가져오기 실패. 어플을 재시작해주세요!".concat(t.toString()), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // 새로운 유저
                        moveToRegister();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "유저 정보 가져오기 실패. 어플을 재시작해주세요!".concat(t.toString()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}