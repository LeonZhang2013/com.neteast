package com.neteast.clouddisk.customerview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
 
public class MyHorizontalScrollView extends HorizontalScrollView {  

	private int width;
	//private globalvarclass glc;
	private int itemwidth;
	private int realwidth;
	private ImageView leftimg;
	private ImageView rightimg;
	private int left_resid;
	private int right_resid;
	public MyHorizontalScrollView(Context context) {  
		super(context);  
	}  
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
	}  
	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {  
		super(context, attrs, defStyle);  
	}  
	@Override 
	protected int computeHorizontalScrollRange() {  
		return super.computeHorizontalScrollRange();  
	}  
	@Override 
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {  
		return super.computeScrollDeltaToGetChildRectOnScreen(rect);  
	}  
	@Override 
	protected float getLeftFadingEdgeStrength() {  
		return super.getLeftFadingEdgeStrength();  
	}  
	@Override 
	protected float getRightFadingEdgeStrength() {  
		return super.getRightFadingEdgeStrength();  
	}  
	@Override 
	protected void measureChild(View child, int parentWidthMeasureSpec,  
			int parentHeightMeasureSpec) {  
		super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);  
	}  
	@Override 
	protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,  
			int parentHeightMeasureSpec, int heightUsed) {  
		super.measureChildWithMargins(child, parentWidthMeasureSpec,  widthUsed, parentHeightMeasureSpec, heightUsed);  
	}  
	
	public void setleftimage(ImageView img,int resid){
		leftimg= img;
		left_resid = resid;
	}
	public void setrightimage(ImageView img,int resid){
		rightimg= img;
		right_resid = resid;
	}
	
	public void setscrollwidth(int w){
		width=w;
	}
	public void setitemwidth(int w){
		itemwidth=w;
	}
	public void setrealwidth(int w){
		realwidth=w;
	}
	
	/*
	public void setglchand(globalvarclass gl){
    	glc=gl;
    }
	*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pos=this.getScrollX();
	
		/*
		if((pos==0||pos <itemwidth-20)&& realwidth > width+itemwidth){
			//right blue
			//rightimg.setBackgroundResource(R.drawable.rightarrow_focus);
			rightimg.setBackgroundResource(right_resid);
			leftimg.setBackgroundResource(R.drawable.leftarrow_normal);
		}else if(pos>0 && pos+width <realwidth-itemwidth){
			//double blue
			//leftimg.setBackgroundResource(R.drawable.leftarrow_focus);
			leftimg.setBackgroundResource(left_resid);
			//rightimg.setBackgroundResource(R.drawable.rightarrow_focus);
			rightimg.setBackgroundResource(right_resid);
		}else if(pos >0 && pos+width >=realwidth- itemwidth){
			//left blue
			rightimg.setBackgroundResource(R.drawable.rightarrow_normal);
			//leftimg.setBackgroundResource(R.drawable.leftarrow_focus);
			leftimg.setBackgroundResource(left_resid);
		}
		*/
		super.onTouchEvent(event);
	    return false;
	}
} 

