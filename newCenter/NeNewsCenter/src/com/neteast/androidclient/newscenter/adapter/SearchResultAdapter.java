package com.neteast.androidclient.newscenter.adapter;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.fragment.ShareCloudsFragment.ContactStateObserver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultAdapter extends BaseAdapter {

    private ArrayList<Contact> mData;
    private LayoutInflater mInflater;
    private ContactStateObserver mObserver;
    
    public SearchResultAdapter(Context context,ArrayList<Contact> searchResult,ContactStateObserver observer) {
        mInflater = LayoutInflater.from(context);
        this.mData=searchResult;
        mObserver=observer;
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
        ViewCache viewCache;
        if (convertView==null) {
            convertView=mInflater.inflate(R.layout.item_share_cloud_list, parent, false);
            viewCache=new ViewCache(convertView);
            convertView.setTag(viewCache);
        }else {
            viewCache=(ViewCache) convertView.getTag();
        }
        
        final Contact contact=mData.get(position);
        viewCache.getImage().setBackgroundResource(R.drawable.share_default_avatar);
        viewCache.getName().setText(contact.displayName);
        viewCache.getHandler().setChecked(contact.isChecked);
        viewCache.getHandler().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mObserver.changeContactCheckedState(contact);
            }
        });
        return convertView;
    }
    
    /**
     * 视图缓存类
     * @author emellend
     */
    class ViewCache{
        private View image;
        private TextView name;
        private CheckBox handler;
        
        public ViewCache(View rootView) {
            image=rootView.findViewById(R.id.item_share_img);
            name=(TextView) rootView.findViewById(R.id.item_share_name);
            handler=(CheckBox) rootView.findViewById(R.id.item_share_hander);
        }
        public View getImage() {
            return image;
        }

        public TextView getName() {
            return name;
        }

        public CheckBox getHandler() {
            return handler;
        }
    }

}
