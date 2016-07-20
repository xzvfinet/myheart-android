package com.xzvfi.myheart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listview;
        final StrangerListViewAdapter adapter = new StrangerListViewAdapter();
        listview = (ListView) findViewById(R.id.stranger_list_view);
        listview.setAdapter(adapter);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        Call<List<User>> userList = Singleton.getNetworkService().getUsers(user.getUserGroup());
        userList.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); ++i) {
                        adapter.addItem(response.body().get(i).getToken(), ContextCompat.getDrawable(MainActivity.this, ids[1]),
                                response.body().get(i).getUserName(), "Assignment Ind Black 36dp");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "그룹 내 유저 목록 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

//        final String[] names = {
//                "이정연",
//                "김세정",
//                "서현진",
//                "이성경",
//                "박소담",
//                "이수민",
//                "쯔위"
//        };
//
//        for (int i = 0; i < ids.length; ++i) {
//            adapter.addItem(ContextCompat.getDrawable(this, ids[i]),
//                    names[i], "Assignment Ind Black 36dp");
//        }
    }
}
