package com.neteast.videotv.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;
import com.neteast.videotv.bean.LeftNavTab;
import com.neteast.videotv.listener.BackPressListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.widget.ChannelList;
import com.neteast.videotv.utils.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-23
 * Time: 下午5:46
 */
@EFragment(R.layout.frag_leftnav)
public class LeftNavFragment extends NetFragment implements BackPressListener{

    @ViewById(R.id.container)
    ChannelList mTabList;

    MainActivity mActivity;

    public static final int HOME_PAGE = 0;
    public static final int TOPIC = 1;
    public static final int CHANNEL = 2;
    public static final int MY_VIDEO = 3;
    public static final int SEARCH = 4;
    public static final int SETTING = 5;
    private List<LeftNavTab> mLeftNavTabs;
    private ConfirmDialog mConfirmDialog;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    public void requestFocus() {
        mTabList.requestFocus();
    }

    public boolean hasFocused() {
        return mTabList.getFocusedChild() != null;
    }

    public void setSelectNav(int index){
        mTabList.setSelection(index);
    }

    @AfterViews
    void initTabBar() {
        mLeftNavTabs = LeftNavTab.getLeftNavTabs();
        mTabList.setAdapter(new NavTabAdapter(getActivity()));
        mTabList.setSelectionChangedListener(new ChannelList.SelectionChangedListener() {
            @Override
            public void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex) {
                switch (currentSelectedIndex) {
                    case HOME_PAGE:
                        mActivity.showHomePage();
                        break;
                    case TOPIC:
                        mActivity.showTopic();
                        break;
                    case CHANNEL:
                        mActivity.showChannel();
                        break;
                    case MY_VIDEO:
                        mActivity.showMyVideo();
                        break;
                    case SEARCH:
                        mActivity.showSearch();
                        break;
                    case SETTING:
                        mActivity.showSetting();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (mConfirmDialog == null ){
            mConfirmDialog = new ConfirmDialog();
            mConfirmDialog.setOKListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConfirmDialog.dismiss();
                    getActivity().finish();
                }
            });
        }
        mConfirmDialog.show(getFragmentManager(), "logout");
        return true;
    }

    private class NavTabAdapter extends ArrayAdapter<LeftNavTab> {

        public NavTabAdapter(Context context) {
            super(context, 0, 0, mLeftNavTabs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LeftNavTab tab = getItem(position);
            convertView = getActivity().getLayoutInflater().inflate(R.layout.item_nav_tab, parent, false);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageResource(tab.getIcon());
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(tab.getText());
            int width = getResources().getDimensionPixelOffset(R.dimen.nav_tab_bar_width) ;
            int height = getResources().getDimensionPixelOffset(R.dimen.nav_tab_height) ;
            LinearLayout.LayoutParams params=
                    new LinearLayout.LayoutParams(width,height);
            params.gravity= Gravity.CENTER;
            convertView.setLayoutParams(params);
            convertView.setNextFocusRightId(R.id.ChannelList);
            return convertView;
        }
    }

}
