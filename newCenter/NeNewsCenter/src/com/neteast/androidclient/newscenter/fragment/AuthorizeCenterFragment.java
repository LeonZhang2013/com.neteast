package com.neteast.androidclient.newscenter.fragment;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.activity.MainActivity;
import com.neteast.androidclient.newscenter.domain.SNSEntity;
import com.neteast.androidclient.newscenter.domain.SNSEntity.AuthListener;
import com.neteast.androidclient.newscenter.domain.SNSEntity.SnsType;
import com.neteast.androidclient.newscenter.exception.AuthorizeDialogError;
import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AuthorizeCenterFragment extends Fragment implements OnClickListener{

    private Button mQQAuthorizeBtn;
    private Button mSinaAuthorizeBtn;
    private Button mRenrenAuthorizeBtn;
    private Button mKaixinAuthorizeBtn;
    
    private Button[] mSnsButtons;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.frag_authorize_center, container, false);
        //返回按钮
        contentView.findViewById(R.id.authorize_back).setOnClickListener(this);
        //QQ授权按钮
        mQQAuthorizeBtn = (Button) contentView.findViewById(R.id.authorize_qq);
        mQQAuthorizeBtn.setTag(SnsType.QQWeiBo);
        //新浪授权按钮
        mSinaAuthorizeBtn = (Button) contentView.findViewById(R.id.authorize_sina);
        mSinaAuthorizeBtn.setTag(SnsType.SinaWeiBo);
        //人人网授权按钮
        mRenrenAuthorizeBtn = (Button) contentView.findViewById(R.id.authorize_renren);
        mRenrenAuthorizeBtn.setTag(SnsType.RenRen);
        //开心网授权按钮
        mKaixinAuthorizeBtn = (Button) contentView.findViewById(R.id.authorize_kaixin);
        mKaixinAuthorizeBtn.setTag(SnsType.KaiXin);
        
        mSnsButtons=new Button[]{mQQAuthorizeBtn,mSinaAuthorizeBtn,mRenrenAuthorizeBtn,mKaixinAuthorizeBtn};
        for(int i=0,size=mSnsButtons.length;i<size;i++){
            mSnsButtons[i].setOnClickListener(this);
        }
        return contentView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        for(int i=0,size=mSnsButtons.length;i<size;i++){
            refreshAuthorizeState(mSnsButtons[i]);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authorize_back:
                backToUserCenter();
                break;
            case R.id.authorize_qq:
            case R.id.authorize_sina:
            case R.id.authorize_renren:
            case R.id.authorize_kaixin:
                onClickAuthorizeButton(v);
                break;
        }
    }


    private void backToUserCenter() {
        MainActivity activity = (MainActivity) getActivity();
        activity.openUserCenterPage();
    }
    
    private void refreshAuthorizeState(Button snsButton) {
        SnsType snsType=(SnsType) snsButton.getTag();
        SNSEntity entity = SNSEntity.getEntity(getActivity(), snsType);
        if (entity.isSessionValid()) {
            snsButton.setBackgroundResource(R.drawable.share_unbind);
        }else {
        	snsButton.setBackgroundResource(R.drawable.share_bind);
        }
    }
    
    private void onClickAuthorizeButton(View view) {
        final Button currentButton=(Button) view;
        SnsType snsType=(SnsType) currentButton.getTag();
        SNSEntity entity = SNSEntity.getEntity(getActivity(), snsType);
        if (entity.isSessionValid()) {
            entity.deauthorize();
            currentButton.setBackgroundResource(R.drawable.share_bind);
        }else {
            entity.authorize(getFragmentManager(),new AuthListener() {
                @Override
                public void onError(AuthorizeDialogError e) {
                    Utils.showToast(getActivity(),  "Auth error : " + e.getMessage());
                }
                @Override
                public void onComplete() {
                	currentButton.setBackgroundResource(R.drawable.share_unbind);
                }
                @Override
                public void onCancel() {
                    Utils.showToast(getActivity(),  "Auth cancel");
                }
                @Override
                public void onAuthorizeException(AuthorizeException e) {
                    Utils.showToast(getActivity(),  "Auth exception : " + e.getMessage());
                }
            });
        }
    }
}
