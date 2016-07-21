package com.xzvfi.myheart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xzvfi.myheart.NetworkUtils;
import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.User;

import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// params.put("group_id", String.valueOf(group_id));

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    private String token;

    private int group_id;
    private User user;
    private Group group;

    private EditText groupNameEditText;
    private EditText userNameEditText;
    private EditText descriptionEditText;
    private Button galleryButton;
    private Button pictureButton;
    private Button sendButton;
    private ImageView profileImageView;

    static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        Toast.makeText(RegisterActivity.this, "token: " + token, Toast.LENGTH_SHORT).show();

        setTitle("새로 가입");

        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        galleryButton = (Button) findViewById(R.id.galleryButton);
        pictureButton = (Button) findViewById(R.id.pictureButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        profileImageView = (ImageView) findViewById(R.id.profileImageView);

        galleryButton.setOnClickListener(galleryButtonListener);

        pictureButton.setOnClickListener(pictureButtonListener);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(this, "Data is null!!", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
                if (inputStream != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    profileImageView.setImageBitmap(bmp);
                    Toast.makeText(this, "Success!!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Success!!");
                } else {
                    Toast.makeText(this, "Failed!!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "failed!!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(RegisterActivity.this, "Picture", Toast.LENGTH_SHORT).show();
            Bundle extras = data.getExtras();

            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(imageBitmap);
        }
    }

    private void moveToMain(User user, Group group) {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("group", group);
        startActivity(intent);
        finish();
    }

    private View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String groupName = groupNameEditText.getText().toString();
            String userName = userNameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            Group group = new Group(group_id, groupName, 1);

            User user = new User(userName, token, description, group_id, 0);

            Call<User> userCreateCall = Singleton.getNetworkService().createUser(token, user);
            Call<Group> groupCreateCall = Singleton.getNetworkService().createGroup(group_id, group);

            if (groupNameEditText.isFocusable()) {
                groupCreateCall.enqueue(groupCreationCallback);
            } else {
                counter++;
            }

            userCreateCall.enqueue(userCreationCallback);
        }
    };

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
            Toast.makeText(RegisterActivity.this, "디비 접속 및 유저 생성 실패!!!: ", Toast.LENGTH_LONG).show();
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
                Toast.makeText(RegisterActivity.this, "그룹 생성 실패!", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        public void onFailure(Call<Group> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(RegisterActivity.this, "디비 접속 및 그룹 생성 실패!!!", Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener pictureButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    private View.OnClickListener galleryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    };
}
