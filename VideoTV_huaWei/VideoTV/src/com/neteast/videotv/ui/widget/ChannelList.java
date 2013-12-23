package com.neteast.videotv.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-7
 * Time: 下午3:46
 */
public class ChannelList extends LinearLayout {

    public static interface ItemClickListener{
        void onItemClickListener(ViewGroup parent, View view, int position);
    }
    public static interface SelectionChangedListener{
        void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex);
    }

    private View mHeadView;
    protected int mSelectionIndex=-1;
    protected Adapter mAdapter;

    private ItemClickListener BLANK_LISTENER= new ItemClickListener() {
        @Override
        public void onItemClickListener(ViewGroup parent, View view, int position) {}
    };

    private SelectionChangedListener BLANK_SELECTION_LISTENER=new SelectionChangedListener() {
        @Override
        public void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex) {}
    };

    private ItemClickListener mItemClickListener=BLANK_LISTENER;
    private SelectionChangedListener mSelectionChangedListener=BLANK_SELECTION_LISTENER;


    public ChannelList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(Adapter adapter){
        mAdapter=adapter;
        removeAllViews();
        generateContent();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        this.mSelectionChangedListener = selectionChangedListener;
    }

    public void addHeadView(View headView){
        this.mHeadView = headView;
    }

    protected  void generateContent(){
        if (mHeadView!=null){
            addView(mHeadView);
        }
        for(int i=0,size=mAdapter.getCount();i<size;i++){
            View view = mAdapter.getView(i, null, null);
            view.setOnClickListener(mClickListener);
            view.setTag(getChildCount());
            addView(view);
        }
    }

    public void setDefaultSelected(){
        mSelectionIndex=-1;
        if (getChildCount()>0){
            if (mHeadView!=null){
                setSelection(1);
            } else {
                setSelection(0);
            }
        }
    }

    public void setSelection(int currentIndex) {
    	Log.i("ChannelList", "ChannelList setSelection = "+currentIndex);
        if (mHeadView!=null && currentIndex==0){
            return;
        }
        final int oldSelectionIndex = mSelectionIndex;
        mSelectionIndex = currentIndex;
        if (oldSelectionIndex == -1) {
            getChildAt(mSelectionIndex).setActivated(true);
            mSelectionChangedListener.onSelectionChanged(oldSelectionIndex,mSelectionIndex);
            return;
        }
        if (oldSelectionIndex != mSelectionIndex) {
            getChildAt(oldSelectionIndex).setActivated(false);
            getChildAt(mSelectionIndex).setActivated(true);
            mSelectionChangedListener.onSelectionChanged(oldSelectionIndex,mSelectionIndex);
        }
    }

    public int getSelectionIndex() {
        return mSelectionIndex;
    }

    public Object getSelectionItem(){
        if(mSelectionIndex>=0 && mSelectionIndex< getChildCount() ){
            int index = mHeadView==null? mSelectionIndex : mSelectionIndex-1;
            return mAdapter.getItem(index);
        }else {
            return null;
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position=(Integer)v.getTag();
            setSelection(position);
            mItemClickListener.onItemClickListener(ChannelList.this,v,position);
        }
    };

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (mSelectionIndex!=-1 && mSelectionIndex<getChildCount()){
            return getChildAt(mSelectionIndex).requestFocus(direction, previouslyFocusedRect);
        }else {
            return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (direction==FOCUS_LEFT || direction==FOCUS_RIGHT){
            if (mSelectionIndex!=-1 && mSelectionIndex < getChildCount()){
                if (!hasFocus()){
                    views.clear();
                }
                getChildAt(mSelectionIndex).addFocusables(views, direction, focusableMode);
                return;
            }
        }
        super.addFocusables(views, direction, focusableMode);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (mSelectionIndex!=-1 && mSelectionIndex<getChildCount()){
            return getChildAt(mSelectionIndex).dispatchUnhandledMove(focused,direction);
        }else {
            return super.dispatchUnhandledMove(focused, direction);
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        if (hasFocus() && (direction==FOCUS_UP || direction==FOCUS_DOWN)){
            return FocusFinder.getInstance().findNextFocus(this, focused, direction);
        }else {
            return super.focusSearch(focused,direction);
        }
    }

}
