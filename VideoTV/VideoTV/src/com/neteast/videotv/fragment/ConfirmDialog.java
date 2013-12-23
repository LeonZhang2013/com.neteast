package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.neteast.videotv.R;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-23
 * Time: 上午10:34
 */
public class ConfirmDialog extends TVDialog {


    private CharSequence mCharSequence;
    private View mOk;
    private View mCancel;
    
    public void setMessage(CharSequence message){
    	mCharSequence = message;
    }
    
    private View.OnClickListener BLANK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private View.OnClickListener mOKListener = BLANK;
    private View.OnClickListener mCancelListener = BLANK;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_confirm, container, false);
        TextView mMessage = (TextView) root.findViewById(R.id.dialog_message);
        if(mCharSequence!=null)mMessage.setText(mCharSequence);
        mOk = root.findViewById(R.id.confirmOK);
        mCancel = root.findViewById(R.id.confirmCancel);
        mOk.setOnClickListener(mOKListener);
        mCancel.setOnClickListener(mCancelListener);
        return root;
    }

    public ConfirmDialog setOKListener(View.OnClickListener listener){
        mOKListener = listener;
        return this;
    }

    public ConfirmDialog setCancelListener(View.OnClickListener listener){
        mCancelListener = listener;
        return this;
    }
}
