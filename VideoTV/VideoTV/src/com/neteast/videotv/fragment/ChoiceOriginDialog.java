package com.neteast.videotv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-3
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_choice_origin)
public class ChoiceOriginDialog extends TVDialog {


    @ViewById(R.id.origin_container)LinearLayout mOriginContainer;
    public static interface OriginChangedListener{
        void onOriginChanged(String newOrigin);
    }

    private String[] mOrigins;
    private String mCurrentOrigin;
    private OriginChangedListener mListener;

    public static final ChoiceOriginDialog newDialog(String currentOrigin,List<String> origins){
        ChoiceOriginDialog_ dialog=new ChoiceOriginDialog_();
        Bundle args=new Bundle();
        args.putStringArray("origins",origins.toArray(new String[]{}));
        args.putString("currentOrigin", currentOrigin);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener= (OriginChangedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrigins = getArguments().getStringArray("origins");
        mCurrentOrigin = getArguments().getString("currentOrigin");
    }


    @AfterViews
    void initUI(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(int i=0;i<mOrigins.length;i++){
            String origin=mOrigins[i];
            View view = inflater.inflate(R.layout.item_nav_subitem, mOriginContainer, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(origin);

            LinearLayout.LayoutParams params=
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin=getResources().getDimensionPixelOffset(R.dimen.choice_origin_margin_top);
            if (i==mOrigins.length-1){
                params.bottomMargin=getResources().getDimensionPixelOffset(R.dimen.choice_origin_margin_bottom);
            }
            if (mCurrentOrigin==origin){
                view.setActivated(true);
            }
            view.setTag(origin);
            mOriginContainer.addView(view,params);
            view.setOnClickListener(mChoiceOriginListener);
        }

    }

    private View.OnClickListener mChoiceOriginListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             String origin= (String) v.getTag();
            if (!origin.equals(mCurrentOrigin)){
                mListener.onOriginChanged(origin);
            }
            dismiss();
        }
    };
}
