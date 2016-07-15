package com.xzvfi.myheart.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.xzvfi.myheart.NetworkUtils;
import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// params.put("group_id", String.valueOf(group_id));

public class RegisterActivity extends AppCompatActivity {
    private int group_id;
    private EditText groupNameEditText;
    private EditText userNameEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        userNameEditText = (EditText) findViewById(R.id.nameEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString();
                String userName = userNameEditText.getText().toString();

                Group group = new Group();
                group.setName(groupName);

                String user_token = AccessToken.getCurrentAccessToken().getToken();
                User user = new User();
                user.setName(userName);

                Call<Boolean> groupCreateCall = Singleton.getNetworkService().createGroup(group_id, group);
                groupCreateCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });

                Call<Boolean> userCreateCall = Singleton.getNetworkService().createUser(user_token, user);
                userCreateCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }
        });

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

                Call<Group> groupCheckCall = Singleton.getNetworkService().getGroup(group_id);
                groupCheckCall.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        if (response.isSuccessful()) {
                            // 그룹 존재. 입력 막음.
                            Toast.makeText(getApplicationContext(), "그룹 존재." + response.body().getName(), Toast.LENGTH_LONG).show();

                            groupNameEditText.setFocusable(false);
                            groupNameEditText.setText(response.body().getName());

                        } else {
                            // 그룹 없음. 새로 입력
                            Toast.makeText(getApplicationContext(), "그룹 없음. 새로 입력.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "그룹 가져오기 실패: " + t.toString(), Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                Toast.makeText(this, "WiFi에 연결되어있지 않습니다. 정확한 그룹에 속하기 위해서는 WiFi에 연결되어 있어야 합니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "ActiveNetwork 가 null 입니다.", Toast.LENGTH_LONG).show();
        }
    }

//    private StringRequest checkGroupRequest = new StringRequest(Request.Method.POST, checkGroupUrl,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    if (response.equals("exist")) {
//                        Toast.makeText(getApplicationContext(), "그룹 " + group_id + " 가 존재합니다.", Toast.LENGTH_LONG).show();
////                        groupNameEditText.
//                    } else if (response.equals("new")) {
//                        Toast.makeText(getApplicationContext(), "그룹 " + group_id + " 가 존재하지 않습니다. 새로 만들어주세요..", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "처리되지 않은 response!", Toast.LENGTH_LONG).show();
//                    }
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    Toast.makeText(getApplicationContext(), "에러: " + volleyError.toString(), Toast.LENGTH_LONG).show();
//                }
//            }) {
//        @Override
//        protected Map<String, String> getParams() throws AuthFailureError {
//            Map<String, String> params = new HashMap<>();
//            params.put("group_id", String.valueOf(group_id));
//            return params;
//        }
//    };
}
