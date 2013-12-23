package com.neteast.clouddisk.customerview;

import com.neteast.clouddisk.draganddrop.DragController;
import com.neteast.clouddisk.draganddrop.DragScroller;
import com.neteast.clouddisk.draganddrop.DragSource;
import com.neteast.clouddisk.draganddrop.DropTarget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DragLayer extends LinearLayout implements  DragController{
	private static final float DRAG_SCALE = 12f;
	
	private Bitmap mDragBitmap = null;

	private Rect mDragRect = new Rect();
	private Paint mDragPaint = new Paint();

	private View mIgnoredDropTarget;
	private View mDraggedView;
	
	/**
	 * X offset from where we touched on the cell to its upper-left corner
	 */
	private float mTouchOffsetX;

	/**
	 * Y offset from where we touched on the cell to its upper-left corner
	 */
	private float mTouchOffsetY;
	private boolean mDragging = false;
	private float mLastMotionX;
	private float mLastMotionY;
	private int mBitmapOffsetX;
	private int mBitmapOffsetY;
	/**
	 * Where the drag originated
	 */
	private DragSource mDragSource;
	/**
	 * The data associated with the object being dragged
	 */
	private Object mDragInfo;
	private boolean mShouldDrop;
	private final Rect mRect = new Rect();
	private DropTarget mLastDropTarget;
	private DragListener mListener;
	private final int[] mDropCoordinates = new int[2];

	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mLastDropTarget = null;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mShouldDrop && drop(x, y)) {
                    mShouldDrop = false;
                }
                endDrag();
                break;
        }

        return mDragging;
    }

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mDragging) {
			return false;
		}
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			// Remember where the motion event started
			mLastMotionX = x;
			mLastMotionY = y;

			// if ((x < SCROLL_ZONE) || (x > getWidth() - SCROLL_ZONE)) {
			// mScrollState = SCROLL_WAITING_IN_ZONE;
			// postDelayed(mScrollRunnable, SCROLL_DELAY);
			// } else {
			// mScrollState = SCROLL_OUTSIDE_ZONE;
			// }

			break;
		case MotionEvent.ACTION_MOVE:
			// Logger.d("Drag", "MOVE  x = "+x + "   y =" + y);
			final int scrollX = this.getScrollX();
			final int scrollY = this.getScrollY();

			final float touchX = mTouchOffsetX;
			final float touchY = mTouchOffsetY;

			final int offsetX = mBitmapOffsetX;
			final int offsetY = mBitmapOffsetY;

			int left = (int) (scrollX + mLastMotionX - touchX - offsetX);
			int top = (int) (scrollY + mLastMotionY - touchY - offsetY);

			final Bitmap dragBitmap = mDragBitmap;
			final int width = dragBitmap.getWidth();
			final int height = dragBitmap.getHeight();

			final Rect rect = mRect;
			rect.set(left - 1, top - 1, left + width + 1, top + height + 1);

			mLastMotionX = x;
			mLastMotionY = y;

			left = (int) (scrollX + x - touchX - offsetX);
			top = (int) (scrollY + y - touchY - offsetY);

			rect.union(left - 1, top - 1, left + width + 1, top + height + 1);
//			invalidate(rect);
			invalidate();

			final int[] coordinates = mDropCoordinates;
			DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
			if (dropTarget != null) {
				if (mLastDropTarget == dropTarget) {
					dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
				} else {
					if (mLastDropTarget != null) {
						mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo, false);
					}
					dropTarget.onDragEnter(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
				}
			} else {
				if (mLastDropTarget != null) {
					mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo, false);
				}
			}
			mLastDropTarget = dropTarget;

			// boolean inDragRegion = false;
			// if (mDragRegion != null) {
			// final RectF region = mDragRegion;
			// final boolean inRegion = region.contains(ev.getRawX(),
			// ev.getRawY());
			// if (!mEnteredRegion && inRegion) {
			// mDragPaint = mTrashPaint;
			// mEnteredRegion = true;
			// inDragRegion = true;
			// } else if (mEnteredRegion && !inRegion) {
			// mDragPaint = null;
			// mEnteredRegion = false;
			// }
			// }
			//
			// if (!inDragRegion && x < SCROLL_ZONE) {
			// if (mScrollState == SCROLL_OUTSIDE_ZONE) {
			// mScrollState = SCROLL_WAITING_IN_ZONE;
			// mScrollRunnable.setDirection(SCROLL_LEFT);
			// postDelayed(mScrollRunnable, SCROLL_DELAY);
			// }
			// } else if (!inDragRegion && x > getWidth() - SCROLL_ZONE) {
			// if (mScrollState == SCROLL_OUTSIDE_ZONE) {
			// mScrollState = SCROLL_WAITING_IN_ZONE;
			// mScrollRunnable.setDirection(SCROLL_RIGHT);
			// postDelayed(mScrollRunnable, SCROLL_DELAY);
			// }
			// } else {
			// if (mScrollState == SCROLL_WAITING_IN_ZONE) {
			// mScrollState = SCROLL_OUTSIDE_ZONE;
			// mScrollRunnable.setDirection(SCROLL_RIGHT);
			// removeCallbacks(mScrollRunnable);
			// }
			// }

			break;
		case MotionEvent.ACTION_UP:
			// Logger.d("Drag", "UP  x = "+x + "   y =" + y);
			// removeCallbacks(mScrollRunnable);
			if (mShouldDrop) {
				drop(x, y);
				mShouldDrop = false;
			}
			endDrag();

			break;
		case MotionEvent.ACTION_CANCEL:
			endDrag();
		}

		return true;
	}
	
	private void endDrag() {
		if (mDragging) {
			mDragging = false;
			if (mDragBitmap != null) {
				mDragBitmap.recycle();
			}
			if(mDraggedView != null){
				mDraggedView.setVisibility(View.VISIBLE);
			}
			if (mListener != null) {
				mListener.onDragEnd();
			}
		}
	}
	
	DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
		return findDropTarget(this, x, y, dropCoordinates);
	}

	private DropTarget findDropTarget(ViewGroup container, int x, int y, int[] dropCoordinates) {
		final Rect r = mDragRect;
		final int count = container.getChildCount();
		final int scrolledX = x + container.getScrollX();
		final int scrolledY = y + container.getScrollY();
		final View ignoredDropTarget = mIgnoredDropTarget;

		for (int i = count - 1; i >= 0; i--) {
			final View child = container.getChildAt(i);
			if (child.getVisibility() == VISIBLE && child != ignoredDropTarget) {
				child.getHitRect(r);
				if (r.contains(scrolledX, scrolledY)) {
					DropTarget target = null;
					if (child instanceof ViewGroup) {
						x = scrolledX - child.getLeft();
						y = scrolledY - child.getTop();
						target = findDropTarget((ViewGroup) child, x, y, dropCoordinates);
					}
					if (target == null) {
						if (child instanceof DropTarget) {
							dropCoordinates[0] = x;
							dropCoordinates[1] = y;
							return (DropTarget) child;
						}
					} else {
						return target;
					}
				}
			}
		}
		return null;
	}

	private boolean drop(float x, float y) {
		invalidate();

		final int[] coordinates = mDropCoordinates;
		DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);
		if (dropTarget != null) {
			// 在退出Drag之前需要补发一次DragOver，以防止在之前过程中没有DragOver事件，直接Drop的情况
			dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);

			dropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo, true);
			if (dropTarget.acceptDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo)) {
				dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
				mDragSource.onDropCompleted((View) dropTarget, true);
				return true;
			} else {
				mDragSource.onDropCompleted((View) dropTarget, false);
				return true;
			}
		}

		return false;
	}

	@Override
	public void startDrag(View v, DragSource source, Object dragInfo, int dragAction) {

		BitmapFactory.Options o = new Options();

		// Hide soft keyboard, if visible
		// if (mInputMethodManager == null) {
		// mInputMethodManager = (InputMethodManager)
		// getContext().getSystemService(
		// Context.INPUT_METHOD_SERVICE);
		// }
		// mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);

		if (mListener != null) {
			mListener.onDragStart(v, source, dragInfo, dragAction);
		}

		Rect r = mDragRect;
		r.set(v.getScrollX(), v.getScrollY(), 0, 0);
		offsetDescendantRectToMyCoords(v, r);

		Bitmap viewBitmap = getViewBitmap(v);

		mTouchOffsetX = mLastMotionX - r.left;
		mTouchOffsetY = mLastMotionY - r.top;

		int width = viewBitmap.getWidth();
		int height = viewBitmap.getHeight();

		Matrix scale = new Matrix();
		float scaleFactor = v.getWidth();
		scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
		scale.setScale(scaleFactor, scaleFactor);

		mDragBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, width, height, scale, true);

		final Bitmap dragBitmap = mDragBitmap;
		mBitmapOffsetX = (dragBitmap.getWidth() - width) / 2;
		mBitmapOffsetY = (dragBitmap.getHeight() - height) / 2;

		if (dragAction == DRAG_ACTION_MOVE) {
			v.setVisibility(View.INVISIBLE);
			// //TODO:直接删除
			// ViewGroup group = (ViewGroup)v.getParent();
			// group.removeView(v);
		}

		mDragging = true;
		mShouldDrop = true;
		mDragSource = source;
		mDragInfo = dragInfo;
		mDraggedView = v;

		invalidate();
	}

	@Override
	public void setDragListener(DragListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDragListener(DragListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDragScoller(DragScroller scroller) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mDragging && mDragBitmap != null) {
			canvas.drawBitmap(mDragBitmap, this.getScrollX() + mLastMotionX - mTouchOffsetX - mBitmapOffsetX, this.getScrollY() + mLastMotionY - mTouchOffsetY - mBitmapOffsetY, mDragPaint);
		}
	}
	
	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e("", "failed getViewBitmap(" + v + ")", new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}
}
