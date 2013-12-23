package com.neteast.androidclient.newscenter.fragment;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.activity.MainActivity;
import com.neteast.androidclient.newscenter.adapter.InfoAdapter;
import com.neteast.androidclient.newscenter.domain.Information;
import com.neteast.androidclient.newscenter.domain.PacketKeepLive;
import com.neteast.androidclient.newscenter.provider.InformationColumns;
import com.neteast.androidclient.newscenter.utils.ImageUtil;
import com.neteast.androidclient.newscenter.view.NewsWidget;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

public class InformationListFragment extends Fragment implements OnClickListener {

    private static final String TYPE = "type";
    private int mType;
    private InfoAdapter mInfoAdapter;
    private Cursor mCursor;
    
    public static InformationListFragment newInstance(int type) {
        InformationListFragment f=new InformationListFragment();
        Bundle args=new Bundle();
        args.putInt(TYPE, type);
        f.setArguments(args);
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(TYPE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_infos, container, false);
        //顶部标题栏
        View top = root.findViewById(R.id.infos_top);
        //清空按钮
        View clear = root.findViewById(R.id.infos_clear);
        clear.setOnClickListener(this);
        //根据消息类型的不同，改变对应的视图内容
        changeContentByType(top,clear);
        //刷新按钮
        root.findViewById(R.id.infos_refresh).setOnClickListener(this);
        //消息列表为空时的视图
        View empty = root.findViewById(R.id.infos_empty);
        //消息列表
        ListView infosListView=(ListView) root.findViewById(R.id.infos_list);
        infosListView.setEmptyView(empty);
        mCursor=Information.queryInformation(getActivity(), mType);
        mInfoAdapter = new InfoAdapter(getActivity(), mCursor,infosListView);
        infosListView.setAdapter(mInfoAdapter);
        infosListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(InformationColumns.CONTENT_URI, id);
                Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    Information information = Information.parseCursor(cursor);
                    PacketKeepLive.addUserOption(information.getInfoId());
                }
            }
        });
        return root;
    }
    /**
     * 根据消息类型的不同，改变对应的视图内容
     * @param top
     * @param clear
     */
    private void changeContentByType(View top, View clear) {
        switch (mType) {
            case Information.SYS_INFO:
                top.setBackgroundResource(R.drawable.infos_sys_title);
                clear.setVisibility(View.GONE);
                break;
            case Information.APP_INFO:
                top.setBackgroundResource(R.drawable.infos_app_title);
                clear.setVisibility(View.VISIBLE);
                break;
            case Information.USER_INFO:
                top.setBackgroundResource(R.drawable.infos_users_title);
                clear.setVisibility(View.VISIBLE);
                break;
            default:
                throw new RuntimeException("错误的类型");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infos_clear:
                deleteAllInfo();
                break;
            case R.id.infos_refresh:
                refreshInfo();
                break;
            default:
                break;
        }
    }
    /**
     * 删除所有的消息
     */
    private void deleteAllInfo() {
        //先把当前的消息中包含有图片的，把图片链接收集起来
        ArrayList<String> pictures=new ArrayList<String>();
        for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()){
            Information information = Information.parseCursor(mCursor);
            if (!TextUtils.isEmpty(information.getPicture())) {
                pictures.add(information.getPicture());
            }
        }
        //删除数据库中的消息记录，刷新UI界面，此时，图片没有被删除掉
        Information.deleteAllInformation(getActivity(), mType);
        mCursor.requery();
        mInfoAdapter.notifyDataSetChanged();
        //删除已经过期的图片
        String[] array = pictures.toArray(new String[]{});
        for(int i=0,size=array.length;i<size;i++){
            ImageUtil.getInstance().deleteImage(array[i]);
        }
        //如果设置了清空消息时自动关闭，那么调用MainActivity的动画关闭方法
        if (ConfigManager.isAutoClose(getActivity())) {
            ((MainActivity)getActivity()).finishWithAnimation();
        }
    }

    /**
     * 刷新消息列表
     */
    private void refreshInfo() {
        NewsWidget newsWidget = NewsWidget.getIntance(getActivity());
        switch (mType) {
            case Information.SYS_INFO:
                newsWidget.clearSysNumsNum();
                break;
            case Information.APP_INFO:
                newsWidget.clearAppNumsNum();
                break;
            case Information.USER_INFO:
                newsWidget.clearUserNumsNum();
                break;
            default:
                throw new RuntimeException("错误的类型");
        }
        mCursor.requery();
        mInfoAdapter.notifyDataSetChanged();
    }
}
