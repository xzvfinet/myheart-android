package com.xzvfi.myheart.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.xzvfi.myheart.R;
import com.xzvfi.myheart.Singleton;
import com.xzvfi.myheart.model.Heart;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xzvfi on 2016-07-13.
 */
public class StrangerListViewAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<StrangerListItem> itemList = new ArrayList<StrangerListItem>();

    public StrangerListViewAdapter() {
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        this.context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stranger_list_item, parent, false);
        }

        ImageView faceImageView = (ImageView) convertView.findViewById(R.id.faceImageView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
        TextView descTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
        TextView heartNumTextView = (TextView) convertView.findViewById(R.id.heartNumTextView);
        ImageButton heartButton = (ImageButton) convertView.findViewById(R.id.heartButton);

        final StrangerListItem listViewItem = itemList.get(position);

        faceImageView.setImageDrawable(listViewItem.getFaceDrawable());
        nameTextView.setText(listViewItem.getName());
        descTextView.setText(listViewItem.getDescription());
        heartNumTextView.setText(listViewItem.getHeartNum() + "");
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Heart> sendHeartCall = Singleton.getNetworkService().sendHeart(
                        new Heart(AccessToken.getCurrentAccessToken().getToken(),
                                listViewItem.getId())
                );

                sendHeartCall.enqueue(new Callback<Heart>() {
                    @Override
                    public void onResponse(Call<Heart> call, Response<Heart> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, listViewItem.getName() + " 님에게 하트 보내기 성공!", Toast.LENGTH_SHORT).show();
                            listViewItem.setHeartNum(listViewItem.getHeartNum() + 1);
                            StrangerListViewAdapter.this.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "하트 보내기 실패 ㅠㅠ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Heart> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(context, "디비 접속 및 하트 보내기 실패 ㅠㅠ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return convertView;
    }

    public void addItem(String id, Drawable face, String name, String description, int heartNum) {
        StrangerListItem item = new StrangerListItem();

        item.setId(id);
        item.setFaceDrawable(face);
        item.setName(name);
        item.setDescription(description);
        item.setHeartNum(heartNum);

        itemList.add(item);
    }
}
