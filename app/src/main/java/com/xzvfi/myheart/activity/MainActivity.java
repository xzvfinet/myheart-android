package com.xzvfi.myheart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.User;
import com.xzvfi.myheart.view.StrangerListViewAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xzvfi on 2016-07-12.
 */
public class MainActivity extends AppCompatActivity {

    final int[] ids = {
            R.drawable.jung_yeon,
            R.drawable.se_jeong,
            R.drawable.seo_hyun,
            R.drawable.seong_gyung,
            R.drawable.so_dam,
            R.drawable.soo_min,
            R.drawable.tzu_yu
    };

    final String[] names = {
            "이정연",
            "김세정",
            "서현진",
            "이성경",
            "박소담",
            "이수민",
            "쯔위"
    };

    final String[] desc = {
            "안녕하세요!",
            "테스트를 위한 사람들입니다.",
            "DB에는 존재하지 않기 때문에 ",
            "하트를 보내도 저장이 되지 않아요.",
            "아래부터 새로운 사람들이",
            "추가된답니다.",
            "감사합니다!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Group group = (Group) intent.getSerializableExtra("group");

        setTitle("같은 그룹(" + group.getName() + ") 유저 목록");

        ListView listview;
        final StrangerListViewAdapter adapter = new StrangerListViewAdapter();
        listview = (ListView) findViewById(R.id.stranger_list_view);
        listview.setAdapter(adapter);

        User registeredUser = (User) intent.getSerializableExtra("user");

        Call<List<User>> userList = Singleton.getNetworkService().getUsers(registeredUser.getUserGroup(), registeredUser.getUserId());
        userList.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    for (User user : response.body()) {
                        adapter.addItem(user.getUserId(), ContextCompat.getDrawable(MainActivity.this, R.drawable.person),
                                user.getUserName(), user.getUserDescription(), user.getHeartNum());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "그룹 내 유저 목록 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버와 연결 실패. 어플을 재실행해주세요", Toast.LENGTH_LONG).show();
            }
        });

        for (int i = 0; i < ids.length; ++i) {
            adapter.addItem("merong", ContextCompat.getDrawable(this, ids[i]), names[i], desc[i], 999);
        }
    }
}
