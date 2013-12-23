package com.neteast.videotv.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;
import com.neteast.videotv.dao.Menu;
import static com.neteast.videotv.dao.MenuDao.*;
import com.neteast.videotv.dao.MySQLiteOpenHelper;
import com.neteast.videotv.ui.widget.ResetChannelView;
import java.util.Collections;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 上午10:04
 */
@EActivity(R.layout.activity_reset_channel)
public class ResetChannelActivity extends Activity {

    public static final int COLUMN_COUNT = 8;
    @ViewById(R.id.channel1) TextView mChannel1;
    @ViewById(R.id.channel2) TextView mChannel2;
    @ViewById(R.id.channelScroll) ScrollView mChannelScroll;
    @ViewById(R.id.channelGrid) GridLayout mChannelGrid;
    @ViewById(R.id.btnOk) Button mBtnOk;

    private SQLiteDatabase db;
    private List<Menu> mMenus;

    private int currentInputIndex;
    private ProgressDialog loading;

    @AfterViews
    void initUI(){
        MySQLiteOpenHelper helper=new MySQLiteOpenHelper(this);
        db = helper.getWritableDatabase();

        mChannel1.setActivated(true);

        mChannelScroll.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mChannelScroll.getViewTreeObserver().removeOnPreDrawListener(this);
                int measuredWidth = mChannelScroll.getMeasuredWidth();
                int measuredHeight = mChannelScroll.getMeasuredHeight();
                int itemW=measuredWidth / COLUMN_COUNT;
                int itemH=measuredHeight / 4;
                generateGrids(itemW,itemH);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db!=null){
            db.close();
        }
    }

    private void generateGrids(int width, int height) {
        mMenus = getAllMenu(db);
        for (int i=0,size=mMenus.size();i<size;i++) {
            Menu menu=mMenus.get(i);
            ResetChannelView channelView = new ResetChannelView(this, width, height);
            channelView.setChannel(menu.getCurrentIndex(),menu.getTitle());
            channelView.setOnClickListener(mClickChannelListener);
            mChannelGrid.addView(channelView);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }


    @Click(R.id.btnOk)
    void swapChannel(){

        int channel1No=1;
        int channel2No=1;
        try {
            channel1No= Integer.valueOf(mChannel1.getText().toString());
            channel2No = Integer.valueOf(mChannel2.getText().toString());
        }catch (NumberFormatException e){
            return;
        }

        int item1Index=channel1No-1;
        int item2Index=channel2No-1;

        Menu menu1 = mMenus.get(item1Index);
        Menu menu2 = mMenus.get(item2Index);

        ResetChannelView channel1 = (ResetChannelView) mChannelGrid.getChildAt(item1Index);
        ResetChannelView channel2 = (ResetChannelView) mChannelGrid.getChildAt(item2Index);
        channel1.setChannel(item1Index, menu2.getTitle());
        channel2.setChannel(item2Index, menu1.getTitle());

        menu2.setCurrentIndex(item1Index);
        menu1.setCurrentIndex(item2Index);

        updateCurrentIndex(db, menu1);
        updateCurrentIndex(db, menu2);

        Collections.swap(mMenus, item1Index, item2Index);

        mChannel1.setText("");
        mChannel2.setText("");
        if (mChannelGrid.getChildCount()>0){
            mChannelGrid.getChildAt(0).requestFocus();
        }
    }

    @Click(R.id.btnClear)
    void clearChannelInput(){
        mChannel1.setText("");
        mChannel2.setText("");
        if (mChannelGrid.getChildCount()>0){
            mChannelGrid.getChildAt(0).requestFocus();
        }
    }

    @Click(R.id.btnDefault)
    void setChannelIndexToDefault(){
        showLoading();
        for (Menu menu : mMenus) {
            menu.setCurrentIndex(menu.getDefaultIndex());
            updateCurrentIndex(db, menu);
        }
        mMenus = getAllMenu(db);
        for(int i=0,size=mMenus.size();i<size;i++){
            Menu menu = mMenus.get(i);
            ResetChannelView view= (ResetChannelView) mChannelGrid.getChildAt(i);
            view.setChannel(menu.getCurrentIndex(),menu.getTitle());
        }
        dismissLoading();
    }

    private void showLoading(){
        loading = ProgressDialog.show(this, "重置，请稍候", null, true, false);
    }

    private void dismissLoading(){
        if (loading!=null && loading.isShowing()){
            loading.dismiss();
            loading=null;
        }
    }

    private View.OnClickListener mClickChannelListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ResetChannelView view= (ResetChannelView) v;
            int currentIndex = view.getCurrentIndex();
            if (currentInputIndex==0){
                mChannel1.setText(String.valueOf(currentIndex+1));
                mChannel1.setActivated(false);
                mChannel2.setActivated(true);
                currentInputIndex=1;
            }else {
                mChannel2.setText(String.valueOf(currentIndex+1));
                mChannel2.setActivated(false);
                mChannel1.setActivated(true);
                currentInputIndex=0;
                mBtnOk.requestFocus();
            }
        }
    };



}