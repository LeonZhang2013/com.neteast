package com.neteast.androidclient.newscenter.adapter;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectedContactAdapter extends BaseAdapter {
    
    private ArrayList<Contact> mData;
    private LayoutInflater mInflater;
    
    public SelectedContactAdapter(Context context,ArrayList<Contact> selectedContacts) {
        mInflater = LayoutInflater.from(context);
        mData=selectedContacts;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView=mInflater.inflate(R.layout.item_share_cloud_selected, parent, false);
        }
        TextView textView=(TextView) convertView;
        textView.setText(mData.get(position).displayName);
        return convertView;
    }
    
}
