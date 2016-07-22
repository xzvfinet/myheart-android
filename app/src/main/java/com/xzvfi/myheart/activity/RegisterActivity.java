package com.xzvfi.myheart.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.xzvfi.myheart.NetworkUtils;
import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.googleservices.MyRegistrationIntentService;
import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.User;

import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    private String user_id;
    private String user_token;

    private int group_id;
    private User user;
    private Group group;

    private EditText groupNameEditText;
    private EditText userNameEditText;
    private EditText descriptionEditText;
    private Button sendButton;

    private ProgressBar mRegistrationProgressBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String REGISTRATION_READY = "registrationReady";
    public static final String REGISTRATION_GENERATING = "registrationGenerating";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_token = intent.getStringExtra("user_token");
        Toast.makeText(RegisterActivity.this, "user_id: " + user_id, Toast.LENGTH_SHORT).show();

        setTitle("새로 가입");

        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(sendButtonListener);

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                Thread ipThread = new Thread() {
                    public void run() {
                        group_id = NetworkUtils.getGroupId(getApplicationContext());
                    }
                };

                ipThread.start();
                try {
                    // wait for getting ip
                    ipThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "WiFi에 연결되어 있습니다." + group_id, Toast.LENGTH_SHORT).show();

                Call<Group> groupCall = Singleton.getNetworkService().getGroup(group_id);
                groupCall.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        if (response.isSuccessful()) {
                            // 그룹 존재. 입력 막음.
                            Toast.makeText(getApplicationContext(), "그룹 존재." + response.body().getName(), Toast.LENGTH_LONG).show();

                            groupNameEditText.setFocusable(false);
                            groupNameEditText.setText(response.body().getName());

                        } else {
                            // 그룹 없음. 새로 입력
                            Toast.makeText(getApplicationContext(), "그룹 없음. 이름을 새로 입력하세요.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Log.e(TAG, t.toString());
                        Toast.makeText(getApplicationContext(), "디비 접속 및 그룹 가져오기 실패! 어플을 재시작하세요:  " + t.toString(), Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                Toast.makeText(this, "WiFi에 연결되어있지 않습니다. 정확한 그룹에 속하기 위해서는 WiFi에 연결되어 있어야 합니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "ActiveNetwork 가 null 입니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter = 0;
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void moveToMain(User user, Group group) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("group", group);
        startActivity(intent);
        this.finish();
    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, MyRegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(REGISTRATION_READY)) {
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mInformationTextView.setVisibility(View.GONE);
                } else if (action.equals(REGISTRATION_GENERATING)) {
                    mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mInformationTextView.setVisibility(View.VISIBLE);
                    mInformationTextView.setText(getString(R.string.registering_message_generating));
                } else if (action.equals(REGISTRATION_COMPLETE)) {
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    sendButton.setText(getString(R.string.registering_message_complete));
                    sendButton.setEnabled(false);
                    String gcm_token = intent.getStringExtra("token");

                    // 새로운 유저 생성
                    String groupName = groupNameEditText.getText().toString();
                    String userName = userNameEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();

                    Group group = new Group(group_id, groupName, 1);

                    User user = new User(user_id, userName, user_token, gcm_token, description, group_id, 0);

                    Call<User> userCreateCall = Singleton.getNetworkService().createUser(user_id, user);
                    Call<Group> groupCreateCall = Singleton.getNetworkService().createGroup(group_id, group);

                    if (groupNameEditText.isFocusable()) {
                        groupCreateCall.enqueue(groupCreationCallback);
                    } else {
                        counter++;
                    }

                    userCreateCall.enqueue(userCreationCallback);
                }

            }
        };
    }

    /**
     * Google Play Service를 사용할 수 있는 환경인지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private static int counter = 0;
    Callback<User> userCreationCallback = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "유저 생성 성공!: " + response.body().getUserName(), Toast.LENGTH_LONG).show();
                user = response.body();
                if (++counter == 2) moveToMain(user, group);
            } else {
                Toast.makeText(RegisterActivity.this, "유저 생성 실패!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(RegisterActivity.this, "디비 접속 및 유저 생성 실패. 어플을 재실행해주세요", Toast.LENGTH_LONG).show();
        }
    };

    Callback<Group> groupCreationCallback = new Callback<Group>() {
        @Override
        public void onResponse(Call<Group> call, Response<Group> response) {
            if (response.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "그룹 생성 성공!: " + response.body().getName(), Toast.LENGTH_LONG).show();
                group = response.body();
                if (++counter == 2) moveToMain(user, group);
            } else {
                Toast.makeText(RegisterActivity.this, "그룹 생성 실패. 어플을 재실행해주세요", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        public void onFailure(Call<Group> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(RegisterActivity.this, "디비 접속 및 그룹 생성 실패. 어플을 재실행해주세요", Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getInstanceIdToken();
        }
    };
}
