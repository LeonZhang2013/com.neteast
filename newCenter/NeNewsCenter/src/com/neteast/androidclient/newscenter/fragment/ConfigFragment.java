package com.neteast.androidclient.newscenter.fragment;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.R.id;
import com.neteast.androidclient.newscenter.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConfigFragment extends Fragment implements OnCheckedChangeListener {

    private CheckBox mAutoClose;
    private CheckBox mNotifyBroadcast;
    private CheckBox mDeleteConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_config, container, false);
        
        mAutoClose = (CheckBox) root.findViewById(R.id.config_auto_close);
        mAutoClose.setChecked(ConfigManager.isAutoClose(getActivity()));
        mAutoClose.setOnCheckedChangeListener(this);
        
        mNotifyBroadcast = (CheckBox) root.findViewById(R.id.config_broadcast_notify);
        mNotifyBroadcast.setChecked(ConfigManager.isNotifyBroadcast(getActivity()));
        mNotifyBroadcast.setOnCheckedChangeListener(this);
        
        mDeleteConfirm = (CheckBox) root.findViewById(R.id.config_delete_confirm);
        mDeleteConfirm.setChecked(ConfigManager.isDeleteConfirm(getActivity()));
        mDeleteConfirm.setOnCheckedChangeListener(this);
        
        TextView versionInfo=(TextView) root.findViewById(R.id.config_versioninfo);
        versionInfo.setText(Utils.getVersionInfo(getActivity()));
        return root;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.config_auto_close:
                ConfigManager.setAutoClose(getActivity(), isChecked);
                break;
            case R.id.config_broadcast_notify:
                ConfigManager.setNotifyBroadcast(getActivity(), isChecked);
                break;
            case R.id.config_delete_confirm:
                ConfigManager.setDeleteConfirm(getActivity(), isChecked);
                break;
            default:
                break;
        }
    }
}
