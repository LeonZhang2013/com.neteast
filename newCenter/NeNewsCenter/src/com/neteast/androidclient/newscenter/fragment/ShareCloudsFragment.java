package com.neteast.androidclient.newscenter.fragment;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.adapter.SearchResultAdapter;
import com.neteast.androidclient.newscenter.adapter.UserByGroupAdapter;
import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
/**
 * 包含当前用户所有云好友的二级列表页面，按照分组不同进行分类
 * @author emellend
 *
 */
public class ShareCloudsFragment extends Fragment implements Observer{

    private ListView mSearchList;
    private ExpandableListView mCloudList;
    private UIHandler mUIHandler;
    private Activity mContext;
    private ContactStateObserver mContactStateObserver;
    private UserByGroupAdapter mUserByGroupAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mUIHandler = new UIHandler(this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.frag_share_clouds, container, false);
        //搜索框
        EditText searchView=(EditText) contentView.findViewById(R.id.share_search);
        searchView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                String search = s.toString().trim();
                if (TextUtils.isEmpty(search)) {
                    showCloudList();
                }else {
                    showSearchResult(search);
                }
            }
        });
        mSearchList = (ListView) contentView.findViewById(R.id.share_search_result);
        
        mCloudList = (ExpandableListView) contentView.findViewById(R.id.share_clouds);
        return contentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactStateObserver=new ContactStateObserver();
        downLoadCloudList();
    }
    
    public void registerContactStateObserver(Observer observer) {
        mContactStateObserver.addObserver(observer);
    }
    
    public void unRegisterContactStateObserver(Observer observer) {
        mContactStateObserver.deleteObserver(observer);
    }
    
    public void onContactCheckedChanged(Contact contact) {
        mContactStateObserver.changeContactCheckedState(contact);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        registerContactStateObserver(this);
    }
    
    private void showSearchResult(String search) {
        mCloudList.setVisibility(View.GONE);
        mSearchList.setVisibility(View.VISIBLE);
        
        ArrayList<Contact> allCloudFriends = mContactStateObserver.getAllCloudFriends();
        ArrayList<Contact> searchResult=new ArrayList<Contact>();
        for (Contact contact : allCloudFriends) {
            if(contact.displayName.contains(search)){
                searchResult.add(contact);
            }
        }
        mSearchList.setAdapter(new SearchResultAdapter(mContext, searchResult, mContactStateObserver));
    }

    private void showCloudList() {
        mCloudList.setVisibility(View.VISIBLE);
        mSearchList.setVisibility(View.GONE);
    }
    
    /**
     * 下载云好友列表
     */
    private void downLoadCloudList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CloudAccount account = CloudAccount.getInstance(mContext);
                String path=ConfigManager.URL_USERCENTER+
                            "/Usercenter/yuserlist/datatype/j/token/"+account.getToken()+"/userid/"+account.getUserId();
                LogUtil.i(path);
                String json=null;
                for(int i=0;i<3;i++){//若网络不好，则会尝试连接3次
                    try {
                        json= Utils.doGet(path);
                        break;
                    } catch (IOException e) {
                        LogUtil.printException(e);
                        continue;
                    }
                }
                LogUtil.i("json="+json);
                mContactStateObserver.setObserveredData(Contact.newInstances(json));
                mUIHandler.sendEmptyMessage(UIHandler.CLOUD_DOWNLOAD_FINISHED);
            }
        }).start();
    }
    
    /**
     * 当云好友列表下载完成，无论成功或是失败
     */
    private void onDownLoadCloudFinished() {
        //隐藏进度条，显示列表页
        getView().findViewById(R.id.share_loading).setVisibility(View.GONE);
        getView().findViewById(R.id.share_list_block).setVisibility(View.VISIBLE);
        //搜索输入框可用
        getView().findViewById(R.id.share_search).setEnabled(true);
        //注册监听器
        registerContactStateObserver(this);
        
        mUserByGroupAdapter = new UserByGroupAdapter(mContext, mCloudList,mContactStateObserver);
        mCloudList.setAdapter(mUserByGroupAdapter);
    }

    
    
    private static class UIHandler extends Handler{
        public static final int CLOUD_DOWNLOAD_FINISHED=1099;
        
        private ShareCloudsFragment mFragment;
        
        public UIHandler(ShareCloudsFragment fragment) {
            this.mFragment = fragment;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOUD_DOWNLOAD_FINISHED:
                    if (mFragment.isAdded()) {
                        mFragment.onDownLoadCloudFinished();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    @Override
    public void update(Observable observable, Object data) {
        mUserByGroupAdapter.notifyDataSetChanged();
    }
    
    public static class ContactStateObserver extends Observable{
        
        ArrayList<Contact> mContacts=new ArrayList<Contact>();
        private ArrayList<String> mGroups;
        private Map<String, ArrayList<Contact>> mUserByGroup;
        
        public void setObserveredData(ArrayList<Contact> contacts) {
            mContacts.addAll(contacts);
            mGroups = generateGroupList(mContacts);
            mUserByGroup = classifyUserByGroup(mContacts);
        }
        /**
         * 对用户根据所属用户组不同，进行分类。所有的用户都属于一个叫"全部"的分组
         */
        private Map<String, ArrayList<Contact>> classifyUserByGroup(ArrayList<Contact> cloudContacts) {
            Map<String, ArrayList<Contact>> userClassifiedByGroup = new HashMap<String, ArrayList<Contact>>();
            
            if (cloudContacts.isEmpty()) {
                return userClassifiedByGroup;
            }
            
            for (int i = 0, size = cloudContacts.size(); i < size; i++) {
                Contact contact = cloudContacts.get(i);
                addContactToAllGroup(contact, userClassifiedByGroup);
                addContactToHisGroup(contact, userClassifiedByGroup);
            }
            return userClassifiedByGroup;
        }
        
        /**
         * 将用户加入他所在的分组
         * @param contact
         * @param userClassifiedByGroup
         */
        private void addContactToHisGroup(Contact contact, Map<String, ArrayList<Contact>> userClassifiedByGroup) {
            final ArrayList<String> userGroups = contact.groups;
            for (int i = 0, size = userGroups.size(); i < size; i++) {
                String group = userGroups.get(i);
                ArrayList<Contact> groupList = userClassifiedByGroup.get(group);
                if (groupList == null) {
                    groupList = new ArrayList<Contact>();
                    userClassifiedByGroup.put(group, groupList);
                }
                groupList.add(contact);
            }
        }
        /**
         * 将用户加入“全部” 分组
         * @param contact Contact
         * @param userClassifiedByGroup  Map<String, ArrayList<Contact>>
         */
        private void addContactToAllGroup(Contact contact, Map<String, ArrayList<Contact>> userClassifiedByGroup) {
            ArrayList<Contact> allGroupList = userClassifiedByGroup.get("全部");
            if (allGroupList == null) {
                allGroupList = new ArrayList<Contact>();
                userClassifiedByGroup.put("全部", allGroupList);
            }
            allGroupList.add(contact);
        }
        
        /**
         * 得到所有云好友的分组总和，包含一个默认的"全部"分组
         * @param cloudContacts
         * @return
         */
        private ArrayList<String> generateGroupList(ArrayList<Contact> cloudContacts) {
            ArrayList<String> allGroupList = new ArrayList<String>();
            
            if (cloudContacts.isEmpty()) {
                return allGroupList;
            }
            
            allGroupList.add("全部");
            for (int contactIndex = 0, contactCount = cloudContacts.size(); contactIndex < contactCount; contactIndex++) {
                Contact contact = cloudContacts.get(contactIndex);
                final ArrayList<String> contactGroups = contact.groups;

                for (int groupIndex = 0, groupCount = contactGroups.size(); groupIndex < groupCount; groupIndex++) {
                    String group = contactGroups.get(groupIndex);
                    if (!allGroupList.contains(group)) {
                        allGroupList.add(group);
                    }
                }
            }
            return allGroupList;
        }
        
        public ArrayList<String> getGroups() {
            return mGroups;
        }
        
        public ArrayList<Contact> getContactByGroup(String groupName) {
            return mUserByGroup.get(groupName);
        }
        
        public ArrayList<Contact> getAllCloudFriends() {
            return mContacts;
        }
        
        public void changeContactCheckedState(Contact contact) {
            contact.isChecked=!contact.isChecked;
            setChanged();
            notifyObservers();
        }
        
        public void changeOneGroupCheckedState(String groupName,boolean isChecked) {
            ArrayList<Contact> contacts = mUserByGroup.get(groupName);
            for (Contact contact : contacts) {
                contact.isChecked=isChecked;
            }
            setChanged();
            notifyObservers();
        }
        
        public boolean isGroupChecked(String groupName) {
            ArrayList<Contact> contacts = mUserByGroup.get(groupName);
            
            boolean allChecked=false;
            for (Contact contact : contacts) {
                if (contact.isChecked) {
                    allChecked=true;
                    continue;
                }else {
                    allChecked=false;
                    break;
                }
            }
            return allChecked;
        }
        
        public ArrayList<Contact> getAllCheckedContacts() {
            ArrayList<Contact> checkedContacts=new ArrayList<Contact>();
            for (Contact contact : mContacts) {
                if (contact.isChecked) {
                    checkedContacts.add(contact);
                }
            }
            return checkedContacts;
        }
    }

}
