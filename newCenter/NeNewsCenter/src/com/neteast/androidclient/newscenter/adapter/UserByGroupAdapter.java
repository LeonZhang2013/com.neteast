package com.neteast.androidclient.newscenter.adapter;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.fragment.ShareCloudsFragment.ContactStateObserver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * 以组名为一级菜单，各个分组用户为二级菜单的适配器
 * @author emellend
 */
public class UserByGroupAdapter extends BaseExpandableListAdapter {
    
    private ContactStateObserver mStateObserver;
    private ArrayList<String> mGroups;
    private Context mContext;
    private LayoutInflater mInflater;
    private ExpandableListView mExpandList;
    
    public UserByGroupAdapter(Context context,ExpandableListView exlist,ContactStateObserver observer) {
        mStateObserver=observer;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mExpandList = exlist;
        mGroups = mStateObserver.getGroups();
    }
    
    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupName = mGroups.get(groupPosition);
        ArrayList<Contact> contacts = mStateObserver.getContactByGroup(groupName);
        return contacts.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupName = mGroups.get(groupPosition);
        ArrayList<Contact> contacts = mStateObserver.getContactByGroup(groupName);
        return contacts.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition,final  boolean isExpanded, View convertView,ViewGroup parent) {
        ViewCache viewCache;
        if (convertView==null) {
            convertView=mInflater.inflate(R.layout.item_share_cloud_list, parent, false);
            viewCache=new ViewCache(convertView);
            convertView.setTag(viewCache);
        }else {
            viewCache=(ViewCache) convertView.getTag();
        }
        //设置左边的箭头方向
        int indicatorRes=isExpanded? R.drawable.triangle_bottom:R.drawable.triangle_right;
        viewCache.getImage().setBackgroundResource(indicatorRes);
        //设置分组名字
        final String groupName=mGroups.get(groupPosition);
        viewCache.getName().setText(groupName);
        //设置当前分组是否被选中
        boolean groupChecked = mStateObserver.isGroupChecked(groupName);
        viewCache.getHandler().setChecked(groupChecked);
        viewCache.getHandler().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox=(CheckBox) v;
                mStateObserver.changeOneGroupCheckedState(groupName, checkBox.isChecked());
            }
        });
        
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    mExpandList.collapseGroup(groupPosition);
                }else {
                    mExpandList.expandGroup(groupPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,View convertView, ViewGroup parent) {
        ViewCache viewCache;
        if (convertView==null) {
            convertView=mInflater.inflate(R.layout.item_share_cloud_list, parent, false);
            viewCache=new ViewCache(convertView);
            convertView.setTag(viewCache);
        }else {
            viewCache=(ViewCache) convertView.getTag();
        }
        final Contact contact=(Contact) getChild(groupPosition, childPosition);
        viewCache.getImage().setBackgroundResource(R.drawable.share_default_avatar);
        viewCache.getName().setText(contact.displayName);
        viewCache.getHandler().setChecked(contact.isChecked);
        viewCache.getHandler().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStateObserver.changeContactCheckedState(contact);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
