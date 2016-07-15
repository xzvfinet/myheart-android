package com.xzvfi.myheart.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzvfi.myheart.R;

import java.util.ArrayList;

/**
 * Created by xzvfi on 2016-07-13.
 */
public class StrangerListViewAdapter extends BaseAdapter {
    private ArrayList<StrangerListItem> itemList = new ArrayList<StrangerListItem>() ;

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
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stranger_list_item, parent, false);
        }

        ImageView faceImageView = (ImageView) convertView.findViewById(R.id.faceImageView) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.userNameTextView) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);

        StrangerListItem listViewItem = itemList.get(position);

        faceImageView.setImageDrawable(listViewItem.getFaceDrawable());
        nameTextView.setText(listViewItem.getName());
        descTextView.setText(listViewItem.getDescription());

        return convertView;
    }

    public void addItem(Drawable face, String name, String dexcription) {
        StrangerListItem item = new StrangerListItem();

        item.setFaceDrawable(face);
        item.setName(name);
        item.setDescription(dexcription);

        itemList.add(item);
    }
}
