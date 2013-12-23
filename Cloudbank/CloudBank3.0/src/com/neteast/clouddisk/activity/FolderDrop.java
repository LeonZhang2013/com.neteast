package com.neteast.clouddisk.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.neteast.clouddisk.draganddrop.DragSource;
import com.neteast.clouddisk.draganddrop.DropTarget;

public class FolderDrop extends LinearLayout implements DropTarget {
	private FloderDropListener mFloderDropListener;
	
	public FolderDrop(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setFloderDropListener(FloderDropListener listener){
		mFloderDropListener = listener;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		if(mFloderDropListener != null){
			mFloderDropListener.onDropOutFoder(dragInfo);
		}
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo, boolean isDrop) {
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		return true;
	}

	
	public interface FloderDropListener{
		public void onDropOutFoder(Object dragInfo);
	}
}