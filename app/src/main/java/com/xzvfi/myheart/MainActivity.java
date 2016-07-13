package com.xzvfi.myheart;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.xzvfi.myheart.view.StrangerListViewAdapter;

/**
 * Created by xzvfi on 2016-07-12.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listview ;
        StrangerListViewAdapter adapter;

        adapter = new StrangerListViewAdapter() ;

        listview = (ListView) findViewById(R.id.stranger_list_view);
        listview.setAdapter(adapter);

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

        for (int i=0; i<ids.length; ++i) {
            adapter.addItem(ContextCompat.getDrawable(this, ids[i]),
                    names[i], "Assignment Ind Black 36dp") ;
        }
    }
}
