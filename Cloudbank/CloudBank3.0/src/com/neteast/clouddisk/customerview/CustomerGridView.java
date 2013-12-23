package com.neteast.clouddisk.customerview;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.lib.db.DataInfo;
import com.neteast.clouddisk.draganddrop.DragController;
import com.neteast.clouddisk.draganddrop.DragSource;
import com.neteast.clouddisk.draganddrop.DropTarget;
import com.neteast.clouddisk.utils.OnDropCallBack;

public class CustomerGridView extends GridView implements DragSource, DropTarget{
	private static final String TAG = "CustomerGridView";
	private DragController mDragController;
	private Rect mRect = new Rect();
	private List<DataInfo> mDataList;
	private BaseAdapter mAdapter;
	private OnDropCallBack mcallback = null ;

	public CustomerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		/*
		 * if (ev.getAction() == MotionEvent.ACTION_MOVE) { return true; }
		 */
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		Log.d(TAG, "onDrop");
		final ItemViewGroup selected = findView(x, y);
		if(selected==null) {
			if(mcallback!=null){
				mcallback.onDropCompleted(null,null);
			}
			return ;
		}
		final DataInfo currentInfo = (DataInfo)selected.getTag();
		final DataInfo draggedInfo = (DataInfo) dragInfo;
		if(mcallback !=null){
			mcallback.onDropCompleted(draggedInfo,currentInfo);
		}
		/*
		if(currentInfo==draggedInfo) return ;
		if (currentInfo != null && draggedInfo != null) {
			if(mcallback !=null){
				mcallback.onDropCompleted(draggedInfo,currentInfo);
			}
			
		}
		*/
	}
	public void setOnDropListener(OnDropCallBack callback){
		mcallback = callback;
	}
	public void setAdapter(BaseAdapter adapter) {
		mAdapter = adapter;
		super.setAdapter(mAdapter);
	}

	private DataInfo getDataInfo(Object obj) {
		if (obj instanceof Map) {
			Map map = (Map) obj;
			return (DataInfo) map.get("datainfo");
		}
		return null;
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo, boolean isDrop) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset, Object dragInfo) {
		return true;
	}

	@Override
	public void setDragger(DragController dragger) {
		mDragController = dragger;
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
	}

	public void setDataList(List<DataInfo> mDataList) {
		this.mDataList = mDataList;
	}

	public boolean startDrag(View v) {
		if (mDragController != null) {
			mDragController.startDrag(v, this, v.getTag(), DragController.DRAG_ACTION_MOVE);
			return true;
		}
		return false;
	}

	private ItemViewGroup findView(ViewGroup parent, int x, int y) {
		final Rect frame = mRect;
		final int count = parent.getChildCount();
		
		for (int i = count - 1; i >= 0; i--) {
			final View child = parent.getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				if (child instanceof ItemViewGroup) {
					return (ItemViewGroup) child;
				} else if (child instanceof ViewGroup) {
					final ItemViewGroup result = findView((ViewGroup) child, x - child.getLeft(), y - child.getTop());
					if(result != null){
						return result;
					}
					else{
						continue;
					}
				}
			}
		}

		return null;
	}

	private ItemViewGroup findView(int x, int y) {
		return findView(this, x, y);
	}
}
